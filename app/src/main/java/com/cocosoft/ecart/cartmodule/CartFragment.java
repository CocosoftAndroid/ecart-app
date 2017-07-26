package com.cocosoft.ecart.cartmodule;

import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cocosoft.ecart.R;
import com.cocosoft.ecart.database.DatabaseHandler;
import com.cocosoft.ecart.listeners.CheckboxListener;
import com.cocosoft.ecart.listeners.IndividualItemListener;
import com.cocosoft.ecart.listeners.QuantityListener;
import com.cocosoft.ecart.listeners.WishlistListener;
import com.cocosoft.ecart.loginmodule.IndividualItemFragment;
import com.cocosoft.ecart.loginmodule.LoginFragment;
import com.cocosoft.ecart.network.APIInterface;
import com.cocosoft.ecart.network.RetrofitAPIClient;
import com.cocosoft.ecart.orderHistory.OrderList;
import com.cocosoft.ecart.orderHistory.OrderMaster;
import com.cocosoft.ecart.scanlistmodule.ProductItem;
import com.cocosoft.ecart.wishlistmodule.WishList;
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

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class CartFragment extends Fragment implements View.OnClickListener, QuantityListener, WishlistListener, IndividualItemListener, CheckboxListener {

    private TextView mAddCartTxt, mGrandTotalTxt;
    private LinearLayoutManager mLManager;
    private RecyclerView mProductRView;
    private CartAdapter mCartAdapter;
    private QuantityListener mQtyListener;
    private ArrayList<CartItem> mCartArray = new ArrayList<>();
    private TextView mCountTxtView;
    private TextView mTitleTxtView;
    private ImageView mCartImg;
    private SharedPreferences prefs;
    private TextView mAddWishTxt;
    private Gson gson;
    private SharedPreferences.Editor prefsEditor;
    private DatabaseHandler mDB;
    int _checkoutAmount = 0;
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
            mCartArray = gson.fromJson(tempdata, type);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        apiInterface = RetrofitAPIClient.getClient(this.getActivity()).create(APIInterface.class);
        init(view);
        setListeners();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mTitleTxtView.setText("Cart");
    }

    private void setListeners() {
        mAddCartTxt.setOnClickListener(this);
        mAddWishTxt.setOnClickListener(this);
    }

    public void setListener(QuantityListener lis) {
        mQtyListener = lis;
    }

    private void init(View view) {
        gson = new Gson();
        apiInterface = RetrofitAPIClient.getClient(getContext()).create(APIInterface.class);
        mAddCartTxt = (TextView) view.findViewById(R.id.add_cart_txt);
        mAddWishTxt = (TextView) view.findViewById(R.id.add_wish_txt);
        mGrandTotalTxt = (TextView) view.findViewById(R.id.grandtotal_txt);
        mDB = new DatabaseHandler(getContext());
        mLManager = new LinearLayoutManager(getContext());
        mProductRView = (RecyclerView) view.findViewById(R.id.rview);
        mProductRView.setLayoutManager(mLManager);
        mCartAdapter = new CartAdapter(getContext(), mCartArray, this, this, this, this);
        mProductRView.setAdapter(mCartAdapter);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        mCountTxtView = (TextView) toolbar.findViewById(R.id.total_count);
        mTitleTxtView = (TextView) toolbar.findViewById(R.id.title_txt);
        mCartImg = (ImageView) toolbar.findViewById(R.id.cart_img);
        mCountTxtView.setVisibility(View.GONE);
        mCartImg.setVisibility(View.GONE);
        calculateTotal();
    }

    @Override
    public void onClick(View v) {
        Payu.setInstance(this.getActivity());
        boolean isloggedin = prefs.getBoolean("isloggedin", false);
        username = prefs.getString("username", "");
        firstName = prefs.getString("firstname", "nfound");
        token = prefs.getString("token", "");
        countryName = prefs.getString("country", "");

        switch (v.getId()) {
            case R.id.add_cart_txt:
                if (isloggedin) {
                    Toast.makeText(getContext(), "Processing Payment", Toast.LENGTH_SHORT).show();
                    if (_checkoutAmount != 0) {
                        productInfo = mCartArray.get(0).getProductName();
                        if (countryName.equals("India")) {
                            navigateToBaseActivity();  // call this function for payumoney gateway

                        } else {
                            //For US and UK (Authorize.net)
                            Intent i = new Intent(CartFragment.this.getActivity(), BillingPage.class);
                            i.putExtra("Checkout Amount", _checkoutAmount);
                            Bundle args = new Bundle();
                            args.putParcelableArrayList("ARRAYLIST", mCartArray);
                            i.putExtra("BUNDLE", args);
                            startActivity(i);
                        }
                        // uncomment below for authorize.net code and comment navigateToBaseActivity func call

                    } else {
                        Toast.makeText(getContext(), "Sorry !! No items in your Cart to check out", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.i("CartFragment", "User Not logged IN");
                    openFrag(1, "");
//                    openFrag(3,"");

                }
                break;

        }
    }

    public void navigateToBaseActivity() {

        merchantKey = "gtKFFx"; //0MQaQP
        amount = String.valueOf(_checkoutAmount);
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
                    addBillDetailToWeb(new OrderMaster("", 0, status, card_typ, "", "", "", username, "", "", totalitems, totalPrice, orderlist, null));
                    emptyCheckoutData();
                    Toast.makeText(this.getActivity(), "Transaction successfull", Toast.LENGTH_LONG).show();
                    onPressGotoHomePage();


                } else {
                    Toast.makeText(this.getActivity(), "Payment failed with Error message(" + status + ") . Please try checkout again", Toast.LENGTH_LONG).show();
                }
                Log.d("payUMoney data", data.getStringExtra("payu_response"));


            } else {
                Toast.makeText(this.getActivity(), "Couldn't do transaction ! Try again", Toast.LENGTH_LONG).show();
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
        _checkoutAmount = 0;
        for (int i = 0; i < mCartArray.size(); i++) {
            _checkoutAmount = _checkoutAmount + (mCartArray.get(i).getCount() * mCartArray.get(i).getProductPrice().intValue());
        }
        mGrandTotalTxt.setText("Grand Total = $ " + _checkoutAmount);
    }

    private void openFrag(int i, String productid) {
        Fragment firstFragment = null;
        switch (i) {
            case 1:
                firstFragment = new LoginFragment();
                break;
            case 2:
                firstFragment = new IndividualItemFragment();
                ProductItem item = null;
                for (int ij = 0; ij < mCartArray.size(); ij++) {
                    if (mCartArray.get(ij).getProductId().equals(productid))
                        item = new ProductItem(mCartArray.get(ij).getProductId(), mCartArray.get(ij).getProductName(), mCartArray.get(ij).getProductDesc(), mCartArray.get(ij).getProductPrice(), 0, 0, false);
                }
                Bundle bundles = new Bundle();
                bundles.putParcelable("item", item);
                firstFragment.setArguments(bundles);
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
    public void onFavouriteClicked(String productid, String productname, Double price, boolean isChecked) {
        String username = prefs.getString("username", "");
        String token = prefs.getString("token", "");
        if (isChecked) {

            Toast.makeText(getContext(), "Added to Wishlist", Toast.LENGTH_SHORT).show();
//            mDB.addToWishlist(productid, username);
            addWishList(new WishList(Integer.parseInt(productid), username, productname, price, null, true, false), token);

        } else {
            Toast.makeText(getContext(), "Wishlist Removed", Toast.LENGTH_SHORT).show();
//            mDB.removeWishlist(productid, username);
            addWishList(new WishList(Integer.parseInt(productid), username, productname, price, null, false, false), token);
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
