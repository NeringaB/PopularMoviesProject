package com.tioliaapp.android.tioliamovies.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.text.format.DateUtils;

import com.tioliaapp.android.tioliamovies.data.MovieContract.PopularMovieEntry;
import com.tioliaapp.android.tioliamovies.data.MovieContract.TopRatedMovieEntry;
import com.tioliaapp.android.tioliamovies.data.Preferences;
import com.tioliaapp.android.tioliamovies.utilities.MovieDbJsonUtils;
import com.tioliaapp.android.tioliamovies.utilities.NetworkUtils;
import com.tioliaapp.android.tioliamovies.utilities.NotificationUtils;

import java.net.URL;

public class MovieDataSyncTask {

    // Performs the network request for updated movie data, parses the JSON from that request,
    // and inserts the new popular and top rated movie information into the ContentProvider.
    // In addition to this, this will notify the user that the new movie data has been loaded
    // if the user hasn't been notified of the most popular movie within the last day
    // AND they haven't disabled notifications in the preferences screen.
    synchronized public static void syncMovieData(Context context) {

        try {

            // Gets a handle on the ContentResolver to delete and insert data
            ContentResolver contentResolver = context.getContentResolver();

            // Fetches new popular movie data

            // The getPopularMoviesUrl method returns the URL that
            // will be used to get the popular movie data JSON.
            URL popularMoviesRequestUrl = NetworkUtils.getPopularMoviesUrl();

            // Uses the URL to retrieve popular movies JSON
            String jsonPopularMoviesResponse = NetworkUtils
                    .getResponseFromHttpUrl(popularMoviesRequestUrl);

            // Parses JSON into a list of popular movie values
            ContentValues[] popularMovieValues = MovieDbJsonUtils
                    .getPopularMovieContentValuesFromJson(jsonPopularMoviesResponse);

            // If the JSON contains an error code, getMoviesContentValuesFromJson would return null.
            // Checks for these cases to prevent any NullPointerExceptions being thrown.
            // There is also no reason to insert fresh data if there isn't any to insert.
            if (popularMovieValues != null && popularMovieValues.length != 0) {

                // Deletes old popular movie data because there is no need to keep multiple movies data
                contentResolver.delete(
                        PopularMovieEntry.CONTENT_URI,
                        null,
                        null);

                // Inserts new popular movie data into ContentProvider
                contentResolver.bulkInsert(
                        PopularMovieEntry.CONTENT_URI,
                        popularMovieValues);
            }

            // Fetches new top rated movie data

            // The getTopRatedMoviesUrl method will return the URL
            // that will be used to get the top rated movie data JSON.
            URL topRatedMoviesRequestUrl = NetworkUtils.getTopRatedMoviesUrl();

            // Uses the URL to retrieve top rated JSON
            String jsonTopRatedMoviesResponse = NetworkUtils.
                    getResponseFromHttpUrl(topRatedMoviesRequestUrl);

            // Parses JSON into a list of top rated movie values
            ContentValues[] topRatedMovieValues = MovieDbJsonUtils.
                    getTopRatedMovieContentValuesFromJson(jsonTopRatedMoviesResponse);

            // If the JSON contains an error code, getMoviesContentValuesFromJson would return null.
            // Checks for these cases to prevent any NullPointerExceptions being thrown.
            // There is also no reason to insert fresh data if there isn't any to insert.
            if (topRatedMovieValues != null && topRatedMovieValues.length != 0) {

                // Deletes old top rated movie data because there is no need to keep multiple movies data
                contentResolver.delete(
                        TopRatedMovieEntry.CONTENT_URI,
                        null,
                        null);

                // Inserts new top rated movie data into ContentProvider
                contentResolver.bulkInsert(
                        TopRatedMovieEntry.CONTENT_URI,
                        topRatedMovieValues);
            }

            // After all the data is inserted into the ContentProvider, determines
            // whether or not notify the user that the movie data has been refreshed.
            boolean notificationsEnabled = Preferences.areNotificationsEnabled(context);

            // Check when the last notification was shown
            long timeSinceLastNotification = Preferences
                    .getElapsedTimeSinceLastNotification(context);

            // Initializes boolean
            boolean oneDayPassedSinceLastNotification = false;

            // Checks if the last notification was shown more than 1 day ago,
            // if it was, set oneDayPassedSinceLastNotification to true
            if (timeSinceLastNotification >= DateUtils.DAY_IN_MILLIS) {
                oneDayPassedSinceLastNotification = true;
            }

            // If the user wants notifications to be shown about the most popular movie
            // and one day has passed from the last notification,
            // Popular Movies app should send another notification
            if (notificationsEnabled && oneDayPassedSinceLastNotification) {
                NotificationUtils.notifyUserOfTheMostPopularMovie(context);
            }

        } catch (Exception e) {
            // Server probably invalid
            e.printStackTrace();
        }
    }
}