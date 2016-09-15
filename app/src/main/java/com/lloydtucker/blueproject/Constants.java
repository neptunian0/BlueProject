package com.lloydtucker.blueproject;

import com.microsoft.aad.adal.AuthenticationResult;

/**
 * Created by lloydtucker on 14/09/2016.
 */
public class Constants {
    public static final String SDK_VERSION = "1.0";
    public static final String UTF8_ENCODING = "UTF-8";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_AUTHORIZATION_VALUE_PREFIX = "Bearer ";

    // -------------------------------AAD
    // PARAMETERS----------------------------------
    public static String AUTHORITY_URL = "https://login.microsoftonline.com/GreenBankADB2C.onmicrosoft.com/";
    public static String CLIENT_ID = "05e869e3-6002-456b-b920-34ddce133cc6";
    public static String[] SCOPES = {"05e869e3-6002-456b-b920-34ddce133cc6"};
    public static String[] ADDITIONAL_SCOPES = {""};
    public static String REDIRECT_URL = "urn:ietf:wg:oauth:2.0:oob";
    public static String CORRELATION_ID = "";
    public static String USER_HINT = "";
    public static String EXTRA_QP = "";
    public static String FB_POLICY = "B2C_1_SiIn";
    public static String EMAIL_SIGNIN_POLICY = "B2C_1_SiIn";
    public static String EMAIL_SIGNUP_POLICY = "B2C_1_SiUp";
    public static boolean FULL_SCREEN = true;
    public static AuthenticationResult CURRENT_RESULT = null;
    // Endpoint we are targeting for the deployed WebAPI service
    public static String SERVICE_URL = "http://localhost:3000/tasks";

    // ------------------------------------------------------------------------------------------

    static final String TABLE_WORKITEM = "WorkItem";
    public static final String SHARED_PREFERENCE_NAME = "com.example.com.test.settings";
}
