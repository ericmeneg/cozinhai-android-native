package com.example.cozinhai;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkClient {
    private static final String BASE_URL = "https://pi-3sem-backend.onrender.com/";
    private static final String SPOONACULAR_BASE_URL = "https://api.spoonacular.com/";
    private static Retrofit retrofit = null;
    private static Retrofit spoonacularRetrofit = null;

    public static OkHttpClient getOkHttpClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(getOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getSpoonacularClient() {
        if (spoonacularRetrofit == null) {
            spoonacularRetrofit = new Retrofit.Builder()
                    .baseUrl(SPOONACULAR_BASE_URL)
                    .client(getOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return spoonacularRetrofit;
    }

    public static AuthApi getAuthApi() {
        return getClient().create(AuthApi.class);
    }

    public static SpoonacularApi getSpoonacularApi() {
        return getSpoonacularClient().create(SpoonacularApi.class);
    }
}
