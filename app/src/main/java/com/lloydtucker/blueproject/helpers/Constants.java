package com.lloydtucker.blueproject.helpers;

import com.microsoft.aad.adal.AuthenticationResult;

/**
 * Created by lloydtucker on 19/09/2016.
 */
public class Constants {

    public static final String SDK_VERSION = "1.0";

    /**
     * UTF-8 encoding
     */
    public final String UTF8_ENCODING = "UTF-8";

    public static final String HEADER_AUTHORIZATION = "Authorization";

    public static final String HEADER_AUTHORIZATION_VALUE_PREFIX = "Bearer ";

    // -------------------------------AAD
    // PARAMETERS----------------------------------
    public static String AUTHORITY_URL = "https://login.microsoftonline.com/GreenBankADB2C.onmicrosoft.com";
    public static String CLIENT_ID = "4e9fbd39-52ae-4a83-9b38-77a5521ff834";
    public static String RESOURCE_ID = "https://bluebank.azure-api.net/greenbank/customers";
    public static String REDIRECT_URL = "urn:ietf:wg:oauth:2.0:oob";
    public static String CORRELATION_ID = "";
    public static String USER_HINT = "";
    public static String EXTRA_QP = "";
    public static boolean FULL_SCREEN = true;
    public static AuthenticationResult CURRENT_RESULT = null;
    // Endpoint we are targeting for the deployed WebAPI service
    public static String SERVICE_URL = "http://10.0.1.44:8080/tasks";

    // ------------------------------------------------------------------------------------------

    static final String TABLE_WORKITEM = "WorkItem";

    public static final String SHARED_PREFERENCE_NAME = "com.example.com.test.settings";

    public static final String KEY_NAME_ASK_BROKER_INSTALL = "test.settings.ask.broker";
    public static final String KEY_NAME_CHECK_BROKER = "test.settings.check.broker";
}
