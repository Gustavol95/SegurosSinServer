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
import co.allza.mararewards.adapter.SegurosPagerAdapter;
import co.allza.mararewards.items.CustomerItem;
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
    public static String token;

    private static RequestQueue queue;
    private static StringRequest stringRequest;
    private static JSONObject respuesta;
    private static SegurosPagerAdapter adapter;
    private static boolean segurosCargados;
    private static Context context;

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

    private static ArrayList<SeguroItem> parseSeguros(JSONObject data, VolleyCallback callback)
    {
        RealmConfiguration config = new RealmConfiguration.Builder(context).build();
        Realm.setDefaultConfiguration(config);
        Realm realm = Realm.getDefaultInstance();
        try {
            JSONObject datos=data.getJSONObject("data");
            JSONObject cliente=datos.getJSONObject("Customer");
            JSONArray seguros=datos.getJSONArray("insurance");
            ArrayList<SeguroItem> items=new ArrayList<>();

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
                realm.beginTransaction();
                SeguroItem temp=realm.copyToRealmOrUpdate(seg);
                realm.commitTransaction();
                items.add(seg);

            }

            adapter=new SegurosPagerAdapter(context,items);
            callback.onSuccess(adapter);

            return items;
        } catch (JSONException e) {
            e.printStackTrace();
            //System.out.println( "VERGA"+e.toString());
            callback.onFailure(e.toString());
            return null;
        }


    }

    public static ArrayList<SeguroItem> getSegurosFromDatabase(Context ctx, String usuario, VolleyCallback callback)
    {
        context=ctx;
        RealmConfiguration config = new RealmConfiguration.Builder(context).build();
        Realm.setDefaultConfiguration(config);
        Realm realm = Realm.getDefaultInstance();
        ArrayList<SeguroItem> items=new ArrayList<>();
        CustomerItem cliente=realm.where(CustomerItem.class)
                .findFirst();

        RealmResults<SeguroItem> result = realm.where(SeguroItem.class)
                .equalTo("customer_id",cliente.getId())
                .findAll();
        for (int i=0; i<result.size(); i++){
            items.add(result.get(i));
            }
         adapter=new SegurosPagerAdapter(context,items);
        callback.onSuccess(adapter);

        return items;
    }

    public static boolean adapterIsNull()
    {
        if(adapter==null)
            return true;
        if(adapter.getCount()<=0)
            return true;

        return false;
    }

    public static SegurosPagerAdapter getAdapter()
    {
        return adapter;
    }


    public interface VolleyCallback{
        void onSuccess(SegurosPagerAdapter result);
        void onFailure(String error);
        void onTokenReceived(String token);
    }


}
