package com.tioliaapp.android.tioliamovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * An {@link Movie} object contains information related to a single movie.
 */
public class Movie implements Parcelable {

    // Movie ID
    private int id;
    // Movie title
    private String title;
    // Movie release date
    private String releaseDate;
    // Movie poster path
    private String posterPath;
    // Movie backdrop path
    private String backdropPath;
    // Movie vote average
    private double voteAverage;
    // Movie plot synopsis
    private String overview;

    /**
     * Constructs a new {@link Movie} object.
     *
     * @param id            is the id of the movie
     * @param title         is the title of the movie
     * @param releaseDate   is the release date of the movie
     * @param posterPath    is the poster path of the movie
     * @param backdropPath  is the backdrop path of the horizontal image from the movie
     * @param voteAverage   is the vote average (rating) of the movie
     * @param overview      is the plot synopsis (overview) of the movie
     */
    public Movie(int id, String title, String releaseDate, String posterPath, String backdropPath,
                 double voteAverage, String overview) {
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.voteAverage = voteAverage;
        this.overview = overview;
    }

    protected Movie(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.releaseDate = in.readString();
        this.posterPath = in.readString();
        this.backdropPath = in.readString();
        this.overview = in.readString();
        this.voteAverage = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.id);
        parcel.writeString(this.title);
        parcel.writeString(this.releaseDate);
        parcel.writeString(this.posterPath);
        parcel.writeString(this.backdropPath);
        parcel.writeString(this.overview);
        parcel.writeDouble(this.voteAverage);
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    // Returns the movie id.
    public int getId() {
        return id;
    }

    // Returns the movie title.
    public String getTitle() {
        return title;
    }

    // Returns the movie release date.
    public String getReleaseDate() {
        return releaseDate;
    }

    // Returns the movie poster path.
    public String getPosterPath() {
        return posterPath;
    }

    // Returns the movie backdrop path.
    public String getBackdropPath() {
        return backdropPath;
    }

    // Returns the movie vote average(rating).
    public double getVoteAverage() {
        return voteAverage;
    }

    // Returns the movie plot synopsis (overview).
    public String getOverview() {
        return overview;
    }
}