package koroshiya.com.lswipe.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import koroshiya.com.lswipe.R;
import koroshiya.com.lswipe.fragments.MainMenuFragment;
import koroshiya.com.lswipe.fragments.SettingsFragment;
import koroshiya.com.lswipe.services.SwipeService;

/**
 * Main activity. Holds fragments which act as other activities.
 * Defaults to a menu.
 **/
public class MainActivity extends AppCompatActivity {

    public static final int PAGE_MAIN_MENU = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        setFragment(MainMenuFragment.newInstance());
    }

    public void receiveEvent(int page, String item, View v){

        switch (page){

            case PAGE_MAIN_MENU:

                switch (item){

                    case MainMenuFragment.MENU_ITEM_TURN_SERVICE_ON_OFF:

                        if (SwipeService.serviceRunning != null){
                            Snackbar.make(v, "LibreSwipe has been turned off", Snackbar.LENGTH_SHORT).show();
                            SwipeService.serviceRunning.stopSelf();
                        }else{
                            Snackbar.make(v, "LibreSwipe has been turned on", Snackbar.LENGTH_SHORT).show();
                            startService(SwipeService.getIntent(this));
                        }
                        break;

                    case MainMenuFragment.MENU_ITEM_SETTINGS:

                        setFragment(SettingsFragment.newInstance());
                        break;


                }

                break;

        }

    }

    private void setFragment(Fragment frag){
        FragmentManager fm = this.getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.activity_main_menu_fragment, frag);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = this.getFragmentManager();
        if (fm.getBackStackEntryCount() == 0) {
            super.onBackPressed();
        }else{
            fm.popBackStackImmediate();
        }
    }

}
