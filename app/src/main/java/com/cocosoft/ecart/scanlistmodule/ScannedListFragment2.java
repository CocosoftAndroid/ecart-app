package com.cocosoft.ecart.scanlistmodule;

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
import android.widget.TextView;

import com.cocosoft.ecart.R;
import com.cocosoft.ecart.cartmodule.CartFragment;
import com.cocosoft.ecart.listeners.CheckboxListener;
import com.cocosoft.ecart.loginmodule.DescriptionFragment;
import com.cocosoft.ecart.network.APIInterface;
import com.cocosoft.ecart.network.RetrofitAPIClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by cocoadmin on 10/13/2017.
 */

public class ScannedListFragment2 extends Fragment implements CheckboxListener {


    private ArrayList<Product> mProductList = new ArrayList<>();
    private ScanListAdapter2 mScanListAdapter;
    private RecyclerView mScanListRView;
    private ArrayList<String> mProductIdList = new ArrayList<>();
    private APIInterface apiInterface;
    private SharedPreferences prefs;
    private TextView mTitleTxtView;
    private DescriptionFragment firstFragment;
    private String result = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Gson gson = new Gson();
        prefs = getContext().getSharedPreferences("cocosoft", MODE_PRIVATE);
        String tempdata2 = prefs.getString("tempscanlist2", null);
        Type type2 = new TypeToken<List<String>>() {
        }.getType();
        ArrayList<String> arr2 = gson.fromJson(tempdata2, type2);
        if (arr2 != null) {
            mProductIdList = gson.fromJson(tempdata2, type2);
        }
        Log.e("Scan2", "=" + mProductIdList.size());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scannedlist2, container, false);
        init(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mTitleTxtView.setText("Scanned List");

    }

    private void init(View view) {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        mTitleTxtView = (TextView) toolbar.findViewById(R.id.title_txt);
        mScanListRView = (RecyclerView) view.findViewById(R.id.rview);
        mScanListAdapter = new ScanListAdapter2(getContext(), mProductList, this);
        mScanListRView.setAdapter(mScanListAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mScanListRView.setLayoutManager(layoutManager);
        fetchData();
    }

    private void fetchData() {
        apiInterface = RetrofitAPIClient.getClient(getContext()).create(APIInterface.class);
        mProductList.clear();
        for (int i = 0; i < mProductIdList.size(); i++) {
            apiInterface.getProduct(Integer.parseInt(mProductIdList.get(i))).enqueue(new Callback<Product>() {
                @Override
                public void onResponse(Call<Product> call, Response<Product> response) {
                    mProductList.add(response.body());
                    mScanListAdapter.notifyDataSetChanged();
                }
                @Override
                public void onFailure(Call<Product> call, Throwable t) {

                }
            });
        }
    }

    @Override
    public void onChecked(String productid, boolean isChecked) {
        result = productid;
        openFrag(1);
    }

    private void openFrag(int i) {
        switch (i) {
            case 1:
                firstFragment = new DescriptionFragment();
                Bundle b = new Bundle();
                b.putString("id", result);
                firstFragment.setArguments(b);
                break;
        }
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.frame, firstFragment, "h");
        fragmentTransaction.addToBackStack("h");
        fragmentTransaction.commit();
    }

}
