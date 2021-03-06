package com.cocosoft.ecart.loginmodule;

/**
 * Created by.dmin on 6/16/2017.
 */

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cocosoft.ecart.R;
import com.cocosoft.ecart.common.MonthYearPickerDialog;
import com.cocosoft.ecart.database.DatabaseHandler;
import com.cocosoft.ecart.network.APIInterface;
import com.cocosoft.ecart.network.RetrofitAPIClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by.dmin on 3/16/2017.
 */

public class ProfileFragment extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private EditText mUserNameEdtTxt;
    private EditText mPwdEdtTxt;
    private EditText mConfirmPwdEdtTxt, mEmailEdtText, mFirstNameETxt, mLastNameETxt;
    private TextView mSignupTxt, mWarnTxt, mDOBTxt;
    private DatabaseHandler mDB;
    private TextView mCountTxtView;
    private TextView mTitleTxtView;
    private ImageView mCartImg;
    private RelativeLayout mSearchLayout;
    private String mUserType = "user";
    private RadioGroup mRadioGroup1;
    private APIInterface apiInterface;
    private Call<User> response;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        init(v);
        setListeners();
        return v;
    }

    private void setListeners() {
        mSignupTxt.setOnClickListener(this);
        mDOBTxt.setOnClickListener(this);
        mRadioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.user_radio_btn) {
                    mUserType = "user";
                } else if (checkedId == R.id.admin_radio_btn) {
                    mUserType = "admin";
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mTitleTxtView.setText("SignUp");
    }

    private void init(View v) {
        mUserNameEdtTxt = (EditText) v.findViewById(R.id.username_etxt);
        mPwdEdtTxt = (EditText) v.findViewById(R.id.pwd_etxt);
        mEmailEdtText = (EditText) v.findViewById(R.id.email_txt);
        mFirstNameETxt = (EditText) v.findViewById(R.id.firstname_etxt);
        mLastNameETxt = (EditText) v.findViewById(R.id.lastname_etxt);
        mConfirmPwdEdtTxt = (EditText) v.findViewById(R.id.confirm_pwd_etxt);
        mSignupTxt = (TextView) v.findViewById(R.id.save_txt);
        mWarnTxt = (TextView) v.findViewById(R.id.warning_txt);
        mDOBTxt = (TextView) v.findViewById(R.id.dob_txt);
        mDB = new DatabaseHandler(getContext());
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        mCountTxtView = (TextView) toolbar.findViewById(R.id.total_count);
        mTitleTxtView = (TextView) toolbar.findViewById(R.id.title_txt);
        mCartImg = (ImageView) toolbar.findViewById(R.id.cart_img);
        mCountTxtView.setVisibility(View.GONE);
        mCartImg.setVisibility(View.GONE);
        mSearchLayout = (RelativeLayout) getActivity().findViewById(R.id.search_layout);
        mSearchLayout.setVisibility(View.GONE);
        mRadioGroup1 = (RadioGroup) v.findViewById(R.id.radioGroup1);
        apiInterface = RetrofitAPIClient.getClient(getContext()).create(APIInterface.class);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_txt:
                updateProfile();
                break;
            case R.id.dob_txt:
                MonthYearPickerDialog pd = new MonthYearPickerDialog();
                pd.setListener(this);
                pd.show(getFragmentManager(), "MonthYearPickerDialog");
                break;
        }
    }

    private void updateProfile() {
        response = apiInterface.updateUser(new User(0, mFirstNameETxt.getText().toString().trim(), mLastNameETxt.getText().toString().trim(), mEmailEdtText.getText().toString().trim(), mPwdEdtTxt.getText().toString().trim(), 0, "", "", ""));
        response.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("LoginFragment", "=" + t.getMessage());
            }
        });

        Toast.makeText(getContext(), "Profile Updated", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        mDOBTxt.setText("" + month + "-" + year);
    }


    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
}
