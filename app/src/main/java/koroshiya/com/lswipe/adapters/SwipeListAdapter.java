package koroshiya.com.lswipe.adapters;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import java.util.Collections;
import java.util.List;

import koroshiya.com.lswipe.R;
import koroshiya.com.lswipe.util.Util;

/**
 * Adapter for the RecyclerView used to show the app list.
 * This class accepts a list of apps, then displays each as a separate
 * card.
 **/
public class SwipeListAdapter extends RecyclerView.Adapter<SwipeListAdapter.ViewHolder> {

    private final List<ResolveInfo> items;
    private boolean hideAppNames;

    public SwipeListAdapter(Context c){

        final List<ResolveInfo> apps = Util.getAppList(c);
        Collections.sort(apps, new ResolveInfo.DisplayNameComparator(c.getPackageManager()));
        List<String> pinned = Util.getStringListFromType(c, PinnedAppsAdapter.PINNED);
        boolean onlyShowPinned = PreferenceManager
                                    .getDefaultSharedPreferences(c)
                                    .getBoolean(c.getString(R.string.pref_hide_non_pinned), false);

        if (onlyShowPinned){

            for (int i = apps.size() - 1; i >= 0; i--) {
                ResolveInfo app = apps.get(i);
                if (!pinned.contains(app.activityInfo.name)){
                    apps.remove(i);
                }
            }

        }else {

            List<String> hidden = Util.getStringListFromType(c, HiddenAppsAdapter.HIDDEN);

            for (int i = apps.size() - 1; i >= 0; i--) {
                ResolveInfo app = apps.get(i);
                for (String h : hidden) {
                    if (h.equals(app.activityInfo.name)) {
                        apps.remove(app);
                        break;
                    }
                }
            }

            Collections.reverse(pinned); //Add them backwards, so they're in alphabetical order
            for (String p : pinned) {
                for (int i = apps.size() - 1; i >= 0; i--) {
                    ResolveInfo app = apps.get(i);
                    if (p.equals(app.activityInfo.name)) {
                        apps.remove(i);
                        apps.add(0, app);
                        break;
                    }
                }
            }
        }

        this.items = apps;
    }

    @Override
    public SwipeListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context c = parent.getContext();
        hideAppNames = PreferenceManager
                        .getDefaultSharedPreferences(c)
                        .getBoolean(c.getString(R.string.pref_hide_app_names), true);

        int resId = hideAppNames ? R.layout.vw_item_icon_only : R.layout.vw_item;

        View v = LayoutInflater.from(parent.getContext()).inflate(resId, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SwipeListAdapter.ViewHolder holder, int position) {
        holder.setDataOnView(position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView tv_large, tv_small;
        private final AppCompatImageView iv;
        private final View view;

        public ViewHolder(View itemView) {
            super(itemView);

            Context c = itemView.getContext();
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
            if (sp.getBoolean(c.getString(R.string.pref_dark_draw), false)){
                itemView.setBackgroundColor(Color.DKGRAY);
            }

            if (!hideAppNames) {
                tv_large = (AppCompatTextView) itemView.findViewById(R.id.vw_item_tv_large);
                tv_small = (AppCompatTextView) itemView.findViewById(R.id.vw_item_tv_small);
            }
            iv = (AppCompatImageView) itemView.findViewById(R.id.vw_item_iv);
            view = itemView;
        }

        public void setDataOnView(int cur) {

            final Context c = iv.getContext();
            final ResolveInfo info = items.get(cur);

            PackageManager pm = c.getPackageManager();
            Drawable d = info.loadIcon(pm);

            if (!hideAppNames) {
                CharSequence appName = info.loadLabel(pm);
                String packageName = info.activityInfo.applicationInfo.packageName;

                tv_large.setText(appName);
                tv_small.setText(packageName);
            }

            iv.setImageDrawable(d);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    closeDrawer();

                    ActivityInfo activity = info.activityInfo;
                    ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);

                    Intent i = new Intent(Intent.ACTION_MAIN);
                    i.addCategory(Intent.CATEGORY_LAUNCHER);
                    i.setComponent(name);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

                    c.startActivity(i);

                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    closeDrawer();

                    ActivityInfo activity = info.activityInfo;
                    Uri uri = Uri.fromParts("package", activity.applicationInfo.packageName, null);

                    Intent i = new Intent();
                    i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    i.setData(uri);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    c.startActivity(i);

                    return false;
                }
            });

        }

        private void closeDrawer(){
            if (view != null && view.getParent() != null && view.getParent().getParent() != null) {
                ViewParent vp = view.getParent().getParent();
                if (vp instanceof DrawerLayout) {

                    DrawerLayout dl = (DrawerLayout) vp;
                    Context c = dl.getContext();
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
                    int gravity;

                    String dock_side = sp.getString(c.getString(R.string.pref_dock_side), c.getString(R.string.pref_dock_side_default));
                    if (dock_side.equals("0")){
                        gravity = GravityCompat.START;
                    }else{
                        gravity = GravityCompat.END;
                    }

                    ((DrawerLayout) vp).closeDrawer(gravity);
                }
            }
        }

    }
}
