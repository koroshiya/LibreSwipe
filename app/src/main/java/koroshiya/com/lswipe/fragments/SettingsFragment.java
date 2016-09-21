package koroshiya.com.lswipe.fragments;


import android.os.Bundle;
import android.preference.PreferenceFragment;

import koroshiya.com.lswipe.R;
import koroshiya.com.lswipe.util.Util;

/**
 * Fragment containing all of the settings possible for this app.
 **/
public class SettingsFragment extends PreferenceFragment {

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume(){
        super.onResume();
        Util.setTitle(this, R.string.str_settings);
    }

}
