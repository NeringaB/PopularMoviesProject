package com.tioliaapp.android.tioliamovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.tioliaapp.android.tioliamovies.data.MovieContract.FavouriteMovieEntry;
import com.tioliaapp.android.tioliamovies.data.MovieContract.PopularMovieEntry;
import com.tioliaapp.android.tioliamovies.data.MovieContract.TopRatedMovieEntry;

/**
 * This class serves as the ContentProvider for popular and top rated
 * movies data as well as user's favourite movies data.
 * It allows to insert data, query data, and delete data.
 */
public class MovieProvider extends ContentProvider {

    // Constants that will be used to match URIs with the data they are looking for.
    public static final int CODE_POPULAR_MOVIES = 100;
    public static final int CODE_POPULAR_MOVIE_WITH_MOVIE_ID = 101;
    public static final int CODE_TOP_RATED_MOVIES = 200;
    public static final int CODE_TOP_RATED_MOVIE_WITH_MOVIE_ID = 201;
    public static final int CODE_FAVOURITE_MOVIES = 300;
    public static final int CODE_FAVOURITE_MOVIE_WITH_MOVIE_ID = 301;

    // The URI Matcher used by this content provider.
    private static final UriMatcher uriMatcher = buildUriMatcher();

    private MovieDbHelper openHelper;

    // Creates the UriMatcher that will match each URI to the constants defined above.
    public static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        final String authority = MovieContract.CONTENT_AUTHORITY;

        // This URI is content://com.example.android.tioliamovies/popular
        matcher.addURI(authority, MovieContract.PATH_POPULAR_MOVIES, CODE_POPULAR_MOVIES);
        // This URI is something like content://com.example.android.tioliamovies/popular/123456
        matcher.addURI(authority, MovieContract.PATH_POPULAR_MOVIES + "/#",
                CODE_POPULAR_MOVIE_WITH_MOVIE_ID);

        // This URI is content://com.example.android.tioliamovies/topRated
        matcher.addURI(authority, MovieContract.PATH_TOP_RATED_MOVIES, CODE_TOP_RATED_MOVIES);
        // This URI is something like content://com.example.android.tioliamovies/topRated/123456
        matcher.addURI(authority, MovieContract.PATH_TOP_RATED_MOVIES + "/#",
                CODE_TOP_RATED_MOVIE_WITH_MOVIE_ID);

        // This URI is content://com.example.android.tioliamovies/favourite
        matcher.addURI(authority, MovieContract.PATH_FAVOURITE_MOVIES, CODE_FAVOURITE_MOVIES);
        // This URI is something like content://com.example.android.tioliamovies/favourite/123456
        matcher.addURI(authority, MovieContract.PATH_FAVOURITE_MOVIES + "/#",
                CODE_FAVOURITE_MOVIE_WITH_MOVIE_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        // Initializes MovieDbHelper
        openHelper = new MovieDbHelper(getContext());
        return true;
    }

    // This method is used to display popular, top rated and favourite movies
    // in the MainActivity and in the Detail Activity
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Cursor returnedCursor;

        // Switch statement that, given a URI, determines what kind
        // of request is being made and queries the database accordingly.
        switch (uriMatcher.match(uri)) {

            // When uriMatcher's match method is called with a URI that looks something like this:
            // content://com.example.android.tioliamovies/popular,
            // returns a cursor that contains every row of popular movie data in the table
            case CODE_POPULAR_MOVIES: {
                returnedCursor = openHelper.getReadableDatabase().query(
                        PopularMovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            }

            // When uriMatcher's match method is called with a URI that looks like this:
            // content://com.example.android.tioliamovies/topRated,
            // return a cursor that contains every row of top rated movie data in the table
            case CODE_TOP_RATED_MOVIES: {
                returnedCursor = openHelper.getReadableDatabase().query(
                        TopRatedMovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            }

            // When uriMatcher's match method is called with a URI that looks something like this:
            // content://com.example.android.tioliamovies/favourites,
            // returns a cursor that contains every row of favourite movie data in the table
            case CODE_FAVOURITE_MOVIES: {
                returnedCursor = openHelper.getReadableDatabase().query(
                        FavouriteMovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Set notification URI on the Cursor, so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        returnedCursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor
        return returnedCursor;
    }

    // This method is used only for favourite movies table to let users insert
    // one movie in the table when the star button is clicked
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        Uri returnedUri;

        // Switch statement that, given a URI, will determine what kind
        // of request is being made and query the database accordingly.
        switch (uriMatcher.match(uri)) {

            // When uriMatcher's match method is called with a URI that looks something like this:
            // content://com.example.android.tioliamovies/favourite,
            // returns a cursor that contains every row of favourite movie data in the table.
            case CODE_FAVOURITE_MOVIES: {

                // Inserts new favorite movie with the given values and stores its id
                long _id = openHelper.getWritableDatabase().insert(FavouriteMovieEntry.TABLE_NAME,
                        null, values);

                // If the _id is -1, the insertion failed so throws exception.
                if (_id > 0) {
                    returnedUri = ContentUris.withAppendedId(FavouriteMovieEntry.CONTENT_URI, _id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }

            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        // Notifies all listeners that the data has changed
        // for the favourite movies table content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Returns the new URI with the _id of the newly inserted row appended at the end
        return returnedUri;
    }

    // This method inserts a set of new rows. It is used to save popular
    // and top rated movie data after querying movies db server (to sync database)
    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {

        // Gets writable database, because it will have to be referenced several times
        final SQLiteDatabase db = openHelper.getWritableDatabase();

        int rowsInserted = 0;

        // Switch statement that, given a URI, will determine what kind
        // of request is being made and query the database accordingly.
        switch (uriMatcher.match(uri)) {

            // If the data has to be inserted into popular movies table
            case CODE_POPULAR_MOVIES:

                db.beginTransaction();

                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(PopularMovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }

                    db.setTransactionSuccessful();

                } finally {
                    db.endTransaction();
                }

                // If 1 or more rows have been inserted, notifies all listeners that
                // the data has changed for the popular movies table content URI
                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;

            // If the data has to be inserted into top rated movies table
            case CODE_TOP_RATED_MOVIES:

                db.beginTransaction();

                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(TopRatedMovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }

                    db.setTransactionSuccessful();

                } finally {
                    db.endTransaction();
                }

                // If 1 or more rows have been inserted, notifies all listeners that
                // the data has changed for the popular movies table content URI
                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;

            default:

                return super.bulkInsert(uri, values);
        }
    }

    // This method is used to delete all popular and top rated movies data before
    // inserting new data which is performed after querying Movie Db server
    // It is also used to delete favourite movie from the table, when the user unchecks
    // star button (it means that the movie is not favourite anymore)
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        // Gets writable database, because it will have to be referenced several times
        final SQLiteDatabase db = openHelper.getWritableDatabase();

        // The number of rows deleted to be returned.
        int numRowsDeleted;

        // Passing "1" for the selection will delete all rows and return the number
        // of rows deleted, which is what the caller of this method expects.
        if (null == selection) selection = "1";

        // Switch statement that, given a URI, will determine what kind
        // of request is being made and query the database accordingly.
        switch (uriMatcher.match(uri)) {

            // If the data has to be deleted from the popular movies table
            case CODE_POPULAR_MOVIES:
                numRowsDeleted = db.delete(
                        PopularMovieEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;

            // If the data has to be deleted from the top rated movies table
            case CODE_TOP_RATED_MOVIES:
                numRowsDeleted = db.delete(
                        TopRatedMovieEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;

            // If the data has to be deleted from the popular movies table
            case CODE_FAVOURITE_MOVIES:
                numRowsDeleted = db.delete(
                        FavouriteMovieEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;

            default:

                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // If any rows have been deleted, notify that a change has occurred to this URI
        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}