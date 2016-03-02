package koroshiya.com.lswipe;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import java.util.Collections;
import java.util.List;

public class SwipeService extends Service {

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mPaperParams;
    private DrawerLayout v;
    private boolean drawerClosed = true;
    private boolean drawerMoving = false;

    public static Intent getIntent(Context context) {
        return new Intent(context, SwipeService.class);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mPaperParams = new WindowManager.LayoutParams(
                100, WindowManager.LayoutParams.MATCH_PARENT,

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

        v.addDrawerListener(new DrawerLayout.DrawerListener() {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {}

            @Override
            public void onDrawerOpened(View drawerView) {
                drawerClosed = false;
                drawerMoving = false;
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                drawerClosed = false;
                drawerMoving = true;
            }

            @Override
            public void onDrawerStateChanged(int newState) {

                if (newState == DrawerLayout.STATE_SETTLING){
                    if (drawerClosed) {
                        drawerClosed = false;
                        mPaperParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                        mWindowManager.updateViewLayout(v, mPaperParams);
                    }
                }else if (newState == DrawerLayout.STATE_IDLE){
                    if (!drawerClosed && drawerMoving) {
                        drawerClosed = true;
                        mPaperParams.width = 100;
                        mWindowManager.updateViewLayout(v, mPaperParams);
                    }
                }else if (newState == DrawerLayout.STATE_DRAGGING){
                    drawerMoving = true;
                }

            }
        });

        RecyclerView rv = (RecyclerView) v.findViewById(R.id.vw_pane_rv);
        rv.setLayoutManager(new LinearLayoutManager(this));

        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List<ResolveInfo> apps = getPackageManager().queryIntentActivities( mainIntent, 0);
        Collections.sort(apps, new ResolveInfo.DisplayNameComparator(getPackageManager()));

        NavigationAdapter na = new NavigationAdapter(apps);
        rv.setAdapter(na);

        mWindowManager.addView(v, mPaperParams);


        /*Intent notificationIntent = new Intent(this, SettingsActivity.class);
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

        nm.notify(0, n);*/

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (v != null) {
            mWindowManager.removeView(v);
            v = null;
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
