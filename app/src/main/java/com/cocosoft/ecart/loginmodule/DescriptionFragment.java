package com.cocosoft.ecart.loginmodule;

/**
 * Created by cocoadmin on 10/10/2017.
 */

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cocosoft.ecart.R;
import com.cocosoft.ecart.cartmodule.CartFragment;
import com.cocosoft.ecart.cartmodule.CartItem;
import com.cocosoft.ecart.network.APIInterface;
import com.cocosoft.ecart.network.RetrofitAPIClient;
import com.cocosoft.ecart.scanlistmodule.Product;
import com.cocosoft.ecart.wishlistmodule.WishList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static android.content.Context.MODE_PRIVATE;

public class DescriptionFragment extends Fragment implements View.OnClickListener {

    private RelativeLayout mSearchLayout;
    String id = "";
    private int count = 1;
    private APIInterface apiInterface;
    boolean contains = false;
    CartItem item = null;
    private Button mWishlistBtn, mAddCartBtn;
    private CheckBox mFavouriteCheckbox;
    private TextView mProductTitleTxt, mProductPriceTxt, mProductDescTxt, mCountTxtView, mCartCountTxt, mTitleTxtView;
    private ImageView mProductImgView, mPlusImgView, mMinusImgView, mCartImg;
    private ArrayList<CartItem> mCartArray = new ArrayList<>();
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;
    private Gson gson;
    private static CartItem cartItem = null;
    private int prevCount = -1;
    private SearchView sv;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getArguments().getString("id");
        prefs = getContext().getSharedPreferences("cocosoft", MODE_PRIVATE);
        prefsEditor = prefs.edit();
        gson = new Gson();
        Log.e("desc", "onCreate");
    }

    @Override
    public void onResume() {
        super.onResume();
        int theCount = 0;
        String tempdata = prefs.getString("tempcartlist", null);
        Type type = new TypeToken<List<CartItem>>() {
        }.getType();
        ArrayList<CartItem> arr = gson.fromJson(tempdata, type);
        if (arr != null) {
            mCartArray.clear();
            mCartArray.addAll((ArrayList<CartItem>) gson.fromJson(tempdata, type));
        }
        for (int i = 0; i < mCartArray.size(); i++) {
            if (mCartArray.get(i).getCount() > 0) {
                {
                    theCount = theCount + mCartArray.get(i).getCount();
                }
            }
            if (mCartArray.get(i).getProductId().equals(id))
                count = mCartArray.get(i).getCount();
        }
        if (cartItem != null)
            Log.e("cartItem", "=" + cartItem.getProductName());
        Log.e("desc", "onResume");
        mCountTxtView.setText("" + count);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_description, container, false);
        init(v);
        setListeners();
        Log.e("desc", "onCreateView");
        return v;
    }

    private void setListeners() {
        mPlusImgView.setOnClickListener(this);
        mMinusImgView.setOnClickListener(this);
        mAddCartBtn.setOnClickListener(this);
        mWishlistBtn.setOnClickListener(this);
    }

    private void changeCount(int count) {
        int theCount = 0;
        if (mCartArray.size() > 0) {
            for (int i = 0; i < mCartArray.size(); i++) {
                if (mCartArray.get(i).getProductId().equals(id)) {
                    contains = true;
                    item = mCartArray.get(i);
                } else if (mCartArray.get(i).getCount() > 0) {
                    {
                        theCount = theCount + mCartArray.get(i).getCount();
                    }
                }
            }
            if (contains) {
                item.setCount(count);
            } else {
                mCartArray.add(cartItem);
            }
        } else {
            mCartArray.add(cartItem);
        }
        theCount = theCount + count;
        mCartCountTxt.setText("" + (theCount));
        String json = gson.toJson(mCartArray);
        prefsEditor.putString("tempcartlist", json);
        prefsEditor.commit();
    }

    private void init(View v) {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);

        mSearchLayout = (RelativeLayout) getActivity().findViewById(R.id.search_layout);
        mSearchLayout.setVisibility(View.GONE);

        mCartCountTxt = (TextView) toolbar.findViewById(R.id.total_count);
        mTitleTxtView = (TextView) toolbar.findViewById(R.id.title_txt);
        mCartImg = (ImageView) toolbar.findViewById(R.id.cart_img);


        mCartCountTxt.setVisibility(View.VISIBLE);
        mCartImg.setVisibility(View.VISIBLE);
        mTitleTxtView.setText("Scan Result");
        mProductTitleTxt = (TextView) v.findViewById(R.id.product_title_txt);
        mProductPriceTxt = (TextView) v.findViewById(R.id.product_price_txt);
        mProductDescTxt = (TextView) v.findViewById(R.id.product_desc_txt);
        mProductDescTxt = (TextView) v.findViewById(R.id.product_desc_txt);
        mFavouriteCheckbox = (CheckBox) v.findViewById(R.id.fav_btn);
        mWishlistBtn = (Button) v.findViewById(R.id.wishlist_btn);
        mAddCartBtn = (Button) v.findViewById(R.id.addcart_btn);
        mProductImgView = (ImageView) v.findViewById(R.id.product_img_view);
        mPlusImgView = (ImageView) v.findViewById(R.id.plus_img_view);
        mMinusImgView = (ImageView) v.findViewById(R.id.minus_img_view);
        mCountTxtView = (TextView) v.findViewById(R.id.count_txt_view);



        apiInterface = RetrofitAPIClient.getClient(getContext()).create(APIInterface.class);
        apiInterface.getProduct(Integer.parseInt(id)).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                cartItem = new CartItem(response.body().getProductId(), response.body().getProductName(), response.body().getProductDesc(), response.body().getPrice(), response.body().getImgUrl(), 1, response.body().getScantype(), response.body().isChecked());
                mProductTitleTxt.setText(response.body().getProductName());
                mTitleTxtView.setText(response.body().getProductName());
                mProductDescTxt.setText(response.body().getProductDesc());
                mProductPriceTxt.setText("$"+response.body().getPrice()+"0");
                String[] splited = response.body().getImgUrl().split("\\\\");
                if(isVisible())
                Glide.with(getContext()).load("http://54.68.141.32:8080/" + splited[splited.length - 1])
                        .thumbnail(0.5f)
                        .crossFade()
                        .placeholder(R.drawable.ic_placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mProductImgView);
                mWishlistBtn.setEnabled(true);
                mWishlistBtn.setAlpha(1f);
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {

            }
        });
        mFavouriteCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    boolean isloggedin = prefs.getBoolean("isloggedin", false);
                    String username = prefs.getString("username", "");
                    String token = prefs.getString("token", "");
                    if (isloggedin) {
                        mWishlistBtn.setEnabled(false);
                        mWishlistBtn.setAlpha(0.4f);
                        addWishList(new WishList(Integer.parseInt(cartItem.getProductId()), username, cartItem.getProductName(), cartItem.getProductPrice(), null, isChecked, isChecked,mProductDescTxt.getText().toString()), token);
                    } else {
                        Toast.makeText(getContext(), "Please login to continue", Toast.LENGTH_SHORT).show();
                    }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.plus_img_view:
                count = count + 1;
                mCountTxtView.setText("" + count);

                break;
            case R.id.minus_img_view:
                if (count > 1) {
                    count = count - 1;
                    mCountTxtView.setText("" + count);

                }
               /* else
                {
                   mAddCartBtn.setEnabled(false);
                }*/
                break;
            case R.id.addcart_btn:
                if (count != prevCount) {
                    changeCount(count);
                    prevCount = count;
                }
                break;
            case R.id.wishlist_btn:
                break;
        }
    }

    private void addWishList(WishList wlist, String token) {

        apiInterface.addWishList(wlist, token).enqueue(new Callback<WishList>() {
            @Override
            public void onResponse(Call<WishList> call, Response<WishList> response) {
                mWishlistBtn.setEnabled(true);
                mWishlistBtn.setAlpha(1f);
            }
            @Override
            public void onFailure(Call<WishList> call, Throwable t) {
                mWishlistBtn.setEnabled(true);
                mWishlistBtn.setAlpha(1f);
            }
        });
    }

    private void openFrag(int i) {
        Fragment fragment = null;
        switch (i) {
            case 1:
                fragment = new CartFragment();
                break;
        }
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.frame, fragment, "h");
        fragmentTransaction.addToBackStack("h");
        fragmentTransaction.commit();
    }
}
