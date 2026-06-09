package com.example.cozinhai;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("access_token")
    private AccessTokenData accessTokenData;

    private User user;

    public String getAccessToken() {
        return accessTokenData != null ? accessTokenData.getAccessToken() : null;
    }

    public User getUser() {
        return user;
    }

    public static class AccessTokenData {
        @SerializedName("access_token")
        private String accessToken;

        public String getAccessToken() {
            return accessToken;
        }
    }

    public static class User {
        @SerializedName(value = "id", alternate = {"_id"})
        private String id;

        private String email;
        private String name;

        public String getId() { 
            return id;
        }
        public String getEmail() { return email; }
        public String getName() { return name; }
    }
}
