package com.tioliaapp.android.tioliamovies.sync;

import android.app.IntentService;
import android.content.Intent;

/**
 * An {@link IntentService} subclass for handling asynchronous
 * task requests in a service on a separate handler thread.
 */
public class MovieDataSyncIntentService extends IntentService {

    public MovieDataSyncIntentService() {
        super("MovieDataSyncIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        MovieDataSyncTask.syncMovieData(this);
    }
}