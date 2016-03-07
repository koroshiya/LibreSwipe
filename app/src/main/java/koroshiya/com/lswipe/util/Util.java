package koroshiya.com.lswipe.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

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
            fl.delete();
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
            fl.delete();
        }
    }

    public static List<ResolveInfo> getAppList(Context c){
        PackageManager pm = c.getPackageManager();
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        return pm.queryIntentActivities(mainIntent, 0);
    }

}
