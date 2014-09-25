package com.vodafone.global.sdk.http.resolve;

import com.octo.android.robospice.request.okhttp.OkHttpSpiceRequest;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.vodafone.global.sdk.LogUtil;

import java.io.IOException;
import java.util.UUID;

public class ResolveGetRequestDirect extends OkHttpSpiceRequest<Response> {
    private final String url;
    private final String accessToken;
    private final String androidId;
    private final String mobileCountryCode;
    private final String sdkId;
    private final String backendAppKey;
    private final String etag;

    /**
     * Provides builder for {@link com.vodafone.global.sdk.http.resolve.ResolveGetRequestDirect}.
     */
    public static Builder builder() {
        return new Builder();
    }

    protected ResolveGetRequestDirect(
            String url, String accessToken, String androidId, String mobileCountryCode,
            String sdkId, String backendAppKey, String etag
    ) {
        super(Response.class);
        this.url = url;
        this.accessToken = accessToken;
        this.androidId = androidId;
        this.mobileCountryCode = mobileCountryCode;
        this.sdkId = sdkId;
        this.backendAppKey = backendAppKey;
        this.etag = etag;
    }

    @Override
    public Response loadDataFromNetwork() throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("User-Agent", sdkId)
                .addHeader("etag", etag)
                .addHeader("scope", "seamless_id_user_details_all") //TODO: REMOVE ONLY FOR TESTING!!!
                .addHeader("backendScopes", "seamless_id_user_details_all") //TODO: REMOVE ONLY FOR TESTING!!!
                .addHeader("x-int-opco", "DE") //TODO: REMOVE ONLY FOR TESTING!!!
                .addHeader("x-vf-trace-subject-id", androidId)
                .addHeader("x-vf-trace-subject-region", mobileCountryCode)
                .addHeader("x-vf-trace-source", sdkId + "" + backendAppKey)
                .addHeader("x-vf-trace-transaction-id", UUID.randomUUID().toString())
                .get()
                .build();

        LogUtil.log(request);

        OkHttpClient client = getOkHttpClient();
        Response response = client.newCall(request).execute();

        LogUtil.log(response);
        return response;
    }

    /**
     * Builder for {@link com.vodafone.global.sdk.http.resolve.ResolveGetRequestDirect}.
     * @see com.vodafone.global.sdk.http.resolve.ResolveGetRequestDirect#builder()
     */
    public static class Builder {

        private String url;
        private String accessToken;
        private String androidId;
        private String mobileCountryCode;
        private String sdkId;
        private String backendAppKey;
        private String etag;

        private Builder() {
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public Builder androidId(String androidId) {
            this.androidId = androidId;
            return this;
        }

        public Builder mobileCountryCode(String mobileCountryCode) {
            this.mobileCountryCode = mobileCountryCode;
            return this;
        }

        public Builder sdkId(String sdkId) {
            this.sdkId = sdkId;
            return this;
        }

        public Builder backendAppKey(String backendAppKey) {
            this.backendAppKey = backendAppKey;
            return this;
        }

        public Builder etag(String etag) {
            this.etag = etag;
            return this;
        }

        public ResolveGetRequestDirect build() {
            return new ResolveGetRequestDirect(url, accessToken, androidId, mobileCountryCode, sdkId, backendAppKey, etag);
        }
    }
}
