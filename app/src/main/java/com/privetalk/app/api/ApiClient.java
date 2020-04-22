package com.privetalk.app.api;


import androidx.annotation.NonNull;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {
    private static final String BASE_URL = "https://api.instagram.com/oauth/";

    public static Retrofit getClient() {
        OkHttpClient.Builder client = new OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS);

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        client.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Request original = chain.request();
                // Request customization: add request headers
                Request.Builder requestBuilder;
              /*  if (!token.equals("")) {
                    requestBuilder = original.newBuilder()
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Authorization", String.format(java.util.Locale.US,"Bearer %s", token));
                } else {
                    requestBuilder = original.newBuilder()
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Authorization", token);
                }*/
                requestBuilder = original.newBuilder();
                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });
        client.interceptors().add(loggingInterceptor);

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }


}
