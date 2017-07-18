package com.cocosoft.ecart.loginmodule;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cocosoft.ecart.R;
import com.cocosoft.ecart.cartmodule.CartFragment;
import com.cocosoft.ecart.database.DatabaseHandler;
import com.cocosoft.ecart.listeners.QuantityListener;
import com.cocosoft.ecart.listeners.ScanResultListener;
import com.cocosoft.ecart.network.APIInterface;
import com.cocosoft.ecart.network.RetrofitAPIClient;
import com.cocosoft.ecart.scanlistmodule.Product;
import com.cocosoft.ecart.scanlistmodule.ProductItem;
import com.cocosoft.ecart.scanlistmodule.ScannedListFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.R.attr.id;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by.dmin on 3/16/2017.
 */

public class HomeFragment extends Fragment implements View.OnClickListener, QuantityListener, ScanResultListener {

    private LinearLayout mLLayout1, mLLayout2, mLLayout3;
    private ArrayList<ProductItem> mProductArray = new ArrayList<>();
    private TextView mTitleTxtView;
    private CoordinatorLayout coordinatorLayout;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;
    private TextView mCountTxtView;
    private ImageView mCartImg;
    Fragment firstFragment = null;
    private int scanTypeFlag = 0;
    private DatabaseHandler mDB;
    private Gson gson;
    private APIInterface apiInterface;
    private Call<Product> response;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getContext().getSharedPreferences("cocosoft", MODE_PRIVATE);
        prefsEditor = prefs.edit();
        gson = new Gson();
        String tempdata = prefs.getString("tempscanlist", null);
        Type type = new TypeToken<List<ProductItem>>() {
        }.getType();
        ArrayList<ProductItem> arr = gson.fromJson(tempdata, type);
        if (arr != null) {
            mProductArray = gson.fromJson(tempdata, type);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        init(v);
        setListeners();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mTitleTxtView.setText("");
    }

    private void setListeners() {
        mLLayout1.setOnClickListener(this);
        mLLayout2.setOnClickListener(this);
        mLLayout3.setOnClickListener(this);
    }

    private void init(View v) {
        mLLayout1 = (LinearLayout) v.findViewById(R.id.llay1);
        mLLayout2 = (LinearLayout) v.findViewById(R.id.llay2);
        mLLayout3 = (LinearLayout) v.findViewById(R.id.llay3);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        mTitleTxtView = (TextView) toolbar.findViewById(R.id.title_txt);
        mCountTxtView = (TextView) toolbar.findViewById(R.id.total_count);
        mTitleTxtView.setText("");
        coordinatorLayout = (CoordinatorLayout) v.findViewById(R.id.coordinatorLayout);
        mCartImg = (ImageView) toolbar.findViewById(R.id.cart_img);
        mCartImg.setOnClickListener(this);
        mCountTxtView.setVisibility(View.VISIBLE);
        mCartImg.setVisibility(View.VISIBLE);
        mDB = new DatabaseHandler(getContext());
        apiInterface = RetrofitAPIClient.getClient(getContext()).create(APIInterface.class);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llay1:
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "NFC Enabled", Snackbar.LENGTH_SHORT);
                snackbar.show();
                scanTypeFlag = 1;
                break;
            case R.id.llay2:
                scanTypeFlag = 2;
                openFrag(1, true);
                break;
            case R.id.llay3:
                scanTypeFlag = 3;
                openFrag(1, true);
                break;
            case R.id.cart_img:
                openFrag(2, false);
                break;
        }
    }

    private void openFrag(int i, boolean cameraflag) {
        switch (i) {
            case 1:
                firstFragment = new ScannedListFragment();
                ((ScannedListFragment) firstFragment).setInterface(this, this);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("productarray", mProductArray);
                bundle.putBoolean("isscan", cameraflag);
                bundle.putInt("scantype", scanTypeFlag);
                firstFragment.setArguments(bundle);
                break;
            case 2:
                firstFragment = new CartFragment();
                break;
        }
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.frame, firstFragment, "h");
        fragmentTransaction.addToBackStack("h");
        fragmentTransaction.commit();
    }

    public void openScanListFrag(JSONObject jsonObject, int flag) {
        onScanResult(jsonObject, flag);
        openFrag(1, false);
    }

    @Override
    public void onQuantityChange(String productid, int quantity) {
        if (quantity == 0) {
            for (int i = 0; i < mProductArray.size(); i++) {
                if (mProductArray.get(i).getProductId().equals(productid)) {
                    mProductArray.remove(i);
                }
            }
        } else {
            for (int i = 0; i < mProductArray.size(); i++) {
                if (mProductArray.get(i).getProductId().equals(productid)) {
                    int count = mProductArray.get(i).getCount();
                    mProductArray.get(i).setCount(count + quantity);
                }
            }
        }
        String json = gson.toJson(mProductArray);
        prefsEditor.putString("tempscanlist", json);
        prefsEditor.commit();
    }

    @Override
    public void onScanResult(JSONObject obj, final int scantype) {
        final String id = obj.optString("id");
        final ProductItem dbItem = new ProductItem();
        response = apiInterface.getProduct(Integer.parseInt(id));
        response.enqueue(new Callback<Product>() {
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
                            mProductArray.add(new ProductItem(id, dbItem.getProductName(), dbItem.getProductDesc(), dbItem.getProductPrice(),dbItem.getImageUrl(), 1, scantype, false));
                        }
                    } else {
                        mProductArray.add(new ProductItem(id, dbItem.getProductName(), dbItem.getProductDesc(), dbItem.getProductPrice(),dbItem.getImageUrl(), 1, scantype, false));
                    }
                } else {
                    Toast.makeText(getContext(), "Item not found on Database", Toast.LENGTH_SHORT).show();
                }
                String json = gson.toJson(mProductArray);
                prefsEditor.putString("tempscanlist", json);
                prefsEditor.commit();
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {

            }
        });


    }
}
