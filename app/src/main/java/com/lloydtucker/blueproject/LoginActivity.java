package com.lloydtucker.blueproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.lloydtucker.blueproject.helpers.Constants;
import com.microsoft.aad.adal.AuthenticationCallback;
import com.microsoft.aad.adal.AuthenticationContext;
import com.microsoft.aad.adal.AuthenticationResult;
import com.microsoft.aad.adal.AuthenticationSettings;
import com.microsoft.aad.adal.PromptBehavior;
import com.microsoft.aad.adal.UserIdentifier;

import java.net.MalformedURLException;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    /**
     * Show this dialog when activity first launches to check if user has login
     * or not.
     */
    private ProgressDialog mLoginProgressDialog;
    private AuthenticationContext mAuthContext;
    private static AuthenticationResult sResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Show a loading dialog when waiting for the authentication token to return
        mLoginProgressDialog = new ProgressDialog(this);
        mLoginProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mLoginProgressDialog.setMessage("Login in progress...");
        mLoginProgressDialog.show();

        /*
        * Ask for token and provide callback
        */
        try {
            mAuthContext = new AuthenticationContext(LoginActivity.this, Constants.AUTHORITY_URL,
                    false);
            //to pick which signin policy you want
            //String policy = getIntent().getStringExtra("thePolicy");
            String policy = Constants.EMAIL_SIGNIN_POLICY; //hard-coded signin policy

            /* NOT REQUIRED... FOR NOW
            if(Constants.CORRELATION_ID != null &&
                    Constants.CORRELATION_ID.trim().length() !=0){
                mAuthContext.setRequestCorrelationId(UUID.fromString(Constants.CORRELATION_ID));
            }*/

            AuthenticationSettings.INSTANCE.setSkipBroker(true);

            /*
            * This code is where the access token is obtained
            * Below chunk of code cannot be omitted
            */
            mAuthContext.acquireToken(LoginActivity.this, Constants.SCOPES,
                    Constants.ADDITIONAL_SCOPES, policy, Constants.CLIENT_ID,
                    Constants.REDIRECT_URL, getUserInfo(), PromptBehavior.Always,
                    "nux=1&" + Constants.EXTRA_QP,
                    new AuthenticationCallback<AuthenticationResult>() {

                        @Override
                        public void onError(Exception exc) {
                            if (mLoginProgressDialog.isShowing()) {
                                mLoginProgressDialog.dismiss();
                            }
                            SimpleAlertDialog.showAlertDialog(LoginActivity.this,
                                    "Failed to get token", exc.getMessage());
                        }

                        @Override
                        public void onSuccess(AuthenticationResult result) {
                            if (mLoginProgressDialog.isShowing()) {
                                mLoginProgressDialog.dismiss();
                            }

                            if (result != null && !result.getToken().isEmpty()) {
                                setLocalToken(result);
                                LoginActivity.sResult = result;
                                GreenApiCall.setBearer(result.getToken());
                                //Toast.makeText(getApplicationContext(),
                                //        "Token is returned", Toast.LENGTH_SHORT).show();
                                //TODO: Remove this log line
                                Log.d("Response", result.getToken());
                                openMainActivity();
                            } else {
                                //TODO: popup error alert
                                Toast.makeText(getApplicationContext(),
                                        "Token was not returned", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } catch (Exception e) {
            SimpleAlertDialog.showAlertDialog(LoginActivity.this, "Exception caught",
                    e.getMessage());
        }
        //Toast.makeText(LoginActivity.this, TAG + "done", Toast.LENGTH_SHORT).show();
    }

    //this method gives back a username to put into the login field
    private UserIdentifier getUserInfo() {
        //final TextView names = (TextView) findViewById(R.id.userLoggedIn);
        //String name = names.getText().toString();
        String name = Constants.USERNAME;
        return new UserIdentifier(name, UserIdentifier.UserIdentifierType.OptionalDisplayableId);
    }

    private void setLocalToken(AuthenticationResult newToken) {
        Constants.CURRENT_RESULT = newToken;
    }

    private void getToken(final AuthenticationCallback callback) {

        String policy = getIntent().getStringExtra("thePolicy");

        // one of the acquireToken overloads
        mAuthContext.acquireToken(LoginActivity.this, Constants.SCOPES, Constants.ADDITIONAL_SCOPES,
                policy, Constants.CLIENT_ID, Constants.REDIRECT_URL, getUserInfo(),
                PromptBehavior.Always, "nux=1&" + Constants.EXTRA_QP, callback);
    }

    private void openMainActivity(){
        // Then you start a new Activity via Intent
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra(Constants.HEADER_AUTHORIZATION_VALUE_PREFIX, sResult.getToken());
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mAuthContext.onActivityResult(requestCode, resultCode, data);
    }

    private URL getEndpointUrl() {
        URL endpoint = null;
        try {
            endpoint = new URL(Constants.SERVICE_URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return endpoint;
    }
}
