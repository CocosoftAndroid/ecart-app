package com.cocosoft.ecart.cartmodule;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.design.widget.FloatingActionButton;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cocosoft.ecart.R;
import com.cocosoft.ecart.barcode.BarcodeCaptureActivity;
import com.cocosoft.ecart.database.DatabaseHandler;
import com.cocosoft.ecart.listeners.CheckboxListener;
import com.cocosoft.ecart.listeners.IndividualItemListener;
import com.cocosoft.ecart.listeners.QuantityListener;
import com.cocosoft.ecart.listeners.WishlistListener;
import com.cocosoft.ecart.loginmodule.DescriptionFragment;
import com.cocosoft.ecart.loginmodule.EditProfileFragment;
import com.cocosoft.ecart.loginmodule.IndividualItemFragment;
import com.cocosoft.ecart.loginmodule.LoginFragment;
import com.cocosoft.ecart.network.APIInterface;
import com.cocosoft.ecart.network.RetrofitAPIClient;
import com.cocosoft.ecart.orderHistory.OrderList;
import com.cocosoft.ecart.orderHistory.OrderMaster;
import com.cocosoft.ecart.scanlistmodule.ProductItem;
import com.cocosoft.ecart.wishlistmodule.WishList;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.payu.india.Extras.PayUChecksum;
import com.payu.india.Model.PaymentParams;
import com.payu.india.Model.PayuConfig;
import com.payu.india.Model.PayuHashes;
import com.payu.india.Model.PostData;
import com.payu.india.Payu.Payu;
import com.payu.india.Payu.PayuConstants;
import com.payu.india.Payu.PayuErrors;
import com.payu.payuui.Activity.PayUBaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class CartFragment extends Fragment implements View.OnClickListener, QuantityListener, WishlistListener, IndividualItemListener, CheckboxListener {

    private TextView mAddCartTxt, mGrandTotalTxt, mSubTotalTxt, mTaxTxt;
    private LinearLayoutManager mLManager;
    private RecyclerView mProductRView;
    private CartAdapter mCartAdapter;
    private QuantityListener mQtyListener;
    private ArrayList<CartItem> mCartArray = new ArrayList<>();
    private TextView mCountTxtView;
    private TextView mTitleTxtView;
    private ImageView mCartImg, mScanImg;
    private SharedPreferences prefs;

    private TextView mAddWishTxt;
    private Gson gson;
    private SharedPreferences.Editor prefsEditor;

    int _checkoutAmount = 0;
    double grandtotal = 0.0;
    private APIInterface apiInterface;
    private Call<WishList> response;
    private Call<OrderMaster> Orderresponse;
    private Integer totalitems = 0;
    private Double totalPrice = 0.0;
    private List<OrderList> orderlist = new ArrayList<>();
    /* payu money variable declarations */
    private String merchantKey, userCredentials;
    private PaymentParams mPaymentParams;
    private PayuConfig payuConfig;
    private PayUChecksum checksum;
    String username, token, firstName = "default";
    private final String salt = "eCwWELxi";// "13p0PXZk";
    String txnid;
    String amount;
    String productInfo;
    String udf1;
    String udf2;
    String udf3;
    String udf4;
    String udf5;
    String status = null, err_msg = null, card_typ;
    private String countryName;
    private LinearLayout mLinearLayout;
    private LinearLayout mNoItemLayout;
    private ArrayList<String> mScannedList = new ArrayList<>();
    private FloatingActionButton mCameraFAB;
    private DecimalFormat decim;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getContext().getSharedPreferences("cocosoft", MODE_PRIVATE);
        prefsEditor = prefs.edit();
        gson = new Gson();
        String tempdata = prefs.getString("tempcartlist", null);
        Type type = new TypeToken<List<CartItem>>() {
        }.getType();
        ArrayList<CartItem> arr = gson.fromJson(tempdata, type);
        if (arr != null) {
            mCartArray.clear();
            mCartArray.addAll((ArrayList<CartItem>) gson.fromJson(tempdata, type));
        }
        String tempdata2 = prefs.getString("tempscanlist2", null);

        Type type2 = new TypeToken<List<String>>() {
        }.getType();
        ArrayList<String> arr2 = gson.fromJson(tempdata2, type2);
        if (arr2 != null) {
            mScannedList = gson.fromJson(tempdata2, type2);
        }
        Log.e("cart", "onCreate" + mCartArray.size());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        init(view);
        setListeners();
        Log.e("cart", "onCreateView");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("cart", "onResume");
        mTitleTxtView.setText("Cart");
    }

    private void setListeners() {
        mAddCartTxt.setOnClickListener(this);
        mAddWishTxt.setOnClickListener(this);
        mCameraFAB.setOnClickListener(this);
    }

    public void setListener(QuantityListener lis) {
        mQtyListener = lis;
    }

    private void init(View view) {

        apiInterface = RetrofitAPIClient.getClient(getContext()).create(APIInterface.class);
        mLinearLayout = (LinearLayout) view.findViewById(R.id.llayout1);
        mNoItemLayout = (LinearLayout) view.findViewById(R.id.noitem_layout);
        mAddCartTxt = (TextView) view.findViewById(R.id.add_cart_txt);
        mAddWishTxt = (TextView) view.findViewById(R.id.add_wish_txt);
        mCameraFAB = (FloatingActionButton) view.findViewById(R.id.fab);
        mGrandTotalTxt = (TextView) view.findViewById(R.id.grandtotal_txt);
        mTaxTxt = (TextView) view.findViewById(R.id.tax_txt);
        mSubTotalTxt = (TextView) view.findViewById(R.id.subtotal_txt);
        mLManager = new LinearLayoutManager(getContext());
        mProductRView = (RecyclerView) view.findViewById(R.id.rview);
        mProductRView.setLayoutManager(mLManager);
        mCartAdapter = new CartAdapter(getContext(), mCartArray, this, this, this, this);
        mProductRView.setAdapter(mCartAdapter);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);

        mCountTxtView = (TextView) toolbar.findViewById(R.id.total_count);
        mTitleTxtView = (TextView) toolbar.findViewById(R.id.title_txt);
        mCartImg = (ImageView) toolbar.findViewById(R.id.cart_img);

        //Madhu Modification For Title Camera Scan For All
        RelativeLayout relativeLayout = (RelativeLayout) getActivity().findViewById(R.id.search_layout);
        mScanImg = (ImageView) relativeLayout.findViewById(R.id.scan_img);
        mScanImg.setOnClickListener(this);
        relativeLayout.setVisibility(View.GONE);

        mCountTxtView.setVisibility(View.GONE);
        mCartImg.setVisibility(View.GONE);

        RelativeLayout mSearchLayout = (RelativeLayout) getActivity().findViewById(R.id.search_layout);
        mSearchLayout.setVisibility(View.GONE);
        calculateTotal();
        if (mCartArray.size() == 0) {
            mLinearLayout.setVisibility(View.GONE);
            mAddCartTxt.setVisibility(View.GONE);
            mNoItemLayout.setVisibility(View.VISIBLE);
        } else {
            mLinearLayout.setVisibility(View.VISIBLE);
            mNoItemLayout.setVisibility(View.GONE);
            mAddCartTxt.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        Payu.setInstance(this.getActivity());
        boolean isloggedin = prefs.getBoolean("isloggedin", false);
        boolean isGuest = prefs.getBoolean("isGuest", false);
        username = prefs.getString("username", "guest");
        firstName = prefs.getString("firstname", "nfound");
        token = prefs.getString("token", "");
        countryName = prefs.getString("country", "");

        switch (v.getId()) {
            case R.id.add_cart_txt:
                if (isloggedin || isGuest) {
                    if (isAddressFilled()) {
                        doPayment();
                    } else {
                        Toast.makeText(getContext(), "Please fill you Billing and Shipping Address", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    openFrag(1, "");
                }
                break;

            case R.id.fab:
                Intent intent = new Intent(getContext(), BarcodeCaptureActivity.class);
                startActivityForResult(intent, 444);
                break;
        }
    }


    private void addToScannedList(String id) {
        boolean contains = false;
        if (mScannedList.size() > 0) {
            for (int i = 0; i < mScannedList.size(); i++) {
                if (mScannedList.get(i).equals(id)) {
                    contains = true;
                } else {

                }
            }
            if (!contains) {
                mScannedList.add(id);
            }
        } else {
            mScannedList.add(id);
        }
        String json = gson.toJson(mScannedList);
        prefsEditor.putString("tempscanlist2", json);
        prefsEditor.commit();

    }

    private boolean isAddressFilled() {
        String tempdata = prefs.getString("profiledataof" + username, null);
        Type type = new TypeToken<List<EditProfileFragment.AddressItem>>() {
        }.getType();
        ArrayList<EditProfileFragment.AddressItem> arr = gson.fromJson(tempdata, type);
        if (arr != null) {
            return true;
        } else {
            return false;
        }
    }

    private void doPayment() {
        if (_checkoutAmount != 0) {
            productInfo = mCartArray.get(0).getProductName();
            if (countryName.equals("India")) {
                navigateToBaseActivity();  // call this function for payumoney gateway
            } else {
                //For US and UK (Authorize.net)
                //openFrag(3, "");
                navigateToBaseActivity();
            }
        } else {
            Toast.makeText(getContext(), "Sorry !! No items in your Cart to check out", Toast.LENGTH_SHORT).show();
        }
    }

    public void navigateToBaseActivity() {

        merchantKey = "gtKFFx"; //0MQaQP
        amount = String.valueOf(decim.format(grandtotal));
        txnid = "" + System.currentTimeMillis();
        udf1 = "udf1";
        udf2 = "udf2";
        udf3 = "udf3";
        udf4 = "udf4";
        udf5 = "udf5";
        int environment = PayuConstants.STAGING_ENV;
        userCredentials = merchantKey + ":" + username;
        mPaymentParams = new PaymentParams();
        mPaymentParams.setKey(merchantKey);
        mPaymentParams.setAmount(amount);
        mPaymentParams.setProductInfo(productInfo);
        mPaymentParams.setFirstName(firstName);

        mPaymentParams.setEmail(username);
        mPaymentParams.setTxnId(txnid);
        /**
         * Surl --> Success url is where the transaction response is posted by PayU on successful transaction
         * Furl --> Failre url is where the transaction response is posted by PayU on failed transaction
         */
        mPaymentParams.setSurl("http://192.168.0.104:8080/success");
        mPaymentParams.setFurl("http://192.168.0.104:8080/failure");
        mPaymentParams.setUdf1(udf1);
        mPaymentParams.setUdf2(udf2);
        mPaymentParams.setUdf3(udf3);
        mPaymentParams.setUdf4(udf4);
        mPaymentParams.setUdf5(udf5);
        mPaymentParams.setUserCredentials(userCredentials);


        //TODO Sets the payment environment in PayuConfig object
        payuConfig = new PayuConfig();
        payuConfig.setEnvironment(environment);
        generateHashFromSDK(mPaymentParams, salt);

    }


    public void generateHashFromSDK(PaymentParams mPaymentParams, String salt) {
        PayuHashes payuHashes = new PayuHashes();
        PostData postData = new PostData();

        // payment Hash;
        checksum = null;
        checksum = new PayUChecksum();
        checksum.setAmount(mPaymentParams.getAmount());
        checksum.setKey(mPaymentParams.getKey());
        checksum.setTxnid(mPaymentParams.getTxnId());
        checksum.setEmail(mPaymentParams.getEmail());
        checksum.setSalt(salt);
        checksum.setProductinfo(mPaymentParams.getProductInfo());
        checksum.setFirstname(mPaymentParams.getFirstName());
        checksum.setUdf1(mPaymentParams.getUdf1());
        checksum.setUdf2(mPaymentParams.getUdf2());
        checksum.setUdf3(mPaymentParams.getUdf3());
        checksum.setUdf4(mPaymentParams.getUdf4());
        checksum.setUdf5(mPaymentParams.getUdf5());

        postData = checksum.getHash();
        if (postData.getCode() == PayuErrors.NO_ERROR) {
            payuHashes.setPaymentHash(postData.getResult());
        }

        String var1 = mPaymentParams.getUserCredentials() == null ? PayuConstants.DEFAULT : mPaymentParams.getUserCredentials();
        String key = mPaymentParams.getKey();

        if ((postData = calculateHash(key, PayuConstants.PAYMENT_RELATED_DETAILS_FOR_MOBILE_SDK, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR) // Assign post data first then check for success
            payuHashes.setPaymentRelatedDetailsForMobileSdkHash(postData.getResult());
        //vas
        if ((postData = calculateHash(key, PayuConstants.VAS_FOR_MOBILE_SDK, PayuConstants.DEFAULT, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
            payuHashes.setVasForMobileSdkHash(postData.getResult());

        // getIbibocodes
        if ((postData = calculateHash(key, PayuConstants.GET_MERCHANT_IBIBO_CODES, PayuConstants.DEFAULT, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
            payuHashes.setMerchantIbiboCodesHash(postData.getResult());

        if (!var1.contentEquals(PayuConstants.DEFAULT)) {
            // get user card
            if ((postData = calculateHash(key, PayuConstants.GET_USER_CARDS, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR) // todo rename storedc ard
                payuHashes.setStoredCardsHash(postData.getResult());
            // save user card
            if ((postData = calculateHash(key, PayuConstants.SAVE_USER_CARD, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
                payuHashes.setSaveCardHash(postData.getResult());
            // delete user card
            if ((postData = calculateHash(key, PayuConstants.DELETE_USER_CARD, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
                payuHashes.setDeleteCardHash(postData.getResult());
            // edit user card
            if ((postData = calculateHash(key, PayuConstants.EDIT_USER_CARD, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
                payuHashes.setEditCardHash(postData.getResult());
        }

        if (mPaymentParams.getOfferKey() != null) {
            postData = calculateHash(key, PayuConstants.OFFER_KEY, mPaymentParams.getOfferKey(), salt);
            if (postData.getCode() == PayuErrors.NO_ERROR) {
                payuHashes.setCheckOfferStatusHash(postData.getResult());
            }
        }

        if (mPaymentParams.getOfferKey() != null && (postData = calculateHash(key, PayuConstants.CHECK_OFFER_STATUS, mPaymentParams.getOfferKey(), salt)) != null && postData.getCode() == PayuErrors.NO_ERROR) {
            payuHashes.setCheckOfferStatusHash(postData.getResult());
        }

        // we have generated all the hases now lest launch sdk's ui
        launchSdkUI(payuHashes);
    }

    // deprecated, should be used only for testing.
    private PostData calculateHash(String key, String command, String var1, String salt) {
        checksum = null;
        checksum = new PayUChecksum();
        checksum.setKey(key);
        checksum.setCommand(command);
        checksum.setVar1(var1);
        checksum.setSalt(salt);
        return checksum.getHash();
    }

    /**
     * This method adds the Payuhashes and other required params to intent and launches the PayuBaseActivity.java
     *
     * @param payuHashes it contains all the hashes generated from merchant server
     */
    public void launchSdkUI(PayuHashes payuHashes) {

        Intent intent = new Intent(this.getActivity(), PayUBaseActivity.class);
        intent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
        intent.putExtra(PayuConstants.PAYMENT_PARAMS, mPaymentParams);
        intent.putExtra(PayuConstants.PAYU_HASHES, payuHashes);
        startActivityForResult(intent, PayuConstants.PAYU_REQUEST_CODE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == PayuConstants.PAYU_REQUEST_CODE) {
            if (data != null) {


                String status = getStstusandTranDetails(data.getStringExtra("payu_response"));
                if (status.equals("No Error")) {

                    for (int y = 0; y < mCartArray.size(); y++) {
                        totalitems = totalitems + mCartArray.get(y).getCount();
                        for (int i = 0; i < mCartArray.get(y).getCount(); i++)
                            totalPrice = totalPrice + mCartArray.get(y).getProductPrice();
                        Log.i("product details", String.valueOf(totalitems));
                        Log.i("product details", String.valueOf(totalPrice));
                        Log.i("firstname", firstName);
                        Log.i("username", username);

                        orderlist.add(new OrderList(0, mCartArray.get(y).getProductId(), mCartArray.get(y).getProductName(), mCartArray.get(y).getProductPrice(), mCartArray.get(y).getCount()));
                        productInfo = mCartArray.get(y).getProductName();

                    }
                    addBillDetailToWeb(new OrderMaster("", 0, status, card_typ, "", "", "", username, "", "", totalitems, grandtotal, orderlist, null));
                    emptyCheckoutData();
                    Toast.makeText(this.getActivity(), "Transaction successfull", Toast.LENGTH_LONG).show();
                    onPressGotoHomePage();


                } else {
                    Toast.makeText(this.getActivity(), "Payment failed with Error message(" + status + ") . Please try checkout again", Toast.LENGTH_LONG).show();
                    addBillDetailToWeb(new OrderMaster("", 0, "failed", card_typ, "", "", "", username, "", "", totalitems, grandtotal, orderlist, null));


                }
                Log.d("payUMoney data", data.getStringExtra("payu_response"));


            } else {
                Toast.makeText(this.getActivity(), "Couldn't do transaction ! Try again", Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == 444) {

            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Point[] p = barcode.cornerPoints;
                    JSONObject obj = null;
                    try {
                        obj = new JSONObject(barcode.displayValue);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (obj != null) {
                        String id = obj.optString("id");
                        addToScannedList(id);
                        openFrag(4, id);


                    }
                }
            }
        }
    }


    public String getStstusandTranDetails(String response) {
        try {
            JSONObject jsonObj = new JSONObject(response);
            status = jsonObj.getString("status");
            amount = jsonObj.getString("amount");
            err_msg = jsonObj.getString("Error_Message");
            card_typ = jsonObj.getString("card_type");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return err_msg;
    }

    private void addBillDetailToWeb(OrderMaster orderMaster) {

        Orderresponse = apiInterface.addOrder(orderMaster, token);
        Orderresponse.enqueue(new Callback<OrderMaster>() {

            @Override
            public void onResponse(Call<OrderMaster> call, Response<OrderMaster> response) {

            }

            @Override
            public void onFailure(Call<OrderMaster> call, Throwable t) {

            }
        });

    }

    void onPressGotoHomePage() {
        Intent i = new Intent(CartFragment.this.getActivity(), com.cocosoft.ecart.loginmodule.LoginActivity.class);
        startActivity(i);
    }

    void emptyCheckoutData() {
        mCartArray.clear();
        prefsEditor = prefs.edit();
        String json = (new Gson()).toJson(mCartArray);
        prefsEditor.putString("tempcartlist", json);
        prefsEditor.commit();
        String aftertempdata = prefs.getString("tempcartlist", null);
        Log.i("aftertempdata", aftertempdata);


    }


    @Override
    public void onQuantityChange(String productid, int quantity) {
        if (quantity == 0) {
            for (int i = 0; i < mCartArray.size(); i++) {
                if (mCartArray.get(i).getProductId().equals(productid)) {
                    mCartArray.get(i).setCount(0);
                    mCartArray.remove(i);
                }
            }
        } else {
            for (int i = 0; i < mCartArray.size(); i++) {
                if (mCartArray.get(i).getProductId().equals(productid)) {
                    int count = mCartArray.get(i).getCount();
                    mCartArray.get(i).setCount(count + quantity);
                }
            }
        }
        mCartAdapter.notifyDataSetChanged();
        String json = gson.toJson(mCartArray);
        prefsEditor.putString("tempcartlist", json);
        prefsEditor.commit();
        calculateTotal();
    }

    private void calculateTotal() {
        /*double tax=5;
        double ordertotal;
        double purcheseprice;

        double tt=_checkoutAmount*(tax/100.0);
        double tx=_checkoutAmount+tt;
*/
        _checkoutAmount = 0;
        for (int i = 0; i < mCartArray.size(); i++) {
            _checkoutAmount = _checkoutAmount + (mCartArray.get(i).getCount() * mCartArray.get(i).getProductPrice().intValue());
        }
        decim = new DecimalFormat("0.00");
        String subtotal = decim.format(_checkoutAmount);
        mSubTotalTxt.setText("SubTotal = $" + subtotal);

        mTaxTxt.setText("Tax (5%) = $" + calcTax(_checkoutAmount));
       /* mGrandTotalTxt.setText("Order Total = $" + tx+".00");*/
        grandtotal = Double.parseDouble(subtotal) + Double.parseDouble(calcTax(_checkoutAmount));
        mGrandTotalTxt.setText("Order Total = $" + decim.format(grandtotal));
    }

    private String calcTax(int checkoutAmount) {
        double tax = (checkoutAmount / 100.0) * 5;
        DecimalFormat decim = new DecimalFormat("0.00");

        return decim.format(tax);
    }

    private void openFrag(int i, String productid) {
        Fragment firstFragment = null;
        switch (i) {
            case 1:
                firstFragment = new LoginFragment();
                break;
            case 2:
                firstFragment = new DescriptionFragment();

                Bundle bundles = new Bundle();
                bundles.putString("id", productid);
                firstFragment.setArguments(bundles);
                break;
            case 3:
                firstFragment = new BillingFragment();
                Bundle args = new Bundle();
                args.putParcelableArrayList("ARRAYLIST", mCartArray);
                args.putInt("Checkout Amount", _checkoutAmount);
                firstFragment.setArguments(args);
                break;
            case 4:
                firstFragment = new DescriptionFragment();
                Bundle b = new Bundle();
                b.putString("id", productid);
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

    @Override
    public void onFavouriteClicked(String productid, boolean isChecked) {
       /* String username = prefs.getString("username", "");
        if (isChecked) {

            Toast.makeText(getContext(), "Added to Wishlist", Toast.LENGTH_SHORT).show();
            mDB.addToWishlist(productid, username);

        } else {
            Toast.makeText(getContext(), "Wishlist Removed", Toast.LENGTH_SHORT).show();
            mDB.removeWishlist(productid, username);

        }*/

    }

    @Override
    public void onFavouriteClicked(String productid, String productname, Double price, boolean isChecked, String productDesc) {
        String username = prefs.getString("username", "");
        String token = prefs.getString("token", "");
        if (isChecked) {

            Toast.makeText(getContext(), "Added to Wishlist", Toast.LENGTH_SHORT).show();
//            mDB.addToWishlist(productid, username);
            addWishList(new WishList(Integer.parseInt(productid), username, productname, price, null, true, false, productDesc), token);

        } else {
            Toast.makeText(getContext(), "Wishlist Removed", Toast.LENGTH_SHORT).show();
//            mDB.removeWishlist(productid, username);
            addWishList(new WishList(Integer.parseInt(productid), username, productname, price, null, false, false, productDesc), token);
        }

    }

    @Override
    public void OnCardClick(String productid) {
        openFrag(2, productid);
    }

    @Override
    public void onChecked(String productid, boolean isChecked) {

    }


    private void addWishList(WishList wlist, String token) {
        response = apiInterface.addWishList(wlist, token);
        response.enqueue(new Callback<WishList>() {

            @Override
            public void onResponse(Call<WishList> call, Response<WishList> response) {

            }

            @Override
            public void onFailure(Call<WishList> call, Throwable t) {

            }
        });
    }
}
