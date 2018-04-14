package com.tioliaapp.android.tioliamovies.utilities;

import android.content.ContentValues;

import com.tioliaapp.android.tioliamovies.Review;
import com.tioliaapp.android.tioliamovies.Video;
import com.tioliaapp.android.tioliamovies.data.MovieContract.TopRatedMovieEntry;
import com.tioliaapp.android.tioliamovies.data.MovieContract.PopularMovieEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility functions to handle Movie Db JSON data.
 */
public final class MovieDbJsonUtils {

    private static final String TAG = MovieDbJsonUtils.class.getSimpleName();

    private static final String OWM_MESSAGE_CODE = "cod";

    // Parses JSON from a web response and returns an array
    // of content values describing a set of popular movies
    public static ContentValues[] getPopularMovieContentValuesFromJson(String movieJsonStr)
            throws JSONException {

        // Gets the base JSON response
        JSONObject baseResponse = new JSONObject(movieJsonStr);

        // Checks if there is an error?
        if (baseResponse.has(OWM_MESSAGE_CODE)) {

            int errorCode = baseResponse.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    // Location invalid
                    return null;
                default:
                    // Server probably down
                    return null;
            }
        }

        // Gets JSON Array which contains movie objects
        JSONArray moviesArray = baseResponse.getJSONArray("results");

        // Initializes content values
        ContentValues[] popularMoviesContentValues = new ContentValues[moviesArray.length()];

        // Iterates movies array
        for (int i = 0; i < moviesArray.length(); i++) {

            // The values that will be collected
            int id;
            String title;
            String releaseDate;
            String posterPath;
            String backdropPath;
            double voteAverage;
            String overview;

            // Gets the JSON object representing a movie
            JSONObject movie = moviesArray.getJSONObject(i);

            // Gets the int representing the movie ID
            id = movie.getInt("id");

            // Gets the String representing the movie title
            title = movie.getString("title");

            // Gets the String representing the movie release date,
            // which is already in human readable format
            releaseDate = movie.getString("release_date");

            // Gets the String representing the movie poster path
            posterPath = movie.getString("poster_path");

            // Gets the String representing the backdrop path for the horizontal image of the movie
            backdropPath = movie.getString("backdrop_path");

            // Gets the double representing the movie vote average (rating)
            voteAverage = movie.getDouble("vote_average");

            // Gets the String representing the movie plot synopsis (overview)
            overview = movie.getString("overview");

            // Puts values extracted from Json to content values
            ContentValues popularMovieValues = new ContentValues();
            popularMovieValues.put(PopularMovieEntry.COLUMN_MOVIE_ID, id);
            popularMovieValues.put(PopularMovieEntry.COLUMN_MOVIE_TITLE, title);
            popularMovieValues.put(PopularMovieEntry.COLUMN_MOVIE_RELEASE_DATE, releaseDate);
            popularMovieValues.put(PopularMovieEntry.COLUMN_MOVIE_POSTER_PATH, posterPath);
            popularMovieValues.put(PopularMovieEntry.COLUMN_MOVIE_BACKDROP_PATH, backdropPath);
            popularMovieValues.put(PopularMovieEntry.COLUMN_MOVIE_RATING, voteAverage);
            popularMovieValues.put(PopularMovieEntry.COLUMN_MOVIE_OVERVIEW, overview);

            // Adds content values of each movie to content values array
            popularMoviesContentValues[i] = popularMovieValues;

        }
        return popularMoviesContentValues;
    }

    // Parses JSON from a web response and returns an array
    // of content values describing a set of top rated movies
    public static ContentValues[] getTopRatedMovieContentValuesFromJson(String movieJsonStr)
            throws JSONException {

        // Gets the base JSON response
        JSONObject baseResponse = new JSONObject(movieJsonStr);

        // Checks if there is an error?
        if (baseResponse.has(OWM_MESSAGE_CODE)) {

            int errorCode = baseResponse.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    // Location invalid
                    return null;
                default:
                    // Server probably down
                    return null;
            }
        }

        // Gets JSON Array which contains movie objects
        JSONArray moviesArray = baseResponse.getJSONArray("results");

        // Initializes content values
        ContentValues[] topRatedMoviesContentValues = new ContentValues[moviesArray.length()];

        // Iterates movies array
        for (int i = 0; i < moviesArray.length(); i++) {

            // The values that will be collected
            int id;
            String title;
            String releaseDate;
            String posterPath;
            String backdropPath;
            double voteAverage;
            String overview;

            // Gets the JSON object representing a movie
            JSONObject movie = moviesArray.getJSONObject(i);

            // Gets the int representing the movie ID
            id = movie.getInt("id");

            // Gets the String representing the movie title
            title = movie.getString("title");

            // Gets the String representing the movie release date,
            // which is already in human readable format
            releaseDate = movie.getString("release_date");

            // Gets the String representing the movie poster path
            posterPath = movie.getString("poster_path");

            // Gets the String representing the backdrop path for the horizontal image of the movie
            backdropPath = movie.getString("backdrop_path");

            // Gets the double representing the movie vote average (rating)
            voteAverage = movie.getDouble("vote_average");

            // Gets the String representing the movie plot synopsis (overview)
            overview = movie.getString("overview");

            // Puts values extracted from Json to content values
            ContentValues topRatedMovieValues = new ContentValues();
            topRatedMovieValues.put(TopRatedMovieEntry.COLUMN_MOVIE_ID, id);
            topRatedMovieValues.put(TopRatedMovieEntry.COLUMN_MOVIE_TITLE, title);
            topRatedMovieValues.put(TopRatedMovieEntry.COLUMN_MOVIE_RELEASE_DATE, releaseDate);
            topRatedMovieValues.put(TopRatedMovieEntry.COLUMN_MOVIE_POSTER_PATH, posterPath);
            topRatedMovieValues.put(TopRatedMovieEntry.COLUMN_MOVIE_BACKDROP_PATH, backdropPath);
            topRatedMovieValues.put(TopRatedMovieEntry.COLUMN_MOVIE_RATING, voteAverage);
            topRatedMovieValues.put(TopRatedMovieEntry.COLUMN_MOVIE_OVERVIEW, overview);

            // Adds content values of each movie to content values array
            topRatedMoviesContentValues[i] = topRatedMovieValues;

        }
        return topRatedMoviesContentValues;
    }

    // Parses JSON from a web response and returns an array of {@link Video}
    // objects describing a set of videos related to a specific movie
    public static List<Video> getMovieVideosFromJson(String movieJsonStr)
            throws JSONException {

        final String OWM_MESSAGE_CODE = "cod";

        // Gets the base JSON response
        JSONObject baseResponse = new JSONObject(movieJsonStr);

        // Checks if there is an error?
        if (baseResponse.has(OWM_MESSAGE_CODE)) {

            int errorCode = baseResponse.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    // Location invalid
                    return null;
                default:
                    // Server probably down
                    return null;
            }
        }

        // Gets JSON array which contains a list of {@link Movie} objects
        JSONArray videosArray = baseResponse.getJSONArray("results");

        // Creates an empty array list that videos will be added to
        List<Video> videos = new ArrayList<>();

        // Iterates videos array
        for (int i = 0; i < videosArray.length(); i++) {

            // The values that will be collected
            String type;
            String key;
            String name;

            // Gets the JSON object representing a video
            JSONObject video = videosArray.getJSONObject(i);

            // Gets the String of the video name
            name = video.getString("name");

            // Gets the String representing the video type
            type = video.getString("type");

            // Gets the String representing the video key
            key = video.getString("key");

            // Creates a new {@link Video} object with the name,
            // type and key from the JSON response
            Video newVideo = new Video(name, type, key);

            // Adds the new {@link Video} to the list of videos.
            videos.add(newVideo);
        }
        return videos;
    }

    // Parses JSON from a web response and returns an array of {@link Review}
    // objects describing a set of videos related to a specific movie
    public static List<Review> getMovieReviewsFromJson(String movieJsonStr)
            throws JSONException {

        final String OWM_MESSAGE_CODE = "cod";

        // Gets the base JSON response
        JSONObject baseResponse = new JSONObject(movieJsonStr);

        // Checks if there is an error?
        if (baseResponse.has(OWM_MESSAGE_CODE)) {
            int errorCode = baseResponse.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    // Location invalid
                    return null;
                default:
                    // Server probably down
                    return null;
            }
        }

        // Gets JSON Array which contains {@link Review} objects
        JSONArray reviewsArray = baseResponse.getJSONArray("results");

        // Creates an empty array list that reviews will be added to
        List<Review> reviews = new ArrayList<>();

        // Iterates reviews array
        for (int i = 0; i < reviewsArray.length(); i++) {

            // The values that will be collected
            String author;
            String content;
            String url;

            // Gets the JSON object representing a review
            JSONObject review = reviewsArray.getJSONObject(i);

            // Gets the String representing the review author
            author = review.getString("author");

            // Gets the String representing the review content
            content = review.getString("content");

            // Gets the String representing the review key,
            // if later it would be decided to show reviews in a website
            url = review.getString("url");

            // Creates a new {@link Review} object with the
            // author, content and url from the JSON response
            Review newReview = new Review(author, content, url);

            // Adds the new {@link Review} to the list of reviews
            reviews.add(newReview);
        }
        return reviews;
    }
}