package co.allza.mararewards.activities;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.transition.Fade;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import co.allza.mararewards.adapter.NotificacionesAdapter;
import co.allza.mararewards.items.CustomViewPager;
import co.allza.mararewards.items.ResizeAnimation;
import io.realm.Realm;
import io.realm.RealmResults;
import co.allza.mararewards.CargarDatos;
import co.allza.mararewards.R;
import co.allza.mararewards.adapter.SegurosPagerAdapter;
import co.allza.mararewards.interfaces.DialogCallback;
import co.allza.mararewards.interfaces.VolleyCallback;
import co.allza.mararewards.items.CustomSwipeToRefresh;
import co.allza.mararewards.items.CustomerItem;
import co.allza.mararewards.items.DepthPageTransformer;
import co.allza.mararewards.items.NotificacionItem;
import co.allza.mararewards.items.SeguroItem;
import co.allza.mararewards.services.SegurosService;
import com.eftimoff.viewpagertransformers.StackTransformer;
import com.pixelcan.inkpageindicator.InkPageIndicator;

public class SegurosActivity extends AppCompatActivity implements VolleyCallback, DialogCallback {

    RelativeLayout linear;
    ListView listaNotif;
    LinearLayout linearContenido;
    ArrayList<NotificacionItem> arrayNotif;
    NotificacionesAdapter adapterNotif;
    CustomViewPager pagerSeguros;
    SegurosPagerAdapter adapter;
    CustomSwipeToRefresh swipe;
    ArrayList<SeguroItem> arraySeguros;
    TextView seguroActual;
    Button botonCallToAction;
    SimpleDateFormat parserFecha;
    Calendar calendar;
    Date fechaActual;
    Date fechaSeguro;
    InkPageIndicator inkPageIndicator;
    boolean estaVencido ;
    TransitionDrawable fondo;
    TransitionDrawable flecha;
    int id = -1;
    int goTo;
    int theme = R.style.MyAlertDialogStyle;
    Menu menu;
    MenuItem checkNotif;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    int alturaToolbar;
    AppBarLayout barLayout;
    boolean expandir=true;
    Toolbar toolbar;
    LinearLayout linearNotif;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seguros);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        CargarDatos.setDialogCallback(this);
        linear = (RelativeLayout) findViewById(R.id.linearSeguros);
        linearContenido=(LinearLayout)findViewById(R.id.linearContenido);
        linearNotif=(LinearLayout)findViewById(R.id.linearNotif);
        barLayout=(AppBarLayout)findViewById(R.id.aver);
        listaNotif=(ListView)findViewById(R.id.listViewNotificaciones);
        listaNotif.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent i=new Intent(SegurosActivity.this,SegurosActivity.class);
                if(adapterNotif.getItem(position).getId()<100){
                    i.putExtra("goTo",  adapterNotif.getItem(position).getId());}
                i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(i);

            }
        });
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        int anchoViejo = toolbar.getLayoutParams().height;
        toolbar.getLayoutParams().height = getStatusBarHeight() + anchoViejo;
        alturaToolbar=toolbar.getLayoutParams().height;
        getSupportActionBar().setTitle("Mis seguros");
        toolbar.setBackgroundColor(getResources().getColor(R.color.toolbarSeguros));
        flecha = (TransitionDrawable) getResources().getDrawable(R.drawable.morphing_arrow);
        linear.setBackground(fondo);
        toolbar.setNavigationIcon(flecha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(expandir)
                    expandirToolbar();
                else
                   colapsarToolbar();
            }
        });

        fondo = (TransitionDrawable) getResources().getDrawable(R.drawable.fondo_seguros);
        linear.setBackground(fondo);
        pagerSeguros = (CustomViewPager) findViewById(R.id.viewPagerSeguros);
        inkPageIndicator = (InkPageIndicator) findViewById(R.id.indicator);
        seguroActual = (TextView) findViewById(R.id.tituloSeguroActual);
        seguroActual.setTypeface(CargarDatos.getTypeface(getApplicationContext(), CargarDatos.RUBIK_MEDIUM));
        botonCallToAction = (Button) findViewById(R.id.botonCallToAction);
        botonCallToAction.setTypeface(CargarDatos.getTypeface(getApplicationContext(), CargarDatos.ROBOTO_MEDIUM));
        botonCallToAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SegurosActivity.this, CallToActionActivity.class);
                startActivity(i);
            }
        });
        swipe = (CustomSwipeToRefresh) findViewById(R.id.swipe);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Realm realm = CargarDatos.getRealm(SegurosActivity.this);
                CustomerItem result = realm.where(CustomerItem.class)
                        .findFirst();
                if (result != null) {
                    CargarDatos.pullSeguros(SegurosActivity.this, result.getUsertoken(), result.getToken(), SegurosActivity.this);
                    pagerSeguros.setDeshabilitarTouch(true);
                    realm.beginTransaction();
                    RealmResults<SeguroItem> segurosAnteriores = realm.where(SeguroItem.class).findAll();
                    segurosAnteriores.deleteAllFromRealm();
                    realm.commitTransaction();

                }

            }
        });
        swipe.setColorSchemeResources(R.color.rectanguloSplash,R.color.toolbarSeguros);
        parserFecha = new SimpleDateFormat("dd/MMM/yyyy");
        calendar = Calendar.getInstance();
        fechaActual = calendar.getTime();
        validarPrimerUso();
        CargarDatos.getSegurosFromDatabase(this,this);
        linearContenido.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!expandir)
                    colapsarToolbar();
                return false;
            }
        });
        pagerSeguros.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!expandir)
                    colapsarToolbar();
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_seguros, menu);
        this.menu = menu;
        checkNotif=menu.findItem(R.id.iniciarNotif);
        validarPantalla();
        return true;
    }

    @Override
    protected void onPause() {
        if(!expandir)
            colapsarToolbar();
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.cerrar:
                Realm realm = CargarDatos.getRealm(getApplicationContext());
                RealmResults<CustomerItem> todo = realm.where(CustomerItem.class).findAll();
                RealmResults<NotificacionItem> allNotifications = realm.where(NotificacionItem.class).findAll();
                realm.beginTransaction();
                todo.deleteAllFromRealm();
                allNotifications.deleteAllFromRealm();
                realm.commitTransaction();
                Intent i = new Intent(SegurosActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
                return true;

            case R.id.callToAction:
                Intent ii = new Intent(SegurosActivity.this, CallToActionActivity.class);
                startActivity(ii);
                return true;
            case R.id.borrarNotif:
                if (CargarDatos.getNotifAdapter().size() != 0) {
                    for (int b = 0; b < CargarDatos.getNotifAdapter().size(); b++) {
                        adapterNotif.remove(CargarDatos.getNotifAdapter().get(b));
                    }
                    listaNotif.setAdapter(adapterNotif);
                }
                CargarDatos.getNotifAdapter().clear();
                realm = CargarDatos.getRealm(this);
                RealmResults<NotificacionItem> borrarNotif = realm.where(NotificacionItem.class).findAll();
                realm.beginTransaction();
                borrarNotif.deleteAllFromRealm();
                realm.commitTransaction();
                return true;

            case R.id.iniciarNotif:
                if (checkNotif.isChecked()) {
                    checkNotif.setChecked(false);
                    stopService(new Intent(this, SegurosService.class));
                    editor = settings.edit();
                    editor.putBoolean("servicio", false);
                    editor.commit();

                    return true;
                } else {
                    checkNotif.setChecked(true);
                    startService(new Intent(this, SegurosService.class));
                    editor = settings.edit();
                    editor.remove("servicio");
                    editor.commit();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            if (CargarDatos.getNotifAdapter().size() != 0) {
                                for (int i = 0; i < CargarDatos.getNotifAdapter().size(); i++) {
                                    adapterNotif.remove(CargarDatos.getNotifAdapter().get(i));
                                }
                            }
                            CargarDatos.getNotificacionesFromDatabase(SegurosActivity.this);
                            arrayNotif = CargarDatos.getNotifAdapter();
                            if (CargarDatos.getNotifAdapter().size() != 0) {
                                for (int i = 0; i < CargarDatos.getNotifAdapter().size(); i++) {
                                    adapterNotif.add(CargarDatos.getNotifAdapter().get(i));
                                }
                                listaNotif.setAdapter(adapterNotif);
                            }
                        }
                    }, 1000);

                    return true;
                }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();     }

    @Override
    protected void onResume() {
        super.onResume();
        validarPosicion();    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);                     }

    @Override
    public void onSuccess(SegurosPagerAdapter result) {
        adapter = new SegurosPagerAdapter(SegurosActivity.this, CargarDatos.getArraySeguros());
        onSegurosPulled(adapter);
        if (swipe != null && swipe.isRefreshing())
            swipe.setRefreshing(false);
    }

    @Override
    public void onFailure(String error) {
        if (swipe != null && swipe.isRefreshing())
            swipe.setRefreshing(false);
    }

    @Override
    public void onTokenReceived(String token) {

    }

    @Override
    public void onDialogPetition(int id) {
        SeguroItem temp = adapter.getArrayList().get(id);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, theme);
        builder.setTitle(temp.getDescription());
        builder.setPositiveButton("Cerrar", null);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_custom, null);
        TextView text = (TextView) layout.findViewById(R.id.text);
        if (theme == R.style.MyAlertDialogStyleBlanco)
            text.setTextColor(Color.BLACK);
        text.setText(Html.fromHtml(temp.getFeatures()));
        builder.setView(layout);
        builder.show();
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void onSegurosPulled(final SegurosPagerAdapter pagerAdapter) {
        final AlphaAnimation alphaReveal=new AlphaAnimation(0.0f,1.0f);
        alphaReveal.setDuration(500);
        alphaReveal.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                pagerSeguros.setVisibility(View.VISIBLE);
                pagerSeguros.setAdapter(pagerAdapter);
                if(pagerAdapter.getCount()>1)
                    inkPageIndicator.setViewPager(pagerSeguros);
                else
                    inkPageIndicator.setVisibility(View.GONE);}
            @Override
            public void onAnimationEnd(Animation animation)   {

                if(CargarDatos.getArraySeguros().size()>0){
           }

                pagerSeguros.setDeshabilitarTouch(false);

            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        AlphaAnimation alpha=new AlphaAnimation(1.0f,0.0f);
        alpha.setDuration(150);
        alpha.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                pagerSeguros.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        pagerSeguros.startAnimation(alpha);
        pagerSeguros.postDelayed(new Runnable() {
            @Override
            public void run() {
                pagerSeguros.clearAnimation();
                pagerSeguros.startAnimation(alphaReveal);  }},170);
        arraySeguros = CargarDatos.getArraySeguros();
        if(arraySeguros.size()<=0)
        {
            Realm realm = CargarDatos.getRealm(SegurosActivity.this);
            CustomerItem result = realm.where(CustomerItem.class)
                    .findFirst();
            if (result != null) {
                if (pagerSeguros != null){
                    CargarDatos.pullSeguros(SegurosActivity.this, result.getUsertoken(), result.getToken(), SegurosActivity.this);
                }
                realm.beginTransaction();
                RealmResults<SeguroItem> segurosAnteriores = realm.where(SeguroItem.class).findAll();
                segurosAnteriores.deleteAllFromRealm();
                realm.commitTransaction();
                                }
        }
        pagerSeguros.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                seguroActual.setText(arraySeguros.get(position).getRefname());
            }

            @Override
            public void onPageSelected(int position) {


                try {
                    fechaSeguro = parserFecha.parse(arraySeguros.get(position).getExpiration());
                    if (fechaActual.after(fechaSeguro) && !estaVencido) {
                        estaVencido=true;
                        fondo.startTransition(500);
                        theme = R.style.MyAlertDialogStyleBlanco;
                    }
                    if (fechaActual.before(fechaSeguro)&& estaVencido ) {
                        estaVencido=false;
                        fondo.reverseTransition(500);
                        theme = R.style.MyAlertDialogStyle;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
       // pagerSeguros.setPageTransformer(true, new DepthPageTransformer());
       pagerSeguros.setPageTransformer(true, new StackTransformer());

        if (pagerAdapter.getCount() > 0) {
            parserFecha = new SimpleDateFormat("dd/MMM/yyyy");
            calendar = Calendar.getInstance();
            fechaActual = calendar.getTime();
            try {
                fechaSeguro = parserFecha.parse(pagerAdapter.getArrayList().get(0).getExpiration());
                if (fechaActual.after(fechaSeguro)) {
                    estaVencido=true;
                    fondo.startTransition(1500);
                    theme = R.style.MyAlertDialogStyleBlanco;
                }
                else if(estaVencido){
                    fondo.reverseTransition(1500);
                    estaVencido=false;
                    theme = R.style.MyAlertDialogStyle;}
                pagerSeguros.setCurrentItem(0);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public void validarPantalla() {
        if (((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL
                || (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL)
                && getResources().getDisplayMetrics().density <= 2.0f) {
            botonCallToAction.setVisibility(View.GONE);
            if (menu != null)
                menu.findItem(R.id.callToAction).setVisible(true);

        }
    }

    public void validarPrimerUso() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean("firstTime", false)) {
            SharedPreferences.Editor editor = prefs.edit();
            startService(new Intent(SegurosActivity.this, SegurosService.class));
            editor.remove("servicio");
            editor.putBoolean("firstTime", true);
            editor.commit();
        }
    }

    public void validarPosicion(){
        if(getIntent().hasExtra("goTo"))
            goTo=getIntent().getExtras().getInt("goTo",-1);
        if(goTo!=-1){
            pagerSeguros.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pagerSeguros.setCurrentItem(goTo ,true);
                    goTo=-1;
                }
            }, 100);

        }
    }

    public void expandirToolbar() {
        expandir=false;
        flecha.startTransition(200);
        adapterNotif = new NotificacionesAdapter(this, R.layout.listview_notificaciones);
        CargarDatos.getNotificacionesFromDatabase(this);
        arrayNotif=CargarDatos.getNotifAdapter();
        menu.findItem(R.id.borrarNotif).setVisible(true);
        menu.findItem(R.id.iniciarNotif).setVisible(true);
        settings = getSharedPreferences(SegurosService.PREFS_NAME, 0);
        editor = settings.edit();
        if(settings.getBoolean("servicio",true))
        {
            checkNotif.setChecked(true);
        }
        final AlphaAnimation alpha =new AlphaAnimation(0.0f,1.0f);
        alpha.setDuration(300);
        alpha.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if(arrayNotif.size()!=0){
                    for(int i=0;i<arrayNotif.size();i++)
                    {
                        adapterNotif.add(arrayNotif.get(i));
                    }
                    listaNotif.setAdapter(adapterNotif);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        ResizeAnimation expandirAnim=new ResizeAnimation(barLayout,(int)(CargarDatos.convertirAPixel(350,SegurosActivity.this)),alturaToolbar);
        expandirAnim.setDuration(300);
        expandirAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                linearNotif.setVisibility(View.VISIBLE);
                listaNotif.setVisibility(View.VISIBLE);
                linearNotif.startAnimation(alpha);
                toolbar.setTitle("Notificaciones");
                linearContenido.animate().alpha(0f).setDuration(300);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        barLayout.startAnimation(expandirAnim);


    }
    public void colapsarToolbar(){
        expandir=true;
        flecha.reverseTransition(200);
        toolbar.setTitle("Mis Seguros");
        menu.findItem(R.id.borrarNotif).setVisible(false);
        menu.findItem(R.id.iniciarNotif).setVisible(false);
        ResizeAnimation colapsarAnim=new ResizeAnimation(barLayout,-barLayout.getLayoutParams().height+alturaToolbar,barLayout.getLayoutParams().height);
        colapsarAnim.setDuration(500);
        colapsarAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                linearContenido.animate().alpha(1f).setDuration(300);
                //listaNotif.animate().alpha(0f).setDuration(500);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                linearNotif.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        barLayout.startAnimation(colapsarAnim);


    }

}
