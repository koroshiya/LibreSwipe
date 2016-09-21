package koroshiya.com.lswipe.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import koroshiya.com.lswipe.R;
import koroshiya.com.lswipe.activities.MainActivity;
import koroshiya.com.lswipe.adapters.SwipeListAdapter;

/**
 * Service which should remain running in the background.
 * This is the service responsible for handling the DrawerLayout.
 * If this service is killed, LibreSwipe stops.
 **/
public class SwipeService extends Service {

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mPaperParams;
    private DrawerLayout v;
    private int drawer_width_dp;
    public static boolean isRunning = false;

    private final static int PERSISTENT_NOTIFICATION = 0;
    public final static String KILL_SERVICE = "kill_service", RESTART_SERVICE = "restart_service";

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (action.equals(KILL_SERVICE)){

                Log.i("SS", "Killing service...");
                stopSelf();
                isRunning = false;

            }else if (action.equals(RESTART_SERVICE)){

                Intent i = new Intent();
                i.setAction(SwipeService.KILL_SERVICE);
                onReceive(context, i);

                Log.i("SS", "Starting service...");
                startService(SwipeService.getIntent(context));
            }

        }
    };

    private final DrawerLayout.DrawerListener drawerListener = new DrawerLayout.SimpleDrawerListener() {

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
            Log.d("SS", Float.toString(slideOffset));

            if (slideOffset == 0.0f) {
                mPaperParams.width = drawer_width_dp;
                mWindowManager.updateViewLayout(v, mPaperParams);
            }else if (mPaperParams.width != WindowManager.LayoutParams.WRAP_CONTENT) {
                mPaperParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                mWindowManager.updateViewLayout(v, mPaperParams);
            }

        }

    };

    public static void killService(Context c){

        Intent i = new Intent();
        i.setAction(SwipeService.KILL_SERVICE);
        c.sendBroadcast(i);

    }

    public static Intent getIntent(Context context) {
        return new Intent(context, SwipeService.class);
    }

    @Override
    public void onCreate(){
        super.onCreate();

        IntentFilter filter = new IntentFilter();
        filter.addAction(KILL_SERVICE);

        registerReceiver(receiver, filter);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        drawer_width_dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics());
        int icon_width_dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, getResources().getDisplayMetrics());

        mPaperParams = new WindowManager.LayoutParams(
                drawer_width_dp, WindowManager.LayoutParams.MATCH_PARENT,

                WindowManager.LayoutParams.TYPE_PHONE,

                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);

        String dock_side = sp.getString(getString(R.string.pref_dock_side), getString(R.string.pref_dock_side_default));
        int gravityMode;

        if (dock_side.equals("0")){
            gravityMode = GravityCompat.START;
        }else{
            gravityMode = GravityCompat.END;
        }
        mPaperParams.gravity = gravityMode;

        mWindowManager = (WindowManager) getSystemService(Service.WINDOW_SERVICE);
        LayoutInflater layoutInflater = LayoutInflater.from(this);

        v = (DrawerLayout) layoutInflater.inflate(R.layout.activity_swipe, null, false);
        v.addDrawerListener(drawerListener);

        RecyclerView rv = (RecyclerView) v.findViewById(R.id.vw_pane_rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        DrawerLayout.LayoutParams rvlp = (DrawerLayout.LayoutParams) rv.getLayoutParams();
        rvlp.gravity = gravityMode;
        rv.setLayoutParams(rvlp);

        SwipeListAdapter na = new SwipeListAdapter(this);
        rv.setAdapter(na);

        boolean bool_hide_app_names = sp.getBoolean(getString(R.string.pref_hide_app_names), true);
        if (bool_hide_app_names){
            ViewGroup.LayoutParams params = rv.getLayoutParams();
            params.width = icon_width_dp;
            rv.setLayoutParams(params);
        }

        mWindowManager.addView(v, mPaperParams);

        boolean bool_notification = sp.getBoolean(getString(R.string.pref_notification), true);
        if (bool_notification) {
            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            Resources res = getResources();
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

            String strTapToOpenSettings = getString(R.string.tap_to_open_settings);
            String strTitle = getString(R.string.app_name);

            builder.setContentIntent(contentIntent)
                    .setSmallIcon(R.drawable.ic_touch_app_white_24dp)
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_touch_app_white_24dp))
                    .setTicker(strTapToOpenSettings)
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true)
                    .setContentTitle(strTitle)
                    .setContentText(strTapToOpenSettings);
            Notification n = builder.build();

            nm.notify(PERSISTENT_NOTIFICATION, n);
        }

        isRunning = true;

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        isRunning = false;

        try{
            unregisterReceiver(receiver);
        }catch (Exception e){
            e.printStackTrace();
        }

        if (v != null) {
            mWindowManager.removeView(v);
            v = null;
        }

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(PERSISTENT_NOTIFICATION);

    }

    @Override
    public IBinder onBind(Intent intent) {
        String msg = getString(R.string.not_yet_implemented);
        throw new UnsupportedOperationException(msg);
    }

}
