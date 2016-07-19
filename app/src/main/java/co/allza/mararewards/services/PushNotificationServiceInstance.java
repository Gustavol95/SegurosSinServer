package co.allza.mararewards.services;

import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Tavo on 13/07/2016.
 */
public class PushNotificationServiceInstance extends FirebaseInstanceIdService{
    public PushNotificationServiceInstance() {
        super();
    }


    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
       


    }
}
