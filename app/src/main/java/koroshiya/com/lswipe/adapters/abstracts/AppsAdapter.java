package koroshiya.com.lswipe.adapters.abstracts;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import koroshiya.com.lswipe.R;
import koroshiya.com.lswipe.util.Util;

/**
 * Adapter for controlling a list of applications.
 * Apps are shown as cardviews in a recyclerview.
 **/
public abstract class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.ViewHolder> {

    private final List<ResolveInfo> items;
    private final boolean isAddAdapter;
    private final ArrayList<ResolveInfo> selected;
    private final int primary;
    private final int secondary;

    /**
     * @param isAddAdapter True if this is to add a new hidden app, false if we're displaying already hidden apps.
     **/
    protected AppsAdapter(Context c, boolean isAddAdapter){
        final List<ResolveInfo> apps = Util.getAppList(c);
        this.isAddAdapter = isAddAdapter;
        selected = new ArrayList<>();
        items = new ArrayList<>();
        primary = Color.WHITE;
        secondary = Color.LTGRAY;

        List<String> resultList = Util.getStringListFromFile(getAppsFile(c));

        for (ResolveInfo app : apps) {
            if (isAddAdapter != resultList.contains(app.activityInfo.name)){
                items.add(app);
            }
        }

        Collections.sort(items, new ResolveInfo.DisplayNameComparator(c.getPackageManager()));

    }

    protected abstract String getType();

    private File getAppsFile(Context c){
        File cacheDir = c.getCacheDir();
        return new File(cacheDir, getType() + ".txt");
    }

    public void addApps(Context c, ArrayList<ResolveInfo> apps){

        ResolveInfo.DisplayNameComparator comparator = new ResolveInfo.DisplayNameComparator(c.getPackageManager());
        Collections.sort(apps, comparator);

        int totalApps = apps.size();
        int totalItems = items.size();
        ResolveInfo app;
        ResolveInfo item;

        for (int iApp = totalApps - 1; iApp >= 0; iApp--){
            app = apps.get(iApp);

            for (int iItem = totalItems - 1; iItem >= 0; iItem--){
                item = items.get(iItem);

                if (comparator.compare(app, item) >= 0){
                    items.add(iItem + 1, app);
                    notifyItemInserted(iItem + 1);
                    apps.remove(app);
                    break;
                }else if (iItem == 0){
                    items.add(iItem, app);
                    notifyItemInserted(iItem);
                    apps.remove(app);
                    break;
                }

            }

        }

        totalApps = apps.size();
        if (totalApps > 0){
            Collections.reverse(apps);
            items.addAll(apps);
            notifyItemRangeInserted(0, totalApps);
        }

        Util.writeAppListToFile(getAppsFile(c), items);
    }

    private void removeApp(Context c, ResolveInfo app){
        int i = 0;
        for (ResolveInfo item : items){
            if (item.equals(app)){
                break;
            }
            i++;
        }
        if (i < items.size()) {
            this.items.remove(i);
            this.notifyItemRemoved(i);
            Util.writeAppListToFile(getAppsFile(c), items);
        }else{
            Log.d("HiddenAppsAdapter", "App not found");
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.vw_item_full_width, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setDataOnView(position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public ArrayList<ResolveInfo> getSelectedItems(){
        return selected;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final AppCompatTextView tv_large, tv_small;
        private final AppCompatImageView iv;
        private final CardView view;

        public ViewHolder(CardView itemView) {
            super(itemView);

            tv_large = (AppCompatTextView) itemView.findViewById(R.id.vw_item_tv_large);
            tv_small = (AppCompatTextView) itemView.findViewById(R.id.vw_item_tv_small);
            iv = (AppCompatImageView) itemView.findViewById(R.id.vw_item_iv);
            view = itemView;
        }

        public void setDataOnView(int cur) {

            final Context c = iv.getContext();
            final ResolveInfo info = items.get(cur);

            PackageManager pm = c.getPackageManager();
            Drawable d = info.loadIcon(pm);
            final CharSequence appName = info.loadLabel(pm);
            String packageName = info.activityInfo.applicationInfo.packageName;

            tv_large.setText(appName);
            tv_small.setText(packageName);
            iv.setImageDrawable(d);

            if (selected.contains(info)) {
                view.setCardBackgroundColor(secondary);
            }else {
                view.setCardBackgroundColor(primary);
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isAddAdapter){
                        int cur = getAdapterPosition();
                        if (selected.contains(info)) {
                            selected.remove(info);
                            view.setCardBackgroundColor(primary);
                        }
                        else {
                            selected.add(info);
                            view.setCardBackgroundColor(secondary);
                        }
                        notifyItemChanged(cur);
                    }else{
                        new AlertDialog.Builder(c)
                                .setTitle("Remove app")
                                .setMessage("Are you sure you want to remove "+appName+" from your "+getType()+" apps?")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        removeApp(c, info);
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }

                }
            });
        }
    }
}