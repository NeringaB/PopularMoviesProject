package com.tioliaapp.android.tioliamovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

/**
 * SettingsActivity is responsible for displaying the {@link SettingsFragment} and
 * for orchestrating proper navigation when the up button is clicked.
 * When the up button is clicked from the SettingsActivity,
 * we want to navigate to the Activity that the user came from to get to the SettingsActivity.
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Uses the up button's ID (android.R.id.home) to listen for when the up button is clicked
        // and then calls onBackPressed to navigate to the previous Activity when this happens.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}