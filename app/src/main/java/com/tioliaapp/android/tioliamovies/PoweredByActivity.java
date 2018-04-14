package com.tioliaapp.android.tioliamovies;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PoweredByActivity extends AppCompatActivity {

    @BindView(R.id.pb_loading_indicator)
    ProgressBar loadingIndicator;
    ObjectAnimator animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_powered_by);
        ButterKnife.bind(this);

        // Animate the Progress Bar
        animation = ObjectAnimator.ofInt(loadingIndicator, "progress", 0, 100);
        // Animation duration: 2000 milliseconds(= 2 seconds)
        animation.setDuration(2000);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.addListener(addAnimatorListener());
        animation.start();

    }


    @Override
    protected void onPause() {
        super.onPause();
        animation.removeAllListeners();
        animation.cancel();
    }

    public Animator.AnimatorListener addAnimatorListener() {
        return new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animator) {
                // Make the Progress Bar visible
                loadingIndicator.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                Intent mainIntent = new Intent(PoweredByActivity.this, MainActivity.class);
                PoweredByActivity.this.startActivity(mainIntent);
                PoweredByActivity.this.finish();
                overridePendingTransition(0, 0);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        };
    }
}