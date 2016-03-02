package koroshiya.com.lswipe.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import koroshiya.com.lswipe.services.SwipeService;

/**
 * Activity which launches the SwipeService Service.
 * This activity has only one purpose: to launch the Service.
 *
 * It is done this way so the service can run in the background
 * without any Activity being shown.
 **/
public class SwipeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startService(SwipeService.getIntent(this));
        finish();
    }

}
