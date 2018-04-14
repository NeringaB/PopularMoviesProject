package com.tioliaapp.android.tioliamovies.utilities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;

import com.tioliaapp.android.tioliamovies.DetailActivity;
import com.tioliaapp.android.tioliamovies.R;
import com.tioliaapp.android.tioliamovies.data.MovieContract.PopularMovieEntry;
import com.tioliaapp.android.tioliamovies.data.Preferences;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class NotificationUtils {

    // The columns of data that will be displayed within the notification
    public static final String[] MOVIE_DATA_NOTIFICATION_PROJECTION = {
            PopularMovieEntry.COLUMN_MOVIE_ID,
            PopularMovieEntry.COLUMN_MOVIE_TITLE,
            PopularMovieEntry.COLUMN_MOVIE_POSTER_PATH,
            PopularMovieEntry.COLUMN_MOVIE_RATING,
            PopularMovieEntry.COLUMN_MOVIE_RELEASE_DATE,
    };

    // Indices of the values in the array of Strings above to more quickly be able
    // to access the data from thr query. If the order of the Strings above changes, these
    // indices must be adjusted to match the order of the Strings.
    public static final int INDEX_MOVIE_ID = 0;
    public static final int INDEX_MOVIE_TITLE = 1;
    public static final int INDEX_MOVIE_POSTER_PATH = 2;
    public static final int INDEX_MOVIE_RATING = 3;
    public static final int INDEX_MOVIE_RELEASE_DATE = 4;

    // Notification ID which can be used to access our notification after we've displayed it.
    // This can be handy if there is a need to cancel the notification, or perhaps update it.
    // The number is arbitrary.
    private static final int MOVIE_DATA_NOTIFICATION_ID = 3004;

    // The notification channel id which will be used to link notifications to this channel
    private static final String POPULAR_MOVIE_NOTIFICATION_CHANNEL_ID =
            "popular_movie_notification_channel";

    // Constructs and displays a notification to let the user know there is
    // new movie data available and what is the current most popular movie
    public static void notifyUserOfTheMostPopularMovie(Context context) {

        int movieId;

        // Gets the URI for the most popular movies table
        Uri mostPopularMovieUri = PopularMovieEntry.CONTENT_URI;

        // Queries the popular movies table
        // The uri for the whole popular movies table is used to get the most popular movie
        // without any selection and selection args, because when selection and selection args
        // restrict query to the row whose id is 1, then notification works only for the first time,
        // but later no notifications are showed
        Cursor mostPopularMovieCursor = context.getContentResolver().query(
                mostPopularMovieUri,
                MOVIE_DATA_NOTIFICATION_PROJECTION,
                null,
                null,
                null);

        // If the cursor is not null and is not empty,
        // moveToFirst row and the notification will be shown
        if (mostPopularMovieCursor.moveToFirst()) {
            movieId = mostPopularMovieCursor.getInt(INDEX_MOVIE_ID);
            String movieTitle = mostPopularMovieCursor.getString(INDEX_MOVIE_TITLE);
            String moviePosterPath = mostPopularMovieCursor.getString(INDEX_MOVIE_POSTER_PATH);
            String movieRating = mostPopularMovieCursor.getString(INDEX_MOVIE_RATING);
            String movieReleaseDate = mostPopularMovieCursor.getString(INDEX_MOVIE_RELEASE_DATE);
            String formattedMovieReleaseString = movieReleaseDate.substring(0, 4);

            // Gets notification title which will be apps name
            String notificationTitle = context.getString(R.string.app_name);

            // Creates the text for the notification that appears when the movie data is refreshed.
            // The String will look something like this: "Most Popular: Title (2017) 8/10"
            String notificationText = "Most Popular: " + movieTitle + " (" +
                    formattedMovieReleaseString + ") " + movieRating + "/10";

            // Gets notification system service
            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);

            // Checks if the user's android version is oreo or higher
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                // If it is, creates new notification channel for the most popular movie notification
                NotificationChannel mChannel = new NotificationChannel(
                        POPULAR_MOVIE_NOTIFICATION_CHANNEL_ID,
                        context.getString(R.string.main_notification_channel_name),
                        NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(mChannel);
            }

            try {

                // Uses NotificationCompat Builder to build backward-compatible notifications
                NotificationCompat.Builder notificationBuilder = new NotificationCompat
                        .Builder(context, POPULAR_MOVIE_NOTIFICATION_CHANNEL_ID)
                        .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                        .setSmallIcon(R.drawable.ic_small_for_notification)
                        .setLargeIcon(Picasso.with(context)
                                .load("http://image.tmdb.org/t/p/w342/" + moviePosterPath)
                                .get())
                        .setContentTitle(notificationTitle)
                        .setContentText(notificationText)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationText))
                        .setDefaults(Notification.DEFAULT_VIBRATE);

                // The Intent which will be triggered when the user clicks the notification.
                // It opens DetailActivity to display the most popular movie.
                Intent detailIntentForMostPopularMovie = new Intent(context, DetailActivity.class);
                detailIntentForMostPopularMovie.putExtra("movieId", movieId);

                // Uses TaskStackBuilder to create the proper PendingIntent
                TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
                taskStackBuilder.addNextIntentWithParentStack(detailIntentForMostPopularMovie);
                PendingIntent detailPendingIntentForMostPopularMovie = taskStackBuilder
                        .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                // Sets the content intent to open DetailActivity for the movie
                notificationBuilder.setContentIntent(detailPendingIntentForMostPopularMovie)
                        .setAutoCancel(true);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                        && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
                }

                // Notifies the user with the ID MOVIE_DATA_NOTIFICATION_ID which
                // allows to update or cancel the notification later on
                notificationManager.notify(MOVIE_DATA_NOTIFICATION_ID, notificationBuilder.build());

                // Since we just showed a notification, save the current time.
                // This way, it will be possible to check next time the movie data
                // is refreshed if another notification should be shown.
                Preferences.saveLastNotificationTime(context, System.currentTimeMillis());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Closes the cursor to avoid memory leaks
        mostPopularMovieCursor.close();
    }
}