package co.allza.mararewards;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import co.allza.mararewards.activities.SplashActivity;
import co.allza.mararewards.adapter.SegurosPagerAdapter;
import co.allza.mararewards.interfaces.OnInfoClicked;
import co.allza.mararewards.items.NotificacionItem;
import co.allza.mararewards.items.SeguroItem;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.RealmSchema;
import io.realm.Sort;

/**
 * Created by Tavo on 22/06/2016.
 */
public class CargarDatos {

    private static String user;
    private static Realm realm;
    private static RequestQueue queue;
    private static StringRequest stringRequest;
    private static SegurosPagerAdapter adapter;
    private static Context context;
    private static ArrayList<SeguroItem> arraySeguros;
    private static ArrayList<NotificacionItem> arrayNotif;
    private static int counter=0;
    private static OnInfoClicked onInfoClicked;
    private static ArrayList<String> titulos= new ArrayList<>();
    public static final int ROBOTO_MEDIUM =   0;
    public static final int ROBOTO_REGULAR =   1;
    public static final int RUBIK_LIGHT =   2;
    public static final int RUBIK_REGULAR =   3;
    public static final int RUBIK_MEDIUM =   4;
    public static final int RUBIK_BOLD =   5;
    private static final int NUM_OF_CUSTOM_FONTS = 6;
    private static boolean fontsLoaded = false;
    private static Typeface[] fonts = new Typeface[6];
    private static String[] fontPath = {
            "fonts/Roboto-Medium.ttf",
            "fonts/Roboto-Regular.ttf",
            "fonts/Rubik-Light.ttf",
            "fonts/Rubik-Regular.ttf",
            "fonts/Rubik-Medium.ttf",
            "fonts/Rubik-Bold.ttf"
    };

    public static void saveSeguro(Context ctx,SeguroItem seguro){
       Realm realm= getRealm(ctx);
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(seguro);
        realm.commitTransaction();
        realm.close();
        realm=null;
    }
    public static void deleteSeguro(Context ctx,SeguroItem seguro){
        Realm realm= getRealm(ctx);
        RealmResults<SeguroItem> result=realm.where(SeguroItem.class)
                .equalTo("id",seguro.getId())
                .findAll();
        realm.beginTransaction();
        result.deleteAllFromRealm();
        realm.commitTransaction();
        realm.close();
        realm=null;
    }

    public static void getSegurosFromDatabase(Context ctx) {
        context=ctx;
        Realm realm = getRealm(ctx);
        arraySeguros=new ArrayList<>();
        RealmResults<SeguroItem> result = realm.where(SeguroItem.class)
                .findAll();
        for (int i=0; i<result.size(); i++){
            arraySeguros.add(result.get(i)); }
        adapter=new SegurosPagerAdapter(context,arraySeguros);

    }

    public static ArrayList<SeguroItem> getArraySeguros() {  return arraySeguros;  }

    public static SegurosPagerAdapter getAdapter() {  return adapter;  }

    public static Realm getRealm(Context ctx) {
        context=ctx;

        RealmConfiguration config = new RealmConfiguration
                .Builder(ctx)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();
        return realm;

    }

    public static void getNotificacionesFromDatabase(Context ctx) {
        context=ctx;
        Realm realm = getRealm(ctx);
        arrayNotif=new ArrayList<>();
        RealmResults<NotificacionItem> result = realm.where(NotificacionItem.class)
                .findAll();
        for (int i=0; i<result.size(); i++){
            arrayNotif.add(result.get(i));
        }
    }

    public static ArrayList<NotificacionItem> getNotifAdapter()
    {
        return arrayNotif;
    }

    public static void pushNotification(Context ctx,NotificacionItem item) {
        context=ctx;
        Realm realm = getRealm(ctx);
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(item);
        realm.commitTransaction();

    }

    public static String getUser() {  return user;  }

    public static void setUser(String user) {  CargarDatos.user = user;  }

    public static Typeface getTypeface(Context context, int fontIdentifier) {
        if (!fontsLoaded) {
            loadFonts(context);
        }
        return fonts[fontIdentifier];
    }

    private static void loadFonts(Context context) {
        for (int i = 0; i < NUM_OF_CUSTOM_FONTS; i++) {
            fonts[i] = Typeface.createFromAsset(context.getAssets(), fontPath[i]);
        }
        fontsLoaded = true;

    }

    public static float convertirAPixel(int dp, Context context){
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return (int)(px);
    }

    public static void notificationIsUp( String titulo,Context context, NotificationManager notifManager, boolean withRefresh) {
        titulos.add(titulo);
        counter++;
        NotificationCompat.InboxStyle estilo=null;
        Notification notif ;
        if(counter>1) {
            notifManager.cancelAll();
            estilo=new NotificationCompat.InboxStyle();
            for(int i=0;i<counter;i++)
            {   if(i<3)
                estilo.addLine(titulos.get(i));
            }
            if(counter>3)
            {
                estilo.setSummaryText((counter-3)+" más pendientes");
            }
            estilo.setBigContentTitle("Notificaciones pendientes");
        Intent resultIntent = new Intent(context, SplashActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        resultIntent.putExtra("goTo",101);
        resultIntent.putExtra("refresh",withRefresh);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
         notif = new NotificationCompat.Builder(context)
                .setContentTitle("Tienes "+counter+" notificaciones" )
                .setContentText("Seguros De Verdad")
                .setSmallIcon(R.drawable.perfil_whitebg)
                .setColor(4)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.logoandroid))
                .setStyle(estilo)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .build();


        notifManager.notify(101,notif);}

    }

    public static void emptyNotificationCounter(){
        counter=0;
        titulos.clear();
    }

    public static void clearArraySeguros() {
        arraySeguros.clear();
    }

    public static Context getContext(){return context;}

    public static void setOnInfoCLicked(OnInfoClicked interf){
        onInfoClicked=interf;
    }

    public static OnInfoClicked getOnInfoClicked(){return onInfoClicked;}


}
