package koroshiya.com.lswipe.adapters;

import android.content.Context;

import koroshiya.com.lswipe.adapters.abstracts.AppsAdapter;

/**
 * Adapter for displaying "pinned" apps.
 * ie. Apps which are glued to the top of the sidebar.
 **/
public class PinnedAppsAdapter extends AppsAdapter {

    public static final String PINNED = "pinned";

    /**
     * @param isAddAdapter True if this is to add a new pinned app, false if we're displaying already pinned apps.
     **/
    public PinnedAppsAdapter(Context c, boolean isAddAdapter){
        super(c, isAddAdapter);
    }

    public String getType(){
        return PINNED;
    }

}