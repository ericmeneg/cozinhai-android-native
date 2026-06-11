package com.example.cozinhai;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class OverpassResponse {
    @SerializedName("elements")
    private List<Element> elements;

    public List<Element> getElements() { return elements; }

    public static class Element {
        @SerializedName("lat")
        private double lat;
        @SerializedName("lon")
        private double lon;
        @SerializedName("tags")
        private Tags tags;

        public double getLat() { return lat; }
        public double getLon() { return lon; }
        public Tags getTags() { return tags; }
    }

    public static class Tags {
        @SerializedName("name")
        private String name;

        public String getName() { return name; }
    }
}
