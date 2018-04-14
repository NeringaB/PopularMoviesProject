package com.tioliaapp.android.tioliamovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines tables and their column names for the movie database.
 * Movie database contains three tables for popular, top rated and favourite movies that
 * Popular Movies app could display popular, top rated and favourite movies offline.
 * However, movie videos and reviews are displayed only online
 */
public class MovieContract {

    // Content authority for the content provider name
    public static final String CONTENT_AUTHORITY = "com.tioliaapp.android.tioliamovies";

    // Base of all URIs, which app will use to contact
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths that can be appended to BASE_CONTENT_URI
    // to form valid URIs that Popular Movies app can handle:
    //
    // Path for popular movies table
    public static final String PATH_POPULAR_MOVIES = "popular";
    // Path for top rated movies table
    public static final String PATH_TOP_RATED_MOVIES = "topRated";
    // Path for favourite movies table
    public static final String PATH_FAVOURITE_MOVIES = "favourite";

    // Table for popular movies
    public static final class PopularMovieEntry implements BaseColumns {

        // The base CONTENT_URI used to query the popular movies table from the content provider
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_POPULAR_MOVIES)
                .build();

        // Name for the popular movies table
        public static final String TABLE_NAME = "popular";

        // Columns for the popular movies table
        public static final String COLUMN_MOVIE_ID = "movieId";
        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_MOVIE_POSTER_PATH = "poster";
        public static final String COLUMN_MOVIE_BACKDROP_PATH = "horizontalImage";
        public static final String COLUMN_MOVIE_OVERVIEW = "overview";
        public static final String COLUMN_MOVIE_RATING = "rating";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "releaseDate";
    }

    //Table for top rated movies
    public static final class TopRatedMovieEntry implements BaseColumns {

        // The base CONTENT_URI used to query the top rated movies table from the content provider
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_TOP_RATED_MOVIES)
                .build();

        // Name for the top rated movies table
        public static final String TABLE_NAME = "topRated";

        // Columns for the top rated movies table
        public static final String COLUMN_MOVIE_ID = "movieId";
        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_MOVIE_POSTER_PATH = "poster";
        public static final String COLUMN_MOVIE_BACKDROP_PATH = "horizontalImage";
        public static final String COLUMN_MOVIE_OVERVIEW = "overview";
        public static final String COLUMN_MOVIE_RATING = "rating";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "releaseDate";
    }

    // Table for favourite movies
    public static final class FavouriteMovieEntry implements BaseColumns {

        // The base CONTENT_URI used to query the favourite movies table from the content provider
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVOURITE_MOVIES)
                .build();

        // Name for the favourite movies table
        public static final String TABLE_NAME = "favourite";

        // Columns for the favourite movies table
        public static final String COLUMN_MOVIE_ID = "movieId";
        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_MOVIE_POSTER_PATH = "poster";
        public static final String COLUMN_MOVIE_BACKDROP_PATH = "horizontalImage";
        public static final String COLUMN_MOVIE_OVERVIEW = "overview";
        public static final String COLUMN_MOVIE_RATING = "rating";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "releaseDate";
    }
}