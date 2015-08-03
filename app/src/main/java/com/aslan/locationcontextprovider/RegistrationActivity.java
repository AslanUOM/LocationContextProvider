package com.aslan.locationcontextprovider;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class RegistrationActivity extends Activity {
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    /**
     * Tag used on log messages.
     */
    static final String TAG = "<<<<<< Context >>>>>>";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private final String KEY_NAME = "name";
    private final String KEY_USERNAME = "username";
    private final String KEY_PWD = "password";
    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    String SENDER_ID = "986180772600";
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    Context context;
    String deviceToken;
    private String VALUE_NAME;
    private String VALUE_USERNAME;
    private String VALUE_PWD;
    private RegistrationData regData;
    private EditText etxtName, etxtUname, etxtPassword;
    private Button btnSave;

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        context = getApplicationContext();

        etxtName = (EditText) findViewById(R.id.etxtName);
        etxtUname = (EditText) findViewById(R.id.etxtUname);
        etxtPassword = (EditText) findViewById(R.id.etxtPassword);

        etxtName.setSelectAllOnFocus(true);
        etxtUname.setSelectAllOnFocus(true);
        etxtPassword.setSelectAllOnFocus(true);

        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VALUE_NAME = etxtName.getText().toString();
                VALUE_USERNAME = etxtUname.getText().toString();
                VALUE_PWD = etxtPassword.getText().toString();

                // Check device for Play Services APK.
                if (checkPlayServices()) {
                    // If this check succeeds, proceed with normal processing.
                    // Otherwise, prompt user to get valid Play Services APK.
                    gcm = GoogleCloudMessaging.getInstance(context);
                    deviceToken = getRegistrationId(context);

                    if (deviceToken.isEmpty()) {
                        registerInBackground();
                    }
                } else {
                    Log.i(TAG, "No valid Google Play Services APK found.");
                }
            }
        });
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            registerInBackground();
            return "";
        } else {
            sendRegistrationIdToBackend(deviceToken);
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new MyHttpAsyncTask().execute(null, null, null);

    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
     * or CCS to send messages to your app. Not needed for this demo since the
     * device sends upstream messages to a server that echoes back the message
     * using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend(String deviceToken) {
        // Your implementation here.

    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId   registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();

        regData = new RegistrationData();
        regData.setName(VALUE_NAME);
        regData.setUsername(VALUE_USERNAME);
        regData.setPassword(VALUE_PWD);
        regData.setDeviceToken(deviceToken);
        RegistrationDAO.getInstance().saveData(context, regData);
    }

    // You need to do the Play Services APK check here too.
    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If it
     * doesn't, display a dialog that allows users to download the APK from the
     * Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("Google Play Service", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private class MyHttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String msg = "";
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }
                deviceToken = gcm.register(SENDER_ID);
                msg = "Device registered, registration ID=" + deviceToken;

            } catch (IOException ex) {
                msg = "Error :" + ex.getMessage();
                // If there is an error, don't just keep trying to register.
                // Require the user to click a button again, or perform
                // exponential back-off.
            }
            return msg;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {

                // You should send the registration ID to your server over HTTP,
                // so it can use GCM/HTTP or CCS to send messages to your app.
                // The request to your server should be authenticated if your app
                // is using accounts.
                sendRegistrationIdToBackend(deviceToken);

                // For this demo: we don't need to send it because the device
                // will send upstream messages to a server that echo back the
                // message using the 'from' address in the message.

                // Persist the regID - no need to register again.
                storeRegistrationId(context, deviceToken);

                Intent openMainActivity = new Intent(RegistrationActivity.this, MainActivity.class);
                Log.i("LOGIN", "Call to Main");
                startActivity(openMainActivity);
                RegistrationActivity.this.finish();
            } else {
                // invoked when no data received due to error in internet
                // connection
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        RegistrationActivity.this);
                builder.setMessage(R.string.internet_error_msg)
                        .setTitle("Unable to retrive data from internet")
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        RegistrationActivity.this.finish();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }

    }
}
