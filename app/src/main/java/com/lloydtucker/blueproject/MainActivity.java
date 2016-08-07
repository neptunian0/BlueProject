package com.lloydtucker.blueproject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    //public final static String EXTRA_MESSAGE = "com.lloydtucker.testproject.CONTACTS";
    private ListView listView;
    private ArrayAdapter<Accounts> adapter;
    OkHttpClient client;
    String response;
    String bearer;

    //JSON Node Names
    private static final String TAG_ID = "id";
    private static final String TAG_GIVEN_NAME = "givenName";
    private static final String TAG_FAMILY_NAME = "familyName";
    private static final String TAG_ADDRESS_1 = "address1";
    private static final String TAG_ADDRESS_2 = "address2";
    private static final String TAG_TOWN = "town";
    private static final String TAG_COUNTY = "county";
    private static final String TAG_POST_CODE = "postCode";
    private static final String TAG_MOBILE_PHONE = "mobilePhone";

    private static final String TAG_CUSTOMER_ID = "customerId";
    private static final String TAG_SORT_CODE = "sortCode";
    private static final String TAG_ACCOUNT_NUMBER = "accountNumber";
    private static final String TAG_ACCOUNT_TYPE = "accountType";
    private static final String TAG_ACCOUNT_FRIENDLY_NAME = "accountFriendlyName";
    private static final String TAG_ACCOUNT_BALANCE = "accountBalance";
    private static final String TAG_ACCOUNT_CURRENCY = "accountCurrency";

    private static final String TAG_BEARER = "bearer";

    private static final String TAG_CUSTOMERS = "customer";
    private static final String TAG_ACCOUNTS = "accounts";
    private static final String TAG_TRANSACTIONS = "transactions";

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
        TextView greetingView = (TextView) findViewById(R.id.greeting);
        TextView greetingDateView = (TextView) findViewById(R.id.greetDate);
        client = new OkHttpClient();

        //Make the API call
        getBearer();
        loadContent();
        //Wait for the contacts to be loaded into the array
        while(!contacts_retrieved) {
        }
        getGreeting(greetingView, greetingDateView);

        adapter = new AccountAdapter(this,
                accounts);
        listView.setAdapter(adapter);
    }

    private void loadContent(){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params){
                try {
                    while(bearer == null){
                    }
                    response = ApiCall.GET(client, buildURL(), bearer);
                    Log.d("Response", "" + response);
                    //Parse the response string here
                    if (response != null) {
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
                                Log.d("Response", "" + customers[i]);
                            }
                            response = null;

                            /*
                            * Get Accounts
                            */
                            if(customers[0] != null){
                                response = ApiCall.GET(client, buildURL(customers[0].getId()), bearer);
                                Log.d("Response", "" + response);
                                if(response != null){
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
                                            Log.d("Response", "" + accounts[i]);
                                        }
                                        response = null;
                                    }
                                    catch (JSONException e) {
                                        e.printStackTrace();
                                        Log.d(TAG, "ERROR: JSONException");
                                    }
                                }
                            }
                            contacts_retrieved = true;
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "ERROR: JSONException");
                        }
                    }
                }catch(IOException e){
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    private void getBearer(){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params){
                try {
                    response = ApiCall.GET_BEARER(client, buildBearerURL());
                    Log.d("Bearer", "" + response);
                    //Parse the response string here
                    if (response != null) {
                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            response = null;
                            bearer = jsonObj.getString(TAG_BEARER);
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "ERROR: JSONException");
                        }
                    }
                }catch(IOException e){
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    public static HttpUrl buildURL(){
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("bluebank.azure-api.net")
                .addPathSegment("api")
                .addPathSegment("v0.6.3")
                .addPathSegment("customers")
                .build();
        return url;
    }

    public static HttpUrl buildURL(String s){
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("bluebank.azure-api.net")
                .addPathSegment("api")
                .addPathSegment("v0.6.3")
                .addPathSegment("customers")
                .addPathSegment(s)
                .addPathSegment("accounts")
                .build();
        return url;
    }

    public static HttpUrl buildBearerURL(){
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("cloudlevel.io")
                .addPathSegment("token")
                .build();
        return url;
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
        }else if(timeOfDay >= 12 && timeOfDay < 16){
            greeting += "Good afternoon, ";
        }else if(timeOfDay >= 16 && timeOfDay < 21){
            greeting += "Good evening, ";
        }else if(timeOfDay >= 21 && timeOfDay < 24){
            greeting += "Good night, ";
        }
        g.setText(greeting + customers[0].getGivenName());

        //set the date at the bottom of the header greeting
        DateFormat dateFormat = new SimpleDateFormat("dd MMMMM yyyy");
        Date date = new Date();
        gD.setText(dateFormat.format(date));
    }
}