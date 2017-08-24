package com.cocosoft.ecart.loginmodule;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cocosoft.ecart.R;
import com.cocosoft.ecart.database.DatabaseHandler;
import com.cocosoft.ecart.network.APIInterface;
import com.cocosoft.ecart.network.RetrofitAPIClient;
import com.cocosoft.ecart.scanlistmodule.ProductItem;
import com.cocosoft.ecart.wishlistmodule.WishList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class IndividualItemFragment extends Fragment implements View.OnClickListener {

    private ProductItem item;
    private TextView mProductName, mProductPrice,mProductDesc;
    private ImageView mProductImg;
    private TextView mTitleTxtView;
    private Button mAddWishlistBtn;
    private SharedPreferences prefs;
    private APIInterface apiInterface;
    private Call<WishList> response;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            item = bundle.getParcelable("item");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_individual_item, container, false);
        init(v);
        setListeners();
        return v;
    }

    private void setListeners() {
        mAddWishlistBtn.setOnClickListener(this);
    }

    private void init(View v) {
        apiInterface = RetrofitAPIClient.getClient(getContext()).create(APIInterface.class);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        mTitleTxtView = (TextView) toolbar.findViewById(R.id.title_txt);
        mProductName = (TextView) v.findViewById(R.id.productname_txt);
        mProductDesc = (TextView) v.findViewById(R.id.productdesc_txt);
        prefs = getContext().getSharedPreferences("cocosoft", MODE_PRIVATE);
        mProductPrice = (TextView) v.findViewById(R.id.productprice_txt);
        mProductImg = (ImageView) v.findViewById(R.id.product_img);
        mAddWishlistBtn = (Button) v.findViewById(R.id.add_wish_btn);
        mProductName.setText(item.getProductName());
        mProductPrice.setText("$ " + item.getProductPrice());
        mProductDesc.setText(item.getProductDesc());
        String[] splited = item.getImageUrl().split("\\\\");
        mTitleTxtView.setText(item.getProductName());
        Log.e("ewewqwe","="+"http://54.68.141.32:8080/"+splited[splited.length-1]);
        Glide.with(getContext()).load("http://54.68.141.32:8080/"+splited[splited.length-1])
                .thumbnail(0.5f)
                .crossFade()
                .placeholder(R.drawable.ic_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mProductImg);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_wish_btn:
                boolean isloggedin = prefs.getBoolean("isloggedin", false);
                String username = prefs.getString("username", "");
                String token = prefs.getString("token", "");
                if (isloggedin) {
                    mAddWishlistBtn.setEnabled(false);
                    mAddWishlistBtn.setAlpha(0.4f);
                    addWishList(new WishList(Integer.parseInt(item.getProductId()), username, item.getProductName(), item.getProductPrice(), null, true,true), token);
                } else {
                    Toast.makeText(getContext(), "Please login to continue", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    private void addWishList(WishList wlist, String token) {
        response = apiInterface.addWishList(wlist, token);
        response.enqueue(new Callback<WishList>() {
            @Override
            public void onResponse(Call<WishList> call, Response<WishList> response) {
                mAddWishlistBtn.setEnabled(true);
                mAddWishlistBtn.setAlpha(1f);
            }
            @Override
            public void onFailure(Call<WishList> call, Throwable t) {
                Log.e("EREC","=");
                mAddWishlistBtn.setEnabled(true);
                mAddWishlistBtn.setAlpha(1f);
            }
        });
    }
}
