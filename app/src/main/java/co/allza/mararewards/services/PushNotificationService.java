package co.allza.mararewards.services;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import co.allza.mararewards.CargarDatos;
import co.allza.mararewards.R;
import co.allza.mararewards.activities.SplashActivity;
import co.allza.mararewards.items.NotificacionItem;
import io.realm.Realm;
import io.realm.RealmConfiguration;


/**
 * Created by Tavo on 13/07/2016.
 */
public class PushNotificationService extends FirebaseMessagingService
{
    String url="http://verdad.herokuapp.com/campaigns/conta?idcampaigns=";
    public PushNotificationService() {
        super();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        switch (Integer.parseInt(remoteMessage.getData().get("type")))
        {
            case 1:
                notificacionSimple(remoteMessage);
                break;
            case 2:
                notificacionBigText(remoteMessage);
                break;
            case 3:
                notificacionBigPicture(remoteMessage);
                break;
            case 4:
                notificationInbox(remoteMessage);
                break;
        }

    }

    @Override
    public void onDeletedMessages() {
        Log.e("Notification Service","onDeletedMEssages");
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

    public void notificacionSimple(RemoteMessage remote) {
        NotificationManager mNotifyMgr= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        SimpleDateFormat parserFormal= new SimpleDateFormat("dd MMM yyyy");
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.perfil_whitebg)
                        .setContentTitle(remote.getData().get("title"))
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logoandroid))
                        .setColor(4)
                        .setContentText(remote.getData().get("message"));
        mBuilder.setAutoCancel(true);
        Intent resultIntent = new Intent(this, SplashActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        resultIntent.putExtra("goTo",0);
        int id=Integer.parseInt(remote.getData().get("idcampaigns"))+100;
        NotificacionItem item=new NotificacionItem(R.drawable.logoandroid,remote.getData().get("title"),remote.getData().get("message"),parserFormal.format(Calendar.getInstance().getTime()));
        item.setId(id);
        push(item);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);

        mNotifyMgr.notify(id, mBuilder.build());
        CargarDatos.makePetition(PushNotificationService.this,url+(id-100));
        System.out.println(url+(id-100)+"    A LA VERGAAAAAAAAAAAAAAAAA");
    }

    public void notificacionBigPicture(RemoteMessage remote) {
        NotificationManager mNotifyMgr= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        SimpleDateFormat parserFormal= new SimpleDateFormat("dd MMM yyyy");
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.perfil_whitebg)
                        .setContentTitle("Titulo")
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logoandroid))
                        .setColor(4)
                        .setContentText("Texto de contenido")
                        .setStyle(new NotificationCompat.BigPictureStyle()
                        .setBigContentTitle("Titulo Extendido")
                        .bigLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logoandroid))
                        .bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.bg_deverdad_blur_rojo)));

        mBuilder.setAutoCancel(true);
        Intent resultIntent = new Intent(this, SplashActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        resultIntent.putExtra("goTo",0);
        NotificacionItem item = new NotificacionItem(R.drawable.logoandroid, remote.getFrom(), "De servidor", parserFormal.format(Calendar.getInstance().getTime()));
        item.setId(0);
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

    public void notificacionBigText(RemoteMessage remote) {
        NotificationManager mNotifyMgr= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        SimpleDateFormat parserFormal= new SimpleDateFormat("dd MMM yyyy");
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.perfil_whitebg)
                        .setContentTitle("Titulo")
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logoandroid))
                        .setColor(4)
                        .setContentText("texto de contenido")
                        .setStyle(new NotificationCompat.BigTextStyle()
                        .setBigContentTitle("Titulo Extentido")
                        .bigText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec euismod nulla quam, et bibendum ipsum elementum sit amet. Sed at urna vulputate turpis dignissim sodales. Integer hendrerit eleifend blandit. Praesent dui quam, porttitor et felis pretium, dignissim semper tellus. Vestibulum molestie eros at consequat imperdiet. Sed sagittis est et risus volutpat eleifend. Duis magna neque, pellentesque vel metus sit amet, ornare consectetur libero. ")
                        .setSummaryText("Texto resumido")
                        );

        mBuilder.setAutoCancel(true);
        Intent resultIntent = new Intent(this, SplashActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        resultIntent.putExtra("goTo",0);
        NotificacionItem item = new NotificacionItem(R.drawable.logoandroid, remote.getFrom(), "De servidor", parserFormal.format(Calendar.getInstance().getTime()));
        item.setId(0);
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

    public void notificationInbox(RemoteMessage remote) {
        NotificationManager mNotifyMgr= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        SimpleDateFormat parserFormal= new SimpleDateFormat("dd MMM yyyy");
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.perfil_whitebg)
                        .setContentTitle("Titulo")
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logoandroid))
                        .setColor(4)
                        .setContentText("texto de contenido")
                        .setStyle(new NotificationCompat.InboxStyle()
                        .setBigContentTitle("Texto extendido")
                        .setSummaryText("Texto resumido")
                        .addLine("Linea 1")
                        .addLine("Linea 2")
                        );

        mBuilder.setAutoCancel(true);
        Intent resultIntent = new Intent(this, SplashActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        resultIntent.putExtra("goTo",0);
        NotificacionItem item = new NotificacionItem(R.drawable.logoandroid, remote.getFrom(), "De servidor", parserFormal.format(Calendar.getInstance().getTime()));
        item.setId(0);
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

    public void push(NotificacionItem item)
    {
        RealmConfiguration config = new RealmConfiguration
                .Builder(this)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
         Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(item);
        realm.commitTransaction();
    }

}
