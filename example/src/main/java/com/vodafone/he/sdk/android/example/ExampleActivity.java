package com.vodafone.he.sdk.android.example;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.vodafone.he.sdk.android.*;

public class ExampleActivity extends Activity implements
        UserDetailsCallback
{
    private TextView resolved;
    private TextView stillRunning;
    private TextView source;
    private TextView token;
    private TextView tetheringConflict;
    private TextView secure;
    private TextView validated;
    private Button logInButton;

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

        logInButton = (Button) findViewById(R.id.log_in);
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vodafone.retrieveUserDetails(options);
            }
        });
    }

    // prepare options for service
    Options options = Options.builder()
            .enableSmsValidation()
            .enableSecureFlow()
            .setSecureMessage(ExampleConstants.SECURE_MESSAGE)
            .build();

    @Override
    protected void onResume() {
        super.onResume();
        // start listening to UserDetails changes
        Vodafone.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // stop listening to UserDetails changes
        Vodafone.unregister(this);
    }

    @Override
    public void onUserDetailsUpdated(UserDetails userDetails) {
        resolved.setText(String.valueOf(userDetails.getResolved()));
        stillRunning.setText(String.valueOf(userDetails.getStillRunning()));
        source.setText(userDetails.getSource());
        token.setText(userDetails.getToken());
        tetheringConflict.setText(String.valueOf(userDetails.getTetheringConflict()));
        secure.setText(String.valueOf(userDetails.getSecure()));
        validated.setText(String.valueOf(userDetails.getToken()));
    }

    @Override
    public void onUserDetailsError(VodafoneException ex) {
        Toast.makeText(ExampleActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
    }
}