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
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import com.pixelcan.inkpageindicator.InkPageIndicator;

public class SegurosActivity extends AppCompatActivity implements VolleyCallback, DialogCallback {

    RelativeLayout linear;
    ViewPager pagerSeguros;
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
    int id = -1;
    int goTo;
    int theme = R.style.MyAlertDialogStyle;
    Menu menu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seguros);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        CargarDatos.setDialogCallback(this);
        linear = (RelativeLayout) findViewById(R.id.linearSeguros);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        int anchoViejo = toolbar.getLayoutParams().height;
        toolbar.getLayoutParams().height = getStatusBarHeight() + anchoViejo;
        getSupportActionBar().setTitle("Mis seguros");
        toolbar.setBackgroundColor(getResources().getColor(R.color.toolbarSeguros));
        toolbar.setNavigationIcon(R.drawable.ic_notifications_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SegurosActivity.this, NotificacionesActivity.class);
                SegurosActivity.this.startActivity(i);
            }
        });

        fondo = (TransitionDrawable) getResources().getDrawable(R.drawable.fondo_seguros);
        linear.setBackground(fondo);
        pagerSeguros = (ViewPager) findViewById(R.id.viewPagerSeguros);
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
                    if (pagerSeguros != null){
                        AlphaAnimation alpha=new AlphaAnimation(1.0f,0.0f);
                        alpha.setDuration(250);
                        alpha.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                pagerSeguros.setAdapter(null);

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        pagerSeguros.startAnimation(alpha);

                    }
                    realm.beginTransaction();
                    RealmResults<SeguroItem> segurosAnteriores = realm.where(SeguroItem.class).findAll();
                    segurosAnteriores.deleteAllFromRealm();
                    realm.commitTransaction();
                    CargarDatos.pullSeguros(SegurosActivity.this, result.getUsertoken(), result.getToken(), SegurosActivity.this);
                }

            }
        });
        swipe.setColorSchemeResources(R.color.rectanguloSplash,R.color.toolbarSeguros);
        CargarDatos.getSegurosFromDatabase(this,this);
        parserFecha = new SimpleDateFormat("dd/MMM/yyyy");
        calendar = Calendar.getInstance();
        fechaActual = calendar.getTime();
        validarPrimerUso();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_seguros, menu);
        this.menu = menu;
        validarPantalla();
        return true;
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
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

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
        pagerSeguros.setVisibility(View.INVISIBLE);
        pagerSeguros.setAdapter(pagerAdapter);
        pagerSeguros.setVisibility(View.VISIBLE);
        AlphaAnimation alpha=new AlphaAnimation(0.0f,1.0f);
        alpha.setDuration(500);
        alpha.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                pagerSeguros.setAdapter(pagerAdapter); }
            @Override
            public void onAnimationEnd(Animation animation)   {
                inkPageIndicator.setViewPager(pagerSeguros);  }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

            pagerSeguros.startAnimation(alpha);

        arraySeguros = CargarDatos.getArraySeguros();
        if(arraySeguros.size()<=0)
        {

            Realm realm = CargarDatos.getRealm(SegurosActivity.this);
            CustomerItem result = realm.where(CustomerItem.class)
                    .findFirst();
            if (result != null) {
                if (pagerSeguros != null){

                    pagerSeguros.setVisibility(View.VISIBLE);
                    pagerSeguros.clearAnimation();
                    pagerSeguros.startAnimation(alpha);

                }
                realm.beginTransaction();
                RealmResults<SeguroItem> segurosAnteriores = realm.where(SeguroItem.class).findAll();
                segurosAnteriores.deleteAllFromRealm();
                realm.commitTransaction();
                CargarDatos.pullSeguros(SegurosActivity.this, result.getUsertoken(), result.getToken(), SegurosActivity.this);
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
        pagerSeguros.setPageTransformer(true, new DepthPageTransformer());
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
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    void enterReveal(View myView) {
        // previously invisible view

        // get the center for the clipping circle
        int cx = myView.getMeasuredWidth() / 2;
        int cy = myView.getMeasuredHeight() / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(myView.getWidth(), myView.getHeight()) / 2;

        // create the animator for this view (the start radius is zero)
        Animator anim =
                null;

        anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);


        // make the view visible and start the animation
        myView.setVisibility(View.VISIBLE);
        anim.start();
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    void exitReveal(final View myView) {
        // previously visible view

        // get the center for the clipping circle
        int cx = myView.getMeasuredWidth() / 2;
        int cy = myView.getMeasuredHeight() / 2;

        // get the initial radius for the clipping circle
        int initialRadius = myView.getWidth() / 2;

        // create the animation (the final radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, initialRadius, 0);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                myView.setVisibility(View.INVISIBLE);
            }
        });

        // start the animation
        anim.start();
    }
}
