package koroshiya.com.lswipe.fragments;

import android.content.Context;

import koroshiya.com.lswipe.R;
import koroshiya.com.lswipe.adapters.PinnedAppsAdapter;
import koroshiya.com.lswipe.adapters.abstracts.AppsAdapter;
import koroshiya.com.lswipe.fragments.abstracts.AppsFragment;

/**
 * Fragment for displaying and adding "pinned" apps.
 * These are apps which show at the top of the swipe list, regardless
 * of alphabetical order.
 **/
public class PinnedAppsFragment extends AppsFragment {

    public static PinnedAppsFragment newInstance() {
        return new PinnedAppsFragment();
    }

    @Override
    protected AppsAdapter setupAdapter(Context c){
        return new PinnedAppsAdapter(c, false);
    }

    @Override
    public int getLayout(){
        return R.layout.fragment_pinned_apps;
    }

    @Override
    protected int getLayoutRecyclerView() {
        return R.id.frag_pinned_apps_rv;
    }

    @Override
    public void onResume(){
        super.onResume();
        getActivity().setTitle(String.format("%s -> %s", getString(R.string.app_name), getString(R.string.str_pinned_apps)));
    }

}
