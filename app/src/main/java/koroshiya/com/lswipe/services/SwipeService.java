package koroshiya.com.lswipe.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
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

import koroshiya.com.lswipe.activities.MainActivity;
import koroshiya.com.lswipe.adapters.SwipeListAdapter;
import koroshiya.com.lswipe.R;

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

    public static SwipeService serviceRunning = null;
    private final static int PERSISTENT_NOTIFICATION = 0;

    private final DrawerLayout.DrawerListener drawerListener = new DrawerLayout.DrawerListener() {

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {}

        @Override
        public void onDrawerOpened(View drawerView) {
            Log.d("SwipeService", "Opened");
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            Log.d("SwipeService", "Closed");
        }

        @Override
        public void onDrawerStateChanged(int newState) {

            if (newState == DrawerLayout.STATE_SETTLING){
                Log.d("SwipeService", "Settling");
                hideOrShowDrawer(false);
            }else if (newState == DrawerLayout.STATE_IDLE){
                Log.d("SwipeService", "Idle");
                hideOrShowDrawer(true);
            }else if (newState == DrawerLayout.STATE_DRAGGING){
                Log.d("SwipeService", "Dragging");
            }

        }

        private void hideOrShowDrawer(boolean isIdle){
            boolean isOpen = v.isDrawerVisible(GravityCompat.START);
            Log.d("SS", "Open: "+Boolean.toString(isOpen));
            if (!v.isDrawerVisible(GravityCompat.START) && mPaperParams.width != drawer_width_dp) {
                mPaperParams.width = drawer_width_dp;
                mWindowManager.updateViewLayout(v, mPaperParams);

                setAdapter();

            }else if (v.isDrawerVisible(GravityCompat.START) == isIdle && mPaperParams.width != WindowManager.LayoutParams.WRAP_CONTENT) {
                mPaperParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                mWindowManager.updateViewLayout(v, mPaperParams);
            }
        }

    };

    public static Intent getIntent(Context context) {
        return new Intent(context, SwipeService.class);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        drawer_width_dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics());
        int icon_width_dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, getResources().getDisplayMetrics());

        mPaperParams = new WindowManager.LayoutParams(
                drawer_width_dp, WindowManager.LayoutParams.MATCH_PARENT,

                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,

                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        | WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);

        mPaperParams.gravity = GravityCompat.START;

        mWindowManager = (WindowManager) getSystemService(Service.WINDOW_SERVICE);
        LayoutInflater layoutInflater = LayoutInflater.from(this);

        v = (DrawerLayout) layoutInflater.inflate(R.layout.activity_swipe, null);
        v.addDrawerListener(drawerListener);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        RecyclerView rv = (RecyclerView) v.findViewById(R.id.vw_pane_rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        setAdapter();

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

            builder.setContentIntent(contentIntent)
                    .setSmallIcon(R.drawable.ic_touch_app_black_24dp)
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_touch_app_black_24dp))
                    .setTicker("LibreSwipe is running - Tap to open settings")
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true)
                    .setContentTitle("LibreSwipe")
                    .setContentText("LibreSwipe is running - Tap to open settings");
            Notification n = builder.build();

            nm.notify(PERSISTENT_NOTIFICATION, n);
        }

        serviceRunning = this;

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        serviceRunning = null;

        if (v != null) {
            mWindowManager.removeView(v);
            v = null;
        }

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(PERSISTENT_NOTIFICATION);

    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void setAdapter(){
        SwipeListAdapter na = new SwipeListAdapter(this);
        RecyclerView rv = (RecyclerView) v.findViewById(R.id.vw_pane_rv);
        rv.setAdapter(na);
    }

}
