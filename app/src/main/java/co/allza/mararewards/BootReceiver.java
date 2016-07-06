package co.allza.mararewards;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import co.allza.mararewards.services.SegurosService;

/**
 * Created by Tavo on 04/07/2016.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent myIntent = new Intent(context, SegurosService.class);
        context.startService(myIntent);

    }
}
