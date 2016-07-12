package co.allza.mararewards.services;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import co.allza.mararewards.CargarDatos;
import co.allza.mararewards.R;
import co.allza.mararewards.activities.NotificacionesActivity;
import co.allza.mararewards.activities.SegurosActivity;
import co.allza.mararewards.activities.SplashActivity;
import co.allza.mararewards.items.CustomerItem;
import co.allza.mararewards.items.NotificacionItem;
import co.allza.mararewards.items.SeguroItem;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Tavo on 27/06/2016.
 */
public class SegurosService extends Service {

    Date fechaActual;
    Date fechaSeguro;
    SimpleDateFormat parserFecha;
    SimpleDateFormat  parserFormal;
    NotificationManager mNotifyMgr;
    SeguroItem seguroTemporal;
    AlarmManager alarmMgr;
    Intent intent ;
    PendingIntent pintent;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if(alarmMgr==null)
        alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);

        intent = new Intent(this, SegurosService.class);
        pintent=PendingIntent.getService(this,0,intent,0);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, 17);
        cal.set(Calendar.MINUTE,48);
        alarmMgr.setInexactRepeating(AlarmManager.RTC, cal.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pintent);
         mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(SegurosService.this, "OnStart", Toast.LENGTH_SHORT).show();
        Calendar calendar=Calendar.getInstance();
        parserFecha=new SimpleDateFormat("dd/MMM/yyyy");
        parserFormal=new SimpleDateFormat("dd MMM yyyy");
        fechaActual=calendar.getTime();
        Realm realm = CargarDatos.getRealm(this);
        ArrayList<SeguroItem> items=new ArrayList<>();

        CustomerItem cliente=realm.where(CustomerItem.class)
                .findFirst();

        if(cliente!=null){
        RealmResults<SeguroItem> result = realm.where(SeguroItem.class)
                .equalTo("customer_id",cliente.getId())
                .findAll();
        for (int i=0; i<result.size(); i++){
            try {
                fechaSeguro=parserFecha.parse(result.get(i).getExpiration());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long temp=(((fechaActual.getTime()-fechaSeguro.getTime())/1000)/60)/60;
            Toast.makeText(SegurosService.this, ""+temp, Toast.LENGTH_SHORT).show();
            if(temp>-300 && temp<0){
                //Próximo a Expirar.
                seguroTemporal=result.get(i);
                RealmResults<NotificacionItem> alreadyInDB=realm.where(NotificacionItem.class)
                        .equalTo("id",i).findAll();
                if(alreadyInDB.isEmpty()) {
                    notifProximoAExpirar(i);
                }

            }
            if(temp>0){
                //Ya expiró
                seguroTemporal=result.get(i);
                RealmResults<NotificacionItem> alreadyInDB=realm.where(NotificacionItem.class)
                        .equalTo("id",i).findAll();
                if(alreadyInDB.isEmpty()) {
                    notifExpiro(i);
                }

                        }


        }


        }
        else
            Toast.makeText(SegurosService.this, "Esperando", Toast.LENGTH_SHORT).show();

        CargarDatos.getNotificacionesFromDatabase(this);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        if(alarmMgr!=null)
            alarmMgr.cancel(pintent);
        mNotifyMgr.cancelAll();
        Toast.makeText(SegurosService.this, "onDestroy", Toast.LENGTH_SHORT).show();
        super.onDestroy();

    }


    public void notifProximoAExpirar(int id)
    {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_notifications_white_48dp)
                        .setContentTitle(seguroTemporal.getDescription())
                        .setLargeIcon( BitmapFactory.decodeResource(getResources(), R.drawable.logoandroid))
                        .setColor(122)
                        .setContentText("Está próximo a expirar");

        mBuilder.setAutoCancel(true);
        Intent resultIntent = new Intent(this, SplashActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        NotificacionItem item=new NotificacionItem(R.drawable.logoandroid,seguroTemporal.getDescription(),"Próximo a expirar", parserFormal.format(fechaActual));
        item.setId(id);
        CargarDatos.pushNotification(this, item);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);
        int mNotificationId = id;
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
    public void notifExpiro(int id)
    {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_notifications_white_48dp)
                        .setContentTitle(seguroTemporal.getDescription())
                        .setLargeIcon( BitmapFactory.decodeResource(getResources(), R.drawable.logoandroid))
                        .setColor(122)
                        .setContentText("Ha expirado, comunícate con tu aseguradora");
        mBuilder.setAutoCancel(true);
        Intent resultIntent = new Intent(this, SplashActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        NotificacionItem item=new NotificacionItem(R.drawable.logoandroid,seguroTemporal.getDescription(),"Ha expirado, comunícate con tu aseguradora", parserFormal.format(fechaActual));
        item.setId(id);
        CargarDatos.pushNotification(this, item);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);
        int mNotificationId = id+15;
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

    @Override
    public boolean stopService(Intent name) {
        mNotifyMgr.cancelAll();
        Toast.makeText(SegurosService.this, "StopService", Toast.LENGTH_SHORT).show();
        return super.stopService(name);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mNotifyMgr.cancelAll();
        return super.onUnbind(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        mNotifyMgr.cancelAll();
        super.onTaskRemoved(rootIntent);
    }
}
