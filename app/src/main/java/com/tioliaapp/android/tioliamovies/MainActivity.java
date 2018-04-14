package com.tioliaapp.android.tioliamovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.tioliaapp.android.tioliamovies.data.MovieContract.FavouriteMovieEntry;
import com.tioliaapp.android.tioliamovies.data.MovieContract.PopularMovieEntry;
import com.tioliaapp.android.tioliamovies.data.MovieContract.TopRatedMovieEntry;
import com.tioliaapp.android.tioliamovies.data.Preferences;
import com.tioliaapp.android.tioliamovies.sync.MovieDataSyncUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
        MovieAdapter.MovieAdapterOnClickHandler,
        SharedPreferences.OnSharedPreferenceChangeListener {

    // Indices of the values in the array of projection used in the loader
    // below to more quickly be able to access the data from the query.
    public static final int INDEX_MOVIE_ID = 0;
    public static final int INDEX_MOVIE_POSTER_PATH = 1;
    private static final String TAG = MainActivity.class.getSimpleName();
    // The ID which will be used to identify the Loader responsible
    // for loading movie data in the MainActivity
    private static final int ID_MOVIE_DATA_LOADER = 44;
    // Boolean flag for preference updates
    private static boolean PREFERENCES_HAVE_BEEN_UPDATED = false;
    @BindView(R.id.recyclerview_movie)
    RecyclerView recyclerView;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar loadingIndicator;
    Parcelable layoutManagerSavedState;
    private MovieAdapter movieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Gets the number of how many columns will be displayed in the GridLayout
        // The number depends on the portrait (2) and landscape (4) modes
        int posterColumns = getResources().getInteger(R.integer.poster_columns);
        // Creates GridLayout with GridLayoutManager
        GridLayoutManager layoutManager = new GridLayoutManager(this, posterColumns);

        recyclerView.setLayoutManager(layoutManager);

        // Improves performance if changes in content do not
        // change the child layout size in the RecyclerView
        recyclerView.setHasFixedSize(true);

        // The MovieAdapter is responsible for linking movie data
        // with the Views that will end up displaying movie posters
        movieAdapter = new MovieAdapter(MainActivity.this, this);

        // Attaches the adapter to the RecyclerView in the layout.
        recyclerView.setAdapter(movieAdapter);

        showLoading();

        // Ensures a loader is initialized and active.
        // If the loader doesn't already exist, one is created and
        // if the activity is currently started starts the loader.
        // Otherwise the last created loader is re-used.
        getSupportLoaderManager().initLoader(ID_MOVIE_DATA_LOADER, null, this);

        MovieDataSyncUtils.initialize(this);

        // Registers MainActivity as an OnPreferenceChangedListener
        // to receive a callback when a SharedPreference has changed.
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {

        // The following two values depend on user's choice which movies to display
        // (popular, top rated or favourite movies)
        //
        // Uri that will be used to query tables
        Uri moviesQueryUri = null;
        // The columns of data that we are interested in displaying
        // within our MainActivity's list of weather data.
        String[] mainMovieDataProjection = new String[0];

        Context context = getBaseContext();

        // Gets the user's show movies preference key value which is used to decide
        // which movies to show (popular, top rated or favourite)
        String showMoviesValue = Preferences.getUserShowMoviesChoice(context);

        // Switch statement to check loader's ID
        switch (loaderId) {

            // If it is ID_DETAIL_MOVIE_DATA_LOADER
            case ID_MOVIE_DATA_LOADER:

                // Checks if the user selected popular or top rated movies
                if (showMoviesValue.equals(context.getString(R.string.pref_show_movies_popular_value))) {
                    // User selected popular movies
                    // Creates uri for popular movies table
                    moviesQueryUri = PopularMovieEntry.CONTENT_URI;
                    // Creates projection for columns in the popular movies table
                    mainMovieDataProjection = new String[]{
                            PopularMovieEntry.COLUMN_MOVIE_ID,
                            PopularMovieEntry.COLUMN_MOVIE_POSTER_PATH,
                    };

                } else if (showMoviesValue.equals(context.getString(R.string.pref_show_movies_top_rated_value))) {

                    // User selected top rated movies
                    // Creates uri for top rated movies table
                    moviesQueryUri = TopRatedMovieEntry.CONTENT_URI;
                    // Creates projection for columns in the top rated movies table
                    mainMovieDataProjection = new String[]{
                            TopRatedMovieEntry.COLUMN_MOVIE_ID,
                            TopRatedMovieEntry.COLUMN_MOVIE_POSTER_PATH,
                    };

                } else if (showMoviesValue.equals(context.getString(R.string.pref_show_movies_favourite_value))) {

                    // User selected favourite movies
                    // Creates uri for favourite movies table
                    moviesQueryUri = FavouriteMovieEntry.CONTENT_URI;
                    mainMovieDataProjection = new String[]{
                            FavouriteMovieEntry.COLUMN_MOVIE_ID,
                            FavouriteMovieEntry.COLUMN_MOVIE_POSTER_PATH,
                    };
                }

                return new CursorLoader(this,
                        moviesQueryUri,
                        mainMovieDataProjection,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);

        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        // Shows the data from the cursor in the DetailActivity
        movieAdapter.swapCursor(data);
        // Gets the position of the LayoutManager
        recyclerView.getLayoutManager().onRestoreInstanceState(layoutManagerSavedState);

        if (data.getCount() != 0) showMovieDataView();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Since this loader's data is now invalid,
        // clears the adapter that is displaying the data.
        movieAdapter.swapCursor(null);
    }

    // Method is overridden by the MainActivity class in order to handle RecyclerView item clicks
    @Override
    public void onClick(int movieId) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra("movieId", movieId);
        startActivity(intentToStartDetailActivity);
    }

    // Hides loading indicator and makes the view for the movie data visible
    private void showMovieDataView() {
        // Firstly, makes the loading indicator invisible
        loadingIndicator.setVisibility(View.INVISIBLE);
        // Secondly, makes sure the movie data is visible
        recyclerView.setVisibility(View.VISIBLE);
    }

    // Hides the movie data invisible and makes the loading indicator visible
    private void showLoading() {
        // Firstly, makes sure the movie data is invisible
        recyclerView.setVisibility(View.INVISIBLE);
        // Secondly, makes sure the loading indicator is visible
        loadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // If the preferences have changed since the user was last in MainActivity,
        // perform another query and set the flag to false.
        if (PREFERENCES_HAVE_BEEN_UPDATED) {
            getSupportLoaderManager().restartLoader(ID_MOVIE_DATA_LOADER, null, this);
            PREFERENCES_HAVE_BEEN_UPDATED = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Unregisters MainActivity as an OnPreferenceChangedListener to avoid any memory leaks.
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Gets a handle on the menu inflater
        MenuInflater inflater = getMenuInflater();
        // Inflates the menu layout
        inflater.inflate(R.menu.main, menu);
        // Returns true so that the menu is displayed in the Toolbar
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Gets the ID of the clicked item
        int id = item.getItemId();

        // If settings menu item is clicked
        if (id == R.id.action_settings) {
            // Starts new intent to open SettingsActivity
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        // Sets the flag to true so that when control returns
        // to MainActivity, it can refresh the data.
        PREFERENCES_HAVE_BEEN_UPDATED = true;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("RecyclerViewLayoutManager",
                recyclerView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        if (state != null) {
            // Restores the state of the LayoutManager
            layoutManagerSavedState = ((Bundle) state)
                    .getParcelable("RecyclerViewLayoutManager");
        }
        super.onRestoreInstanceState(state);
    }
}