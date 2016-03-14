package koroshiya.com.lswipe.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import koroshiya.com.lswipe.R;
import koroshiya.com.lswipe.adapters.MainMenuAdapter;

/**
 * Fragment containing a list of menu items.
 * Acts as the main menu of the application.
 **/
public class MainMenuFragment extends Fragment {

    public static MainMenuFragment newInstance() {
        return new MainMenuFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_menu, container, false);

        int[] items = new int[]{
                R.string.str_turn_on_off,
                R.string.str_settings,
                R.string.str_pinned_apps,
                R.string.str_hidden_apps,
        };

        RecyclerView rv = (RecyclerView) v.findViewById(R.id.frag_main_menu_rv);
        rv.setLayoutManager(new LinearLayoutManager(v.getContext()));
        rv.setAdapter(new MainMenuAdapter(items));

        return v;
    }

    @Override
    public void onResume(){
        super.onResume();
        getActivity().setTitle(R.string.app_name);
    }

}
