package com.vodafone.global.sdk.http.worker;

import android.content.Context;
import android.net.Uri;
import android.os.Message;
import com.google.common.base.Optional;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;
import com.vodafone.global.sdk.*;
import com.vodafone.global.sdk.http.HttpCode;
import com.vodafone.global.sdk.http.oauth.OAuthToken;
import com.vodafone.global.sdk.http.parser.Parsers;
import com.vodafone.global.sdk.http.sms.ValidatePinRequestDirect;
import org.json.JSONException;

import java.io.IOException;

import static com.vodafone.global.sdk.http.HttpCode.*;

public class ValidatePinProcessor {
    protected final Worker worker;
    protected final Settings settings;
    protected final Context context;
    protected final ResolveCallbacks resolveCallbacks;
    private String backendAppKey;
    private final RequestBuilderProvider requestBuilderProvider;
    private Optional<OAuthToken> authToken;

    public ValidatePinProcessor(
            Context context,
            Worker worker,
            Settings settings,
            String backendAppKey,
            ResolveCallbacks resolveCallbacks,
            RequestBuilderProvider requestBuilderProvider
    ) {
        this.context = context;
        this.worker = worker;
        this.settings = settings;
        this.resolveCallbacks = resolveCallbacks;
        this.backendAppKey = backendAppKey;
        this.requestBuilderProvider = requestBuilderProvider;
    }

    public void process(Optional<OAuthToken> authToken, Message msg) {
        this.authToken = authToken;
        ValidatePinParameters validatePinParameters = (ValidatePinParameters) msg.obj;

        try {
            Response response = queryServer(validatePinParameters);
            parseResponse(response);
        } catch (Exception e) {
            resolveCallbacks.notifyError(new GenericServerError());
        }
    }

    Response queryServer(ValidatePinParameters validatePinParameters) throws IOException, JSONException {
        ValidatePinRequestDirect request = getRequest(validatePinParameters);

        request.setRetryPolicy(null);
        request.setOkHttpClient(new OkHttpClient());

        return request.loadDataFromNetwork();
    }

    private ValidatePinRequestDirect getRequest(ValidatePinParameters validatePinParameters) {
        return ValidatePinRequestDirect.builder()
                .url(getUrl(validatePinParameters))
                .accessToken(authToken.get().accessToken)
                .pin(validatePinParameters.getPin())
                .requestBuilderProvider(requestBuilderProvider)
                .build();
    }

    private String getUrl(ValidatePinParameters validatePinParameters) {
        return new Uri.Builder().scheme(settings.apix.protocol)
                .authority(settings.apix.host)
                .path(settings.apix.path)
                .appendPath(validatePinParameters.getToken())
                .appendPath("pins")
                .appendQueryParameter("backendId", backendAppKey).build().toString();
    }

    void parseResponse(Response response) throws IOException, JSONException {
        int code = response.code();
        switch (code) {
            case HttpCode.OK_200:
                resolveCallbacks.notifyUserDetailUpdate(Parsers.parseUserDetails(response));
                // TODO ValidateSmsCallback.onSmsValidationSuccessful()
                break;
            case BAD_REQUEST_400:
                // TODO verify behaviour with flow diagram
                resolveCallbacks.notifyError(new RequestValidationError());
                break;
            case FORBIDDEN_403:
                resolveCallbacks.notifyError(new TokenNotFound());
                break;
            case NOT_FOUND_404:
                resolveCallbacks.notifyError(new TokenNotFound());
                break;
            case CONFLICT_409:
                // TODO pin validated failed
                // if (intercepts)
                //   com.vodafone.global.sdk.ResolutionCallback.onFailed()
                // else
                //   com.vodafone.global.sdk.ValidateSmsCallback.onSmsValidationError()
                break;
            default:
                resolveCallbacks.notifyError(new GenericServerError());
        }
    }
}
