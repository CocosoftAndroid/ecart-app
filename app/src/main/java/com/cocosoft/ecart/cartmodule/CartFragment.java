package com.cocosoft.ecart.cartmodule;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.cocosoft.ecart.R;
import com.cocosoft.ecart.database.DatabaseHandler;
import com.cocosoft.ecart.listeners.CheckboxListener;
import com.cocosoft.ecart.listeners.IndividualItemListener;
import com.cocosoft.ecart.listeners.QuantityListener;
import com.cocosoft.ecart.listeners.WishlistListener;
import com.cocosoft.ecart.loginmodule.IndividualItemFragment;
import com.cocosoft.ecart.loginmodule.LoginFragment;
import com.cocosoft.ecart.network.APIInterface;
import com.cocosoft.ecart.network.RetrofitAPIClient;
import com.cocosoft.ecart.scanlistmodule.ProductItem;
import com.cocosoft.ecart.wishlistmodule.WishList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class CartFragment extends Fragment implements View.OnClickListener, QuantityListener, WishlistListener, IndividualItemListener, CheckboxListener {

    private TextView mAddCartTxt, mGrandTotalTxt;
    private LinearLayoutManager mLManager;
    private RecyclerView mProductRView;
    private CartAdapter mCartAdapter;
    private QuantityListener mQtyListener;
    private ArrayList<CartItem> mCartArray=new ArrayList<>();
    private TextView mCountTxtView;
    private TextView mTitleTxtView;
    private ImageView mCartImg;
    private SharedPreferences prefs;
    private TextView mAddWishTxt;
    private Gson gson;
    private SharedPreferences.Editor prefsEditor;
    private DatabaseHandler mDB;
    int _checkoutAmount =0;
    private APIInterface apiInterface;
    private Call<WishList> response;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getContext().getSharedPreferences("cocosoft", MODE_PRIVATE);
        prefsEditor = prefs.edit();
        gson=new Gson();
        String tempdata = prefs.getString("tempcartlist", null);
        Type type = new TypeToken<List<CartItem>>() {}.getType();
        ArrayList<CartItem> arr=gson.fromJson(tempdata, type);
        if(arr!=null) {
            mCartArray = gson.fromJson(tempdata, type);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        init(view);
        setListeners();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mTitleTxtView.setText("Cart");
    }

    private void setListeners() {
        mAddCartTxt.setOnClickListener(this);
        mAddWishTxt.setOnClickListener(this);
    }

    public void setListener(QuantityListener lis) {
        mQtyListener = lis;
    }

    private void init(View view) {
        gson = new Gson();
        apiInterface = RetrofitAPIClient.getClient(getContext()).create(APIInterface.class);
        mAddCartTxt = (TextView) view.findViewById(R.id.add_cart_txt);
        mAddWishTxt = (TextView) view.findViewById(R.id.add_wish_txt);
        mGrandTotalTxt = (TextView) view.findViewById(R.id.grandtotal_txt);
        mDB=new DatabaseHandler(getContext());
        mLManager = new LinearLayoutManager(getContext());
        mProductRView = (RecyclerView) view.findViewById(R.id.rview);
        mProductRView.setLayoutManager(mLManager);
        mCartAdapter = new CartAdapter(getContext(), mCartArray, this, this, this,this);
        mProductRView.setAdapter(mCartAdapter);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        mCountTxtView = (TextView) toolbar.findViewById(R.id.total_count);
        mTitleTxtView = (TextView) toolbar.findViewById(R.id.title_txt);
        mCartImg = (ImageView) toolbar.findViewById(R.id.cart_img);
        mCountTxtView.setVisibility(View.GONE);
        mCartImg.setVisibility(View.GONE);
        calculateTotal();
    }

    @Override
    public void onClick(View v) {
        boolean isloggedin = prefs.getBoolean("isloggedin", false);
        String username = prefs.getString("username", "");
        switch (v.getId()) {
            case R.id.add_cart_txt:
                if (isloggedin) {
                    Toast.makeText(getContext(), "Processing Payment", Toast.LENGTH_SHORT).show();
                    if(_checkoutAmount!=0)
                    {
                        Intent i=new Intent(CartFragment.this.getActivity(),BillingPage.class);
                        i.putExtra("Checkout Amount",_checkoutAmount);
                        Bundle args = new Bundle();
                        args.putParcelableArrayList("ARRAYLIST",mCartArray);
                        i.putExtra("BUNDLE",args);
                        startActivity(i);
                    }
                    else
                    {
                        Toast.makeText(getContext(), "Sorry !! No items in your Cart to check out", Toast.LENGTH_SHORT).show();
                    }
                } else
                    {
                    Log.i("CartFragment","User Not logged IN");
                    openFrag(1, "");
//                    openFrag(3,"");

                }
                break;

        }
    }

    @Override
    public void onQuantityChange(String productid, int quantity) {
        if (quantity == 0) {
            for (int i = 0; i < mCartArray.size(); i++) {
                if (mCartArray.get(i).getProductId().equals(productid)) {
                    mCartArray.get(i).setCount(0);
                    mCartArray.remove(i);
                }
            }
        } else {
            for (int i = 0; i < mCartArray.size(); i++) {
                if (mCartArray.get(i).getProductId().equals(productid)) {
                    int count = mCartArray.get(i).getCount();
                    mCartArray.get(i).setCount(count + quantity);
                }
            }
        }
        mCartAdapter.notifyDataSetChanged();
        String json = gson.toJson(mCartArray);
        prefsEditor.putString("tempcartlist", json);
        prefsEditor.commit();
        calculateTotal();
    }

    private void calculateTotal() {
         _checkoutAmount  = 0;
        for (int i = 0; i < mCartArray.size(); i++) {
            _checkoutAmount  = _checkoutAmount  + (mCartArray.get(i).getCount() * mCartArray.get(i).getProductPrice().intValue());
        }
        mGrandTotalTxt.setText("Grand Total = $ " + _checkoutAmount );
    }

    private void openFrag(int i, String productid) {
        Fragment firstFragment = null;
        switch (i) {
            case 1:
                firstFragment = new LoginFragment();
                break;
            case 2:
                firstFragment = new IndividualItemFragment();
                ProductItem item = null;
                for(int ij=0;ij<mCartArray.size();ij++)
                {
                    if(mCartArray.get(ij).getProductId().equals(productid))
                        item=new ProductItem(mCartArray.get(ij).getProductId(),mCartArray.get(ij).getProductName(),mCartArray.get(ij).getProductDesc(),mCartArray.get(ij).getProductPrice(),0,0,false);
                }
                Bundle bundles = new Bundle();
                bundles.putParcelable("item", item);
                firstFragment.setArguments(bundles);
                break;
        }
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.frame, firstFragment, "h");
        fragmentTransaction.addToBackStack("h");
        fragmentTransaction.commit();
    }

    @Override
    public void onFavouriteClicked(String productid, boolean isChecked) {
       /* String username = prefs.getString("username", "");
        if (isChecked) {

            Toast.makeText(getContext(), "Added to Wishlist", Toast.LENGTH_SHORT).show();
            mDB.addToWishlist(productid, username);

        } else {
            Toast.makeText(getContext(), "Wishlist Removed", Toast.LENGTH_SHORT).show();
            mDB.removeWishlist(productid, username);

        }*/

    }

    @Override
    public void onFavouriteClicked(String productid, String productname, Double price, boolean isChecked) {
        String username = prefs.getString("username", "");
        String token = prefs.getString("token", "");
        if (isChecked) {

            Toast.makeText(getContext(), "Added to Wishlist", Toast.LENGTH_SHORT).show();
//            mDB.addToWishlist(productid, username);
            addWishList(new WishList(Integer.parseInt(productid), username, productname, price, null, true,true), token);

        } else {
            Toast.makeText(getContext(), "Wishlist Removed", Toast.LENGTH_SHORT).show();
//            mDB.removeWishlist(productid, username);
            addWishList(new WishList(Integer.parseInt(productid), username, productname, price, null, false,true), token);
        }

    }

    @Override
    public void OnCardClick(String productid) {
        openFrag(2, productid);
    }

    @Override
    public void onChecked(String productid, boolean isChecked) {

    }


    private void addWishList(WishList wlist, String token) {
        response = apiInterface.addWishList(wlist, token);
        response.enqueue(new Callback<WishList>() {

            @Override
            public void onResponse(Call<WishList> call, Response<WishList> response) {

            }

            @Override
            public void onFailure(Call<WishList> call, Throwable t) {

            }
        });
    }
}
