package com.tioliaapp.android.tioliamovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * An {@link Video} object contains information related to a single movie video.
 *
 */
public class Video implements Parcelable {

    // Video type
    private String type;
    // Video key for url to watch video on Youtube
    private String key;
    // Video name
    private String name;

    /**
     * Constructs a new {@link Video} object.
     *
     * @param name is the name of the video
     * @param type is the type of the video
     * @param key is the key of the video (to create url)
     */
    public Video(String name, String type, String key) {
        this.name = name;
        this.type = type;
        this.key = key;
    }

    protected Video(Parcel in) {
        this.name = in.readString();
        this.type = in.readString();
        this.key = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.name);
        parcel.writeString(this.type);
        parcel.writeString(this.key);
    }

    public static final Creator<Video> CREATOR = new Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel source) {
            return new Video(source);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };

    // Returns the movie poster path.
    public String getVideoName() {
        return name;
    }

    // Returns the movie release date.
    public String getVideoType() {
        return type;
    }

    // Returns the movie poster path.
    public String getVideoKey() {
        return key;
    }
}