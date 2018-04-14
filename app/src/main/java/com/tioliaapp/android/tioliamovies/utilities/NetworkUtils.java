package com.tioliaapp.android.tioliamovies.utilities;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Utilities to communicate with the Movie Db servers
 */
public final class NetworkUtils {

    // !!!Insert your Movie Db API key here!!!
    //
    // API key required to query Movie Db
    private static final String apiKey = "";

    // API key param required to query Movie Db
    final static String API_PARAM = "api_key";

    // Uri to query popular movies:
    // http://api.themoviedb.org/3/movie/popular?api_key=apiKey
    //
    // Uri to query top rated movies:
    // http://api.themoviedb.org/3/movie/top_rated?api_key=apiKey
    //
    // Uri to query movie videos:
    // https://api.themoviedb.org/3/movie/245891/videos?api_key=apiKey
    //
    // Uri to query movie reviews:
    // https://api.themoviedb.org/3/movie/245891/reviews?api_key=apiKey
    private static final String TAG = NetworkUtils.class.getSimpleName();
    // Bse URIs to query popular and top rated movies
    private static final String BASE_URL_POPULAR = "http://api.themoviedb.org/3/movie/popular?";
    private static final String BASE_URL_TOP_RATED = "http://api.themoviedb.org/3/movie/top_rated?";
    // Base URI to query movie videos and reviews
    private static final String BASE_URL_MOVIE_VIDEOS_AND_REVIEWS = "https://api.themoviedb.org/3/movie/";

    // Retrieves the proper URL to query popular movies data
    public static URL getPopularMoviesUrl() {

        Uri builtUri = Uri.parse(BASE_URL_POPULAR).buildUpon()
                .appendQueryParameter(API_PARAM, apiKey)
                .build();

        try {
            return new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Retrieves the proper URL to query top rated movies data
    public static URL getTopRatedMoviesUrl() {

        Uri builtUri = Uri.parse(BASE_URL_TOP_RATED).buildUpon()
                .appendQueryParameter(API_PARAM, apiKey)
                .build();

        try {
            return new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Retrieves the proper URL to query movie videos
    public static URL buildUrlForMovieVideos(String movieId) {

        Uri builtUri = Uri.parse(BASE_URL_MOVIE_VIDEOS_AND_REVIEWS).buildUpon()
                .appendPath(movieId)
                .appendPath("videos")
                .appendQueryParameter(API_PARAM, apiKey)
                .build();

        try {
            return new URL(builtUri.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Retrieves the proper URL to query movie reviews
    public static URL buildUrlForMovieReviews(String movieId) {

        Uri builtUri = Uri.parse(BASE_URL_MOVIE_VIDEOS_AND_REVIEWS).buildUpon()
                .appendPath(movieId)
                .appendPath("reviews")
                .appendQueryParameter(API_PARAM, apiKey)
                .build();

        try {
            return new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // This method returns the entire result from the HTTP response.
    public static String getResponseFromHttpUrl(URL url) throws IOException {

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try {
            // Sets read timeout
            urlConnection.setReadTimeout(10000);
            // Sets connection timeout
            urlConnection.setConnectTimeout(15000);
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}