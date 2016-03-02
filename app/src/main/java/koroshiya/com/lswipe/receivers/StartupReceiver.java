package koroshiya.com.lswipe.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import koroshiya.com.lswipe.services.SwipeService;

/**
 * Receiver to intercept startup events.
 * This class enables LibreSwipe to start automatically when the device boots.
 **/
public class StartupReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            context.startService(SwipeService.getIntent(context));
        }
    }
}
