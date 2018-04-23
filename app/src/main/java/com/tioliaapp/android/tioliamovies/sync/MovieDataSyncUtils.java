package com.tioliaapp.android.tioliamovies.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.tioliaapp.android.tioliamovies.data.MovieContract.PopularMovieEntry;
import com.tioliaapp.android.tioliamovies.data.MovieContract.TopRatedMovieEntry;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

public class MovieDataSyncUtils {

    // Interval at which to sync the movie data
    private static final int SYNC_INTERVAL_HOURS = 3;
    private static final int SYNC_INTERVAL_SECONDS =
            (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3;

    // Sync tag to identify our sync job
    private static final String MOVIE_DATA_SYNC_TAG = "movie-data-sync";

    // Helps to track if initialization has been performed or not
    private static boolean initialized;

    // Schedules a repeating sync of movie data using FirebaseJobDispatcher.
    static void scheduleFirebaseJobDispatcherSync(@NonNull final Context context) {

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        // Creates the Job to periodically sync movie data
        Job syncMovieDataJob = dispatcher.newJobBuilder()
                // The Service that will be used to sync movie data
                .setService(MovieDataFirebaseJobService.class)
                // The UNIQUE tag used to identify this Job
                .setTag(MOVIE_DATA_SYNC_TAG)
                // Network constraints on which this Job should run.
                .setConstraints(Constraint.ON_ANY_NETWORK)
                // Job should persist forever
                .setLifetime(Lifetime.FOREVER)
                // Movie data should stay up to date, so the Job should recur
                .setRecurring(true)
                // The movie data should be synced about every 4 hours
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                // If a Job with the provided tag already exists,
                // this new job should replace the old one.
                .setReplaceCurrent(true)
                // Once the Job is ready, this returns the Job
                .build();

        // Schedules the Job with the dispatcher
        dispatcher.schedule(syncMovieDataJob);
    }

    // Creates periodic sync tasks and checks to see if an immediate sync is required. If an
    // immediate sync is required, this method will take care of making sure that sync occurs.
    synchronized public static void initialize(@NonNull final Context context) {

        // If initialization has already been performed, we have nothing to do in this method.
        if (initialized) return;

        // If the method body is executed, set initialized to true
        initialized = true;

        // This triggers to create task and synchronize movie data periodically
        scheduleFirebaseJobDispatcherSync(context);

        // Checks if the ContentProvider has data to display. Creates a thread in which
        // the query will be run to check the contents of the ContentProvider.
        Thread checkForEmpty = new Thread(new Runnable() {
            @Override
            public void run() {

                // URIs for every row of movie data in the popular and top rated movies table
                Uri popularMoviesQueryUri = PopularMovieEntry.CONTENT_URI;
                Uri topRatedMoviesQueryUri = TopRatedMovieEntry.CONTENT_URI;

                // Since this query is going to be used only as a check to see
                // if any data exists, only ID of each row should be projected
                String[] popularMoviesProjectionColumns = {PopularMovieEntry._ID};
                String[] topRatedMoviesProjectionColumns = {TopRatedMovieEntry._ID};

                // Performs the query to check if popular
                // movies table has any popular movie data
                Cursor popularMoviesCursor = context.getContentResolver().query(
                        popularMoviesQueryUri,
                        popularMoviesProjectionColumns,
                        null,
                        null,
                        null);

                // Performs the query to check if top rated
                // movies table has any top rated movie data
                Cursor topRatedMoviesCursor = context.getContentResolver().query(
                        topRatedMoviesQueryUri,
                        topRatedMoviesProjectionColumns,
                        null,
                        null,
                        null);

                // Checks if cursors are null OR if they are empty, if they are,
                // sync should be performed immediately to be able to display data to the user.
                if (null == popularMoviesCursor || popularMoviesCursor.getCount() == 0 ||
                        null == topRatedMoviesCursor || topRatedMoviesCursor.getCount() == 0) {
                    startImmediateSync(context);
                }

                // Closes cursors to avoid memory leaks
                popularMoviesCursor.close();
                topRatedMoviesCursor.close();
            }
        });

        // Once the thread is prepared, fire it off to perform checks. */
        checkForEmpty.start();
    }

    // Helper method to perform a sync immediately using
    // an IntentService for asynchronous execution.
    public static void startImmediateSync(@NonNull final Context context) {

        Intent intentToSyncImmediately = new Intent(context, MovieDataSyncIntentService.class);
        context.startService(intentToSyncImmediately);
    }
}