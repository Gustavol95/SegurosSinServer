package co.allza.mararewards.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pixelcan.inkpageindicator.InkPageIndicator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import co.allza.mararewards.CargarDatos;
import co.allza.mararewards.CargarFuentes;
import co.allza.mararewards.R;
import co.allza.mararewards.adapter.SegurosPagerAdapter;
import co.allza.mararewards.items.CustomerItem;
import co.allza.mararewards.items.DepthPageTransformer;
import co.allza.mararewards.items.NotificacionItem;
import co.allza.mararewards.items.SeguroItem;
import co.allza.mararewards.services.SegurosService;
import io.realm.Realm;
import io.realm.RealmResults;

public class SegurosActivity extends AppCompatActivity implements CargarDatos.VolleyCallback {

    LinearLayout linear;
    ViewPager pagerSeguros;
    SegurosPagerAdapter adapter;
    ArrayList<SeguroItem> arraySeguros;
    TextView seguroActual;
    Button botonCallToAction;
    SimpleDateFormat parserFecha;
    Calendar calendar;
    Date fechaActual;
    Date fechaSeguro;
    InkPageIndicator inkPageIndicator;
    boolean estaVencido=false;
    TransitionDrawable fondo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seguros);
        //para Lollipop
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //getWindow().setStatusBarColor(getResources().getColor(R.color.statusbarSeguros));
        }
        linear=(LinearLayout)findViewById(R.id.linearSeguros);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        int anchoViejo=toolbar.getLayoutParams().height;
        toolbar.getLayoutParams().height=getStatusBarHeight()+anchoViejo;
        toolbar.setTitle("Seguros");
        toolbar.setBackgroundColor(getResources().getColor(R.color.toolbarSeguros));
        toolbar.setNavigationIcon(R.drawable.ic_notifications_active_white_24dp);
        toolbar.setNavigationOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent i=new Intent(SegurosActivity.this,NotificacionesActivity.class);
                SegurosActivity.this.startActivity(i);
            }
        });

        fondo=(TransitionDrawable)getResources().getDrawable(R.drawable.fondo_seguros);
        linear.setBackground(fondo);


        pagerSeguros=(ViewPager) findViewById(R.id.viewPagerSeguros);
        inkPageIndicator = (InkPageIndicator) findViewById(R.id.indicator);
        //CargarDatos.pullSeguros(getApplicationContext(),"","",this);
        CargarDatos.getNotificacionesFromDatabase(this);
        if(!CargarDatos.adapterIsNull()){
        onSuccess(CargarDatos.getAdapter());}
        seguroActual=(TextView)findViewById(R.id.tituloSeguroActual);
        seguroActual.setTypeface(CargarFuentes.getTypeface(getApplicationContext(),CargarFuentes.RUBIK_MEDIUM));
        botonCallToAction=(Button)findViewById(R.id.botonCallToAction);
        botonCallToAction.setTypeface(CargarFuentes.getTypeface(getApplicationContext(),CargarFuentes.ROBOTO_MEDIUM));
        botonCallToAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(SegurosActivity.this,CallToActionActivity.class);
                startActivity(i);
            }
        });

        parserFecha=new SimpleDateFormat("dd/MMM/yyyy");
        calendar=Calendar.getInstance();
        fechaActual=calendar.getTime();

        //iniciarServicioSeguros();
        validarPrimerUso();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_seguros, menu);
        return true;
        
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.desactivarNotif) {

            return true;
        }
        if (id == R.id.cerrar) {

            Realm realm = CargarDatos.getRealm(getApplicationContext());
            RealmResults<CustomerItem> todo=realm.where(CustomerItem.class).findAll();
            RealmResults<NotificacionItem> allNotifications=realm.where(NotificacionItem.class).findAll();
            realm.beginTransaction();
            todo.deleteAllFromRealm();
            allNotifications.deleteAllFromRealm();
            realm.commitTransaction();
            Intent i=new Intent(SegurosActivity.this,LoginActivity.class);
            startActivity(i);
            finish();


            return true;
        }

      
        return super.onOptionsItemSelected(item);
    }
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void onSegurosPulled(SegurosPagerAdapter pagerAdapter ){

        pagerSeguros.setAdapter(pagerAdapter);
        arraySeguros=pagerAdapter.getArrayList();
        pagerSeguros.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                seguroActual.setText(arraySeguros.get(position).getDescription());


            }

            @Override
            public void onPageSelected(int position) {
                try {
                    fechaSeguro= parserFecha.parse(arraySeguros.get(position).getExpiration());
                    if(fechaActual.after(fechaSeguro) && !estaVencido){
                        estaVencido=!estaVencido;
                        fondo.startTransition(500);

                    }
                    if(fechaActual.before(fechaSeguro) && estaVencido){
                        estaVencido=!estaVencido;
                        fondo.reverseTransition(500);
                    }



                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(SegurosActivity.this, ""+e.toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        pagerSeguros.setPageTransformer(true,new DepthPageTransformer());

        inkPageIndicator.setViewPager(pagerSeguros);
       if(pagerAdapter.getCount()>0)
       {
           parserFecha=new SimpleDateFormat("dd/MMM/yyyy");
           calendar=Calendar.getInstance();
           fechaActual=calendar.getTime();
           try {
               fechaSeguro= parserFecha.parse(pagerAdapter.getArrayList().get(0).getExpiration());
               if(fechaActual.after(fechaSeguro) && !estaVencido){
                   estaVencido=!estaVencido;
                   fondo.startTransition(1500);}
           } catch (ParseException e) {
               e.printStackTrace();
           }
       }

    }

    public void iniciarServicioSeguros()
    {
        AlarmManager alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, SegurosService.class);
        PendingIntent pintent=PendingIntent.getService(this,0,intent,0);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, 16);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pintent);
    }

    public void validarPrimerUso()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(!prefs.getBoolean("firstTime", false)) {
            // run your one time code
            AlarmManager alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, SegurosService.class);
            PendingIntent pintent=PendingIntent.getService(this,0,intent,0);
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(System.currentTimeMillis());
            cal.set(Calendar.HOUR_OF_DAY, 15);
            cal.set(Calendar.MINUTE,22);
            alarmMgr.setInexactRepeating(AlarmManager.RTC, cal.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pintent);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.commit();
        }
    }
    @Override
    protected void onDestroy() {

        super.onDestroy();

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        pagerSeguros.setAdapter(CargarDatos.getAdapter());
    }

    @Override
    public void onSuccess(SegurosPagerAdapter result) {
        onSegurosPulled(result);
    }

    @Override
    public void onFailure(String error) {

    }

    @Override
    public void onTokenReceived(String token) {

    }
}
