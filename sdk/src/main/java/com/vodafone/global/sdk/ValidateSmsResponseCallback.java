package com.vodafone.global.sdk;

import android.util.Log;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

class ValidateSmsResponseCallback implements Callback {

    private final List<ValidateSmsCallback> validateSmsCallbacks;

    public ValidateSmsResponseCallback(List<ValidateSmsCallback> validateSmsCallbacks) {
        this.validateSmsCallbacks = validateSmsCallbacks;
    }

    @Override
    public void onFailure(Request request, IOException e) {
        for (ValidateSmsCallback callback : validateSmsCallbacks) {
            callback.onSmsValidationError(new VodafoneException(e.getMessage(), e));
        }
    }

    @Override
    public void onResponse(Response response) throws IOException {
        final int httpCode = response.code();
        switch (httpCode) {
            case 200:
                // sms validation successful
                for (ValidateSmsCallback callback : validateSmsCallbacks) {
                    callback.onSmsValidationSuccessful();
                }
                break;
            case 400:
                // sms validation failed
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    String error = json.getString("error");
                    String errorMessage = json.getString("errorMessage");

                    Log.e("Vodafone", error + ": " + errorMessage);

                    for (ValidateSmsCallback callback : validateSmsCallbacks) {
                        callback.onSmsValidationFailure();
                    }
                } catch (JSONException e) {
                    Log.e("Vodafone", e.getMessage(), e);
                }
                break;
        }
    }
}
