package com.cocosoft.ecart.loginmodule;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cocosoft.ecart.R;
import com.cocosoft.ecart.database.DatabaseHandler;
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
 * Created by.dmin on 4/13/2017.
 */

public class EditProfileFragment extends Fragment implements View.OnClickListener {

    private TextView mCountTxtView;
    private TextView mTitleTxtView, mSaveTxt;
    private ImageView mCartImg;
    private RelativeLayout mSearchLayout;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;
    private Gson gson;
    private AppCompatCheckBox mCheckBox;
    private ArrayList<AddressItem> mAddressArray = new ArrayList<>();
    private ArrayList<AddressItem> mAddressArray2 = new ArrayList<>();
    private EditText mNameTxt, mAddress1Txt, mAddress2Txt, mCityTxt, mStateTxt, mZipTxt, mCountryTxt, mPhoneNoTxt, mCNameTxt, mCAddress1Txt, mCAddress2Txt, mCCityTxt, mCStateTxt, mCZipTxt, mCCountryTxt, mCPhoneNoTxt;

    private EditText mUserNameEdtTxt;
    private EditText mPwdEdtTxt;
    private EditText mConfirmPwdEdtTxt, mEmailEdtText, mFirstNameETxt, mLastNameETxt;
    private TextView mSignupTxt, mWarnTxt, mDOBTxt;
    private DatabaseHandler mDB;

    private String mUserType = "user";
    private RadioGroup mRadioGroup1;
    private APIInterface apiInterface;
    private Call<User> response;
    private Call<AddressItem> response2;
    private LinearLayout mShippingLayout;
    private String token;
    ArrayList<AddressItem> arr = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getContext().getSharedPreferences("cocosoft", MODE_PRIVATE);
        prefsEditor = prefs.edit();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editprofile, container, false);
        init(view);
        setListeners();
        return view;
    }

    private void setListeners() {
        mSaveTxt.setOnClickListener(this);
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                 @Override
                                                 public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                     switch (buttonView.getId()) {
                                                         case R.id.checkbox:
                                                             if (isChecked) {
                                                                 mNameTxt.setText(mCNameTxt.getText().toString().trim());
                                                                 mAddress1Txt.setText(mCAddress1Txt.getText().toString().trim());
                                                                 mAddress2Txt.setText(mCAddress2Txt.getText().toString().trim());
                                                                 mCityTxt.setText(mCCityTxt.getText().toString().trim());
                                                                 mZipTxt.setText(mCZipTxt.getText().toString().trim());
                                                                 mStateTxt.setText(mCStateTxt.getText().toString().trim());
                                                                 mPhoneNoTxt.setText(mCPhoneNoTxt.getText().toString().trim());
                                                                 mCountryTxt.setText(mCCountryTxt.getText().toString().trim());
                                                                 mShippingLayout.setVisibility(View.GONE);
                                                                 prefsEditor.putBoolean("sameshippingaddress", true);
                                                                 prefsEditor.commit();
                                                             } else {
                                                                 mNameTxt.setText("");
                                                                 mAddress1Txt.setText("");
                                                                 mAddress2Txt.setText("");
                                                                 mCityTxt.setText("");
                                                                 mZipTxt.setText("");
                                                                 mStateTxt.setText("");
                                                                 mPhoneNoTxt.setText("");
                                                                 mCountryTxt.setText("");
                                                                 mShippingLayout.setVisibility(View.VISIBLE);
                                                                 prefsEditor.putBoolean("sameshippingaddress", false);
                                                                 prefsEditor.commit();
                                                             }
                                                             break;
                                                     }
                                                 }
                                             }
        );
    }

    private void init(View view) {
        token = prefs.getString("token", null);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        mCountTxtView = (TextView) toolbar.findViewById(R.id.total_count);
        mTitleTxtView = (TextView) toolbar.findViewById(R.id.title_txt);
        mSaveTxt = (TextView) view.findViewById(R.id.save_txt);
        mCheckBox = (AppCompatCheckBox) view.findViewById(R.id.checkbox);
        mNameTxt = (EditText) view.findViewById(R.id.ship_adr_name);
        mAddress1Txt = (EditText) view.findViewById(R.id.ship_adr_address1);
        mAddress2Txt = (EditText) view.findViewById(R.id.ship_adr_address2);
        mCityTxt = (EditText) view.findViewById(R.id.ship_adr_city);
        mStateTxt = (EditText) view.findViewById(R.id.ship_adr_state);
        mZipTxt = (EditText) view.findViewById(R.id.ship_adr_zip);
        mPhoneNoTxt = (EditText) view.findViewById(R.id.ship_adr_phoneno);
        mCountryTxt = (EditText) view.findViewById(R.id.ship_adr_country);
        mCCountryTxt = (EditText) view.findViewById(R.id.ccountry_txt);
        mCNameTxt = (EditText) view.findViewById(R.id.cname_txt);
        mCAddress1Txt = (EditText) view.findViewById(R.id.caddr1_txt);
        mCAddress2Txt = (EditText) view.findViewById(R.id.caddr2_txt);
        mCCityTxt = (EditText) view.findViewById(R.id.ccity_txt);
        mCStateTxt = (EditText) view.findViewById(R.id.cstate_txt);
        mCZipTxt = (EditText) view.findViewById(R.id.czip_txt);
        mCCountryTxt = (EditText) view.findViewById(R.id.ccountry_txt);
        mCPhoneNoTxt = (EditText) view.findViewById(R.id.cphno_txt);
        mShippingLayout = (LinearLayout) view.findViewById(R.id.shippinglayout);
        mCartImg = (ImageView) toolbar.findViewById(R.id.cart_img);
        mSearchLayout = (RelativeLayout) getActivity().findViewById(R.id.search_layout);
        mCountTxtView.setVisibility(View.GONE);
        mCartImg.setVisibility(View.GONE);
        mSearchLayout.setVisibility(View.GONE);
        mTitleTxtView.setText("Profile");
        gson = new Gson();
        mEmailEdtText = (EditText) view.findViewById(R.id.email_txt);
        mFirstNameETxt = (EditText) view.findViewById(R.id.firstname_etxt);
        mLastNameETxt = (EditText) view.findViewById(R.id.lastname_etxt);
        apiInterface = RetrofitAPIClient.getClient(getContext()).create(APIInterface.class);

        mCheckBox.setChecked(prefs.getBoolean("sameshippingaddress", true));
        if (mCheckBox.isChecked()) {
            mShippingLayout.setVisibility(View.GONE);
        } else {
            mShippingLayout.setVisibility(View.VISIBLE);
        }
        apiInterface.getProfileData(token).enqueue(new Callback<List<AddressItem>>() {
            @Override
            public void onResponse(Call<List<AddressItem>> call, Response<List<AddressItem>> response) {


                if (response.body() != null) {
                    Log.e("profile","="+response.body().size());
                    if (response.body().size() == 2) {

                        setProfileData(response.body());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<AddressItem>> call, Throwable t) {

            }
        });
    }

    private void setProfileData(List<AddressItem> arr) {
        gson = new Gson();
        String username = prefs.getString("username", null);
        if (true) {
            AddressItem addrItem = arr.get(0);
            mCNameTxt.setText(addrItem.getName());
            mCAddress1Txt.setText(addrItem.getAddress1());
            mCAddress2Txt.setText(addrItem.getAddress2());
            mCCityTxt.setText(addrItem.getCity());
            mCStateTxt.setText(addrItem.getState());
            mCZipTxt.setText(addrItem.getZip());
            mCCountryTxt.setText(addrItem.getCountry());
            mCPhoneNoTxt.setText(addrItem.getPhonenumber());
            AddressItem addrItem2 = arr.get(1);
            mNameTxt.setText(addrItem2.getName());
            mAddress1Txt.setText(addrItem2.getAddress1());
            mAddress2Txt.setText(addrItem2.getAddress2());
            mCityTxt.setText(addrItem2.getCity());
            mStateTxt.setText(addrItem2.getState());
            mZipTxt.setText(addrItem2.getZip());
            mCountryTxt.setText(addrItem2.getCountry());
            mPhoneNoTxt.setText(addrItem2.getPhonenumber());
        }
    }

    private void saveProfileData() {
        String username = prefs.getString("username", "guest");
        mAddressArray.clear();
        mAddressArray.add(new AddressItem(new Long(1), mCNameTxt.getText().toString().trim(), mCAddress1Txt.getText().toString().trim(), mCAddress2Txt.getText().toString().trim(), mCCityTxt.getText().toString().trim(), mCStateTxt.getText().toString().trim(), mCZipTxt.getText().toString().trim(), mCCountryTxt.getText().toString().trim(), mCPhoneNoTxt.getText().toString().trim(), username));
        mAddressArray.add(new AddressItem(new Long(2), mNameTxt.getText().toString().trim(), mAddress1Txt.getText().toString().trim(), mAddress2Txt.getText().toString().trim(), mCityTxt.getText().toString().trim(), mStateTxt.getText().toString().trim(), mZipTxt.getText().toString().trim(), mCountryTxt.getText().toString().trim(), mPhoneNoTxt.getText().toString().trim(), username));
        String json = gson.toJson(mAddressArray);
        prefsEditor.putString("profiledataof" + username, json);
        prefsEditor.commit();
        response = apiInterface.updateUser(new User(0, mFirstNameETxt.getText().toString().trim(), mLastNameETxt.getText().toString().trim(), mEmailEdtText.getText().toString().trim(), "", 0, "", mPhoneNoTxt.getText().toString().trim(), ""));
        response.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });


        response2 = apiInterface.addProfileData(mAddressArray.get(0), token);
        response2.enqueue(new Callback<AddressItem>() {
            @Override
            public void onResponse(Call<AddressItem> call, Response<AddressItem> response) {

            }

            @Override
            public void onFailure(Call<AddressItem> call, Throwable t) {

            }
        });
        response2 = apiInterface.addProfileData(mAddressArray.get(1), token);
        response2.enqueue(new Callback<AddressItem>() {
            @Override
            public void onResponse(Call<AddressItem> call, Response<AddressItem> response) {

            }

            @Override
            public void onFailure(Call<AddressItem> call, Throwable t) {

            }
        });
        Toast.makeText(getContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_txt:
                saveProfileData();
                break;
        }
    }

    public class AddressItem {
        private Long id;
        private String name = "";
        private String address1 = "";
        private String address2 = "";
        private String city = "";
        private String state = "";
        private String zip = "";
        private String country = "";
        private String phonenumber = "";
        private String userEmail = "";

        public String getUserEmail() {
            return userEmail;
        }

        public void setUserEmail(String userEmail) {
            this.userEmail = userEmail;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress1() {
            return address1;
        }

        public void setAddress1(String address1) {
            this.address1 = address1;
        }

        public String getAddress2() {
            return address2;
        }

        public void setAddress2(String address2) {
            this.address2 = address2;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getZip() {
            return zip;
        }

        public void setZip(String zip) {
            this.zip = zip;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getPhonenumber() {
            return phonenumber;
        }

        public void setPhonenumber(String phonenumber) {
            this.phonenumber = phonenumber;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public AddressItem(Long id, String name, String address1, String address2, String city, String state, String zip, String country, String phonenumber, String userEmail) {
            this.id = id;
            this.name = name;
            this.address1 = address1;
            this.address2 = address2;
            this.city = city;
            this.state = state;
            this.zip = zip;
            this.country = country;
            this.phonenumber = phonenumber;
            this.userEmail = userEmail;
        }

        @Override
        public String toString() {
            return "AddressItem{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", address1='" + address1 + '\'' +
                    ", address2='" + address2 + '\'' +
                    ", city='" + city + '\'' +
                    ", state='" + state + '\'' +
                    ", zip='" + zip + '\'' +
                    ", country='" + country + '\'' +
                    ", phonenumber='" + phonenumber + '\'' +
                    '}';
        }
    }
}
