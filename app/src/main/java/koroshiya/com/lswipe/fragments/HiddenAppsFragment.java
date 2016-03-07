package koroshiya.com.lswipe.fragments;

import android.content.Context;

import koroshiya.com.lswipe.R;
import koroshiya.com.lswipe.adapters.HiddenAppsAdapter;
import koroshiya.com.lswipe.adapters.abstracts.AppsAdapter;
import koroshiya.com.lswipe.fragments.abstracts.AppsFragment;

/**
 * Fragment for displaying and adding "hidden" apps.
 * These are apps which will not show in the swipe list.
 **/
public class HiddenAppsFragment extends AppsFragment {

    public static HiddenAppsFragment newInstance() {
        return new HiddenAppsFragment();
    }

    @Override
    protected AppsAdapter setupAdapter(Context c){
        return new HiddenAppsAdapter(c, false);
    }

    public int getLayout(){
        return R.layout.fragment_hidden_apps;
    }

    @Override
    protected int getLayoutRecyclerView() {
        return R.id.frag_hidden_apps_rv;
    }

    @Override
    public void onResume(){
        super.onResume();
        getActivity().setTitle(String.format("%s -> %s", getString(R.string.app_name), getString(R.string.str_hidden_apps)));
    }

}
