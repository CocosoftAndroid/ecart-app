package com.cocosoft.ecart.scanlistmodule;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cocosoft.ecart.R;
import com.cocosoft.ecart.barcode.BarcodeCaptureActivity;
import com.cocosoft.ecart.cartmodule.CartFragment;
import com.cocosoft.ecart.cartmodule.CartItem;
import com.cocosoft.ecart.database.DatabaseHandler;
import com.cocosoft.ecart.listeners.CheckboxListener;
import com.cocosoft.ecart.listeners.IndividualItemListener;
import com.cocosoft.ecart.listeners.QuantityListener;
import com.cocosoft.ecart.listeners.ScanResultListener;
import com.cocosoft.ecart.listeners.WishlistListener;
import com.cocosoft.ecart.loginmodule.IndividualItemFragment;
import com.cocosoft.ecart.loginmodule.LoginCredentials;
import com.cocosoft.ecart.loginmodule.LoginFragment;
import com.cocosoft.ecart.network.APIInterface;
import com.cocosoft.ecart.network.RetrofitAPIClient;
import com.cocosoft.ecart.wishlistmodule.WishList;
import com.cocosoft.ecart.wishlistmodule.WishListFragment;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class ScannedListFragment extends Fragment implements View.OnClickListener, QuantityListener, WishlistListener, IndividualItemListener, CheckboxListener {

    private Button mAddCartTxt;
    private static final String LOG_TAG = ScannedListFragment.class.getSimpleName();
    private static final int BARCODE_READER_REQUEST_CODE = 1;
    private DatabaseHandler mDB;
    private LinearLayoutManager mLManager;
    private RecyclerView mProductRView;
    private ScanListAdapter mScanListAdapter;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;
    private ArrayList<ProductItem> mProductArray = new ArrayList<>();
    private ArrayList<CartItem> mCartArray = new ArrayList<>();
    private TextView mCountTxtView;
    private TextView mTitleTxtView;
    private boolean isScan = false;
    private ImageView mCartImg;
    private RelativeLayout mSearchLayout;
    private int scantype;
    private QuantityListener mQuantityLis;
    private Gson gson;
    private CheckBox mSelectAllChkBox;
    private APIInterface apiInterface;
    private Call<WishList> response;
    private Call<Product> response2;
    private String nfcResult;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProductArray = getArguments().getParcelableArrayList("productarray");
        isScan = getArguments().getBoolean("isscan");
        scantype = getArguments().getInt("scantype");
        if (scantype == 1) {
            nfcResult = getArguments().getString("nfcresult");
        }
        prefs = getContext().getSharedPreferences("cocosoft", MODE_PRIVATE);
        prefsEditor = prefs.edit();

    }

    public void setInterface(QuantityListener qlis) {
        this.mQuantityLis = qlis;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scannedlist, container, false);
        init(view);
        setListeners();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mTitleTxtView.setText("Product List");
        mCountTxtView.setVisibility(View.VISIBLE);
        mCartImg.setVisibility(View.VISIBLE);
        mSearchLayout.setVisibility(View.VISIBLE);
        changeCount();
    }

    private int changeCount() {
        int mCount = 0;
        mCartArray = new ArrayList<>();
        String tempdata = prefs.getString("tempcartlist", null);
        Type type = new TypeToken<List<CartItem>>() {
        }.getType();
        ArrayList<CartItem> arr = gson.fromJson(tempdata, type);
        if (arr != null) {
            mCartArray = gson.fromJson(tempdata, type);
        }
        for (int i = 0; i < mCartArray.size(); i++) {
            if (mCartArray.get(i).getCount() > 0) {
                {
                    mCount = mCount + mCartArray.get(i).getCount();
                }
            }
        }
        mCountTxtView.setText("" + mCount);
        return mCount;
    }

    private void setListeners() {
        mAddCartTxt.setOnClickListener(this);
        mSelectAllChkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    for (int i = 0; i < mProductArray.size(); i++) {
                        {
                            mProductArray.get(i).setChecked(isChecked);
                            mScanListAdapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    for (int i = 0; i < mProductArray.size(); i++) {
                        {
                            mProductArray.get(i).setChecked(false);
                            mScanListAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }

    private void init(View view) {
        gson = new Gson();
        mAddCartTxt = (Button) view.findViewById(R.id.add_cart_txt);
        mSelectAllChkBox = (CheckBox) view.findViewById(R.id.selectall);
        mDB = new DatabaseHandler(getContext());
        mLManager = new LinearLayoutManager(getContext());
        mProductRView = (RecyclerView) view.findViewById(R.id.rview);
        mProductRView.setLayoutManager(mLManager);
        mScanListAdapter = new ScanListAdapter(getContext(), mProductArray, this, this, this, this);
        mProductRView.setAdapter(mScanListAdapter);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        mCountTxtView = (TextView) toolbar.findViewById(R.id.total_count);
        mCartImg = (ImageView) toolbar.findViewById(R.id.cart_img);
        mCartImg.setOnClickListener(this);
        mCountTxtView.setVisibility(View.VISIBLE);
        mCartImg.setVisibility(View.VISIBLE);
        mTitleTxtView = (TextView) toolbar.findViewById(R.id.title_txt);
        mSearchLayout = (RelativeLayout) getActivity().findViewById(R.id.search_layout);
        mSearchLayout.setVisibility(View.GONE);
        if (isScan) {
            Intent intent = new Intent(getContext(), BarcodeCaptureActivity.class);
            startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
        }
        apiInterface = RetrofitAPIClient.getClient(getContext()).create(APIInterface.class);
        if (scantype == 1) {
            try {
                onScanResult(new JSONObject(nfcResult), 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_cart_txt:
                addToCart();
                if (changeCount() == 0) {
                    Toast.makeText(getContext(), "Please select atleast one item", Toast.LENGTH_SHORT).show();
                } else {
                    openFrag(0, "");
                }
                break;
            case R.id.cart_img:
                openFrag(0, "");
                break;
        }
    }


    private void addToCart() {
        mCartArray = new ArrayList<>();
        for (int i = 0; i < mProductArray.size(); i++) {
            if (mProductArray.get(i).isChecked()) {
                {
                    mCartArray.add(new CartItem(mProductArray.get(i).getProductId(), mProductArray.get(i).getProductName(), mProductArray.get(i).getProductDesc(), mProductArray.get(i).getProductPrice(),mProductArray.get(i).getImageUrl(), mProductArray.get(i).getCount(), mProductArray.get(i).getScantype(), mProductArray.get(i).isChecked()));
                }
            }
        }
        String json = gson.toJson(mCartArray);
        prefsEditor.putString("tempcartlist", json);
        prefsEditor.commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BARCODE_READER_REQUEST_CODE) {
            Log.e(LOG_TAG, "dddddddddd");
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Point[] p = barcode.cornerPoints;
                    JSONObject obj = null;
                    try {
                        obj = new JSONObject(barcode.displayValue);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (obj != null) {
                        onScanResult(obj, scantype);
                        mScanListAdapter.notifyDataSetChanged();
                    }
                } else
                    Log.e(LOG_TAG, String.format(getString(R.string.barcode_error_format), CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onQuantityChange(String productid, int quantity) {
        mQuantityLis.onQuantityChange(productid, quantity);
        mScanListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        isScan = false;
    }

    private void openFrag(int i, String productid) {
        Fragment firstFragment = null;
        switch (i) {
            case 0:
                firstFragment = new CartFragment();
                ((CartFragment) firstFragment).setListener(this);
                Bundle bundle = new Bundle();
                firstFragment.setArguments(bundle);
                break;
            case 1:
                firstFragment = new LoginFragment();
                break;
            case 3:
                firstFragment = new WishListFragment();
                break;
            case 2:
                firstFragment = new IndividualItemFragment();
                ProductItem item = null;
                for (int ij = 0; ij < mProductArray.size(); ij++) {
                    if (mProductArray.get(ij).getProductId().equals(productid))
                        item = mProductArray.get(ij);
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
    }

    @Override
    public void onFavouriteClicked(String productid, String productname, Double price, boolean isChecked) {
        String username = prefs.getString("username", "");
        String token = prefs.getString("token", "");
        if (isChecked) {
            Toast.makeText(getContext(), "Added to Wishlist", Toast.LENGTH_SHORT).show();
            //mDB.addToWishlist(productid, username);
            addWishList(new WishList(Integer.parseInt(productid), username, productname, price, null, true, false), token);
        } else {
            Toast.makeText(getContext(), "Wishlist Removed", Toast.LENGTH_SHORT).show();
//            mDB.removeWishlist(productid, username);
            addWishList(new WishList(Integer.parseInt(productid), username, productname, price, null, false, false), token);
        }
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

    @Override
    public void OnCardClick(String productid) {
        openFrag(2, productid);
    }

    @Override
    public void onChecked(String productid, boolean isChecked) {
        for (int i = 0; i < mProductArray.size(); i++) {
            if (mProductArray.get(i).getProductId().equals(productid)) {
                mProductArray.get(i).setChecked(isChecked);
            }
        }
    }

    public void onScanResult(JSONObject obj, final int scantype) {
        final String id = obj.optString("id");
        final ProductItem dbItem = new ProductItem();
        response2 = apiInterface.getProduct(Integer.parseInt(id));
        response2.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                dbItem.setProductId(response.body().getProductId());
                dbItem.setProductName(response.body().getProductName());
                dbItem.setProductPrice(response.body().getPrice());
                dbItem.setProductDesc(response.body().getProductDesc());
                dbItem.setImageUrl(response.body().getImgUrl());
                Log.e("ProdName", "=" + response.body().getProductName());
                if (dbItem != null) {
                    if (mProductArray.size() > 0) {
                        ProductItem item = null;
                        for (int i = 0; i < mProductArray.size(); i++) {
                            if (mProductArray.get(i).getProductId().equals(id)) {
                                item = mProductArray.get(i);
                                Log.e("ProdName2", "=" + item.getProductName());
                                Toast.makeText(getContext(), item.getProductName() + " added", Toast.LENGTH_SHORT).show();
                                int count = item.getCount();
                                item.setCount(count + 1);
                                item.setScantype(scantype);
                            }
                        }
                        if (item == null) {
                            mProductArray.add(new ProductItem(id, dbItem.getProductName(), dbItem.getProductDesc(), dbItem.getProductPrice(), dbItem.getImageUrl(), 1, scantype, false));
                        }
                    } else {
                        mProductArray.add(new ProductItem(id, dbItem.getProductName(), dbItem.getProductDesc(), dbItem.getProductPrice(), dbItem.getImageUrl(), 1, scantype, false));
                    }
                } else {
                    Toast.makeText(getContext(), "Item not found on Database", Toast.LENGTH_SHORT).show();
                }
                String json = gson.toJson(mProductArray);
                prefsEditor.putString("tempscanlist", json);
                prefsEditor.commit();
                mScanListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
            }
        });
    }
}
