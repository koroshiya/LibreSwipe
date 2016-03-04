package koroshiya.com.lswipe.adapters;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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

import java.util.List;

import koroshiya.com.lswipe.R;

/**
 * Adapter for the RecyclerView used to show the app list.
 * This class accepts a list of apps, then displays each as a separate
 * card.
 **/
public class SwipeListAdapter extends RecyclerView.Adapter<SwipeListAdapter.ViewHolder> {

    private final List<ResolveInfo> items;
    private boolean hideAppNames;

    public SwipeListAdapter(List<ResolveInfo> items){
        this.items = items;
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
                    ((DrawerLayout) vp).closeDrawer(GravityCompat.START);
                }
            }
        }

    }
}
