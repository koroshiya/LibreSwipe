package koroshiya.com.lswipe;

import android.app.Activity;
import android.os.Bundle;

public class SwipeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startService(SwipeService.getIntent(this));
        finish();
    }

}
