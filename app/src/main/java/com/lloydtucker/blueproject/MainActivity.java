package com.lloydtucker.blueproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.HttpUrl;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    static final String HTTPS = "https";
    static final String BLUE_URI = "bluebank.azure-api.net";
    static final String GREEN_URI = "greenbank.azure-api.net";
    static final String BLUE_API = "api";
    static final String GREEN_PCA = "PCA";
    static final String BLUE_VERSION = "v0.6.3";
    static final String ACCOUNTS = "accounts";

    private static final String TAG = MainActivity.class.getSimpleName();
    static float sAnimatorScale = 2;
    //public final static String EXTRA_MESSAGE = "com.lloydtucker.testproject.CONTACTS";
    private ListView listView;
    private TextView greetingView, greetingDateView;
    private ProgressBar mainProgressBar;
    private ArrayAdapter<Accounts> adapter;
    private RelativeLayout greetingHeader;
    String response;

    private static String loadingCustomer = "Loading your data...";
    private static String loadingAccount = "Loading your account data...";
    static String takingLonger = "This is taking longer than expected...";

    /*
     * CUSTOMER TAGS
     */
    static final String TAG_ID = "id";
    private static final String TAG_GIVEN_NAME = "givenName";
    private static final String TAG_FAMILY_NAME = "familyName";
    private static final String TAG_ADDRESS_1 = "address1";
    private static final String TAG_ADDRESS_2 = "address2";
    private static final String TAG_TOWN = "town";
    private static final String TAG_COUNTY = "county";
    private static final String TAG_POST_CODE = "postCode";
    private static final String TAG_MOBILE_PHONE = "mobilePhone";

    /*
     * ACCOUNT TAGS
     */
    static final String TAG_CUSTOMER_ID = "customerId";
    static final String TAG_SORT_CODE = "sortCode";
    static final String TAG_ACCOUNT_NUMBER = "accountNumber";
    static final String TAG_ACCOUNT_TYPE = "accountType";
    static final String TAG_ACCOUNT_FRIENDLY_NAME = "accountFriendlyName";
    static final String TAG_ACCOUNT_BALANCE = "accountBalance";
    private static final String TAG_ACCOUNT_CURRENCY = "accountCurrency";

    /*
     * TRANSACTION TAGS
     */
    static final String TAG_TRANSACTION_DATE = "transactionDateTime";
    static final String TAG_TRANSACTION_DESCRIPTION = "transactionDescription";
    static final String TAG_TRANSACTION_AMOUNT = "transactionAmount";
    static final String TAG_TRANSACTION_CURRENCY = "transactionCurrency";

    private static final String TAG_CUSTOMERS = "customers";
    private static final String TAG_ACCOUNTS = "accounts";
    private static final String TAG_TRANSACTIONS = "transactions";

    /*
    * ANIMATION TAGS
    */
    static final String TAG_ACCOUNT_TOP = "accountTop";
    static final String TAG_ACCOUNT_LEFT = "accountLeft";

    private static int timeoutTries = 0;
    private ProgressDialog mMainProgressDialog;

    //Contacts array
    Customers[] customers = new Customers[1];
    Accounts[] accounts = new Accounts[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.accounts);
        listView.setOnItemClickListener(this); //clickable account items
        greetingView = (TextView) findViewById(R.id.greeting);
        greetingDateView = (TextView) findViewById(R.id.greetDate);
        greetingHeader = (RelativeLayout) findViewById(R.id.greetingHeader);
        mMainProgressDialog = new ProgressDialog(this);

        //Make the API call
        getGreenCustomers();
    }

    private void getGreenCustomers() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mMainProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                mMainProgressDialog.setMessage(loadingCustomer);
                mMainProgressDialog.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    response = GreenApiCall.GET(buildGreenURL());
                }
                //if timed out, try again at most 3 times
                catch(SocketTimeoutException e){
                    Log.d(TAG, "ERROR: SocketTimeout");
                    if(timeoutTries < 3) {
                        timeoutTries++;
                        mMainProgressDialog.setMessage(loadingCustomer + " " + takingLonger);
                        getGreenCustomers();
                    }
                    else{
                        networkProblem(e, mMainProgressDialog, MainActivity.this);
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "ERROR: IOException");
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                timeoutTries = 0;//reset timeout tries counter
                //Parse the response string here
                try {
                    JSONArray jsonArr = new JSONArray(response);

                    // looping through All Contacts
                    //should be i < jsonArr.length(), but only want 10 customers
                    for (int i = 0; i < jsonArr.length() && i < 10; i++) {
                        JSONObject c = jsonArr.getJSONObject(i);
                        Customers cus = new Customers();

                        cus.setId(c.getString(TAG_ID));
                        cus.setGivenName(c.getString(TAG_GIVEN_NAME));
                        cus.setFamilyName(c.getString(TAG_FAMILY_NAME));
                        cus.setTown(c.getString(TAG_TOWN));
                        cus.setPostCode(c.getString(TAG_POST_CODE));

                        //assumes only one customer input from BlueBank
                        customers[0] = cus;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "ERROR: JSONException");
                }
                if (customers[0] != null) {
                    getGreeting(greetingView, greetingDateView);
                    greetingHeader.setAlpha(1);
                    getGreenAccounts();
                }
            }
        }.execute();
    }

    /*
    * Get Accounts
    */
    public void getGreenAccounts() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                String accountId = "";
                //NEED TO GET THE ACCOUNT ID FOR THE ACCOUNT
                if(accountIdNull()) {
                    try {
                        response = GreenApiCall.GET(buildGreenURL(customers[0].getId()));
                    }
                    //if timed out, try again at most 3 times
                    catch (SocketTimeoutException e) {
                        Log.d(TAG, "ERROR: SocketTimeout");
                        if (timeoutTries < 3) {
                            timeoutTries++;
                            mMainProgressDialog.setMessage(loadingAccount + " " + takingLonger);
                            getGreenAccounts();
                        } else {
                            networkProblem(e, mMainProgressDialog, MainActivity.this);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d(TAG, "ERROR: IOException");
                    }

                    //PARSE THE JSON TO GET THE ACCOUNT ID
                    try {
                        JSONArray jsonArr = new JSONArray(response);
                        JSONObject jsonObj = jsonArr.getJSONObject(0);//assumes only one account
                        accountId = jsonObj.getString(TAG_ID);
                        timeoutTries = 0; //reset timeout tries
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d(TAG, "ERROR: JSONException");
                    }
                }

                //ACQUIRE THE ADDITIONAL ACCOUNT DETAILS USING THE ACCOUNT ID
                try{
                    response = GreenApiCall.GET(buildGreenAccountURL(accountId));
                }
                //if timed out, try again at most 3 times
                catch(SocketTimeoutException e){
                    Log.d(TAG, "ERROR: SocketTimeout");
                    if(timeoutTries < 3) {
                        timeoutTries++;
                        mMainProgressDialog.setMessage(loadingAccount + " " + takingLonger);
                        getGreenAccounts();
                    }
                    else{
                        networkProblem(e, mMainProgressDialog, MainActivity.this);
                    }
                }
                catch(IOException e){
                    e.printStackTrace();
                    Log.d(TAG, "ERROR: IOException");
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                timeoutTries = 0;
                try {
                    JSONObject jsonObj = new JSONObject(response);
                    Accounts acc = new Accounts();

                    acc.setId(jsonObj.getString(TAG_ID));
                    acc.setSortCode(jsonObj.getString(TAG_SORT_CODE));
                    acc.setAccountNumber(jsonObj.getString(TAG_ACCOUNT_NUMBER));
                    acc.setAccountType(jsonObj.getString(TAG_ACCOUNT_TYPE));
                    acc.setAccountFriendlyName(jsonObj.getString(TAG_ACCOUNT_FRIENDLY_NAME));
                    acc.setAccountBalance(jsonObj.getDouble(TAG_ACCOUNT_BALANCE));
                    acc.setAccountCurrency(jsonObj.getString(TAG_ACCOUNT_CURRENCY));

                    accounts[0] = acc; //assumes two accounts from BlueBank

                    //render the loaded information
                    if(mMainProgressDialog.isShowing()){
                        mMainProgressDialog.dismiss();
                    }
                    adapter = new AccountAdapter(MainActivity.this, accounts);
                    listView.setAdapter(adapter);
                    listView.setAlpha(1);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "ERROR: JSONException");
                }
            }
        }.execute();
    }

    public static HttpUrl buildGreenURL() {
        return new HttpUrl.Builder()
                .scheme(HTTPS)
                .host(GREEN_URI)
                .addPathSegment(GREEN_PCA)
                .addPathSegment(TAG_CUSTOMERS)
                .build();
    }

    public static HttpUrl buildGreenURL(String s) {
        return new HttpUrl.Builder()
                .scheme(HTTPS)
                .host(GREEN_URI)
                .addPathSegment(GREEN_PCA)
                .addPathSegment(TAG_CUSTOMERS)
                .addPathSegment(s)
                .addPathSegment(TAG_ACCOUNTS)
                .build();
    }

    public static HttpUrl buildGreenAccountURL(String s) {
        return new HttpUrl.Builder()
                .scheme(HTTPS)
                .host(GREEN_URI)
                .addPathSegment(GREEN_PCA)
                .addPathSegment(TAG_ACCOUNTS)
                .addPathSegment(s)
                .build();
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnectedOrConnecting());
    }

    public void getGreeting(TextView g, TextView gD) {
        //set the greeting text to good morning, good afternoon,
        //or good evening <name> and the date
        String greeting = "";
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        if (timeOfDay >= 0 && timeOfDay < 12) {
            greeting += "Good morning, ";
        } else if (timeOfDay >= 12 && timeOfDay < 17) {
            greeting += "Good afternoon, ";
        } else if (timeOfDay >= 17 && timeOfDay < 24) {
            greeting += "Good evening, ";
        }
        greeting += customers[0].getGivenName();
        g.setText(greeting);

        //set the date at the bottom of the header greeting
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.UK);
        gD.setText(dateFormat.format(new Date()));
    }

    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        // Then you start a new Activity via Intent
        Intent intent = new Intent(MainActivity.this, TransactionsActivity.class);

        intent.putExtra(TAG_ID, accounts[position].getId());
        intent.putExtra(TAG_ACCOUNT_FRIENDLY_NAME, accounts[position].getAccountFriendlyName());
        intent.putExtra(TAG_ACCOUNT_NUMBER, accounts[position].getAccountNumber());
        intent.putExtra(TAG_SORT_CODE, accounts[position].getSortCode());
        intent.putExtra(TAG_ACCOUNT_BALANCE, accounts[position].getAccountBalance());
        intent.putExtra(TAG_CUSTOMER_ID, customers[0].getId());
        startActivity(intent);
    }

    public static void networkProblem(SocketTimeoutException exc, ProgressDialog pD, Context context){
        if (pD.isShowing()) {
            pD.dismiss();
        }
        SimpleAlertDialog.showAlertDialog(context,
                "Failed to get your data. Please close the app, ensure you have an internet " +
                        "connection, and try again.", exc.getMessage());
    }

    public boolean accountIdNull(){
        for(int i = 0; i < accounts.length; i++){
            if(accounts[i] == null || accounts[i].getId() == null){
                return true;
            }
        }
        return false;
    }
}
