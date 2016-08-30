package com.lloydtucker.blueproject;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private static final TimeInterpolator sDecelerator = new DecelerateInterpolator();
    private static final TimeInterpolator sAccelerator = new AccelerateInterpolator();
    private static final String TAG = TransactionsActivity.class.getSimpleName();
    private static final int ANIM_DURATION = 500;
    static final long duration = (long) (ANIM_DURATION * MainActivity.sAnimatorScale);

    private ListView listView;
    private ArrayAdapter<Transactions> adapter;
    private TextView accountBalance, accountDetails, accountType;
    private ImageView imageView;
    private RelativeLayout transactionAccount;
    private Button paymentButton;
    private String custId, response, accountId, accountT, accountNo, sortCode;
    private double accountBal;
    private int oldAccountTop, oldAccountLeft, accountDeltaTop, accountDeltaLeft;
    private ArrayList<Transactions> transactions;
    private Transactions[] transArr;
    private ColorDrawable transactionsBackground;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        listView = (ListView) findViewById(R.id.transactionList);
        imageView = (ImageView) findViewById(R.id.transactionImage);
        accountType = (TextView) findViewById(R.id.transactionAccountType);
        accountDetails = (TextView) findViewById(R.id.transactionAccountDetails);
        accountBalance = (TextView) findViewById(R.id.transactionAccountBalance);
        transactionAccount = (RelativeLayout) findViewById(R.id.transactionAccount);
        paymentButton = (Button) findViewById(R.id.paymentButton);
        LinearLayout transactionsLayout = (LinearLayout) findViewById(R.id.transactionsLayout);

        //Unpack the bundle
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            accountT = extras.getString(MainActivity.TAG_ACCOUNT_TYPE);
            accountNo = extras.getString(MainActivity.TAG_ACCOUNT_NUMBER);
            sortCode = extras.getString(MainActivity.TAG_SORT_CODE);
            accountId = extras.getString(MainActivity.TAG_ID);
            accountBal = extras.getDouble(MainActivity.TAG_ACCOUNT_BALANCE);
            custId = extras.getString(MainActivity.TAG_CUSTOMER_ID);
            accountBal = extras.getDouble(MainActivity.TAG_ACCOUNT_BALANCE);
            custId = extras.getString(MainActivity.TAG_CUSTOMER_ID);
            oldAccountTop = extras.getInt(MainActivity.TAG_ACCOUNT_TOP);
            oldAccountLeft = extras.getInt(MainActivity.TAG_ACCOUNT_LEFT);
        }
        //Make the API call
        //clear the old data
        loadContent();

        transactionsBackground = new ColorDrawable(Color.WHITE);
        transactionsLayout.setBackground(transactionsBackground);

        //main thread continues
        if(savedInstanceState == null){
            ViewTreeObserver observer = transactionAccount.getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

                @Override
                public boolean onPreDraw() {
                    transactionAccount.getViewTreeObserver().removeOnPreDrawListener(this);

                    // Figure out where the account old/new versions are, relative
                    // to the screen and each other
                    int[] screenLocation = new int[2];
                    transactionAccount.getLocationOnScreen(screenLocation);
                    accountDeltaLeft = oldAccountLeft - screenLocation[0];
                    accountDeltaTop = oldAccountTop - screenLocation[1];

                    //update the TextViews and ImageView data
                    accountType.setText(accountT);
                    String details = accountNo + "   |   "
                            + AccountAdapter.formatSortCode(sortCode);
                    accountDetails.setText(details);
                    String balance = "£" + AccountAdapter.formatBalance(accountBal);
                    accountBalance.setText(balance);
                    //try refactoring to include the resource ID in the intent
                    AccountAdapter.pickAccountImage(accountT, imageView);

                    runEnterAnimation();

                    return true;
                }
            });
        }
    }

    /**
     * The enter animation scales the picture in from its previous thumbnail
     * size/location, colorizing it in parallel. In parallel, the background of the
     * activity is fading in. When the pictue is in place, the text description
     * drops down.
     */
    public void runEnterAnimation() {
        // Set starting values for properties we're going to animate. These
        // values scale and position the full size version down to the thumbnail
        // size/location, from which we'll animate it back up
        transactionAccount.setPivotX(0);
        transactionAccount.setPivotY(0);
        transactionAccount.setTranslationX(accountDeltaLeft);
        transactionAccount.setTranslationY(accountDeltaTop);

        // We'll fade the text in later
        listView.setAlpha(0);
        paymentButton.setAlpha(0);

        // Animate scale and translation to go from thumbnail to full size
        transactionAccount.animate().setDuration(duration).
                translationX(0).translationY(0).
                setInterpolator(sDecelerator).
                withEndAction(new Runnable() {
                    public void run() {
                        // Animate the description in after the image animation
                        // is done. Slide and fade the text in from underneath
                        // the picture.
                        listView.setTranslationY(-listView.getHeight());
                        listView.animate().setDuration(duration/2).
                                translationY(0).alpha(1).
                                setInterpolator(sDecelerator);

                        //want to fade in the Make Payment button
                        paymentButton.animate().setDuration(duration/2).
                                alpha(1).setInterpolator(sDecelerator);
                    }
                });

        // Fade in the black background
        ObjectAnimator bgAnim = ObjectAnimator.ofInt(transactionsBackground, "alpha", 0, 255);
        bgAnim.setDuration(duration);
        bgAnim.start();

        // Animate a color filter to take the image from grayscale to full color.
        // This happens in parallel with the image scaling and moving into place.
        //ObjectAnimator colorizer = ObjectAnimator.ofFloat(TransactionsActivity.this,
        //        "saturation", 0, 1);
        //colorizer.setDuration(duration);
        //colorizer.start();

        // Animate a drop-shadow of the image
        //ObjectAnimator shadowAnim = ObjectAnimator.ofFloat(mShadowLayout, "shadowDepth", 0, 1);
        //shadowAnim.setDuration(duration);
        //shadowAnim.start();
    }

    @Override
    public void onBackPressed(){
        //you could possibly cache the information in a
        //SESSION to reduce the network requirements
        //rather than throwing the data away like this
        finish();
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
                        //Log.d("Response", "" + transactions.get(i));
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
                listView.setAdapter(adapter);
            }
        }.execute();
    }

    public static HttpUrl buildURL(String accId){
        return new HttpUrl.Builder()
                .scheme("https")
                .host("bluebank.azure-api.net")
                .addPathSegment("api")
                .addPathSegment("v0.6.3")
                .addPathSegment("accounts")
                .addPathSegment(accId)
                .addPathSegment("transactions")
                .addQueryParameter("sortOrder", "-transactionDateTime")
                .build();
    }

    public void makePayment(View v){
        // First, slide/fade text out of the way
        listView.animate().translationY(-listView.getHeight()).alpha(0).
                setDuration(duration/2).setInterpolator(sAccelerator);

        paymentButton.animate().setDuration(duration/2).
                alpha(0).setInterpolator(sAccelerator).withEndAction(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(TransactionsActivity.this, PaymentActivity.class);
                intent.putExtra(MainActivity.TAG_ID, accountId);
                intent.putExtra(MainActivity.TAG_ACCOUNT_TYPE, accountT);
                intent.putExtra(MainActivity.TAG_ACCOUNT_NUMBER, accountNo);
                intent.putExtra(MainActivity.TAG_SORT_CODE, sortCode);
                intent.putExtra(MainActivity.TAG_ACCOUNT_BALANCE, accountBal);
                intent.putExtra(MainActivity.TAG_CUSTOMER_ID, custId);
                startActivity(intent);

                //Don't want normal window animations to take place
                overridePendingTransition(0, 0);
            }
        });
    }
}
