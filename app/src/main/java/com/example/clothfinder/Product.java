package com.example.clothfinder;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Product implements Serializable {
    private String linkUri;
    private String imageUri;
    private String name;
    private float similarity;
    private int price;

    public Product() {}

    public Uri getLinkUri() {
        return Uri.parse(linkUri);
    }

    public void setLinkUri(String linkUri) {
        this.linkUri = linkUri;
    }

    public Uri getImageUri() {
        return Uri.parse(imageUri);
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getSimilarity() {
        return similarity;
    }

    public void setSimilarity(float similarity) {
        this.similarity = similarity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
