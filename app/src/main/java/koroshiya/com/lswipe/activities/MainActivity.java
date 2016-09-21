package koroshiya.com.lswipe.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import koroshiya.com.lswipe.R;
import koroshiya.com.lswipe.adapters.HiddenAppsAdapter;
import koroshiya.com.lswipe.adapters.PinnedAppsAdapter;
import koroshiya.com.lswipe.adapters.abstracts.AppsAdapter;
import koroshiya.com.lswipe.fragments.HiddenAppsFragment;
import koroshiya.com.lswipe.fragments.MainMenuFragment;
import koroshiya.com.lswipe.fragments.PinnedAppsFragment;
import koroshiya.com.lswipe.fragments.SettingsFragment;
import koroshiya.com.lswipe.fragments.abstracts.AppsFragment;
import koroshiya.com.lswipe.services.SwipeService;
import koroshiya.com.lswipe.util.Util;

/**
 * Main activity. Holds fragments which act as other activities.
 * Defaults to a menu.
 **/
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        setFragment(MainMenuFragment.newInstance());
    }

    private boolean checkDrawOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(this);
        }
        return true;
    }

    public void receiveEvent(int item, View v){

        Fragment fragment = getFragment();

        if (fragment instanceof MainMenuFragment){

            switch (item){

                case R.string.str_turn_on_off:

                    if (SwipeService.isRunning){

                        SwipeService.killService(this);
                        Snackbar.make(v, R.string.turned_off, Snackbar.LENGTH_SHORT).show();

                    }else if (checkDrawOverlayPermission()){

                        Snackbar.make(v, R.string.turned_on, Snackbar.LENGTH_SHORT).show();
                        startService(SwipeService.getIntent(this));

                    }else {

                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, 0);

                    }
                    break;

                case R.string.str_settings:

                    setFragment(SettingsFragment.newInstance());
                    break;

                case R.string.str_pinned_apps:

                    setFragment(PinnedAppsFragment.newInstance());
                    break;

                case R.string.str_hidden_apps:

                    setFragment(HiddenAppsFragment.newInstance());
                    break;

            }

        }

    }

    private void setFragment(Fragment frag){
        FragmentManager fm = this.getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.activity_main_menu_fragment, frag);
        ft.addToBackStack(null);
        ft.commit();
    }

    private Fragment getFragment(){
        FragmentManager fm = this.getFragmentManager();
        return fm.findFragmentById(R.id.activity_main_menu_fragment);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = this.getFragmentManager();
        if (fm.getBackStackEntryCount() == 1) {
            moveTaskToBack(true);
        }else{
            fm.popBackStackImmediate();
            View v = findViewById(R.id.activity_main_menu_fragment);
            Util.restartSwipeServiceIfNeeded(v);
        }
    }

    public void pinnedAppsFragmentBtnAdd(View v){

        final Context c = v.getContext();

        final RecyclerView rView = new RecyclerView(c);
        rView.setLayoutManager(new LinearLayoutManager(c));
        rView.setAdapter(new PinnedAppsAdapter(c, true));

        appsFragmentBtnAdd(rView, c);

    }

    public void hiddenAppsFragmentBtnAdd(View v){

        final Context c = v.getContext();

        final RecyclerView rView = new RecyclerView(c);
        rView.setLayoutManager(new LinearLayoutManager(c));
        rView.setAdapter(new HiddenAppsAdapter(c, true));

        appsFragmentBtnAdd(rView, c);

    }

    private void appsFragmentBtnAdd(final RecyclerView v, final Context c){

        new AlertDialog.Builder(c)
                //.setTitle("App chooser")
                .setView(v)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    ArrayList<ResolveInfo> items = ((AppsAdapter)v.getAdapter()).getSelectedItems();
                    if (items.size() > 0){
                        Fragment f = getFragment();
                        if (f instanceof AppsFragment){
                            Log.d("MainActivity", "is apps fragment");
                            AppsFragment paf = (AppsFragment) f;
                            paf.addApps(c, items);
                        }else{
                            Log.d("MainActivity", "not apps fragment");
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss())
                .show();

    }

}
