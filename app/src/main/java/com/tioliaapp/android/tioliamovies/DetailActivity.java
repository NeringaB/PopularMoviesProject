package com.tioliaapp.android.tioliamovies;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;
import com.tioliaapp.android.tioliamovies.data.MovieContract.FavouriteMovieEntry;
import com.tioliaapp.android.tioliamovies.data.MovieContract.PopularMovieEntry;
import com.tioliaapp.android.tioliamovies.data.MovieContract.TopRatedMovieEntry;
import com.tioliaapp.android.tioliamovies.data.Preferences;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    // Indices of the values in the array of projection used in the loader
    // below to be able to access the data from the query more quickly.
    public static final int INDEX_MOVIE_ID = 0;
    public static final int INDEX_MOVIE_TITLE = 1;
    public static final int INDEX_MOVIE_POSTER_PATH = 2;
    public static final int INDEX_MOVIE_BACKDROP_PATH = 3;
    public static final int INDEX_MOVIE_OVERVIEW = 4;
    public static final int INDEX_MOVIE_RATING = 5;
    public static final int INDEX_MOVIE_RELEASE_DATE = 6;

    // In this Activity, the user can share the selected movie's information
    // and suggest to check the movie on the Popular Movies app.
    // Hashtag is used to reference the app.
    private static final String MOVIE_SHARE_HASHTAG = "#PopularMoviesApp";

    // The ID which will be used to identify the Loader responsible for loading movie details
    private static final int ID_MOVIE_DETAILS_DATA_LOADER = 11;
    // The ID which will be used to identify the Loader responsible for loading movie videos
    private static final int ID_MOVIE_VIDEOS_DATA_LOADER = 12;
    // The ID which will be used to identify the Loader responsible for loading movie reviews
    private static final int ID_MOVIE_REVIEWS_DATA_LOADER = 13;

    private final String TAG = DetailActivity.class.getSimpleName();

    // Finds the Toolbar
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    // Finds the ImageView of the CollapsingToolbar
    @BindView(R.id.iv_toolbar_image)
    ImageView toolbarImage;
    // Finds RecyclerView
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    // Finds ProgressBar
    @BindView(R.id.pb_loading_indicator)
    ProgressBar loadingIndicator;
    // Finds CoordinatorLayout for the SnackBar
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    String movieTitle;
    String movieReleaseDate;
    double movieRating;
    String movieBackdropPath;
    Parcelable layoutManagerSavedState;

    private DetailAdapter detailAdapter;
    private int currentMovieId;
    private List<Video> videos;
    private List<Review> reviews;
    private String movieSummary;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        context = getApplicationContext();

        // Gets the intent that started DetailActivity
        Intent intentThatStartedThisActivity = getIntent();
        // Checks if the intent is not null
        if (intentThatStartedThisActivity != null) {
            // Checks if the intent has extra with the key "movieId"
            if (intentThatStartedThisActivity.hasExtra("movieId")) {
                // If it does, gets key's value
                currentMovieId = intentThatStartedThisActivity
                        .getIntExtra("movieId", 0);
            }
        }

        // Set CollapsingToolbar as the action bar
        setSupportActionBar(toolbar);
        // Get the action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // Do not show title
        actionBar.setDisplayShowTitleEnabled(false);

        detailAdapter = new DetailAdapter(this, videos, reviews, coordinatorLayout);

        // Gets device orientation
        int orientation = getResources().getConfiguration().orientation;
        // If device is in portrait orientation
        if (orientation == 1) {

            // Creates LinearLayoutManager
            LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                    LinearLayoutManager.VERTICAL, false);
            // Sets LinearLayoutManager
            recyclerView.setLayoutManager(layoutManager);

            // If device is in landscape orientation
        } else {

            // Create GridLayoutManager
            GridLayoutManager layoutManager =
                    new GridLayoutManager(this, 2);

            // Gets how many columns an item has to span
            layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {

                    // Get view type
                    switch(detailAdapter.getItemViewType(position)) {
                        // If the view is of VIEW_TYPE_MOVIE_DETAILS, it has to span two columns
                        case 0:
                            return 2;
                        // If the view is of VIEW_TYPE_MOVIE_VIDEOS, it has to span one column
                        case 1:
                            return 1;
                        // If the view is of VIEW_TYPE_MOVIE_REVIEWS, it has to span two columns
                        case 2:
                            return 2;
                        default:
                            return 2;
                    }
                }
            });

            // Sets GridLayoutManager
            recyclerView.setLayoutManager(layoutManager);
        }

        recyclerView.setAdapter(detailAdapter);

        // Informs the user that the data is being loaded
        showLoading();

        // Initializes movie details loader
        getLoaderManager().initLoader(ID_MOVIE_DETAILS_DATA_LOADER,
                null, movieDetailsLoaderListener);

        // Initializes movie videos loader
        getLoaderManager().initLoader(ID_MOVIE_VIDEOS_DATA_LOADER,
                null, movieVideosLoaderListener);

        // Initializes movie reviews loader
        getLoaderManager().initLoader(ID_MOVIE_REVIEWS_DATA_LOADER,
                null, movieReviewsLoaderListener);
    }

    // LoaderCallbacks listener for movie details
    private LoaderManager.LoaderCallbacks<Cursor> movieDetailsLoaderListener
            = new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

            // Converts movie ID to string.
            // It comes from the intent that opened DetailActivity
            String movieIdString = String.valueOf(currentMovieId);

            // The following three values depend on user's choice which movies to display
            // (popular, top rated or favourite movies)
            //
            // Uri that will be used to query tables
            Uri moviesQueryUri = null;
            // Selection
            String selection = null;
            // The columns of data that will be displayed within DetailActivity
            String[] detailMovieDataProjection = new String[0];

            // Uses movie ID to query data for the movie that is currently showed
            String[] selectionArgs = new String[]{movieIdString};

            // Gets the user's show movies preference key value which is used to decide
            // which movies to show (popular, top rated or favourite)
            String showMoviesValue = Preferences.getUserShowMoviesChoice(context);

            // Checks if the user selected popular movies
            if (showMoviesValue.equals(context
                    .getString(R.string.pref_show_movies_popular_value))) {

                // User selected popular movies
                // Creates uri for popular movies table
                moviesQueryUri = PopularMovieEntry.CONTENT_URI;
                selection = PopularMovieEntry.COLUMN_MOVIE_ID + "=?";
                // Creates projection for columns in the popular movies table
                detailMovieDataProjection = new String[]{
                        PopularMovieEntry.COLUMN_MOVIE_ID,
                        PopularMovieEntry.COLUMN_MOVIE_TITLE,
                        PopularMovieEntry.COLUMN_MOVIE_POSTER_PATH,
                        PopularMovieEntry.COLUMN_MOVIE_BACKDROP_PATH,
                        PopularMovieEntry.COLUMN_MOVIE_OVERVIEW,
                        PopularMovieEntry.COLUMN_MOVIE_RATING,
                        PopularMovieEntry.COLUMN_MOVIE_RELEASE_DATE,
                };

            } else if (showMoviesValue.equals(context
                    .getString(R.string.pref_show_movies_top_rated_value))) {

                // User selected top rated movies
                // Creates uri for top rated movies table
                moviesQueryUri = TopRatedMovieEntry.CONTENT_URI;
                selection = TopRatedMovieEntry.COLUMN_MOVIE_ID + "=?";
                // Creates projection for columns in the top rated movies table
                detailMovieDataProjection = new String[]{
                        TopRatedMovieEntry.COLUMN_MOVIE_ID,
                        TopRatedMovieEntry.COLUMN_MOVIE_TITLE,
                        TopRatedMovieEntry.COLUMN_MOVIE_POSTER_PATH,
                        TopRatedMovieEntry.COLUMN_MOVIE_BACKDROP_PATH,
                        TopRatedMovieEntry.COLUMN_MOVIE_OVERVIEW,
                        TopRatedMovieEntry.COLUMN_MOVIE_RATING,
                        TopRatedMovieEntry.COLUMN_MOVIE_RELEASE_DATE,
                };

            } else if (showMoviesValue.equals(context
                    .getString(R.string.pref_show_movies_favourite_value))) {

                // User selected favourite movies
                // Creates uri for favourite movies table
                moviesQueryUri = FavouriteMovieEntry.CONTENT_URI;
                selection = FavouriteMovieEntry.COLUMN_MOVIE_ID + "=?";
                // Creates projection for columns in the favourite movies table
                detailMovieDataProjection = new String[]{
                        FavouriteMovieEntry.COLUMN_MOVIE_ID,
                        FavouriteMovieEntry.COLUMN_MOVIE_TITLE,
                        FavouriteMovieEntry.COLUMN_MOVIE_POSTER_PATH,
                        FavouriteMovieEntry.COLUMN_MOVIE_BACKDROP_PATH,
                        FavouriteMovieEntry.COLUMN_MOVIE_OVERVIEW,
                        FavouriteMovieEntry.COLUMN_MOVIE_RATING,
                        FavouriteMovieEntry.COLUMN_MOVIE_RELEASE_DATE,
                };
            }

            return new CursorLoader(getApplicationContext(),
                    moviesQueryUri,
                    detailMovieDataProjection,
                    selection,
                    selectionArgs,
                    null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data != null) {

                // Shows the data from the cursor in the DetailActivity
                detailAdapter.swapCursor(data);
                // Gets the position of the LayoutManager
                recyclerView.getLayoutManager().onRestoreInstanceState(layoutManagerSavedState);

                // Checks if cursor is not empty
                if (data.getCount() != 0) {

                    // Shows movies
                    showMovieDataView();

                    // Moves cursor to first row
                    data.moveToFirst();
                    // Gets the data for a specific movie
                    // to create movie summary for the share intent
                    movieTitle = data.getString(INDEX_MOVIE_TITLE);
                    movieRating = data.getDouble(INDEX_MOVIE_RATING);
                    movieReleaseDate = data.getString(INDEX_MOVIE_RELEASE_DATE);
                    movieBackdropPath = data.getString(INDEX_MOVIE_BACKDROP_PATH);

                    // Gets current orientation of the device so that
                    // the right picture size would be used
                    // The bigger image is used when the orientation is landscape that
                    // the image would be crispy
                    int orientation = getResources().getConfiguration().orientation;
                    // Initializes baseUri for the complete image path
                    String baseUri;

                    // Checks what is the current orientation
                    if (orientation == 1) {
                        // If it is portrait, uses baseUri for w780 image
                        baseUri = "http://image.tmdb.org/t/p/w780/";
                    } else {
                        // If it s landscape, uses baseUri for w1280 image
                        baseUri = "http://image.tmdb.org/t/p/w1280/";
                    }

                    // Creates the complete movie backdrop path
                    String completeMovieBackdropPath = baseUri + movieBackdropPath;
                    // Loads the image into the toolbarImage
                    Picasso.with(getBaseContext())
                            .load(completeMovieBackdropPath)
                            .fit()
                            .centerCrop()
                            // Maybe placeholder or image when error occurs will be necessary later,
                            // currently do not know what image to use here
                            // and simply applied darker background
                            //.placeholder(R.drawable.user_placeholder)
                            //.error(R.drawable.user_placeholder_error)
                            .into(toolbarImage);
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            // Since this loader's data is now invalid,
            // clears the adapter that is displaying the data.
            detailAdapter.swapCursor(null);
        }
    };

    // LoaderCallbacks listener for movie videos
    private LoaderManager.LoaderCallbacks<List<Video>> movieVideosLoaderListener
            = new LoaderManager.LoaderCallbacks<List<Video>>() {

        @Override
        public Loader<List<Video>> onCreateLoader(int i, Bundle bundle) {

            // Converts current movie ID to string.
            String movieIdString = String.valueOf(currentMovieId);
            // Creates new MovieVideoLoader,
            // which uses current movie ID to query data for the movie that is currently showed
            return new MovieVideoLoader(context, movieIdString);
        }

        @Override
        public void onLoadFinished(Loader<List<Video>> loader, List<Video> data) {

            // Checks if a list of videos is not null
            if (data != null) {

                // Sets movie data
                detailAdapter.setMovieVideos(data);
                // Restores the state of the LayoutManager
                recyclerView.getLayoutManager().onRestoreInstanceState(layoutManagerSavedState);
            }
        }

        @Override
        public void onLoaderReset(Loader<List<Video>> loader) {
            // Since this loader's data is now invalid,
            // clears the adapter that is displaying the data.
            detailAdapter.setMovieVideos(null);
        }
    };

    // Loader Callbacks listener for movie reviews
    private LoaderManager.LoaderCallbacks<List<Review>> movieReviewsLoaderListener
            = new LoaderManager.LoaderCallbacks<List<Review>>() {

        @Override
        public Loader<List<Review>> onCreateLoader(int i, Bundle bundle) {

            // Converts current movie ID to string.
            String movieIdString = String.valueOf(currentMovieId);
            // Creates new MovieReviewLoader,
            // which uses current movie ID to query data for the movie that is currently showed
            return new MovieReviewLoader(getApplicationContext(), movieIdString);
        }

        @Override
        public void onLoadFinished(Loader<List<Review>> loader, List<Review> data) {

            // Checks if a list of reviews is not null
            if (data != null) {

                // Sets review data
                detailAdapter.setMovieReviews(data);
                // Restores the state of the LayoutManager
                recyclerView.getLayoutManager().onRestoreInstanceState(layoutManagerSavedState);
            }
        }

        @Override
        public void onLoaderReset(Loader<List<Review>> loader) {
            // Since this loader's data is now invalid,
            // clears the adapter that is displaying the data.
            detailAdapter.setMovieReviews(null);
        }
    };

    // Inflates and sets up the menu for Detail Activity.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Gets a handle on the menu inflater
        MenuInflater inflater = getMenuInflater();
        // Inflates the menu layout
        inflater.inflate(R.menu.detail, menu);
        // Returns true so that the menu is displayed in the Toolbar
        return true;
    }

    // The callback which is invoked when a menu item is selected from DetailActivity's menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Gets the ID of the clicked item
        int id = item.getItemId();

        // If share menu item is clicked
        if (id == R.id.action_share) {
            // Creates intent
            Intent shareIntent = createShareMovieIntent();
            // Starts intent activity
            startActivity(shareIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Uses the ShareCompat Intent builder to create movie data intent for sharing.
    private Intent createShareMovieIntent() {

        // Formats the movie that is currently opened release date and rating
        String formattedMovieReleaseDate = movieReleaseDate.substring(0, 4);
        String movieRatingString = String.valueOf(movieRating) + "/10";
        // Creates summary of the movie that is currently opened
        movieSummary = "Check " + movieTitle + " (" + formattedMovieReleaseDate + ") " +
                movieRatingString + " on " + MOVIE_SHARE_HASHTAG;
        // Creates share intent
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(movieSummary)
                .getIntent();
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        return shareIntent;
    }

    // Hides loading indicator and makes the view for the movie data visible
    private void showMovieDataView() {
        // Firstly, makes the loading indicator invisible
        loadingIndicator.setVisibility(View.INVISIBLE);
        // Secondly, makes sure the movie data is visible
        recyclerView.setVisibility(View.VISIBLE);
    }

    // Hides the movie data and makes the loading indicator visible
    private void showLoading() {
        // Firstly, makes sure the movie data is invisible
        recyclerView.setVisibility(View.INVISIBLE);
        // Secondly, makes sure the loading indicator is visible
        loadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Saves the state of the LayoutManager
        outState.putParcelable("RecyclerViewLayoutManager",
                recyclerView.getLayoutManager().onSaveInstanceState());
        super.onSaveInstanceState(outState);
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