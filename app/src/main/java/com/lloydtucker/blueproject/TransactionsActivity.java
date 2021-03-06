package com.lloydtucker.blueproject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.HttpUrl;

public class TransactionsActivity extends Activity {
    private static final String TAG = TransactionsActivity.class.getSimpleName();
    static final String TRANSACTIONS = "transactions";
    static final String SORT_ORDER = "sortOrder";
    static final String TRANSACTION_DATE_TIME_DESC = "-transactionDateTime";


    private ListView transactionsList;
    private ArrayAdapter<Transactions> adapter;
    private TextView accountBalance, accountDetails, accountType;
    private ImageView imageView;
    private RelativeLayout transactionAccount;
    private Button paymentButton;
    private String custId, response, accountId, accountT, accountNo, sortCode;
    private double accountBal;
    private ArrayList<Transactions> transactions;
    private Transactions[] transArr;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        transactionAccount = (RelativeLayout) findViewById(R.id.transactionAccount);
        imageView = (ImageView) transactionAccount.findViewById(R.id.account_image);
        accountType = (TextView) transactionAccount.findViewById(R.id.account_type);
        accountDetails = (TextView) transactionAccount.findViewById(R.id.account_details);
        accountBalance = (TextView) transactionAccount.findViewById(R.id.account_balance);

        //Other three items in the activity
        transactionsList = (ListView) findViewById(R.id.transactionList);
        paymentButton = (Button) findViewById(R.id.paymentButton);
        RelativeLayout transactionsLayout = (RelativeLayout)
                findViewById(R.id.transactionsLayout);

        //Unpack the bundle
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            accountT = extras.getString(MainActivity.TAG_ACCOUNT_FRIENDLY_NAME);
            accountNo = extras.getString(MainActivity.TAG_ACCOUNT_NUMBER);
            sortCode = extras.getString(MainActivity.TAG_SORT_CODE);
            accountId = extras.getString(MainActivity.TAG_ID);
            accountBal = extras.getDouble(MainActivity.TAG_ACCOUNT_BALANCE);
            custId = extras.getString(MainActivity.TAG_CUSTOMER_ID);
            accountBal = extras.getDouble(MainActivity.TAG_ACCOUNT_BALANCE);
            custId = extras.getString(MainActivity.TAG_CUSTOMER_ID);
        }
        //Make the API call
        //clear the old data
        loadContent();

        //update the TextViews and ImageView data
        accountType.setText(accountT);
        String details = accountNo + "   |   "
                + AccountAdapter.formatSortCode(sortCode);
        accountDetails.setText(details);
        String balance = "£" + AccountAdapter.formatBalance(accountBal);
        accountBalance.setText(balance);
        //try refactoring to include the resource ID in the intent
        AccountAdapter.pickAccountImage(accountT, imageView);
    }

    public void loadContent(){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params){
                try {
                    response = ApiCall.GET(buildURL(accountId));
                }catch(IOException e){
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                //Parse the response string here
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
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "ERROR: JSONException");
                }

                //convert ArrayList of Transactions to an Array
                transArr = transactions.toArray(
                        new Transactions[transactions.size()]);
                adapter = new TransactionsAdapter(TransactionsActivity.this, transArr);
                transactionsList.setAdapter(adapter);
            }
        }.execute();
    }

    public static HttpUrl buildURL(String accId){
        return new HttpUrl.Builder()
                .scheme(MainActivity.HTTPS)
                .host(MainActivity.BLUE_URI)
                .addPathSegment(MainActivity.BLUE_API)
                .addPathSegment(MainActivity.BLUE_VERSION)
                .addPathSegment(MainActivity.ACCOUNTS)
                .addPathSegment(accId)
                .addPathSegment(TRANSACTIONS)
                .addQueryParameter(SORT_ORDER, TRANSACTION_DATE_TIME_DESC)
                .build();
    }

    public void makePayment(View v){
        Intent intent = new Intent(TransactionsActivity.this, PaymentActivity.class);
        intent.putExtra(MainActivity.TAG_ID, accountId);
        intent.putExtra(MainActivity.TAG_ACCOUNT_FRIENDLY_NAME, accountT);
        intent.putExtra(MainActivity.TAG_ACCOUNT_NUMBER, accountNo);
        intent.putExtra(MainActivity.TAG_SORT_CODE, sortCode);
        intent.putExtra(MainActivity.TAG_ACCOUNT_BALANCE, accountBal);
        intent.putExtra(MainActivity.TAG_CUSTOMER_ID, custId);
        startActivity(intent);
    }
}
