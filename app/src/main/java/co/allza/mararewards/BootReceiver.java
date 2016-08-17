package co.allza.mararewards;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import co.allza.mararewards.services.SegurosService;

/**
 * Created by Tavo on 04/07/2016.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences settings = context.getSharedPreferences(SegurosService.PREFS_NAME, 0);
        boolean silent = settings.getBoolean("servicio",true);
        if(silent){
        Intent myIntent = new Intent(context, SegurosService.class);
        context.startService(myIntent);
        }

    }

}
