package com.lloydtucker.blueproject;

import android.animation.LayoutTransition;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;

import okhttp3.HttpUrl;
import okhttp3.Response;

public class PaymentActivity extends AppCompatActivity {
    private static final String TAG = PaymentActivity.class.getSimpleName();
    static final String PAYMENTS = "payments";

    private final String TAG_PAYMENT_STATUS = "paymentStatus";
    private final String TAG_PAYMENT_ID = "id";
    //private final String TAG_MOBILE_PHONE = "mobilePhone";
    private final String TAG_OTP_ERROR = "errorMessage";
    private final String TAG_PAYMENT_REFERENCE = "paymentReference";
    private final String TAG_TO_SORT_CODE = "toSortCode";
    private final String TAG_TO_ACCOUNT_NUMBER = "toAccountNumber";
    private final String TAG_PAYMENT_AMOUNT = "paymentAmount";
    private final String TAG_OTP_CODE = "OTPCode";
    private final String PENDING = "Pending";
    private final String TWO_FA = "2FA required";

    private TextView accountBalance, accountDetails, accountType, paymentFrom;
    private EditText paymentSortCode, paymentAccountNumber, paymentReference,
    paymentAmount, OTPCodeField;
    private ProgressBar progressBar;
    private Button submitPayment, submitOTP;
    private LinearLayout paymentFormLayout, paymentOTPLayout;
    private RelativeLayout paymentRelativeOTP, paymentAccountHeader, paymentRelativeForm,
            paymentLayout;
    private ImageView imageView;
    private String accountId, accountT, accountNo, sortCode, paymentId, OTPCode;
    private Response response;
    private double accountBal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        //header of the activity
        paymentAccountHeader = (RelativeLayout) findViewById(R.id.paymentAccountHeader);
        imageView = (ImageView) paymentAccountHeader.findViewById(R.id.account_image);
        accountType = (TextView) paymentAccountHeader.findViewById(R.id.account_type);
        accountDetails = (TextView) paymentAccountHeader.findViewById(R.id.account_details);
        accountBalance = (TextView) paymentAccountHeader.findViewById(R.id.account_balance);
        paymentFrom = (TextView) paymentAccountHeader.findViewById(R.id.account_from);

        //body of the activity
        paymentOTPLayout = (LinearLayout) findViewById(R.id.paymentOTPLayout);
        paymentRelativeOTP = (RelativeLayout) findViewById(R.id.paymentRelativeOTP);
        paymentRelativeForm = (RelativeLayout) findViewById(R.id.paymentRelativeForm);
        paymentFormLayout = (LinearLayout) findViewById(R.id.paymentFormLayout);
        paymentLayout = (RelativeLayout) findViewById(R.id.paymentLayout);
        submitPayment = (Button) findViewById(R.id.paymentMakePayment);
        submitOTP = (Button) findViewById(R.id.submitOTPCode);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        /*
        * SORT CODE EDIT TEXT
        */
        paymentSortCode = (EditText) findViewById(R.id.paymentSortCode);
        paymentSortCode.addTextChangedListener(new TextWatcher()  {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int length = s.length();
                if (length == 0) {
                    paymentSortCode.setError("Please enter a sort code");
                }
                else if(length == 6){
                    paymentSortCode.setError(null);
                }
                else {
                    paymentSortCode.setError("Sort code must have 6 digits");
                }

                //enable the submit button if possible
                submitPayment.setEnabled(
                        enableSubmit(paymentSortCode, paymentAccountNumber, paymentAmount));
            }
        });

        /*
        * ACCOUNT NUMBER EDIT TEXT
        */
        paymentAccountNumber = (EditText) findViewById(R.id.paymentAccountNumber);
        paymentAccountNumber.addTextChangedListener(new TextWatcher()  {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int length = s.length();
                if (length == 0) {
                    paymentAccountNumber.setError("Please enter an account number");
                }
                else if(length == 8){
                    paymentAccountNumber.setError(null);
                }
                else {
                    paymentAccountNumber.setError("Account number must have 8 digits");
                }

                //enable the submit button if possible
                submitPayment.setEnabled(
                        enableSubmit(paymentSortCode, paymentAccountNumber, paymentAmount));
            }
        });

        /*
        * PAYMENT REFERENCE EDIT TEXT
        */
        paymentReference = (EditText) findViewById(R.id.paymentReference);

        /*
        * PAYMENT AMOUNT EDIT TEXT
        */
        paymentAmount = (EditText) findViewById(R.id.paymentAmount);
        paymentAmount.addTextChangedListener(new TextWatcher()  {
            private String current = "";
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    paymentAmount.removeTextChangedListener(this);

                    String replaceable = String.format("[%s,.\\s]",
                            NumberFormat.getCurrencyInstance().getCurrency().getSymbol());
                    String cleanString = s.toString().replaceAll(replaceable, "");

                    double parsed;
                    try {
                        parsed = Double.parseDouble(cleanString);
                    } catch (NumberFormatException e) {
                        parsed = 0.00;
                    }
                    String formatted = NumberFormat.getCurrencyInstance().format((parsed/100));

                    current = formatted;
                    paymentAmount.setText(formatted);
                    paymentAmount.setSelection(formatted.length());
                    paymentAmount.addTextChangedListener(this);
                }
                Double amt = Double.parseDouble(paymentAmount.getText().
                        toString().replaceAll("[£,]", ""));
                if(amt == 0.00){
                    paymentAmount.setError("Please enter an amount greater than £0.00");
                }
                else if(amt > accountBal){
                    paymentAmount.setError("Please enter an amount less than your" +
                            " available balance");
                }
                else{
                    paymentAmount.setError(null);
                }

                //enable the submit button if possible
                submitPayment.setEnabled(
                        enableSubmit(paymentSortCode, paymentAccountNumber, paymentAmount));
            }
        });

        /*
        * OTP CODE EDIT TEXT
        */
        OTPCodeField = (EditText) findViewById(R.id.paymentOTPCode);
        OTPCodeField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    OTPCodeField.setError("Enter OTP Code sent via SMS to continue");
                }
                else{
                    OTPCodeField.setError(null);
                }
                //enable the submit button if possible
                submitOTP.setEnabled(enableSubmit(OTPCodeField));
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            accountT = extras.getString(MainActivity.TAG_ACCOUNT_FRIENDLY_NAME);
            accountNo = extras.getString(MainActivity.TAG_ACCOUNT_NUMBER);
            sortCode = extras.getString(MainActivity.TAG_SORT_CODE);
            accountId = extras.getString(MainActivity.TAG_ID);
            accountBal = extras.getDouble(MainActivity.TAG_ACCOUNT_BALANCE);

            //update the TextViews and ImageView data
            accountType.setText(accountT);
            String accountD = accountNo + "   |   " + AccountAdapter.formatSortCode(sortCode);
            accountDetails.setText(accountD);
            String accountB = "£" + AccountAdapter.formatBalance(accountBal);
            accountBalance.setText(accountB);
            //try refactoring using the image view id instead
            AccountAdapter.pickAccountImage(accountT, imageView);
        }

        //Attempt at animating the header account information when "From" appears
        if (savedInstanceState == null) {
            ViewTreeObserver observer = paymentLayout.getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    paymentLayout.getViewTreeObserver().removeOnPreDrawListener(this);

                    LayoutTransition transition = paymentAccountHeader.getLayoutTransition();
                    transition.enableTransitionType(LayoutTransition.CHANGING);
                    paymentFrom.setVisibility(View.VISIBLE);

                    return true;
                }
            });
        }
    }

    public void submitPayment(View v){
        final String sc = paymentSortCode.getText().toString();
        final String an = paymentAccountNumber.getText().toString();
        final String pr = paymentReference.getText().toString();
        //remove £ symbol and commas from number
        final String pa = paymentAmount.getText().toString().replaceAll("[£,]", "");

        //parse the data collected into JSON
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params){
                try {
                    response = ApiCall.POST(
                            buildURL(accountId), paymentJson(sc, an, pr, pa));
                }catch(IOException e){
                    e.printStackTrace();
                }
                return null;
            }

            protected void onPostExecute(Void result){
                super.onPostExecute(result);
                progressBar.setVisibility(View.GONE);

                String paymentStatus = "",
                        toAccNo = "",
                        toSortCode = "",
                        payRef = "",
                        payAmt = "";
                JSONObject json;
                try {
                    json = new JSONObject(response.body().string());
                    paymentStatus = json.getString(TAG_PAYMENT_STATUS);
                    paymentId = json.getString(TAG_PAYMENT_ID);
                    toAccNo = json.getString(TAG_TO_ACCOUNT_NUMBER);
                    toSortCode = json.getString(TAG_TO_SORT_CODE);
                    payRef = json.getString(TAG_PAYMENT_REFERENCE);
                    payAmt = json.getString(TAG_PAYMENT_AMOUNT);
                }
                catch(Exception e){
                    e.printStackTrace();
                }
                if(paymentStatus.equals(PENDING)){
                    paymentComplete(toAccNo, toSortCode, payRef, payAmt);
                }
                else if(paymentStatus.equals(TWO_FA)){
                    // remove the payment information fields and show the OTP field
                    // send the OTP code in a PATCH request
                    paymentRelativeForm.setVisibility(View.GONE);
                    paymentRelativeOTP.setVisibility(View.VISIBLE);
                }
            }
        }.execute();
    }

    public void submitOTPCode(View v) {
        new AsyncTask<Void, Void, Void>() {
            protected void onPreExecute(){
                OTPCode = OTPCodeField.getText().toString();
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    response = ApiCall.PATCH(
                            paymentURL(accountId, paymentId), OTPJson(OTPCode));
                }
                catch(IOException e){
                    Toast.makeText(PaymentActivity.this,
                            "Could not connect to host", Toast.LENGTH_LONG).show();
                }
                return null;
            }

            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                progressBar.setVisibility(View.GONE);
                JSONObject json = null;

                try{
                    json = new JSONObject(response.body().string());
                    if(json.getString(TAG_PAYMENT_STATUS).equals("Pending")){
                        paymentComplete(json.getString(TAG_TO_ACCOUNT_NUMBER),
                                json.getString(TAG_TO_SORT_CODE),
                                json.getString(TAG_PAYMENT_REFERENCE),
                                json.getString(TAG_PAYMENT_AMOUNT));
                    }
                }
                catch (Exception e) {
                    try {
                        String otpErr = json.getString(TAG_OTP_ERROR);
                        Toast.makeText(PaymentActivity.this, otpErr, Toast.LENGTH_LONG).show();
                    } catch (JSONException je) {
                        Log.d(TAG, "ERROR: Status code isn't 200 or 401");
                    }
                }
            }
        }.execute();
    }

    private void paymentComplete(String an, String sc, String pr, String am){
        //confirmation message
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Payment Complete\n" +
                "Account Number: " + an + "\n" +
                "Sort Code: " + sc + "\n" +
                "Payment Reference: " + pr + "\n" +
                "Amount: £" + am);
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public String paymentJson(String sc, String an, String pr, String pa){
        return "{" +
                "\"" + TAG_TO_ACCOUNT_NUMBER + "\":\"" + an + "\"," +
                "\"" + TAG_TO_SORT_CODE + "\":\"" + sc + "\"," +
                "\"" + TAG_PAYMENT_REFERENCE + "\":\"" + pr + "\"," +
                "\"" + TAG_PAYMENT_AMOUNT + "\":" + pa +
                "}";
    }

    public String OTPJson(String o){
        return "{" +
                "\"" + TAG_OTP_CODE + "\":\"" + o + "\"" +
                "}";
    }

    //checks all the EditText fields
    public boolean enableSubmit(EditText... editTexts){
        for(EditText values:editTexts){
            if(values.getError() != null){
                return false;
            }
            if(values.getText().toString().length() == 0){
                return false;
            }
        }
        return true;
    }

    public static HttpUrl buildURL(String accId){
        return new HttpUrl.Builder()
                .scheme(MainActivity.HTTPS)
                .host(MainActivity.BLUE_URI)
                .addPathSegment(MainActivity.BLUE_API)
                .addPathSegment(MainActivity.BLUE_VERSION)
                .addPathSegment(MainActivity.ACCOUNTS)
                .addPathSegment(accId)
                .addPathSegment(PAYMENTS)
                .build();
    }

    public static HttpUrl paymentURL(String accountId, String paymentId){
        return new HttpUrl.Builder()
                .scheme(MainActivity.HTTPS)
                .host(MainActivity.BLUE_URI)
                .addPathSegment(MainActivity.BLUE_API)
                .addPathSegment(MainActivity.BLUE_VERSION)
                .addPathSegment(MainActivity.ACCOUNTS)
                .addPathSegment(accountId)
                .addPathSegment(PAYMENTS)
                .addPathSegment(paymentId)
                .build();
    }
}

/*  int responseCode = response.code();
    switch (responseCode){
        case 200: Log.d("Response", "OK");
            break;
        case 400: Log.d("Response", "Bad request");
            break;
        case 401: Log.d("Response", "Unauthorised - you are not authenticated");
            String paymentStatus = "";
            try {
                JSONObject json = new JSONObject(response.body().string());
                paymentStatus = json.getString(TAG_PAYMENT_STATUS);
            }
            catch(Exception e){
                e.printStackTrace();
            }
            if(paymentStatus.equals("2FA required")){
                String mobilePhone = "";
                try {
                    JSONObject json = new JSONObject(ApiCall.GET(customerURL(custId)));
                    //switch to TAG_MOBILE_PHONE
                    mobilePhone = json.getString("address2");
                    Log.d("Mobile Phone", mobilePhone);
                }catch(Exception e){
                    e.printStackTrace();
                }
                //put an if statement to see if they have a mobile phone or not
            }
            else{
                Log.d("Response", "Unauthorised - you are not authenticated");
            }
            break;
        case 403: Log.d("Response", "Forbidden - you do not have" +
                " access to this resource");
            break;
        case 404: Log.d("Response", "Not found - the resource does not exist");
            break;
        case 500: Log.d("Response", "Server error - problem at our end");
            break;
        default: break;
    }*/

/*try {
                    String mobilePhone = "";
                    try {
                        JSONObject json = new JSONObject(ApiCall.GET(customerURL(custId)));
                        //switch to TAG_MOBILE_PHONE
                        mobilePhone = json.getString(TAG_MOBILE_PHONE); //change to address2
                        Log.d("Response", mobilePhone);
                    } catch (JSONException e) {
                        //no mobile phone found
                        //assume the ApiCall.GET() works fine
                        if(e.getMessage().equals("No value for " + TAG_MOBILE_PHONE)){ //change to address2
                            //cancel the payment
                        }
                        else{
                            // remove the payment information fields and show the OTP field
                            // send the OTP code in a PATCH request
                            paymentLayout.setVisibility("gone");
                        }
                    }
                    Log.d("Response", mobilePhone);
                    //put an if statement to see if they have a mobile phone or not
                }
                catch(Exception e) {
                    e.printStackTrace();
                }*/