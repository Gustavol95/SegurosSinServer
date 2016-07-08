package co.allza.mararewards;

import android.content.Context;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import co.allza.mararewards.adapter.NotificacionesAdapter;
import co.allza.mararewards.adapter.SegurosPagerAdapter;
import co.allza.mararewards.interfaces.DialogCallback;
import co.allza.mararewards.interfaces.VolleyCallback;
import co.allza.mararewards.items.CustomerItem;
import co.allza.mararewards.items.NotificacionItem;
import co.allza.mararewards.items.SeguroItem;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by Tavo on 22/06/2016.
 */
public class CargarDatos {
    private static String getCustomer="http://verdad.herokuapp.com/api/customers/";
    private static String accessToken="?access_token=";
    private static String getToken=" http://verdad.herokuapp.com/api/apikey/new?access_token=";
    private static String token;
    private static Realm realm;
    private static RequestQueue queue;
    private static StringRequest stringRequest;
    private static JSONObject respuesta;
    private static SegurosPagerAdapter adapter;
    private static NotificacionesAdapter notifAdapter;
    private static boolean segurosCargados;
    private static Context context;
    private static ArrayList<SeguroItem> arraySeguros;
    private static ArrayList<NotificacionItem> arrayNotif;
    private static DialogCallback dialogCallback;

    public static void getTokenFromServer(Context ctx,String usertoken, final VolleyCallback callback)
    {
        context=ctx;
        if(queue==null)
            queue=Volley.newRequestQueue(ctx);
        String url=getToken+usertoken;
        stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject resp=new JSONObject(response);
                    JSONObject data=resp.getJSONObject("data");
                    callback.onTokenReceived(data.getString("token"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    //  Toast.makeText(context, ""+e.toString(), Toast.LENGTH_SHORT).show();
                    callback.onFailure(e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFailure(error.toString());
                //Toast.makeText(context, ""+error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    public static void pullSeguros(final Context ctx, String usuario, String usertoken, final VolleyCallback callback)
    {
        context=ctx;
        final ArrayList<SeguroItem> items=new ArrayList<>();
        if(queue==null)
            queue=Volley.newRequestQueue(ctx);

        String url=getCustomer+usuario+accessToken+usertoken;
        //Toast.makeText(context, usuario + usertoken, Toast.LENGTH_SHORT).show();

        stringRequest = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            respuesta=new JSONObject(response);
                            JSONObject resp=new JSONObject(response);
                            parseSeguros(resp, callback);


                        } catch (Exception e) {
                            //            Toast.makeText(ctx,e.toString(), Toast.LENGTH_SHORT).show();
                            callback.onFailure(e.toString());
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //  Toast.makeText(ctx,error.toString()+"PUTAMADRE", Toast.LENGTH_SHORT).show();
                callback.onFailure(error.toString());
            }
        });

        queue.add(stringRequest);

    }

    private static void parseSeguros(JSONObject data, VolleyCallback callback)
    {

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

            realm.beginTransaction();
            CustomerItem cust=realm.copyToRealmOrUpdate(customer);
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
                realm.beginTransaction();
                realm.copyToRealmOrUpdate(seg);
                realm.commitTransaction();
                arraySeguros.add(seg);

            }

            adapter=new SegurosPagerAdapter(context,arraySeguros);
            callback.onSuccess(adapter);


        } catch (JSONException e) {
            e.printStackTrace();
            //System.out.println( "VERGA"+e.toString());
            callback.onFailure(e.toString());

        }


    }

    public static ArrayList<SeguroItem> getSegurosFromDatabase(Context ctx,  VolleyCallback callback)
    {
        context=ctx;
        Realm realm = getRealm(ctx);
        ArrayList<SeguroItem> items=new ArrayList<>();
        CustomerItem cliente=realm.where(CustomerItem.class)
                .findFirst();

        RealmResults<SeguroItem> result = realm.where(SeguroItem.class)
                .equalTo("customer_id",cliente.getId())
                .findAll();
        for (int i=0; i<result.size(); i++){
            items.add(result.get(i));
            }

        callback.onSuccess(adapter);
        arraySeguros=new ArrayList<>();
        arraySeguros.addAll(items);
        adapter=new SegurosPagerAdapter(context,items);

        return items;
    }
    public static ArrayList<SeguroItem> getArraySeguros()
    {
        return arraySeguros;
    }

    public static boolean adapterIsNull()
    {
        if(adapter==null)
            return true;
        if(adapter.getCount()<=0)
            return true;

        return false;
    }

    public static ArrayList<SeguroItem> getSegurosArray()
    {
        return arraySeguros;
    }
    public static SegurosPagerAdapter getAdapter()
    {
        return adapter;
    }



    public static Realm getRealm(Context ctx)
    {
        context=ctx;
        if(realm==null ){
        RealmConfiguration config = new RealmConfiguration
                .Builder(ctx)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();
        return realm;
        }
        else{return  realm;}
    }


    //NOTIFICACIONES
    //Generar Adapter de las notificaciones
    public static void getNotificacionesFromDatabase(Context ctx)
    {
        context=ctx;
        Realm realm = getRealm(ctx);
        arrayNotif=new ArrayList<>();
        RealmResults<NotificacionItem> result = realm.where(NotificacionItem.class)
                .findAll();
        for (int i=0; i<result.size(); i++){
            arrayNotif.add(result.get(i));
        }
    }
    //Get Adapter
    public static ArrayList<NotificacionItem> getNotifAdapter()
    {
        return arrayNotif;
    }

    //Gererar Realm Object , createOrUpdateToRealm , mandar llamar refresh del adapter
    public static void pushNotification(Context ctx,NotificacionItem item)
    {
        context=ctx;
        Realm realm = getRealm(ctx);
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(item);
        realm.commitTransaction();

    }

    public static void setDialogCallback(DialogCallback callback)
    {
        dialogCallback=callback;
    }
    public static DialogCallback getDialogCallback()
    {
         return dialogCallback;
    }

}
