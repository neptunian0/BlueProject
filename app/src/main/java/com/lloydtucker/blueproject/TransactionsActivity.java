package com.lloydtucker.blueproject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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

public class TransactionsActivity extends AppCompatActivity {
    private static final String TAG = TransactionsActivity.class.getSimpleName();
    private ListView listView;
    private ArrayAdapter<Transactions> adapter;
    private TextView accountBalance, accountDetails, accountType;
    private ImageView imageView;
    private String custId, response, accountId, accountT, accountNo, sortCode;
    private double accountBal;
    private boolean transactions_received;
    private ArrayList<Transactions> transactions;
    private Transactions[] transArr;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        listView = (ListView) findViewById(R.id.transactionList);
        imageView = (ImageView) findViewById(R.id.transactionImage);
        accountType = (TextView) findViewById(R.id.transactionAccountType);
        accountDetails = (TextView) findViewById(R.id.transactionAccountDetails);
        accountBalance = (TextView) findViewById(R.id.transactionAccountBalance);

        //Unpack the bundle
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            accountT = extras.getString(MainActivity.TAG_ACCOUNT_TYPE);
            accountNo = extras.getString(MainActivity.TAG_ACCOUNT_NUMBER);
            sortCode = extras.getString(MainActivity.TAG_SORT_CODE);
            accountId = extras.getString(MainActivity.TAG_ID);
            accountBal = extras.getDouble(MainActivity.TAG_ACCOUNT_BALANCE);
            custId = extras.getString(MainActivity.TAG_CUSTOMER_ID);

            //update the TextViews and ImageView
            accountType.setText(accountT);
            accountDetails.setText(accountNo + "   |   "
                    + AccountAdapter.formatSortCode(sortCode));
            accountBalance.setText("£" + AccountAdapter.formatBalance(accountBal));
            AccountAdapter.pickAccountImage(accountT, imageView);
        }
        //Make the API call
        //clear the old data
        transactions_received = false;
        loadContent();
        //Wait for the contacts to be loaded into the array
        while(!transactions_received) {
        }
        //convert ArrayList of Transactions to an Array
        transArr = transactions.toArray(
                new Transactions[transactions.size()]);
        adapter = new TransactionsAdapter(this,
                transArr);
        listView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed(){
        finish();
    }

    public void loadContent(){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params){
                try {
                    response = ApiCall.GET(buildURL(accountId));
                    //Parse the response string here
                    if (response != null) {
                        try {
                            JSONArray jsonArr = new JSONArray(response);
                            transactions = new ArrayList<>();

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
                                //Log.d("Response", "" + transactions.get(i));
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
                .addQueryParameter("sortOrder", "-transactionDateTime")
                .build();
        return url;
    }

    public void makePayment(View v){
        Log.d("Click", "You clicked Make Payment");
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(MainActivity.TAG_ID, accountId);
        intent.putExtra(MainActivity.TAG_ACCOUNT_TYPE, accountT);
        intent.putExtra(MainActivity.TAG_ACCOUNT_NUMBER, accountNo);
        intent.putExtra(MainActivity.TAG_SORT_CODE, sortCode);
        intent.putExtra(MainActivity.TAG_ACCOUNT_BALANCE, accountBal);
        intent.putExtra(MainActivity.TAG_CUSTOMER_ID, custId);
        startActivity(intent);
    }
}
