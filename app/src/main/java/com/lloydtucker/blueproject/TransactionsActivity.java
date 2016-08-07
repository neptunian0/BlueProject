package com.lloydtucker.blueproject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class TransactionsActivity extends AppCompatActivity {
    private static final String TAG = TransactionsActivity.class.getSimpleName();
    private ListView listView;
    private ArrayAdapter<Transactions> adapter;
    private TextView accountBalance, accountDetails, accountType;
    private ImageView imageView;
    OkHttpClient client;
    String response;
    String bearer;
    String accountId;
    boolean transactions_received = false;
    ArrayList<Transactions> transactions;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        listView = (ListView) findViewById(R.id.transactionList);
        imageView = (ImageView) findViewById(R.id.transactionImage);
        accountType = (TextView) findViewById(R.id.transactionAccountType);
        accountDetails = (TextView) findViewById(R.id.transactionAccountDetails);
        accountBalance = (TextView) findViewById(R.id.transactionAccountBalance);
        client = new OkHttpClient();

        //Unpack the bundle
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String accountT = extras.getString(MainActivity.TAG_ACCOUNT_TYPE);
            String accountNo = extras.getString(MainActivity.TAG_ACCOUNT_NUMBER);
            String sortCode = extras.getString(MainActivity.TAG_SORT_CODE);
            accountId = extras.getString(MainActivity.TAG_ID);
            Double accountBal = extras.getDouble(MainActivity.TAG_ACCOUNT_BALANCE);
            bearer = extras.getString(MainActivity.TAG_BEARER);

            //update the TextViews and ImageView
            accountType.setText(accountT);
            accountDetails.setText(accountNo + "   |   "
                    + AccountAdapter.formatSortCode(sortCode));
            accountBalance.setText(AccountAdapter.formatBalance(accountBal));
            AccountAdapter.pickAccountImage(accountT, imageView);
        }
        //Make the API call
        loadContent();
        //Wait for the contacts to be loaded into the array
        while(!transactions_received) {
        }
        //convert ArrayList of Transactions to an Array
        Transactions[] transArr = transactions.toArray(
                new Transactions[transactions.size()]);
        /*adapter = new TransactionsAdapter(this,
                transArr);
        listView.setAdapter(adapter);//*/
    }

    public void loadContent(){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params){
                try {
                    while(bearer == null){
                    }
                    response = ApiCall.GET(client, buildURL(accountId), bearer);
                    Log.d("Response", "" + response);
                    //Parse the response string here
                    if (response != null) {
                        try {
                            JSONArray jsonArr = new JSONArray(response);
                            transactions = new ArrayList<Transactions>();

                            // looping through All Contacts
                            //should be i < jsonArr.length(), but only want 10 customers
                            for (int i = 0; i < jsonArr.length(); i++) {
                                JSONObject c = jsonArr.getJSONObject(i);
                                Transactions tra = new Transactions();

                                tra.setId(c.getString(MainActivity.TAG_ID));
                                tra.setTransactionDateTime(c.getString(
                                        MainActivity.TAG_TRANSACTION_DATE));
                                tra.setTransactionDescription(c.getString(
                                        MainActivity.TAG_TRANSACTION_DESCRIPTION));
                                tra.setTransactionAmount(c.getDouble(
                                        MainActivity.TAG_TRANSACTION_AMOUNT));

                                transactions.add(tra);
                                Log.d("Response", "" + transactions.get(i));
                            }
                            transactions_received = true;
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

    public static HttpUrl buildURL(String accId){
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("bluebank.azure-api.net")
                .addPathSegment("api")
                .addPathSegment("v0.6.3")
                .addPathSegment("accounts")
                .addPathSegment(accId)
                .addPathSegment("transactions")
                .build();
        return url;
    }
}