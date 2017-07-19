package com.cocosoft.ecart.loginmodule;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cocosoft.ecart.R;
import com.cocosoft.ecart.database.DatabaseHandler;
import com.cocosoft.ecart.network.APIInterface;
import com.cocosoft.ecart.network.RetrofitAPIClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by.dmin on 3/16/2017.
 */

public class LoginFragment extends Fragment implements View.OnClickListener {
    private TextView mSignupTxt;
    private TextView mLoginTxt;
    private EditText mUserNameEdtTxt;
    private EditText mPwdEdtTxt;
    private DatabaseHandler mDB;
    private TextView mWarnTxt;
    private ImageView mSettingsImg;
    private SharedPreferences.Editor editor;
    private TextView mCountTxtView;
    private TextView mTitleTxtView;
    private ImageView mCartImg;
    private RelativeLayout mSearchLayout;
    private APIInterface apiInterface;
    private Call<String> response;
    private CheckBox mAdminCheckbox;

    public static int getValue() {
        return value;
    }

    public static void setValue(int value) {
        LoginFragment.value = value;
    }

    private static int value = 0;

    @Override
    public void onResume() {
        super.onResume();
        mTitleTxtView.setText("Login");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        init(v);
        setListeners();
        return v;
    }

    private void setListeners() {
        mSignupTxt.setOnClickListener(this);
        mLoginTxt.setOnClickListener(this);
        mSettingsImg.setOnClickListener(this);
    }

    private void init(View v) {
        mSignupTxt = (TextView) v.findViewById(R.id.signup_txt);
        mAdminCheckbox = (CheckBox) v.findViewById(R.id.checkbox_admin);
        mLoginTxt = (TextView) v.findViewById(R.id.login_txt);
        mUserNameEdtTxt = (EditText) v.findViewById(R.id.username_etxt);
        mPwdEdtTxt = (EditText) v.findViewById(R.id.pwd_etxt);
        mDB = new DatabaseHandler(getContext());
        mWarnTxt = (TextView) v.findViewById(R.id.warning_txt);
        mSettingsImg = (ImageView) v.findViewById(R.id.settings_img);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        mCountTxtView = (TextView) toolbar.findViewById(R.id.total_count);
        mTitleTxtView = (TextView) toolbar.findViewById(R.id.title_txt);
        mCartImg = (ImageView) toolbar.findViewById(R.id.cart_img);
        mCountTxtView.setVisibility(View.GONE);
        mCartImg.setVisibility(View.GONE);
        mSearchLayout = (RelativeLayout) getActivity().findViewById(R.id.search_layout);
        mSearchLayout.setVisibility(View.GONE);
        apiInterface = RetrofitAPIClient.getClient(getContext()).create(APIInterface.class);
        editor = getContext().getSharedPreferences("cocosoft", MODE_PRIVATE).edit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signup_txt:
                openFrag(1);
                break;
            case R.id.settings_img:
                openFrag(0);
                break;
            case R.id.login_txt:
                doLogin();
                break;
        }
    }

    private void doLogin() {

        if (mUserNameEdtTxt.getText().toString().trim().length() == 0) {
            mWarnTxt.setVisibility(View.VISIBLE);
            mWarnTxt.setText("Please enter a valid Username");
        } else {

         /*   Intent i = new Intent(getContext(), MainActivity.class);
            startActivity(i);*/

            response = apiInterface.loginUser(new LoginCredentials(mUserNameEdtTxt.getText().toString().trim(), mPwdEdtTxt.getText().toString().trim()));
            response.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Log.e("Token", "=" + response.body().toString());

                    editor.putBoolean("isloggedin", true);
                    editor.putString("username", mUserNameEdtTxt.getText().toString().trim());
                    editor.putString("token", "Bearer "+response.body().toString());
                    if (mAdminCheckbox.isChecked())
                        editor.putString("usertype", "admin");
                    else {
                        editor.putString("usertype", "user");
                        editor.commit();
                    }
//                    Toast.makeText(getContext(), "Successfully Logged In", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("Token", "=" + t.getMessage());
                }
            });
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    private void openFrag(int i) {
        Fragment firstFragment = null;
        switch (i) {
            case 0:
                firstFragment = new SettingsFragment();
                break;
            case 1:
                firstFragment = new SignupFragment();
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
