package koroshiya.com.lswipe.util;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import koroshiya.com.lswipe.R;
import koroshiya.com.lswipe.services.SwipeService;

public class Util {

    private static String getStringFromFile(File fl) {

        String ret = "";

        if (fl.exists() && fl.isFile() && fl.canRead()) {
            FileInputStream fin = null;
            BufferedReader reader = null;
            try {
                fin = new FileInputStream(fl);
                reader = new BufferedReader(new InputStreamReader(fin));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                ret = sb.toString();
            }catch (IOException e){
                e.printStackTrace();
            }finally{
                if (fin != null) try {
                    fin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (reader != null) try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return ret;

    }

    private static void writeStringToFile(File fl, String str) throws IOException {

        if (fl.exists() && fl.isFile() && fl.canWrite()){
            if (!fl.delete()){
                String msg = String.format(Locale.ENGLISH, "Failed to delete file: %s", fl.getAbsolutePath());
                Log.w("Util", msg);
            }
        }

        PrintWriter out = new PrintWriter(fl);
        out.write(str);
        out.close();

    }

    public static List<String> getStringListFromFile(File fl){
        String ret = getStringFromFile(fl);
        return getStringListFromString(ret);
    }

    private static List<String> getStringListFromString(String str){
        String[] arr = str.split(";");
        return Arrays.asList(arr);
    }

    public static List<String> getStringListFromType(Context c, String type){
        File cacheDir = c.getCacheDir();
        File fl = new File(cacheDir, type + ".txt");
        return getStringListFromFile(fl);
    }

    private static String writeAppListToString(List<ResolveInfo> apps){
        StringBuilder buf = new StringBuilder();
        for (ResolveInfo app : apps){
            buf.append(app.activityInfo.name).append(";");
        }
        return buf.toString();
    }

    public static void writeAppListToFile(File fl, List<ResolveInfo> apps) {
        String result = writeAppListToString(apps);
        try {
            writeStringToFile(fl, result);
        } catch (IOException e) {
            e.printStackTrace();
            if (!fl.delete()){
                String msg = String.format(Locale.ENGLISH, "Failed to delete file: %s", fl.getAbsolutePath());
                Log.w("Util", msg);
            }
        }
    }

    public static List<ResolveInfo> getAppList(Context c){
        PackageManager pm = c.getPackageManager();
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        return pm.queryIntentActivities(mainIntent, 0);
    }

    public static void restartSwipeServiceIfNeeded(View v){

        Context c = v.getContext();
        Snackbar.make(v, R.string.settings_apps_updated, Snackbar.LENGTH_SHORT).show();

        Intent i = new Intent();
        i.setAction(SwipeService.RESTART_SERVICE);
        c.sendBroadcast(i);

    }

    public static void setTitle(Fragment fragment, int resId) {

        Activity act = fragment.getActivity();
        if (act != null) {

            String appName = act.getString(R.string.app_name);

            if (resId != -1) {

                String pageName = act.getString(resId);
                String format = act.getString(R.string.page_name_nav);

                act.setTitle(String.format(format, appName, pageName));

            }else{

                act.setTitle(appName);

            }

        }

    }
}
