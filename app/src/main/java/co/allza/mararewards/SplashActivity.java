package co.allza.mararewards;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Toast;

import co.allza.mararewards.adapter.SegurosPagerAdapter;
import co.allza.mararewards.items.CustomerItem;
import co.allza.mararewards.items.SeguroItem;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;


public class SplashActivity extends Activity implements CargarDatos.VolleyCallback{
    Handler elHandler;
    CustomerItem result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        Realm realm = CargarDatos.getRealm(getApplicationContext());
        result = realm.where(CustomerItem.class)
                .findFirst();
        if(result==null)
        {
            elHandler=new Handler();
            elHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }},1000);

        }
        else
        {
           CargarDatos.getSegurosFromDatabase(getApplicationContext(),result.getUsertoken(),this);
        }


    }



    @Override
    public void onSuccess(SegurosPagerAdapter result) {


        elHandler=new Handler();
        elHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, SegurosActivity.class);
                startActivity(i);
                finish();
            }},1000);
    }

    @Override
    public void onFailure(String error) {
        Toast.makeText(SplashActivity.this, ""+error, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onTokenReceived(String token) {

    }
}
