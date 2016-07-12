package co.allza.mararewards.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
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
import co.allza.mararewards.interfaces.DialogCallback;
import co.allza.mararewards.interfaces.VolleyCallback;
import co.allza.mararewards.items.CustomerItem;
import co.allza.mararewards.items.DepthPageTransformer;
import co.allza.mararewards.items.NotificacionItem;
import co.allza.mararewards.items.SeguroItem;
import co.allza.mararewards.services.SegurosService;
import io.realm.Realm;
import io.realm.RealmResults;

public class SegurosActivity extends AppCompatActivity implements VolleyCallback, DialogCallback {

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
    int id=0;
    int theme=R.style.MyAlertDialogStyle;
    Menu menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seguros);
        //para Lollipop
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //getWindow().setStatusBarColor(getResources().getColor(R.color.statusbarSeguros));
        }



        CargarDatos.setDialogCallback(this);
        linear=(LinearLayout)findViewById(R.id.linearSeguros);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        int anchoViejo=toolbar.getLayoutParams().height;
        toolbar.getLayoutParams().height=getStatusBarHeight()+anchoViejo;
        toolbar.setTitle("Seguros");
        toolbar.setBackgroundColor(getResources().getColor(R.color.toolbarSeguros));
        toolbar.setNavigationIcon(R.drawable.ic_notifications_white_24dp);
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
        this.menu=menu;
        validarPantalla();

        return true;
        
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id)
        {
            case R.id.cerrar:
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

            case R.id.callToAction:
                Intent ii=new Intent(SegurosActivity.this,CallToActionActivity.class);
                startActivity(ii);
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
                seguroActual.setText(arraySeguros.get(position).getRefname());


            }

            @Override
            public void onPageSelected(int position) {
                try {
                    fechaSeguro= parserFecha.parse(arraySeguros.get(position).getExpiration());
                    if(fechaActual.after(fechaSeguro) && !estaVencido){
                        estaVencido=!estaVencido;
                        fondo.startTransition(500);
                        theme=R.style.MyAlertDialogStyleBlanco;

                    }
                    if(fechaActual.before(fechaSeguro) && estaVencido){
                        estaVencido=!estaVencido;
                        fondo.reverseTransition(500);
                        theme=R.style.MyAlertDialogStyle;
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

    public void validarPantalla()
    {
        if(((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL
                ||(getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL)
                && getResources().getDisplayMetrics().density<=2.0f)
        {
            botonCallToAction.setVisibility(View.GONE);
            if(menu!=null)
            menu.findItem(R.id.callToAction).setVisible(true);

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

        adapter=new SegurosPagerAdapter(SegurosActivity.this,CargarDatos.getArraySeguros());
        onSegurosPulled(adapter);
    }

    @Override
    public void onFailure(String error) {

    }

    @Override
    public void onTokenReceived(String token) {

    }

    @Override
    public void onDialogPetition(int id) {
        SeguroItem temp=adapter.getArrayList().get(id);
        AlertDialog.Builder builder = new AlertDialog.Builder(this,theme);
        builder.setTitle(temp.getDescription());
        builder.setPositiveButton("Cerrar", null);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_custom, null);
        TextView text = (TextView) layout.findViewById(R.id.text);
        if(theme==R.style.MyAlertDialogStyleBlanco)
            text.setTextColor(Color.BLACK);
        text.setText(Html.fromHtml(temp.getFeatures()));
        builder.setView(layout);
        builder.show();
    }
}
