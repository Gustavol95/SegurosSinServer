package co.allza.mararewards;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import co.allza.mararewards.activities.SplashActivity;
import co.allza.mararewards.adapter.SegurosPagerAdapter;
import co.allza.mararewards.interfaces.DialogCallback;
import co.allza.mararewards.interfaces.VolleyCallback;
import co.allza.mararewards.items.CustomerItem;
import co.allza.mararewards.items.NotificacionItem;
import co.allza.mararewards.items.SeguroItem;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by Tavo on 22/06/2016.
 */
public class CargarDatos {
    private static String getCustomer="http://verdad.herokuapp.com/api/customers/";
    private static String accessToken="?access_token=";
    private static String accessToken2="&usertoken=";
    private static String getToken=" http://verdad.herokuapp.com/api/apikey/new?fcm_token=";
    private static JSONObject respuesta;
    private static String token;
    private static String user;
    private static Realm realm;
    private static RequestQueue queue;
    private static StringRequest stringRequest;
    private static SegurosPagerAdapter adapter;
    private static Context context;
    private static ArrayList<SeguroItem> arraySeguros;
    private static ArrayList<NotificacionItem> arrayNotif;
    private static DialogCallback dialogCallback;
    private static int counter=0;
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

    public static void getTokenFromServer(Context ctx, String usertoken, final VolleyCallback callback) {
        context=ctx;
        if(queue==null)
            queue=Volley.newRequestQueue(ctx);
        String url=getToken+ FirebaseInstanceId.getInstance().getToken()+accessToken2+usertoken;
        Log.e("Token",url);
        stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject resp=new JSONObject(response);
                    JSONObject data=resp.getJSONObject("data");
                    callback.onTokenReceived(data.getString("token"));
                } catch (JSONException e) {    callback.onFailure(e.toString());       }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {   callback.onFailure(error.toString());   }
        });

        queue.add(stringRequest);
    }

    public static void makePetition(Context ctx, String url) {
        context=ctx;
        if(queue==null)
            queue=Volley.newRequestQueue(ctx);
        stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {     }
        });
        queue.add(stringRequest);

    }

    public static void pullSeguros( Context ctx, String usuario, String usertoken, final VolleyCallback callback) {
        token=usertoken;
        user=usuario;
        context=ctx;
        final ArrayList<SeguroItem> items=new ArrayList<>();
        if(queue==null)
            queue=Volley.newRequestQueue(ctx);
        String url=getCustomer+usuario+accessToken+usertoken;
        Log.e("Seguros",url);
        stringRequest = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            respuesta=new JSONObject(response);
                            JSONObject resp=new JSONObject(response);
                            parseSeguros(resp, callback);
                        } catch (Exception e) {  callback.onFailure(e.toString()); }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFailure(error.toString());      }
        });
        queue.add(stringRequest);
    }

    private static void parseSeguros(JSONObject data, VolleyCallback callback) {
        Realm realm =getRealm(context);
        try {
            JSONObject datos=data.getJSONObject("data");
            JSONObject cliente=datos.getJSONObject("Customer");
            JSONArray seguros=datos.getJSONArray("insurance");

            // Obtener el cliente
            CustomerItem customer=new CustomerItem();
            customer.setId(cliente.getInt("id"));
            customer.setName(cliente.getString("name"));
            customer.setEmail(cliente.getString("email"));
            customer.setUsertoken(cliente.getString("usertoken"));
            customer.setPhone(cliente.getString("phone"));
            customer.setCreated_at(cliente.getString("created_at"));
            customer.setUpdated_at(cliente.getString("updated_at"));
            customer.setToken(token);
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(customer);
            realm.commitTransaction();
            arraySeguros=new ArrayList<>();

            //Obtener los seguros
            for(int i=0;i<seguros.length();i++){
                JSONObject obj=seguros.getJSONObject(i);
                SeguroItem seg=new SeguroItem();
                seg.setId(obj.getInt("id"));
                seg.setName(obj.getString("name"));
                seg.setDescription(obj.getString("Description"));
                seg.setExpiration(obj.getString("expiration"));
                seg.setUpdated_at(obj.getString("updated_at"));
                seg.setCustomer_id(obj.getInt("customer_id"));
                seg.setInsured_name(obj.getString("insured_name"));
                seg.setPolicy(obj.getString("policy"));
                seg.setEmergency(obj.getString("emergency"));
                seg.setRefname(obj.getString("refname"));
                seg.setFeatures(obj.getString("features"));

                //Meter a la base de datos
                realm.beginTransaction();
                realm.copyToRealmOrUpdate(seg);
                realm.commitTransaction();
                arraySeguros.add(seg);
            }

            adapter=new SegurosPagerAdapter(context,arraySeguros);
            callback.onSuccess(adapter);
        } catch (JSONException e) {  callback.onFailure(e.toString());   }

    }

    public static void getSegurosFromDatabase(Context ctx,  VolleyCallback callback) {
        context=ctx;
        Realm realm = getRealm(ctx);
        arraySeguros=new ArrayList<>();
        CustomerItem cliente=realm.where(CustomerItem.class)
                .findFirst();
        RealmResults<SeguroItem> result = realm.where(SeguroItem.class)
                .equalTo("customer_id",cliente.getId())
                .findAll();
        result.sort("id", Sort.DESCENDING);

        for (int i=0; i<result.size(); i++){
            arraySeguros.add(result.get(i)); }
        adapter=new SegurosPagerAdapter(context,arraySeguros);
        callback.onSuccess(adapter);
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

    public static void setDialogCallback(DialogCallback callback) {  dialogCallback=callback;   }

    public static DialogCallback getDialogCallback()  { return dialogCallback;  }

    public static String getToken() {  return token;  }

    public static void setToken(String token) {  CargarDatos.token = token;  }

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
}
