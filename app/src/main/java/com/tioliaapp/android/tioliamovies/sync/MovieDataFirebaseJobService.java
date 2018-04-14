package com.tioliaapp.android.tioliamovies.sync;

import android.content.Context;
import android.os.AsyncTask;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class MovieDataFirebaseJobService extends JobService {

    private AsyncTask<Void, Void, Void> fetchMovieDataTask;

    // This is called by the Job Dispatcher to tell that the job should be started.
    // This method is run on the application's main thread and work should be offloaded
    // to a background thread so the AsyncTask should be used.
    @Override
    public boolean onStartJob(final JobParameters jobParameters) {

        fetchMovieDataTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Context context = getApplicationContext();
                com.tioliaapp.android.tioliamovies.sync.MovieDataSyncTask.syncMovieData(context);
                jobFinished(jobParameters, false);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                jobFinished(jobParameters, false);
            }
        };

        fetchMovieDataTask.execute();
        return true;
    }

    // Called when the scheduling engine has decided to interrupt the execution of a running job,
    // most likely because the runtime constraints associated with the job are no longer satisfied.
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        if (fetchMovieDataTask != null) {
            fetchMovieDataTask.cancel(true);
        }
        return true;
    }
}