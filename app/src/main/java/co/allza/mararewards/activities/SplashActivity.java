package co.allza.mararewards.activities;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import co.allza.mararewards.CargarDatos;
import co.allza.mararewards.R;
import co.allza.mararewards.adapter.SegurosPagerAdapter;
import co.allza.mararewards.interfaces.VolleyCallback;
import co.allza.mararewards.items.CustomerItem;
import io.realm.Realm;


public class SplashActivity extends Activity implements VolleyCallback {
    Handler elHandler;
    CustomerItem result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);


        Realm realm = CargarDatos.getRealm(SplashActivity.this);
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
           CargarDatos.getSegurosFromDatabase(SplashActivity.this,this);
        }


    }

    @Override
    protected void onStop() {
        super.onStop();
        elHandler=null;
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        elHandler=null;
        finish();
    }

    @Override
    public void onSuccess(SegurosPagerAdapter result) {
        elHandler=new Handler();
        elHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, SegurosActivity.class);
                Intent aver=getIntent();
                if(aver.hasExtra("goTo"))
                    i.putExtra("goTo",aver.getExtras().getInt("goTo",-1));
                i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(i);
                finish();
            }},1000);
    }

    @Override
    public void onFailure(String error) {

    }

    @Override
    public void onTokenReceived(String token) {

    }
}
