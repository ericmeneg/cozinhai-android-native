package com.example.cozinhai;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("access_token")
    private AccessTokenData accessTokenData;

    private User user;

    public AccessTokenData getAccessTokenData() {
        return accessTokenData;
    }

    public User getUser() {
        return user;
    }

    public static class AccessTokenData {
        @SerializedName("access_token")
        private String token;

        private User user;

        public String getToken() {
            return token;
        }

        public User getUser() {
            return user;
        }
    }

    public static class User {
        private String id;
        private String email;
        private String name;
        private boolean status;

        public String getId() {
            return id;
        }

        public String getEmail() {
            return email;
        }

        public String getName() {
            return name;
        }

        public boolean isStatus() {
            return status;
        }
    }
}
