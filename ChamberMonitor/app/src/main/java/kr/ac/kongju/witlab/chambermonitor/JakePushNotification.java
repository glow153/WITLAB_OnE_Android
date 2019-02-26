package kr.ac.kongju.witlab.chambermonitor;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.view.View;

public class JakePushNotification {
    private Context mainCtxt = null;
    public static final int CHAMBER_TEMPHUMI = 1;

    public JakePushNotification(Context mainCtxt) {
        this.mainCtxt = mainCtxt;

    }
    public void sendNotification(int notify_type) {

        // BEGIN_INCLUDE(build_action)
        /** Create an intent that will be fired when the user clicks the notification.
         * The intent needs to be packaged into a {@link android.app.PendingIntent} so that the
         * notification service can fire it on our behalf.
         */
        Intent openMainActivity = new Intent(mainCtxt, MainActivity.class);
        openMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        mainCtxt.startActivity(openMainActivity);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                mainCtxt, 0, openMainActivity, 0);
        // END_INCLUDE(build_action)

        // BEGIN_INCLUDE (build_notification)
        /**
         * Use NotificationCompat.Builder to set up our notification.
         */
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mainCtxt);

        /** Set the icon that will appear in the notification bar. This icon also appears
         * in the lower right hand corner of the notification itself.
         *
         * Important note: although you can use any drawable as the small icon, Android
         * design guidelines state that the icon should be simple and monochrome. Full-color
         * bitmaps or busy images don't render well on smaller screens and can end up
         * confusing the user.
         */
        builder.setSmallIcon(R.drawable.ic_stat_notification);

        // Set the intent that will fire when the user taps the notification.
        builder.setContentIntent(pendingIntent);

        // Set the notification to auto-cancel. This means that the notification will disappear
        // after the user taps it, rather than remaining until it's explicitly dismissed.
        builder.setAutoCancel(true);

        /**
         *Build the notification's appearance.
         * Set the large icon, which appears on the left of the notification. In this
         * sample we'll set the large icon to be the same as our app icon. The app icon is a
         * reasonable default if you don't have anything more compelling to use as an icon.
         */
        builder.setLargeIcon(
                BitmapFactory.decodeResource(mainCtxt.getResources(), R.drawable.ic_warning));

        /**
         * Set the text of the notification. This sample sets the three most commononly used
         * text areas:
         * 1. The content title, which appears in large type at the top of the notification
         * 2. The content text, which appears in smaller text below the title
         * 3. The subtext, which appears under the text on newer devices. Devices running
         *    versions of Android prior to 4.2 will ignore this field, so don't use it for
         *    anything vital!
         */
        builder.setContentTitle("온습도 알림");
        builder.setContentText("챔버 온습도에 이상이 생겼어요!");
        builder.setSubText("여기를 탭하여 온습도를 확인하세요");

        // END_INCLUDE (build_notification)

        // BEGIN_INCLUDE(send_notification)
        /**
         * Send the notification. This will immediately display the notification icon in the
         * notification bar.
         */
        NotificationManager notificationManager =
                (NotificationManager) mainCtxt.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notify_type, builder.build());
        // END_INCLUDE(send_notification)
    }
}
