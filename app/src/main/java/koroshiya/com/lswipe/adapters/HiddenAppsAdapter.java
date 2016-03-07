package koroshiya.com.lswipe.adapters;

import android.content.Context;

import koroshiya.com.lswipe.adapters.abstracts.AppsAdapter;

/**
 * Adapter for displaying "hidden" apps.
 * ie. Apps which are not shown on the sidebar.
 **/
public class HiddenAppsAdapter extends AppsAdapter {

    public static final String HIDDEN = "hidden";

    /**
     * @param isAddAdapter True if this is to add a new hidden app, false if we're displaying already hidden apps.
     **/
    public HiddenAppsAdapter(Context c, boolean isAddAdapter){
        super(c, isAddAdapter);
    }

    protected String getType(){
        return HIDDEN;
    }

}