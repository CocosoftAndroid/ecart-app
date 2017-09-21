package com.cocosoft.ecart.loginmodule;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
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

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by.dmin on 3/16/2017.
 */

public class LoginFragment extends Fragment implements View.OnClickListener, LocationListener {
    private TextView mSignupTxt, mGuestTxt;
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
    private String countryName;

    public static int getValue() {
        return value;
    }

    public static void setValue(int value) {
        LoginFragment.value = value;
    }

    private static int value = 0;
    public double latitude;
    public double longitude;
    public LocationManager locationManager;


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
        mGuestTxt.setOnClickListener(this);
    }

    private void init(View v) {
        mSignupTxt = (TextView) v.findViewById(R.id.signup_txt);
        mGuestTxt = (TextView) v.findViewById(R.id.guest_txt);
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
        getLocation();
    }

    public String getCountryName(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Address result;
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getCountryName();
            }
            return "empty";
        } catch (IOException ignored) {
            //do something
            return "exception";
        }
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
            case R.id.guest_txt:
                editor.putBoolean("isGuest", true);
                editor.commit();
                getActivity().getSupportFragmentManager().popBackStack();
                break;
        }
    }

    private void doLogin() {
        if (mUserNameEdtTxt.getText().toString().trim().length() == 0) {
            mWarnTxt.setVisibility(View.VISIBLE);
            mWarnTxt.setText("Please enter a valid Username");
        } else if (mPwdEdtTxt.getText().toString().trim().length() == 0) {
            mWarnTxt.setVisibility(View.VISIBLE);
            mWarnTxt.setText("Please enter a valid Password");
        } else {
         /* Intent i = new Intent(getContext(), MainActivity.class);
            startActivity(i); */
            response = apiInterface.loginUser(new LoginCredentials(mUserNameEdtTxt.getText().toString().trim(), mPwdEdtTxt.getText().toString().trim()));
            response.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    //Log.e("Token", "=" + response.body().toString());
                    if (response.body() != null) {
                        editor.putBoolean("isloggedin", true);
                        editor.putString("username", mUserNameEdtTxt.getText().toString().trim());
                        editor.putString("token", "Bearer " + response.body().toString());
                        Log.e("token", "=" + response.body().toString());
                        if (mAdminCheckbox.isChecked())
                            editor.putString("usertype", "admin");
                        else {
                            editor.putString("usertype", "user");
                            editor.commit();
                        }
                        Toast.makeText(getContext(), "Successfully Logged In", Toast.LENGTH_SHORT).show();
                        getActivity().getSupportFragmentManager().popBackStack();
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(getContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                }
            });
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

    public static boolean isLocationEnabled(Context context) {
        return true;
    }

    protected void getLocation() {
        if (isLocationEnabled(getContext())) {
            locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            //You can still do this if you like, you might get lucky:
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Log.e("getCountryName", "rrw1");
            if (location != null) {
                Log.e("TAG", "GPS is on");
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Log.e("getCountryName", "rrw" + getCountryName(getContext(), latitude, longitude));
                countryName = getCountryName(getContext(), latitude, longitude);
                editor.putString("country", countryName);
                editor.commit();
            } else {
                //This is what you need:
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
                Log.e("getCountryName", "rrw1else");
            }
        } else {
        }
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onLocationChanged(Location location) {
        //Hey, a non null location! Sweet!
        //remove location callback:
        locationManager.removeUpdates(this);
        //open the map:
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        countryName = getCountryName(getContext(), latitude, longitude);
        editor.putString("country", countryName);
        editor.commit();
        Log.e("getCountryName", "rrwlch");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
