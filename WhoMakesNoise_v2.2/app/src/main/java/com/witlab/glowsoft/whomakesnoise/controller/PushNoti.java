package com.witlab.glowsoft.whomakesnoise.controller;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.witlab.glowsoft.whomakesnoise.MainActivity;
import com.witlab.glowsoft.whomakesnoise.R;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

/**
 * Created by WitLab on 2017-12-01.
 */

public class PushNoti {
    public static void push(Context context, String title, String content) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent1 = new Intent(context.getApplicationContext(), MainActivity.class); //인텐트 생성.
        Notification.Builder builder = new Notification.Builder(context);
        intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //현재 액티비티를 최상으로 올리고, 최상의 액티비티를 제외한 모든 액티비티를 없앤다.

        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(context, 0, intent1, FLAG_UPDATE_CURRENT);
        // PendingIntent는 일회용 인텐트 같은 개념입니다.
        // FLAG_UPDATE_CURRENT - > 만일 이미 생성된 PendingIntent가 존재 한다면, 해당 Intent의 내용을 변경함.
        // FLAG_CANCEL_CURRENT - .이전에 생성한 PendingIntent를 취소하고 새롭게 하나 만든다.
        // FLAG_NO_CREATE -> 현재 생성된 PendingIntent를 반환합니다.
        // FLAG_ONE_SHOT - >이 플래그를 사용해 생성된 PendingIntent는 단 한번밖에 사용할 수 없습니다

        builder.setSmallIcon(R.drawable.ic_caution_push)
                .setTicker("층간소음 알림이 도착하였습니다.")
                .setWhen(System.currentTimeMillis())
                .setNumber(1).setContentTitle(title).setContentText(content)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setContentIntent(pendingNotificationIntent)
                .setAutoCancel(true).setOngoing(false).setVibrate(new long[] {0, 500});
        //해당 부분은 API 4.1버전부터 작동합니다.
        //setSmallIcon - > 작은 아이콘 이미지
        //setTicker - > 알람이 출력될 때 상단에 나오는 문구.
        //setWhen -> 알림 출력 시간.
        //setContentTitle-> 알림 제목
        //setConentText->푸쉬내용

        notificationManager.notify(1, builder.build()); // Notification send
    }
}
