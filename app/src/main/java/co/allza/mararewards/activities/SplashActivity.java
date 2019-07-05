package co.allza.mararewards.activities;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import co.allza.mararewards.CargarDatos;
import co.allza.mararewards.R;


public class SplashActivity extends Activity  {
    Handler elHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        try {
            CargarDatos.getSegurosFromDatabase(this);
        } finally {
            if(CargarDatos.getArraySeguros().size()!=0){
            elHandler = new Handler();
            elHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(SplashActivity.this, SegurosActivity.class);
                    startActivity(i);
                    finish();
                }
            }, 1000);
            }
            else{
                elHandler = new Handler();
                elHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(i);
                        finish();
                    }
                }, 1000);
            }
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


}
