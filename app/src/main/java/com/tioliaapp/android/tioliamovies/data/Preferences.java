package com.tioliaapp.android.tioliamovies.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.tioliaapp.android.tioliamovies.R;

public class Preferences {

    // Returns true if the user prefers to see notifications
    // from Popular Movies app and false otherwise.
    // This preference can be changed by the user within the SettingsFragment.
    public static boolean areNotificationsEnabled(Context context) {

        // Gets the key for accessing the preference for showing notifications
        String displayNotificationsKey = context.getString(R.string.pref_enable_notifications_key);

        // In Popular Movies, the user has the ability to say
        // whether they would like notifications enabled or not.
        // If no preference has been chosen, we want to be able to determine whether
        // or not to show them so a bool stored in bools.xml is referenced.
        boolean shouldDisplayNotificationsByDefault = context.getResources()
                .getBoolean(R.bool.show_notifications_by_default);

        // Accesses the user's preferences
        SharedPreferences sharedPreferences = PreferenceManager.
                getDefaultSharedPreferences(context);

        // If a value is stored with the key, extracts it. If not, uses a default value. This way
        // it determines whether or not notify the user that the movie data has been refreshed.
        return sharedPreferences.getBoolean(displayNotificationsKey,
                shouldDisplayNotificationsByDefault);
    }

    // Returns the elapsed time in milliseconds since the last notification was shown. This is
    // a part of checking if we should show another notification when the movie data is updated.
    public static long getElapsedTimeSinceLastNotification(Context context) {

        long lastNotificationTimeMillis =
                Preferences.getLastNotificationTimeInMillis(context);

        return System.currentTimeMillis() - lastNotificationTimeMillis;
    }

    // Returns the last time that a notification was shown (in UNIX time)
    public static long getLastNotificationTimeInMillis(Context context) {

        // Gets the key for accessing the time at which a notification was last displayed
        String lastNotificationKey = context.getString(R.string.pref_last_notification);

        // Accesses the user's preferences
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        // Retrieves the time in milliseconds when the last notification was shown.
        // If SharedPreferences doesn't have a value for lastNotificationKey, returns 0.
        return sharedPreferences.getLong(lastNotificationKey, 0);
    }

    // Saves the time when a notification is shown.
    // This will be used to get the elapsed time since a notification was shown.
    public static void saveLastNotificationTime(Context context, long timeOfNotification) {

        // Accesses the user's preferences
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        // Gets editor that is needed to update preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Gets last notification key
        String lastNotificationKey = context.getString(R.string.pref_last_notification);

        // Adds current time in millis
        editor.putLong(lastNotificationKey, timeOfNotification);

        // Applies changes
        editor.apply();
    }

    // Returns the value of the user's choice which movies to display (popular, top rated
    // or favourite). In Popular Movies app, this preference is called "Show Movies"
    public static String getUserShowMoviesChoice(Context context) {

        // Accesses the user's preferences to know which movies to query
        // (popular, top rated or favourite)
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        // Gets the key for accessing the user's show movies preference value
        String keyForUserShowMoviesChoice = context.getString(R.string.pref_show_movies_key);

        // Gets default show movies preference value
        // in case the user has not provided their preference
        String defaultShowMoviesValue = context.getString(R.string.pref_show_movies_popular_value);

        // Returns the value for the users show movies preference key
        return sharedPreferences.getString(keyForUserShowMoviesChoice, defaultShowMoviesValue);
    }
}