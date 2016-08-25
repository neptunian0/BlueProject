package com.lloydtucker.blueproject;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.HttpUrl;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private static final String TAG = MainActivity.class.getSimpleName();
    static float sAnimatorScale = 2;
    //public final static String EXTRA_MESSAGE = "com.lloydtucker.testproject.CONTACTS";
    private ListView listView;
    private TextView greetingView,greetingDateView;
    private ArrayAdapter<Accounts> adapter;
    String response;

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

    private static final String TAG_CUSTOMERS = "customer";
    private static final String TAG_ACCOUNTS = "accounts";
    private static final String TAG_TRANSACTIONS = "transactions";

    /*
    * ANIMATION TAGS
    */
    static final String TAG_ACCOUNT_TOP = "accountTop";
    static final String TAG_ACCOUNT_LEFT = "accountLeft";

    //JSON Array
    //JSONArray contacts = new JSONArray();

    //Contacts array
    Customers[] customers = new Customers[1];
    Accounts[] accounts = new Accounts[2];
    public boolean contacts_retrieved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.list);
        listView.setOnItemClickListener(this); //clickable account items
        greetingView  = (TextView) findViewById(R.id.greeting);
        greetingDateView = (TextView) findViewById(R.id.greetDate);

        //Make the API call
        loadContent();
    }

    private void loadContent(){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params){
                try {
                    response = ApiCall.GET(buildURL());
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

                            customers[i] = cus;
                        }
                        response = null;

                        /*
                        * Get Accounts
                        */
                        if(customers[0] != null){
                            response = ApiCall.GET(buildURL(customers[0].getId()));
                            try {
                                jsonArr = new JSONArray(response);

                                // looping through All Accounts for this Customer
                                //should be i < jsonArr.length(), but only want 10 customers
                                for (int i = 0; i < jsonArr.length() && i < 10; i++) {
                                    JSONObject c = jsonArr.getJSONObject(i);
                                    Accounts acc = new Accounts();

                                    acc.setId(c.getString(TAG_ID));
                                    acc.setCustId(c.getString(TAG_CUSTOMER_ID));
                                    acc.setSortCode(c.getString(TAG_SORT_CODE));
                                    acc.setAccountNumber(c.getString(TAG_ACCOUNT_NUMBER));
                                    acc.setAccountType(c.getString(TAG_ACCOUNT_TYPE));
                                    acc.setAccountFriendlyName(c.getString(TAG_ACCOUNT_FRIENDLY_NAME));
                                    acc.setAccountBalance(c.getDouble(TAG_ACCOUNT_BALANCE));
                                    acc.setAccountCurrency(c.getString(TAG_ACCOUNT_CURRENCY));

                                    accounts[i] = acc;
                                }
                                response = null;
                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                                Log.d(TAG, "ERROR: JSONException");
                            }
                        }
                        contacts_retrieved = true;
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                        Log.d(TAG, "ERROR: JSONException");
                    }
                }catch(IOException e){
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                getGreeting(greetingView, greetingDateView);
                adapter = new AccountAdapter(MainActivity.this, accounts);
                listView.setAdapter(adapter);
            }
        }.execute();
    }

    public static HttpUrl buildURL(){
        return new HttpUrl.Builder()
                .scheme("https")
                .host("bluebank.azure-api.net")
                .addPathSegment("api")
                .addPathSegment("v0.6.3")
                .addPathSegment("customers")
                .build();
    }

    public static HttpUrl buildURL(String s){
        return new HttpUrl.Builder()
                .scheme("https")
                .host("bluebank.azure-api.net")
                .addPathSegment("api")
                .addPathSegment("v0.6.3")
                .addPathSegment("customers")
                .addPathSegment(s)
                .addPathSegment("accounts")
                .build();
    }

    public boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnectedOrConnecting());
    }

    public void getGreeting(TextView g, TextView gD){
        //set the greeting text to good morning, good afternoon,
        //or good evening <name> and the date
        String greeting = "";
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        if(timeOfDay >= 0 && timeOfDay < 12){
            greeting += "Good morning, ";
        }else if(timeOfDay >= 12 && timeOfDay < 17){
            greeting += "Good afternoon, ";
        }else if(timeOfDay >= 17 && timeOfDay < 24){
            greeting += "Good evening, ";
        }
        greeting += customers[0].getGivenName();
        g.setText(greeting);

        //set the date at the bottom of the header greeting
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.UK);
        gD.setText(dateFormat.format(new Date()));
    }

    public void onItemClick(AdapterView<?> l, View v, int position, long id){
        // Then you start a new Activity via Intent
        Intent intent = new Intent(MainActivity.this, TransactionsActivity.class);
        int[] screenLocation = new int[2];
        v.getLocationOnScreen(screenLocation);

        intent.putExtra(TAG_ID, accounts[position].getId());
        intent.putExtra(TAG_ACCOUNT_TYPE, accounts[position].getAccountType());
        intent.putExtra(TAG_ACCOUNT_NUMBER, accounts[position].getAccountNumber());
        intent.putExtra(TAG_SORT_CODE, accounts[position].getSortCode());
        intent.putExtra(TAG_ACCOUNT_BALANCE, accounts[position].getAccountBalance());
        intent.putExtra(TAG_CUSTOMER_ID, customers[0].getId());
        intent.putExtra(TAG_ACCOUNT_LEFT, screenLocation[0]);
        intent.putExtra(TAG_ACCOUNT_TOP, screenLocation[1]);
        startActivity(intent);

        //Don't want normal window animations to take place
        overridePendingTransition(0, 0);
    }
}
