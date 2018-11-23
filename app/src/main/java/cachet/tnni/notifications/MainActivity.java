package cachet.tnni.notifications;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
    private static final String TAG = "NOTIFICATIONS";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * Check if permission is given, if not then go to the notification settings screen.
         */
        if (!permissionGiven()) {
            startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
        }

        NotificationReceiver receiver = new NotificationReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NotificationListener.NOTIFICATION_INTENT);
        registerReceiver(receiver, intentFilter);

        /**
         * Start the notification service once permission has been given.
         */
        Intent listenerIntent = new Intent(this, NotificationListener.class);
        startService(listenerIntent);
    }

    /**
     * For all enabled notification listeners, check if any of them matches the package name of this application.
     * If any match is found, return true. Otherwise if no matches were found, return false.
     */
    private boolean permissionGiven() {
        String packageName = getPackageName();
        String flat = Settings.Secure.getString(getContentResolver(),
                ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            String[] names = flat.split(":");
            for (String name : names) {
                ComponentName componentName = ComponentName.unflattenFromString(name);
                boolean nameMatch = TextUtils.equals(packageName, componentName.getPackageName());
                if (nameMatch) {
                    return true;
                }
            }
        }
        return false;
    }

    void send(String s) {
        Log.d(TAG, s);
    }

    class NotificationReceiver extends BroadcastReceiver {
        final static String TAG = "NOTIFICATION_RECEIVER";
        @Override
        public void onReceive(Context context, Intent intent) {
            String packageName = intent.getStringExtra(NotificationListener.NOTIFICATION_PACKAGE_NAME);
            send(packageName);
        }
    }
}


