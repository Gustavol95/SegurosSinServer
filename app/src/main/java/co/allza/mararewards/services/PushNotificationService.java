package co.allza.mararewards.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import co.allza.mararewards.CargarDatos;
import co.allza.mararewards.R;
import co.allza.mararewards.activities.SplashActivity;
import co.allza.mararewards.items.NotificacionItem;


/**
 * Created by Tavo on 13/07/2016.
 */
public class PushNotificationService extends FirebaseMessagingService
{

    public PushNotificationService() {
        super();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        NotificationManager mNotifyMgr= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        SimpleDateFormat parserFormal= new SimpleDateFormat("dd MMM yyyy");
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.perfil_whitebg)
                        .setContentTitle(remoteMessage.getNotification().getBody())
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logoandroid))
                        .setColor(4)
                        .setContentText("Desde Servidor");
        mBuilder.setAutoCancel(true);
        Intent resultIntent = new Intent(this, SplashActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        resultIntent.putExtra("goTo",0);
        NotificacionItem item = new NotificacionItem(R.drawable.logoandroid, remoteMessage.getFrom(), "De servidor", parserFormal.format(Calendar.getInstance().getTime()));
        item.setId(0);
       // CargarDatos.pushNotification(this, item);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);
        int mNotificationId = 0;
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onMessageSent(String s) {
        super.onMessageSent(s);
    }

    @Override
    public void onSendError(String s, Exception e) {
        super.onSendError(s, e);
    }

}
