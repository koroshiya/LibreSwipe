package koroshiya.com.lswipe.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import koroshiya.com.lswipe.R;
import koroshiya.com.lswipe.services.SwipeService;

/**
 * Receiver to intercept startup events.
 * This class enables LibreSwipe to start automatically when the device boots.
 **/
public class StartupReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            boolean startOnBoot = PreferenceManager
                                    .getDefaultSharedPreferences(context)
                                    .getBoolean(context.getString(R.string.pref_startup), false);
            if (startOnBoot){
                context.startService(SwipeService.getIntent(context));
            }
        }
    }
}
