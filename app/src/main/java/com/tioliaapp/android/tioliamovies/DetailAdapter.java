package com.tioliaapp.android.tioliamovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.tioliaapp.android.tioliamovies.Review;
import com.tioliaapp.android.tioliamovies.data.MovieContract.FavouriteMovieEntry;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * {@link DetailAdapter}
 */

public class DetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_MOVIE_DETAILS = 0;
    private final int VIEW_TYPE_MOVIE_VIDEOS = 1;
    private final int VIEW_TYPE_MOVIE_REVIEWS = 2;
    private final Context context;
    boolean starButtonIsChecked;
    private List<Video> videos;
    private List<Review> reviews;
    private int movieId;
    private String movieTitle;
    private String moviePosterPath;
    private String backdropPath;
    private double movieRating;
    private String movieReleaseDate;
    private String movieOverview;
    private Cursor mCursor;
    private final CoordinatorLayout coordinatorLayout;

    public DetailAdapter(@NonNull Context context, List<Video> videos, List<Review> reviews,
                         CoordinatorLayout coordinatorLayout) {
        this.context = context;
        this.videos = videos;
        this.reviews = reviews;
        this.coordinatorLayout = coordinatorLayout;
    }

    // Returns view type based on the position
    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_MOVIE_DETAILS;
            // Also Check if videos is not null, because if it is null than
            // the app crashes trying to get size of a null list
        } else if (position > 0 && videos != null && position <= videos.size()) {
            return VIEW_TYPE_MOVIE_VIDEOS;
        } else {
            return VIEW_TYPE_MOVIE_REVIEWS;
        }
    }

    // Inflates layouts according to view type
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_MOVIE_DETAILS) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_movie_details, parent, false);
            return new ViewHolderMovieDetails(itemView);
        } else if (viewType == VIEW_TYPE_MOVIE_VIDEOS) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_movie_video, parent, false);
            return new ViewHolderVideo(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_movie_review, parent, false);
            return new ViewHolderReview(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MOVIE_DETAILS:
                ViewHolderMovieDetails viewHolderMovieDetails = (ViewHolderMovieDetails) holder;
                viewHolderMovieDetails.bindViews(context);
                break;
            case VIEW_TYPE_MOVIE_VIDEOS:
                ViewHolderVideo viewHolderVideo = (ViewHolderVideo) holder;
                viewHolderVideo.bindViews(context, position - 1);
                break;
            case VIEW_TYPE_MOVIE_REVIEWS:
                // Check if videos is not null, because if it is null than
                // the app crashes trying to get size of a null list
                if (videos != null) {
                    ViewHolderReview viewHolderReview = (ViewHolderReview) holder;
                    viewHolderReview.bindViews(position - 1 - videos.size());
                }
        }
    }

    // Returns correct item count taking into account if
    // a list of videos or a list of reviews is empty
    @Override
    public int getItemCount() {
        if (reviews == null && videos == null) {
            return 1;
        } else if (reviews == null) {
            return 1 + videos.size();
        } else if (videos == null) {
            return 1 + reviews.size();
        } else {
            return 1 + videos.size() + reviews.size();
        }
    }

    public int getVideosCount() {
        if (videos != null) {
            return videos.size();
        }
        return 0;
    }

    // Used to refresh movie videos
    public void setMovieVideos(List<Video> videos) {
        this.videos = videos;
        notifyDataSetChanged();
    }

    // Used to refresh movie reviews
    public void setMovieReviews(List<Review> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    // Inserts a movie to the favourite movies tale
    public void addMovieToFavouritesTable(Context context) {

        // Create new empty ContentValues object
        ContentValues contentValues = new ContentValues();

        // Puts all movie information into the ContentValues
        contentValues.put(FavouriteMovieEntry.COLUMN_MOVIE_ID, movieId);
        contentValues.put(FavouriteMovieEntry.COLUMN_MOVIE_TITLE, movieTitle);
        contentValues.put(FavouriteMovieEntry.COLUMN_MOVIE_POSTER_PATH, moviePosterPath);
        contentValues.put(FavouriteMovieEntry.COLUMN_MOVIE_BACKDROP_PATH, backdropPath);
        contentValues.put(FavouriteMovieEntry.COLUMN_MOVIE_OVERVIEW, movieOverview);
        contentValues.put(FavouriteMovieEntry.COLUMN_MOVIE_RATING, movieRating);
        contentValues.put(FavouriteMovieEntry.COLUMN_MOVIE_RELEASE_DATE, movieReleaseDate);

        // Inserts the content values via the ContentResolver
        Uri uri = context.getContentResolver()
                .insert(FavouriteMovieEntry.CONTENT_URI, contentValues);
    }

    // Deletes a movie from the favourite movies tale
    public void deleteMovieFromFavoritesTable(Context context) {

        // Gets the uri to access favourite movies table
        Uri uri = FavouriteMovieEntry.CONTENT_URI;

        // Creates selection and selection arguments to delete the current movie
        String movieIdString = String.valueOf(movieId);
        String selection = FavouriteMovieEntry.COLUMN_MOVIE_ID + "=?";
        String[] selectionArgs = new String[]{movieIdString};

        // Delete a single row of data using a ContentResolver
        int rowsDeleted = context.getContentResolver().delete(uri, selection, selectionArgs);
    }

    // Used to refresh movies
    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    // ViewHolder for movie details
    public class ViewHolderMovieDetails extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_movie_title)
        TextView movieTitleTextView;

        @BindView(R.id.iv_movie_poster)
        ImageView moviePosterImageView;

        @BindView(R.id.tv_movie_release_date)
        TextView movieReleaseDateTextView;

        @BindView(R.id.tv_movie_rating)
        TextView movieRatingTextView;

        @BindView(R.id.tv_movie_overview)
        TextView movieOverviewTextView;

        @BindView(R.id.ib_favorite_button)
        ImageButton starButton;

        public ViewHolderMovieDetails(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        // Binds views in with data
        public void bindViews(final Context context) {
            if (mCursor != null && mCursor.getCount() != 0) {
                mCursor.moveToFirst();

                // Shows the movie title
                movieTitle = mCursor.getString(DetailActivity.INDEX_MOVIE_TITLE);
                movieTitleTextView.setText(movieTitle);

                // Gets movie poster path and appends it to base uri
                moviePosterPath = mCursor.getString(DetailActivity.INDEX_MOVIE_POSTER_PATH);
                String completeMoviePosterPath = "http://image.tmdb.org/t/p/w342/" + moviePosterPath;
                // Shows the movie poster
                Picasso.with(context)
                        .load(completeMoviePosterPath)
                        .fit()
                        .centerCrop()
                        // Maybe placeholder will be necessary later,
                        // currently do not know what image to use here
                        // and simply applied darker background
                        //.placeholder(R.drawable.user_placeholder)
                        .error(R.drawable.user_placeholder_error)
                        .into(moviePosterImageView);

                // Gets and stores backdrop path to have all information related to a movie,
                // if the user later wants to add the movie to their favourites
                backdropPath = mCursor.getString(DetailActivity.INDEX_MOVIE_BACKDROP_PATH);

                // Gets movie release date and formats string so that it would show only release years
                movieReleaseDate = mCursor.getString(DetailActivity.INDEX_MOVIE_RELEASE_DATE);
                String formattedMovieReleaseDate = movieReleaseDate.substring(0, 4);
                // Shows the movie release date
                movieReleaseDateTextView.setText(formattedMovieReleaseDate);

                // Gets movie vote average (rating) and format so that
                // it would show how many points from 10 the movie has
                movieRating = mCursor.getDouble(DetailActivity.INDEX_MOVIE_RATING);
                String movieRatingString = String.valueOf(movieRating) + "/10";
                movieRatingTextView.setText(movieRatingString);

                // Show the movie review
                movieOverview = mCursor.getString(DetailActivity.INDEX_MOVIE_OVERVIEW);
                movieOverviewTextView.setText(movieOverview);

                // Queries favourite movies table on the background thread that
                // the Popular Movies app would know if the star button should be checked
                // or unchecked when the user opens DetailActivity.
                // It also checks the star button on the main thread,
                // if the movie is in the favourite movies table

                // Uri to query favourite movies table
                Uri moviesQueryUri = FavouriteMovieEntry.CONTENT_URI;

                // Projection has only COLUMN_MOVIE_ID, because the movie that is
                // currently displayed will be searched for in the favourite movies table
                // according to its id (id which is queried from the Movie Db)
                String[] detailMovieDataProjection = new String[]{
                        FavouriteMovieEntry.COLUMN_MOVIE_ID,
                };

                // Gets the id of the movie that is currently displayed
                movieId = mCursor.getInt(DetailActivity.INDEX_MOVIE_ID);

                // Converts movie id into a string
                String movieIdString = String.valueOf(movieId);

                // Selection and selectionArgs to search for
                // the id of the movie that is currently displayed
                String selection = FavouriteMovieEntry.COLUMN_MOVIE_ID + "=?";
                String[] selectionArgs = new String[]{movieIdString};

                // Performs the query
                Cursor favouriteMoviesCursor = context.getContentResolver().query(
                        moviesQueryUri,
                        detailMovieDataProjection,
                        selection,
                        selectionArgs,
                        null);

                Boolean isMovieIdInFavouritesTable = false;

                // Checks if the cursor is null or empty
                if (null != favouriteMoviesCursor
                        && favouriteMoviesCursor.getCount() != 0) {
                    // If it is not null or empty, moves cursor to the first row
                    favouriteMoviesCursor.moveToFirst();
                    // Gets the index of the COLUMN_MOVIE_ID of the favourite movies table
                    int movieIdColumnIndex = favouriteMoviesCursor
                            .getColumnIndex(FavouriteMovieEntry.COLUMN_MOVIE_ID);
                    // Gets the movie id
                    int movieIdFromFavouritesTable = favouriteMoviesCursor.getInt(movieIdColumnIndex);


                    // Once more checks that the movie ID from the cursor is equal
                    // to the ID of the movie that is currently displayed
                    if (movieIdFromFavouritesTable == movieId) {
                        // If it is, set isMovieIdInFavouritesTable to true
                        isMovieIdInFavouritesTable = true;
                    } else {
                        isMovieIdInFavouritesTable = false;
                    }
                }

                if (isMovieIdInFavouritesTable) {
                    // If true was returned, check star button
                    starButton.setImageDrawable(ContextCompat.getDrawable(context,
                            android.R.drawable.btn_star_big_on));
                    // Set starButtonIsChecked to true to follow if
                    // the star button id checked or unchecked
                    starButtonIsChecked = true;
                }

                // Sets on OnClickListener on the star button
                starButton.setOnClickListener(new Button.OnClickListener() {
                    public void onClick(View v) {
                        // If star button is unchecked
                        if (!starButtonIsChecked) {
                            // Sets checked star drawable
                            starButton.setImageDrawable(ContextCompat.getDrawable(context,
                                    android.R.drawable.btn_star_big_on));
                            // Saves the movie to the favorites table
                            addMovieToFavouritesTable(context);
                            // Sets starButtonIsChecked variable to true (star button is checked)
                            starButtonIsChecked = true;

                            // Shows SnackBar to inform the user that
                            // the movie was added to their favourites
                            Snackbar snackbar = Snackbar
                                    .make(coordinatorLayout, "Movie added to favourites",
                                            Snackbar.LENGTH_LONG)
                                    .setAction("UNDO", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            // Sets unchecked star drawable
                                            starButton.setImageDrawable(ContextCompat
                                                    .getDrawable(context,
                                                            android.R.drawable.btn_star_big_off));
                                            // Deletes the movie from the favorites table
                                            deleteMovieFromFavoritesTable(context);
                                            // Sets starButtonIsChecked variable to false
                                            // (star button is unchecked)
                                            starButtonIsChecked = false;
                                            Snackbar snackbar = Snackbar.make(coordinatorLayout,
                                                    "Movie removed from favourites",
                                                    Snackbar.LENGTH_LONG);
                                            snackbar.show();
                                        }
                                    });
                            snackbar.show();
                            // If star button is checked
                        } else {
                            // Sets unchecked star drawable
                            starButton.setImageDrawable(ContextCompat.getDrawable(context,
                                    android.R.drawable.btn_star_big_off));
                            // Deletes the movie from the favorites table
                            deleteMovieFromFavoritesTable(context);
                            // Sets starButtonIsChecked variable to false (star button is unchecked)
                            starButtonIsChecked = false;

                            // Shows SnackBar to inform the user that
                            // the movie was deleted from their favourites
                            Snackbar snackbar = Snackbar
                                    .make(coordinatorLayout, "Movie removed from favourites",
                                            Snackbar.LENGTH_LONG)
                                    .setAction("UNDO", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            // Sets checked star drawable
                                            starButton.setImageDrawable(ContextCompat
                                                    .getDrawable(context,
                                                            android.R.drawable.btn_star_big_on));
                                            // Saves the movie to the favorites table
                                            addMovieToFavouritesTable(context);
                                            // Sets starButtonIsChecked variable to true
                                            // (star button is checked)
                                            starButtonIsChecked = true;
                                            Snackbar snackbar = Snackbar.make(coordinatorLayout,
                                                    "Movie added to favourites",
                                                    Snackbar.LENGTH_LONG);
                                            snackbar.show();
                                        }
                                    });
                            snackbar.show();
                        }
                    }
                });


            }
        }
    }

    // ViewHolder for movie videos
    public class ViewHolderVideo extends RecyclerView.ViewHolder {

        @BindView(R.id.ib_play_arrow)
        ImageView moviePlayArrow;

        @BindView(R.id.tv_video_name)
        TextView movieVideoName;

        @BindView(R.id.tv_video_type)
        TextView movieVideoType;


        public ViewHolderVideo(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindViews(final Context context, final int position) {
            // Sets OnClickListener on moviePlayArrow button
            moviePlayArrow.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    // Gets the key of the current video
                    String videoKey = videos.get(position).getVideoKey();
                    // Creates intent with uri to watch video on Youtube
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("vnd.youtube:" + videoKey));
                    // Starts intent activity
                    context.startActivity(intent);
                }
            });

            // Gets and sets video name
            movieVideoName.setText(videos.get(position).getVideoName());

            // Gets and sets video type
            movieVideoType.setText(videos.get(position).getVideoType());
        }
    }

    // ViewHolder for movie reviews
    public class ViewHolderReview extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_review_author)
        TextView movieReviewAuthor;

        @BindView(R.id.tv_review_content)
        TextView movieReviewContent;

        public ViewHolderReview(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindViews(int position) {
            // Gets and sets review author
            movieReviewAuthor.setText(reviews.get(position).getAuthor());

            // Gets and sets review content
            movieReviewContent.setText(reviews.get(position).getContent());
        }
    }
}