package com.cocosoft.ecart.adminmodule;

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
import android.widget.TextView;

import com.cocosoft.ecart.R;
import com.cocosoft.ecart.loginmodule.User;
import com.cocosoft.ecart.network.APIInterface;
import com.cocosoft.ecart.network.RetrofitAPIClient;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by.dmin on 6/15/2017.
 */

public class AllUsersFragment extends Fragment {

    private LinearLayoutManager mLManager;
    private RecyclerView mUsersRView;
    private AllUsersAdapter mAllUsersAdapter;
    private ArrayList<User> mAllUsersArray=new ArrayList<>();
    private APIInterface apiInterface;
    private Call<ArrayList<User>> response;
    private TextView mTitleTxtView;

    @Override
    public void onResume() {
        super.onResume();
        mTitleTxtView.setText("All Users");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_allusers, container, false);
        init(view);
        setListeners();
        return view;
    }

    private void setListeners() {

    }

    private void init(View view) {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        mTitleTxtView = (TextView) toolbar.findViewById(R.id.title_txt);
        mLManager = new LinearLayoutManager(getContext());
        mUsersRView = (RecyclerView) view.findViewById(R.id.rview);
        mUsersRView.setLayoutManager(mLManager);
        mAllUsersAdapter = new AllUsersAdapter(getContext(), mAllUsersArray);
        mUsersRView.setAdapter(mAllUsersAdapter);
        apiInterface = RetrofitAPIClient.getClient(getContext()).create(APIInterface.class);
       /* response=apiInterface.getAllUsers();
        response.enqueue(new Callback<ArrayList<User>>() {
            @Override
            public void onResponse(Call<ArrayList<User>> call, Response<ArrayList<User>> response) {
                Log.e("AllUsersList","="+response.body().size());
                mAllUsersArray.addAll(response.body());
                mAllUsersAdapter.notifyDataSetChanged();
                Log.e("AllUsersList","="+response.body().size());
            }

            @Override
            public void onFailure(Call<ArrayList<User>> call, Throwable t) {

            }
        });*/
    }
}
