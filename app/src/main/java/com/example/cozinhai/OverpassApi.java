package com.example.cozinhai;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OverpassApi {
    @GET("api/interpreter")
    Call<OverpassResponse> getNearbyShops(@Query("data") String data);
}
