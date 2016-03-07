package koroshiya.com.lswipe.fragments.abstracts;

import android.app.Fragment;
import android.content.Context;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import koroshiya.com.lswipe.adapters.abstracts.AppsAdapter;

/**
 * Abstract fragment for displaying a list of apps, as defined by a text file.
 * Also includes the ability to add to and remove from this list.
 **/
public abstract class AppsFragment extends Fragment {

    private AppsAdapter aa;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(getLayout(), container, false);
        Context c = v.getContext();
        aa = setupAdapter(c);

        RecyclerView rv = (RecyclerView) v.findViewById(getLayoutRecyclerView());
        rv.setLayoutManager(new LinearLayoutManager(c));
        rv.setAdapter(aa);

        return v;
    }

    protected abstract int getLayout();

    protected abstract int getLayoutRecyclerView();

    protected abstract AppsAdapter setupAdapter(Context c);

    public void addApps(Context c, ArrayList<ResolveInfo> apps){
        aa.addApps(c, apps);
    }

}
