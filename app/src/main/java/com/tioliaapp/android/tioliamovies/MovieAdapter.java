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
    private final MovieAdapterOnClickHandler mClickHandler;
    private Cursor mCursor;

    // Creates a MovieAdapter.
    public MovieAdapter(@NonNull Context context, MovieAdapterOnClickHandler clickHandler) {
        this.context = context;
        mClickHandler = clickHandler;
    }

    // This gets called when each new ViewHolder is created.
    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder movieAdapterViewHolder, int position) {
        mCursor.moveToPosition(position);

        // Get movie poster path
        String posterPath = mCursor.getString(MainActivity.INDEX_MOVIE_POSTER_PATH);
        // Create complete movie poster path
        String completePosterPath = "http://image.tmdb.org/t/p/w342/" + posterPath;
        // Load poster to the ImageView using Picasso
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
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    // Used to refresh movie data
    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
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

        // When movie poster is clicked, get the movie id
        // and send it to that movie's DetailActivity.
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int movieId = mCursor.getInt(MainActivity.INDEX_MOVIE_ID);
            mClickHandler.onClick(movieId);
        }
    }
}