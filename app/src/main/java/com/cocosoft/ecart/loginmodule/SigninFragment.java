package com.cocosoft.ecart.loginmodule;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cocosoft.ecart.R;
import com.facebook.CallbackManager;
import com.google.android.gms.common.SignInButton;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Srikanth on 5/5/2017.
 */

public class SigninFragment extends Fragment implements View.OnClickListener {


    private Button _esaySignup;
    private Button loginButton;
    private Button signInButton;
    private Button _esayLoginIn;
    private TextView mCountTxtView;
    private TextView mTitleTxtView, mGuestTxt;
    private ImageView mCartImg;
    private RelativeLayout mSearchLayout;
    private Activity activity;
    private SharedPreferences appSharedPrefs;
    private SharedPreferences.Editor editor;

    @Override
    public void onResume() {
        super.onResume();
        mTitleTxtView.setText("ECART SIGN UP ");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  FacebookSdk.sdkInitialize(getApplicationContext());
        //AppEventsLogger.activateApp(getActivity()this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_signin, container, false);
        init(v);
        setListeners();
        return v;
    }

    private void setListeners() {

        _esaySignup.setOnClickListener(this);
        signInButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        _esayLoginIn.setOnClickListener(this);
        mGuestTxt.setOnClickListener(this);

    }

    private void init(View v) {

        _esaySignup = (Button) v.findViewById(R.id.shopEsaySignIn);
        signInButton = (Button) v.findViewById(R.id.sign_in_button);
        loginButton = (Button) v.findViewById(R.id.facebook_login_button);
        _esayLoginIn = (Button) v.findViewById(R.id.shopEsayLoginIn);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        mCountTxtView = (TextView) toolbar.findViewById(R.id.total_count);
        mTitleTxtView = (TextView) toolbar.findViewById(R.id.title_txt);
        mGuestTxt = (TextView) v.findViewById(R.id.guest_txt);
        mCartImg = (ImageView) toolbar.findViewById(R.id.cart_img);
        mCountTxtView.setVisibility(View.GONE);
        mCartImg.setVisibility(View.GONE);
        mSearchLayout = (RelativeLayout) getActivity().findViewById(R.id.search_layout);
        mSearchLayout.setVisibility(View.GONE);
        appSharedPrefs = getActivity().getSharedPreferences("cocosoft", MODE_PRIVATE);
        editor = appSharedPrefs.edit();
    }

    private void shopEsaySignUpPage() {

        Fragment signUpfmt = null;
        signUpfmt = new SignupFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.frame, signUpfmt, "h");
        fragmentTransaction.addToBackStack("h");
        fragmentTransaction.commit();
    }

    private void Esay_login() {
        Fragment signUpfmt = null;
        signUpfmt = new LoginFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.frame, signUpfmt, "h");
        fragmentTransaction.addToBackStack("h");
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shopEsaySignIn:
                shopEsaySignUpPage();

                break;
            case R.id.sign_in_button:
                Log.i("Google sign in", "google");
                activity = getActivity();
                if (activity instanceof LoginActivity) {
                    ((LoginActivity) activity).signIn();
                }
                break;
            case R.id.facebook_login_button:
                activity = getActivity();
                Log.i("facebook sign in", "facebook");
                if (activity instanceof LoginActivity) {
                    ((LoginActivity) activity).fb_login();
                }

                break;
            case R.id.shopEsayLoginIn:
                if (appSharedPrefs.getBoolean("isloggedin", false)) {
                    Toast.makeText(getContext(), "Already Logged In", Toast.LENGTH_SHORT).show();
                } else {
                    Esay_login();
                    LoginFragment.setValue(2);
                }
                break;

            case R.id.guest_txt:
                editor.putBoolean("isGuest", true);
                editor.commit();
                getActivity().getSupportFragmentManager().popBackStack();
                break;


        }
    }


}
