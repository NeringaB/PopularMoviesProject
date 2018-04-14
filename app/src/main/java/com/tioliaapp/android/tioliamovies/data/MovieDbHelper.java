package com.tioliaapp.android.tioliamovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tioliaapp.android.tioliamovies.data.MovieContract.FavouriteMovieEntry;
import com.tioliaapp.android.tioliamovies.data.MovieContract.PopularMovieEntry;
import com.tioliaapp.android.tioliamovies.data.MovieContract.TopRatedMovieEntry;

/**
 * Manages a local database for movie data.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    // Database name
    public static final String DATABASE_NAME = "movies.db";

    // Database version
    private static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Creates string which defines popular movies table
        final String SQL_CREATE_POPULAR_MOVIES_TABLE =
                "CREATE TABLE " + PopularMovieEntry.TABLE_NAME + " (" +
                        PopularMovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        PopularMovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                        PopularMovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                        PopularMovieEntry.COLUMN_MOVIE_POSTER_PATH + " TEXT NOT NULL, " +
                        PopularMovieEntry.COLUMN_MOVIE_BACKDROP_PATH + " TEXT NOT NULL, " +
                        PopularMovieEntry.COLUMN_MOVIE_OVERVIEW + " TEXT NOT NULL, " +
                        PopularMovieEntry.COLUMN_MOVIE_RATING + " REAL NOT NULL, " +
                        PopularMovieEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL);";

        // Creates the popular movies table defined above
        sqLiteDatabase.execSQL(SQL_CREATE_POPULAR_MOVIES_TABLE);

        // Creates string which defines top rated movies table
        final String SQL_CREATE_TOP_RATED_MOVIES_TABLE =
                "CREATE TABLE " + TopRatedMovieEntry.TABLE_NAME + " (" +
                        TopRatedMovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        TopRatedMovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                        TopRatedMovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                        TopRatedMovieEntry.COLUMN_MOVIE_POSTER_PATH + " TEXT NOT NULL, " +
                        TopRatedMovieEntry.COLUMN_MOVIE_BACKDROP_PATH + " TEXT NOT NULL, " +
                        TopRatedMovieEntry.COLUMN_MOVIE_OVERVIEW + " TEXT NOT NULL, " +
                        TopRatedMovieEntry.COLUMN_MOVIE_RATING + " REAL NOT NULL, " +
                        TopRatedMovieEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL);";

        // Creates the top rated movies table defined above
        sqLiteDatabase.execSQL(SQL_CREATE_TOP_RATED_MOVIES_TABLE);

        // Creates string which defines favourite movies table
        final String SQL_CREATE_FAVORITE_MOVIES_TABLE =
                "CREATE TABLE " + FavouriteMovieEntry.TABLE_NAME + " (" +
                        FavouriteMovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        FavouriteMovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                        FavouriteMovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                        FavouriteMovieEntry.COLUMN_MOVIE_POSTER_PATH + " TEXT NOT NULL, " +
                        FavouriteMovieEntry.COLUMN_MOVIE_BACKDROP_PATH + " TEXT NOT NULL, " +
                        FavouriteMovieEntry.COLUMN_MOVIE_OVERVIEW + " TEXT NOT NULL, " +
                        FavouriteMovieEntry.COLUMN_MOVIE_RATING + " REAL NOT NULL, " +
                        FavouriteMovieEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL);";

        // Creates the favourite movies table defined above
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // Delete popular and top rated movies table, because it does not contain any user specific
        // data and when the user opens Popular Movies app again, MovieDataSyncUtils initialize
        // method will fetch new data for popular and top rated movies
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PopularMovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TopRatedMovieEntry.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }
}