package com.example.cozinhai;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.Path;
import retrofit2.http.GET;

import java.util.List;

public interface AuthApi {
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @PATCH("user/{id}/password")
    Call<Void> changePassword(
        @Path("id") String userId,
        @Header("Authorization") String token,
        @Body ChangePasswordRequest request
    );

    @GET("user/{id}/favorites")
    Call<List<Recipe>> getFavorites(
        @Path("id") String userId,
        @Header("Authorization") String token
    );

    @POST("user/{id}/favorites")
    Call<Void> addFavorite(
        @Path("id") String userId,
        @Header("Authorization") String token,
        @Body FavoriteRequest request
    );

    @POST("user/{id}/ratings")
    Call<Void> rateRecipe(
        @Path("id") String userId,
        @Header("Authorization") String token,
        @Body RatingRequest request
    );
}
