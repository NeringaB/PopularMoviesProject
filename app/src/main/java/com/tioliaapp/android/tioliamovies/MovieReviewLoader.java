package com.tioliaapp.android.tioliamovies;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.tioliaapp.android.tioliamovies.utilities.MovieDbJsonUtils;
import com.tioliaapp.android.tioliamovies.utilities.NetworkUtils;

import java.net.URL;
import java.util.List;

/**
 * Loads a list of earthquakes by using an AsyncTask to perform the
 * network request to the given URL.
 */
public class MovieReviewLoader extends AsyncTaskLoader<List<Review>> {

    // Tag for log messages
    private static final String LOG_TAG = MovieVideoLoader.class.getName();

    // String for the movie id
    private String movieIdString;

    // Constructs a new {@link MovieReviewLoader}.
    public MovieReviewLoader(Context context, String movieIdString) {
        super(context);
        this.movieIdString = movieIdString;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    // This is on a background thread.
    @Override
    public List<Review> loadInBackground() {

        if (movieIdString == null) {
            return null;
        }

        // Performs the network request, parses the response, and extracts a list of movie reviews.

        // Builds url to query videos for the movie that is currently displayed
        URL movieRequestUrl = NetworkUtils.buildUrlForMovieReviews(movieIdString);

        try {
            String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);

            List<Review> movieReviews = MovieDbJsonUtils.getMovieReviewsFromJson(jsonMovieResponse);

            return movieReviews;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}