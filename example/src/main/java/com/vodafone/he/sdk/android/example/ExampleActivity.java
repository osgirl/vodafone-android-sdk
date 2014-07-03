package com.vodafone.he.sdk.android.example;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import com.vodafone.he.sdk.android.*;

public class ExampleActivity extends Activity {
    private TextView resolved;
    private TextView stillRunning;
    private TextView source;
    private TextView token;
    private TextView tetheringConflict;
    private TextView secure;
    private TextView validated;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        resolved = (TextView) findViewById(R.id.resolved);
        stillRunning = (TextView) findViewById(R.id.stillRunning);
        source = (TextView) findViewById(R.id.source);
        token = (TextView) findViewById(R.id.token);
        tetheringConflict = (TextView) findViewById(R.id.tetheringConflict);
        secure = (TextView) findViewById(R.id.secure);
        validated = (TextView) findViewById(R.id.validated);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // prepare options for service
        Options options = Options.builder()
                .enableSmsValidation()
                .enableSecureFlow()
                .setSecureMessage(ExampleConstants.SECURE_MESSAGE)
                .build();

        // prepare callback for handling responses
        UserDetailsCallback userDetailsCallback = new UserDetailsCallback() {
            @Override
            public void onSuccess(UserDetails userDetails) {
                resolved.setText(String.valueOf(userDetails.getResolved()));
                stillRunning.setText(String.valueOf(userDetails.getStillRunning()));
                source.setText(userDetails.getSource());
                token.setText(userDetails.getToken());
                tetheringConflict.setText(String.valueOf(userDetails.getTetheringConflict()));
                secure.setText(String.valueOf(userDetails.getSecure()));
                validated.setText(String.valueOf(userDetails.getToken()));
            }

            @Override
            public void onError(VodafoneException ex) {
                Toast.makeText(ExampleActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        };

        // start listening for changes
        Vodafone.getUserDetails(userDetailsCallback, options);
    }
}
