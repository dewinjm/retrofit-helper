package com.github.dewinjm.retrofithelper.utils;

import android.support.annotation.NonNull;
import java.io.IOException;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Response;
import okhttp3.internal.http.RealResponseBody;
import okio.GzipSource;
import okio.Okio;

public class UnzippingInterceptor implements Interceptor {
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        return unzip(response);
    }

    private Response unzip(final Response response) {
        if (response.body() == null)
            return response;

        GzipSource responseBody = new GzipSource(response.body().source());

        Headers strippedHeaders = response.headers().newBuilder()
                .removeAll("Content-Encoding")
                .removeAll("Content-Length")
                .build();

        return response.newBuilder()
                .headers(strippedHeaders)
                .body(new RealResponseBody(strippedHeaders.toString(),
                        strippedHeaders.byteCount(),
                        Okio.buffer(responseBody)))
                .build();
    }
}
