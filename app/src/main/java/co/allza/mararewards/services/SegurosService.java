package co.allza.mararewards.services;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
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
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import co.allza.mararewards.CargarDatos;
import co.allza.mararewards.R;
import co.allza.mararewards.activities.SplashActivity;
import co.allza.mararewards.items.CustomerItem;
import co.allza.mararewards.items.NotificacionItem;
import co.allza.mararewards.items.SeguroItem;

public class SegurosService extends Service {

    Date fechaActual;
    Date fechaSeguro;
    SimpleDateFormat parserFecha;
    SimpleDateFormat parserFormal;
    NotificationManager mNotifyMgr;
    SeguroItem seguroTemporal;
    AlarmManager alarmMgr;
    Intent intent;
    PendingIntent pintent;
    String action[];

    public static final String PREFS_NAME = "prefServicio";

    @Override
    public void onCreate() {
        super.onCreate();
        if (alarmMgr == null)
            alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        intent = new Intent(this, SegurosService.class);
        pintent = PendingIntent.getService(this, 0, intent, 0);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, 10);
        cal.set(Calendar.MINUTE, 45);
        alarmMgr.setInexactRepeating(AlarmManager.RTC, cal.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pintent);
        mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        action = new String[]{
                        Intent.ACTION_APP_ERROR,
                        Intent.ACTION_REBOOT,
                        Intent.ACTION_ALL_APPS,
                        Intent.ACTION_BATTERY_OKAY,
                        Intent.ACTION_DEFAULT,
                        Intent.ACTION_MAIN,
                        Intent.ACTION_SEND,
                        Intent.ACTION_SCREEN_OFF,
                        Intent.ACTION_SCREEN_ON,
                        Intent.ACTION_SHUTDOWN};

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Calendar calendar = Calendar.getInstance();
        parserFecha = new SimpleDateFormat("dd/MMM/yyyy");
        parserFormal = new SimpleDateFormat("dd MMM yyyy");
        fechaActual = calendar.getTime();
        Realm realm = CargarDatos.getRealm(this);
        ArrayList<SeguroItem> items = new ArrayList<>();
        CustomerItem cliente = realm.where(CustomerItem.class)
                .findFirst();

        if (cliente != null) {
            RealmResults<SeguroItem> result = realm.where(SeguroItem.class)
                    .equalTo("customer_id", cliente.getId())
                    .findAll();
            result.sort("id", Sort.DESCENDING);
            for (int i = 0; i < result.size(); i++) {
                try {
                    fechaSeguro = parserFecha.parse(result.get(i).getExpiration());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long temp = (((fechaActual.getTime() - fechaSeguro.getTime()) / 1000) / 60) / 60;
                if (temp > -300 && temp < 0) {
                    //Próximo a Expirar.
                    seguroTemporal = result.get(i);
                    RealmResults<NotificacionItem> alreadyInDB = realm.where(NotificacionItem.class)
                            .equalTo("id", i).findAll();
                    if (alreadyInDB.isEmpty()) {
                        notifProximoAExpirar(i);
                    }
                }
                if (temp > 0) {
                    //Ya expiró
                    seguroTemporal = result.get(i);
                    RealmResults<NotificacionItem> alreadyInDB = realm.where(NotificacionItem.class)
                            .equalTo("id", i).findAll();
                    if (alreadyInDB.isEmpty()) {
                        notifExpiro(i);
                    }
                }
            }
        }

        CargarDatos.getNotificacionesFromDatabase(this);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        if (alarmMgr != null)
            alarmMgr.cancel(pintent);
        mNotifyMgr.cancelAll();
        super.onDestroy();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean stopService(Intent name) {
        mNotifyMgr.cancelAll();
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

    public void notifProximoAExpirar(int id) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.perfil_whitebg)
                        .setContentTitle(seguroTemporal.getRefname())
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logoandroid))
                        .setColor(4)
                        .setContentText("Está próximo a expirar");

        mBuilder.setAutoCancel(true);
        Intent resultIntent = new Intent(this, SplashActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        resultIntent.setAction(action[id]);
        resultIntent.putExtra("goTo",id);
        NotificacionItem item = new NotificacionItem(R.drawable.logoandroid, seguroTemporal.getRefname(), "Próximo a expirar", parserFormal.format(fechaActual));
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

    public void notifExpiro(int id) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.perfil_whitebg)
                        .setContentTitle(seguroTemporal.getRefname())
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logoandroid))
                        .setColor(4)
                        .setContentText("Ha expirado, comunícate con tu aseguradora");
        mBuilder.setAutoCancel(true);
        Intent resultIntent = new Intent(this, SplashActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        resultIntent.setAction(action[id]);
        resultIntent.putExtra("goTo",id);
        NotificacionItem item = new NotificacionItem(R.drawable.logoandroid, seguroTemporal.getRefname(), "Ha expirado, comunícate con tu aseguradora", parserFormal.format(fechaActual));
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
}
