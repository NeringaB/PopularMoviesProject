package com.tioliaapp.android.tioliamovies;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * {@link MovieAdapter} exposes a list of movies from {@link android.database.Cursor}
 * to a {@link android.support.v7.widget.RecyclerView}
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private final Context context;

    // An on-click handler that makes it easy for an Activity to interface with the RecyclerView.
    private final MovieAdapterOnClickHandler clickHandler;

    private Cursor cursor;

    // Creates a MovieAdapter.
    public MovieAdapter(@NonNull Context context, MovieAdapterOnClickHandler clickHandler) {
        this.context = context;
        this.clickHandler = clickHandler;
    }

    // This gets called when each new ViewHolder is created.
    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        // Gets layout ID for list item
        int layoutIdForListItem = R.layout.movie_item;
        // Gets LayoutInflater
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);

        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder movieAdapterViewHolder, int position) {

        cursor.moveToPosition(position);

        // Gets movie poster path
        String posterPath = cursor.getString(MainActivity.INDEX_MOVIE_POSTER_PATH);
        // Creates complete movie poster path
        String completePosterPath = "http://image.tmdb.org/t/p/w342/" + posterPath;
        // Loads poster to the ImageView using Picasso
        Picasso.with(context)
                .load(completePosterPath)
                .fit()
                .centerCrop()
                // Maybe placeholder will be necessary later,
                // currently do not know what image to use here
                // and simply applied darker background
                //.placeholder(R.drawable.user_placeholder)
                .error(R.drawable.user_placeholder_error)
                .into(movieAdapterViewHolder.mMovieImageView);
    }

    // Returns the number of items to display.
    @Override
    public int getItemCount() {

        // If the cursor is not null, returns 0
        if (null == cursor) return 0;
        // Otherwise, gets item count
        return cursor.getCount();
    }

    // Used to refresh movie data
    void swapCursor(Cursor newCursor) {
        cursor = newCursor;
        // Notifies that the data set changed,
        // so that the data displayed would be updated
        notifyDataSetChanged();
    }

    // The interface that receives onClick messages.
    public interface MovieAdapterOnClickHandler {
        void onClick(int movieId);
    }

    // Cache of the children views for a movie list item.
    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        @BindView(R.id.iv_movie_poster)
        ImageView mMovieImageView;

        public MovieAdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // When a movie poster is clicked,
            // Gets adapter position
            int adapterPosition = getAdapterPosition();
            // Moves to the data of the movie that was clicked on
            cursor.moveToPosition(adapterPosition);
            // Gets the movie id of the poster that was clicked
            int movieId = cursor.getInt(MainActivity.INDEX_MOVIE_ID);
            // Sends this movie ID to the movie's DetailActivity.
            clickHandler.onClick(movieId);
        }
    }
}