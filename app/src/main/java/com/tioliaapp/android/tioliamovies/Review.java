package com.tioliaapp.android.tioliamovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * An {@link Review} object contains information related to a single movie review.
 */

public class Review implements Parcelable {

    // Review author
    private String author;
    // Review content
    private String content;
    // Review url
    private String url;

    /**
     * Constructs a new {@link Review} object.
     *
     * @param author is the author of the review
     * @param content is the text of the review
     * @param url is the url for the review
     */
    public Review(String author, String content, String url) {
        this.author = author;
        this.content = content;
        this.url = url;
    }

    protected Review(Parcel in) {
        this.author = in.readString();
        this.content = in.readString();
        this.url = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.author);
        parcel.writeString(this.content);
        parcel.writeString(this.url);
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel source) {
            return new Review(source);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    // Returns the movie title.
    public String getAuthor() {
        return author;
    }

    // Returns the movie release date.
    public String getContent() {
        return content;
    }

    // Returns the movie poster path.
    public String getUrl() {
        return url;
    }
}