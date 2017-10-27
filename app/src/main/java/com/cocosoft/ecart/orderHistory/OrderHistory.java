package com.cocosoft.ecart.orderHistory;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cocosoft.ecart.R;
import com.cocosoft.ecart.database.DatabaseHandler;
import com.cocosoft.ecart.network.APIInterface;
import com.cocosoft.ecart.network.RetrofitAPIClient;
import com.cocosoft.ecart.scanlistmodule.ScanListAdapter;
import com.cocosoft.ecart.wishlistmodule.WishList;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Guest1 on 5/23/2017.
 */

public class OrderHistory extends Fragment implements View.OnClickListener {


    private TextView mTitleTxtView;
    private List<OrderMaster> mOrderHistoryList = new ArrayList<>();
    private LinearLayoutManager mLManager;
    private RecyclerView mOrderHistoryRView;
    private OrderHistoryAdapter mOrderHistoryAdapter;
    private APIInterface apiInterface;
    private String token;
    private Call<List<OrderMaster>> response;
    private SharedPreferences prefs;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        init(view);
        setListeners();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mTitleTxtView.setText("Orders");
    }

    private void setListeners() {

    }

    private void init(View v) {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        mTitleTxtView = (TextView) toolbar.findViewById(R.id.title_txt);
        prefs = getContext().getSharedPreferences("cocosoft", MODE_PRIVATE);
        token = prefs.getString("token", "");
        apiInterface = RetrofitAPIClient.getClient(getContext()).create(APIInterface.class);
        mLManager = new LinearLayoutManager(getContext());
        mOrderHistoryRView = (RecyclerView) v.findViewById(R.id.rview);
        mOrderHistoryRView.setLayoutManager(mLManager);
        getorderHistoryList();
    }

    private void getorderHistoryList() {
        response = apiInterface.allOrder(token);
        response.enqueue(new Callback<List<OrderMaster>>() {

            @Override
            public void onResponse(Call<List<OrderMaster>> call, Response<List<OrderMaster>> response) {
                if(response.body()!=null) {
                    mOrderHistoryList = response.body();

                    mOrderHistoryAdapter = new OrderHistoryAdapter(getActivity(), getContext(), mOrderHistoryList);
                    mOrderHistoryRView.setAdapter(mOrderHistoryAdapter);
                    mOrderHistoryAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<OrderMaster>> call, Throwable t) {

            }
        });
    }


    @Override
    public void onClick(View v) {

    }
}
