package co.allza.mararewards.services;

import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import co.allza.mararewards.CargarDatos;
import co.allza.mararewards.items.CustomerItem;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Tavo on 13/07/2016.
 */
public class PushNotificationServiceInstance extends FirebaseInstanceIdService{
    public PushNotificationServiceInstance() {
        super();
    }
    String url="http://verdad.herokuapp.com/api/fcmdevice/show?fcm_token=";
    String url2="&usertoken=";

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        RealmConfiguration config = new RealmConfiguration
                .Builder(this)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        Realm realm = Realm.getDefaultInstance();
        CustomerItem result= realm.where(CustomerItem.class)
                .findFirst();
        if(result!=null)
        {
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            CargarDatos.makePetition(this,url+refreshedToken+url2+result.getUsertoken());
        }
        realm.close();
        realm=null;



    }
}
