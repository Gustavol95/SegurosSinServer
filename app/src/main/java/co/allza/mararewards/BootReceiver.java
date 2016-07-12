package co.allza.mararewards;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import co.allza.mararewards.activities.SplashActivity;
import co.allza.mararewards.items.CustomerItem;
import co.allza.mararewards.services.SegurosService;
import io.realm.Realm;

/**
 * Created by Tavo on 04/07/2016.
 */
public class BootReceiver extends BroadcastReceiver {
    CustomerItem result;
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent myIntent = new Intent(context, SegurosService.class);
        context.startService(myIntent);

    }
}
