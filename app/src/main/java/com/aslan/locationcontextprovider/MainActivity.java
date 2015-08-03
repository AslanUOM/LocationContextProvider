package com.aslan.locationcontextprovider;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * A login screen that offers login via email/password.
 */
public class MainActivity extends Activity {

    private RegistrationData registrationData;
    private TextView tvDeviceToken;
    private Button btnSendEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registrationData = RegistrationDAO.getInstance().loadData(this);
        tvDeviceToken = (TextView) findViewById(R.id.tvDeviceToken);
        tvDeviceToken.setText(registrationData.getDeviceToken());
        btnSendEmail = (Button) findViewById(R.id.btnSendEmail);
        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "aslan@uomcse.lk", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Device Registration Data");
                emailIntent.putExtra(Intent.EXTRA_TEXT, registrationData.toString());
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });
    }
}

