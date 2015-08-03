package com.aslan.locationcontextprovider;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

/**
 * The SPLASH ACTIVITY of the application Will check and enable internet
 * connectivity
 *
 * @author Vishnuvathsasarma
 */
public class SplashActivity extends Activity {
    private RegistrationData regData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (isNetworkAvailable()) {
            regData = RegistrationDAO.getInstance().loadData(this);
            if (regData == null) {
                Intent openConfigActivity = new Intent(SplashActivity.this, RegistrationActivity.class);
                Log.i("SPLASH", "Call to Registration");
                startActivity(openConfigActivity);
            } else {
                Intent openMainActivity = new Intent(SplashActivity.this, MainActivity.class);
                Log.i("SPLASH", "Call to Main");
                startActivity(openMainActivity);
            }
            SplashActivity.this.finish();
        } else {
            askToEnableNetwork();
        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }

    // Check Internet connection
    private boolean isNetworkAvailable() {
        // Log.i("&%$^#%^&#%&#%^#$%#^&", "iuyfbuytf");
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // Show alert dialog to confirm and enable the network
    private void askToEnableNetwork() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.internet_request_msg)
                .setTitle("Unable to connect")
                .setCancelable(false)
                .setPositiveButton("Settings",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(
                                        Settings.ACTION_WIRELESS_SETTINGS);
                                startActivity(i);
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}