package co.allza.mararewards.activities;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageButton;
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
import co.allza.mararewards.interfaces.OnInfoClicked;
import co.allza.mararewards.items.CustomViewPager;
import co.allza.mararewards.items.ResizeAnimation;
import co.allza.mararewards.items.SwipeDismissListViewTouchListener;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import co.allza.mararewards.CargarDatos;
import co.allza.mararewards.R;
import co.allza.mararewards.adapter.SegurosPagerAdapter;
import co.allza.mararewards.items.NotificacionItem;
import co.allza.mararewards.items.SeguroItem;
import co.allza.mararewards.services.SegurosService;
import io.realm.Sort;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.pixelcan.inkpageindicator.InkPageIndicator;

public class SegurosActivity extends AppCompatActivity implements  View.OnClickListener , OnShowcaseEventListener, OnInfoClicked {

    RelativeLayout linear;
    RelativeLayout linearNotif;
    LinearLayout linearContenido;
    ListView listaNotif;
    ArrayList<NotificacionItem> arrayNotif;
    ArrayList<SeguroItem> arraySeguros;
    TextView seguroActual;
    SimpleDateFormat parserFecha;
    Calendar calendar;
    Date fechaActual;
    Date fechaSeguro;
    boolean estaVencido;
    boolean expandir = true;
    boolean actualizar = false;
    TransitionDrawable fondo;
    TransitionDrawable flecha;
    int counter = 0;
    int id = 0;
    int posicionActual;
    int goTo;
    int theme = R.style.MyAlertDialogStyle;
    int alturaToolbar;
    private static final int INITIAL_DELAY_MILLIS = 300;
    Menu menu;
    MenuItem checkNotif;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    AppBarLayout barLayout;
    Toolbar toolbar;
    NotificacionesAdapter adapterNotif;
    CustomViewPager pagerSeguros;
    SegurosPagerAdapter adapter;
    InkPageIndicator inkPageIndicator;
    private ShowcaseView showcaseView;
    private FloatingActionButton fab;
    private DatePickerDialog datePickerDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seguros);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        adapter=CargarDatos.getAdapter();
        linear = (RelativeLayout) findViewById(R.id.linearSeguros);
        linearContenido = (LinearLayout) findViewById(R.id.linearContenido);
        linearNotif = (RelativeLayout) findViewById(R.id.linearNotif);
        barLayout = (AppBarLayout) findViewById(R.id.aver);
        fab=(FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newInsurance();
            }
        });
        listaNotif = (ListView) findViewById(R.id.listViewNotificaciones);
        listaNotif.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(SegurosActivity.this, SegurosActivity.class);
                if (adapterNotif.getItem(position).getId() != -1 && adapterNotif.getItem(position).getId() < 100) {
                    i.putExtra("goTo", getInsurancePosition(adapterNotif.getItem(position).getId()));
                    Toast.makeText(SegurosActivity.this, "" + getInsurancePosition(adapterNotif.getItem(position).getId()), Toast.LENGTH_SHORT).show();
                }
                i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(i);

            }
        });
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        int anchoViejo = toolbar.getLayoutParams().height;
        toolbar.getLayoutParams().height = getStatusBarHeight() + anchoViejo;
        alturaToolbar = toolbar.getLayoutParams().height;
        getSupportActionBar().setTitle("Mis seguros");
        toolbar.setBackgroundColor(getResources().getColor(R.color.toolbarSeguros));
        flecha = (TransitionDrawable) getResources().getDrawable(R.drawable.morphing_arrow);
        flecha.setCrossFadeEnabled(true);
        linear.setBackground(fondo);
        toolbar.setNavigationIcon(flecha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expandir) {
                    expandirToolbar();
                    if (CargarDatos.getArraySeguros().size() > 0) {
                        Target viewTarget = new ViewTarget(R.id.cardEmergencia, SegurosActivity.this);
                        new ShowcaseView.Builder(SegurosActivity.this)
                                .setTarget(viewTarget)
                                .setContentTitle("Toca esta zona para cerrar las notificaciones")
                                .setStyle(R.style.CustomShowcaseTheme2)
                                .singleShot(42)
                                .build();
                    }
                } else
                    colapsarToolbar();

            }
        });

        fondo = (TransitionDrawable) getResources().getDrawable(R.drawable.fondo_seguros);
        linear.setBackground(fondo);
        pagerSeguros = (CustomViewPager) findViewById(R.id.viewPagerSeguros);
        inkPageIndicator = (InkPageIndicator) findViewById(R.id.indicator);
        seguroActual = (TextView) findViewById(R.id.tituloSeguroActual);
        seguroActual.setTypeface(CargarDatos.getTypeface(getApplicationContext(), CargarDatos.RUBIK_MEDIUM));
        parserFecha = new SimpleDateFormat("dd/MM/yyyy");
        calendar = Calendar.getInstance();
        fechaActual = calendar.getTime();
        CargarDatos.getSegurosFromDatabase(this);
        onSegurosPulled(CargarDatos.getAdapter());
        CargarDatos.setOnInfoCLicked(this);
        validarPrimerUso();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_seguros, menu);
        this.menu = menu;
        checkNotif = menu.findItem(R.id.iniciarNotif);
        validarPantalla();
        validarPosicion();
        return true;
    }

    @Override
    protected void onPause() {
        if (!expandir)
            colapsarToolbar();
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.cerrar:
                Intent i = new Intent(SegurosActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
                return true;

            case R.id.callToAction:
                Intent ii = new Intent(SegurosActivity.this, CallToActionActivity.class);
                startActivity(ii);
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
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        validarPosicion();
    }


    @Override
    public void onClick(View v) {
        switch (counter) {
            case 0:
                if (CargarDatos.getArraySeguros().size() <= 0) {
                    counter = 2;
                    break;
                }
                showcaseView.setShowcase(new ViewTarget(R.id.cardInfoIcono, SegurosActivity.this), true);
                showcaseView.setContentText("Verifica los detalles de tu seguro aquí");
                showcaseView.setContentTitle("");
                break;
            case 1:
                showcaseView.setShowcase(new ViewTarget(R.id.cardEmergencia, this), true);
                showcaseView.setContentText("o pulsa aquí para llamar en caso de un siniestro");
                break;
            case 2:
                showcaseView.setShowcase(new ViewTarget(R.id.fab, SegurosActivity.this), true);
                showcaseView.setContentText("Comienza agregando un seguro");
                showcaseView.setContentTitle("");
                break;
            case 3:
                showcaseView.hide();
                break;

        }
        counter++;
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
        final AlphaAnimation alphaReveal = new AlphaAnimation(0.0f, 1.0f);
        inkPageIndicator = (InkPageIndicator) findViewById(R.id.indicator);
        inkPageIndicator.setVisibility(View.VISIBLE);
        alphaReveal.setDuration(500);
        alphaReveal.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                pagerSeguros.setVisibility(View.VISIBLE);
                pagerSeguros.setAdapter(pagerAdapter);
                if (pagerAdapter.getCount() > 1)
                    inkPageIndicator.setViewPager(pagerSeguros);
                else
                    inkPageIndicator.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {


                linearContenido.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return false;
                    }
                });
                pagerSeguros.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return false;
                    }
                });
                pagerSeguros.setDeshabilitarTouch(false);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        AlphaAnimation alpha = new AlphaAnimation(1.0f, 0.0f);
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
                pagerSeguros.startAnimation(alphaReveal);
            }
        }, 170);
        arraySeguros = CargarDatos.getArraySeguros();
        for(int i=0;i<arraySeguros.size();i++)
        {
            if(arraySeguros.get(i).getId()>id)
                id=arraySeguros.get(i).getId();
        }

        pagerSeguros.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                seguroActual.setText(arraySeguros.get(position).getRefname());
                posicionActual=position;
                try {
                    fechaSeguro = parserFecha.parse(arraySeguros.get(position).getExpiration());
                    if (fechaActual.after(fechaSeguro) && !estaVencido) {
                        estaVencido = true;
                        fondo.startTransition(500);
                        fab.setBackgroundColor(getResources().getColor(R.color.grisVencido));

                    }
                    if (fechaActual.before(fechaSeguro) && estaVencido) {
                        estaVencido = false;
                        fondo.reverseTransition(500);
                        fab.setBackgroundColor(getResources().getColor(R.color.rectanguloSplash));

                    }
                } catch (ParseException e) {
                    e.printStackTrace();


                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        pagerSeguros.setPageTransformer(true, new co.allza.mararewards.items.StackTransformer());

        if (pagerAdapter.getCount() > 0) {
            parserFecha = new SimpleDateFormat("dd/MM/yyyy");
            calendar = Calendar.getInstance();
            fechaActual = calendar.getTime();
            seguroActual.setText(arraySeguros.get(0).getRefname());
            try {
                fechaSeguro = parserFecha.parse(pagerAdapter.getArrayList().get(0).getExpiration());
                if (fechaActual.after(fechaSeguro)) {
                    estaVencido = true;
                    fondo.startTransition(1500);

                } else if (estaVencido) {
                    fondo.reverseTransition(1500);
                    estaVencido = false;
                }
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
            //botonCallToAction.setVisibility(View.GONE);
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

            showcaseView = new ShowcaseView.Builder(SegurosActivity.this)
                    .setTarget(new ViewTarget(getToolbarNavigationButton()))
                    .withMaterialShowcase()
                    .setContentTitle("¡Bienvenido!")
                    .setOnClickListener(this)
                    .setStyle(R.style.CustomShowcaseTheme2)
                    .setContentText("Aquí podras ver tus notificaciones")
                    .build();
        }
    }

    public void validarPosicion() {
        if (getIntent().hasExtra("goTo"))
            goTo = getIntent().getExtras().getInt("goTo", -1);
        if (goTo != -1 && goTo < 100) {
            pagerSeguros.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (CargarDatos.getArraySeguros().size() > 0) {
                        pagerSeguros.setCurrentItem(goTo, true);
                        goTo = -1;
                    }
                    //else CargarDatos.getSegurosFromDatabase(SegurosActivity.this,SegurosActivity.this);
                }
            }, 100);
        } else if (goTo > 100 && expandir)
            expandirToolbar();

        CargarDatos.emptyNotificationCounter();
    }

    public void expandirToolbar() {
        expandir = false;
        flecha.startTransition(200);
        adapterNotif = new NotificacionesAdapter(this, R.layout.listview_notificaciones);
        CargarDatos.getNotificacionesFromDatabase(this);
        arrayNotif = CargarDatos.getNotifAdapter();
        menu.findItem(R.id.iniciarNotif).setVisible(true);
        settings = getSharedPreferences(SegurosService.PREFS_NAME, 0);
        editor = settings.edit();
        if (settings.getBoolean("servicio", true)) {
            checkNotif.setChecked(true);
        }
        final AlphaAnimation alpha = new AlphaAnimation(0.0f, 1.0f);
        alpha.setDuration(1);
        alpha.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (arrayNotif.size() != 0) {
                    for (int i = 0; i < arrayNotif.size(); i++) {
                        adapterNotif.add(arrayNotif.get(i));
                    }
                    listaNotif.setAdapter(adapterNotif);
                    SwipeDismissListViewTouchListener touchListener =
                            new SwipeDismissListViewTouchListener(
                                    listaNotif,
                                    new SwipeDismissListViewTouchListener.DismissCallbacks() {
                                        @Override
                                        public boolean canDismiss(int position) {
                                            return true;
                                        }

                                        @Override
                                        public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                            for (int position : reverseSortedPositions) {
                                                Realm realm = CargarDatos.getRealm(SegurosActivity.this);
                                                RealmResults<NotificacionItem> borrarNotif = realm.where(NotificacionItem.class)
                                                        .equalTo("id", adapterNotif.getItem(position).getId())
                                                        .findAll();
                                                adapterNotif.remove(adapterNotif.getItem(position));
                                                realm.beginTransaction();
                                                borrarNotif.deleteAllFromRealm();
                                                realm.commitTransaction();
                                            }
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                    listaNotif.setOnTouchListener(touchListener);
                    // Setting this scroll listener is required to ensure that during ListView scrolling,
                    // we don't look for swipes.
                    listaNotif.setOnScrollListener(touchListener.makeScrollListener());
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        ResizeAnimation expandirAnim = new ResizeAnimation(barLayout, (int) (CargarDatos.convertirAPixel(350, SegurosActivity.this)), alturaToolbar);
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
        linearContenido.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!expandir)
                    colapsarToolbar();
                return false;
            }
        });
        pagerSeguros.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!expandir)
                    colapsarToolbar();
                return false;
            }
        });

    }

    public void colapsarToolbar() {
        expandir = true;
        flecha.startTransition(0);
        flecha.reverseTransition(200);
        toolbar.setTitle("Mis Seguros");
        menu.findItem(R.id.iniciarNotif).setVisible(false);
        ResizeAnimation colapsarAnim = new ResizeAnimation(barLayout, -barLayout.getLayoutParams().height + alturaToolbar, barLayout.getLayoutParams().height);
        colapsarAnim.setDuration(500);
        colapsarAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                linearContenido.animate().alpha(1f).setDuration(300);

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

    public ImageButton getToolbarNavigationButton() {
        int size = toolbar.getChildCount();
        for (int i = 0; i < size; i++) {
            View child = toolbar.getChildAt(i);
            if (child instanceof ImageButton) {
                ImageButton btn = (ImageButton) child;
                if (btn.getDrawable() == toolbar.getNavigationIcon()) {
                    return btn;
                }
            }
        }
        return null;
    }

    @Override
    public void onShowcaseViewHide(ShowcaseView showcaseView) {

    }

    @Override
    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
        if (pagerSeguros != null)
            inkPageIndicator.setViewPager(pagerSeguros);
    }

    @Override
    public void onShowcaseViewShow(ShowcaseView showcaseView) {

    }

    @Override
    public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {

    }


    public int getInsurancePosition(int id) {
        RealmConfiguration config = new RealmConfiguration
                .Builder(this)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<SeguroItem> result = realm.where(SeguroItem.class)
                .findAll();
        result.sort("id", Sort.DESCENDING);
        if (result.size() > 0) {
            for (int i = 0; i < result.size(); i++) {
                if (result.get(i).getId() == id)
                    return i;
            }
        }
        realm.close();
        realm = null;
        return 0;
    }

    public void newInsurance() {
        AlertDialog.Builder adBuilder=new AlertDialog.Builder(this);
        adBuilder.setTitle("Nuevo seguro");
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_insurance, null);
        final AppCompatEditText ePoliza=(AppCompatEditText)dialogView.findViewById(R.id.poliza);
        final AppCompatEditText eNombre=(AppCompatEditText)dialogView.findViewById(R.id.nombre);
        final AppCompatEditText eAseguradora=(AppCompatEditText)dialogView.findViewById(R.id.aseguradora);
        final AppCompatEditText eAsegurado=(AppCompatEditText)dialogView.findViewById(R.id.asegurado);
        final AppCompatEditText eTelefono=(AppCompatEditText)dialogView.findViewById(R.id.telefono);
        final AppCompatEditText eRenovacion=(AppCompatEditText)dialogView.findViewById(R.id.renovacion);

        eRenovacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar newCalendar = Calendar.getInstance();
                datePickerDialog=new DatePickerDialog(
                        SegurosActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        String month=""+(monthOfYear+1);
                        String day=""+dayOfMonth;
                        if(monthOfYear+1<10)
                            month="0"+month;
                        if(dayOfMonth<10)
                            day="0"+dayOfMonth;
                        eRenovacion.setText(day+"/"+month+"/"+year);

                    }
                },
                        newCalendar.get(Calendar.YEAR),
                        newCalendar.get(Calendar.MONTH),
                        newCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });
        adBuilder.setView(dialogView);
        adBuilder.setPositiveButton("Finalizar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SeguroItem seguro=new SeguroItem();
                seguro.setPolicy(ePoliza.getText().toString());
                seguro.setRefname(eNombre.getText().toString());
                seguro.setName(eAseguradora.getText().toString());
                seguro.setDescription(eNombre.getText().toString());
                seguro.setExpiration(eRenovacion.getText().toString());
                seguro.setEmergency(eTelefono.getText().toString());
                seguro.setInsured_name(eAsegurado.getText().toString());
                seguro.setId(id+1);
                id++;
                inkPageIndicator.setVisibility(View.GONE);
                inkPageIndicator=null;
                pagerSeguros.setAdapter(null);
                arraySeguros.add(seguro);
                CargarDatos.saveSeguro(SegurosActivity.this,seguro);
                adapter=new SegurosPagerAdapter(SegurosActivity.this,arraySeguros);
                onSegurosPulled(adapter);

                dialog.dismiss();
            }
        });
        adBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        adBuilder.show();
    }

    @Override
    public void borrarSeguro(int position) {
        id++;
        inkPageIndicator.setVisibility(View.GONE);
        inkPageIndicator=null;
        pagerSeguros.setAdapter(null);
        CargarDatos.deleteSeguro(SegurosActivity.this,arraySeguros.get(posicionActual));
        arraySeguros.remove(posicionActual);
        adapter=new SegurosPagerAdapter(SegurosActivity.this,arraySeguros);
        onSegurosPulled(adapter);
    }

    @Override
    public void editarSeguro(final int position) {
        AlertDialog.Builder adBuilder=new AlertDialog.Builder(this);
        adBuilder.setTitle("Nuevo seguro");
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_insurance, null);
        final AppCompatEditText ePoliza=(AppCompatEditText)dialogView.findViewById(R.id.poliza);
        final AppCompatEditText eNombre=(AppCompatEditText)dialogView.findViewById(R.id.nombre);
        final AppCompatEditText eAseguradora=(AppCompatEditText)dialogView.findViewById(R.id.aseguradora);
        final AppCompatEditText eAsegurado=(AppCompatEditText)dialogView.findViewById(R.id.asegurado);
        final AppCompatEditText eTelefono=(AppCompatEditText)dialogView.findViewById(R.id.telefono);
        final AppCompatEditText eRenovacion=(AppCompatEditText)dialogView.findViewById(R.id.renovacion);

        ePoliza.setText(arraySeguros.get(posicionActual).getPolicy());
        eNombre.setText(arraySeguros.get(posicionActual).getRefname());
        eAseguradora.setText(arraySeguros.get(posicionActual).getName());
        eAsegurado.setText(arraySeguros.get(posicionActual).getInsured_name());
        eTelefono.setText(arraySeguros.get(posicionActual).getEmergency());
        eRenovacion.setText(arraySeguros.get(posicionActual).getExpiration());

        eRenovacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar newCalendar = Calendar.getInstance();
                datePickerDialog=new DatePickerDialog(
                        SegurosActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                String month=""+(monthOfYear+1);
                                String day=""+dayOfMonth;
                                if(monthOfYear+1<10)
                                    month="0"+month;
                                if(dayOfMonth<10)
                                    day="0"+dayOfMonth;
                                eRenovacion.setText(day+"/"+month+"/"+year);
                            }
                        },
                        newCalendar.get(Calendar.YEAR),
                        newCalendar.get(Calendar.MONTH),
                        newCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });
        adBuilder.setView(dialogView);
        adBuilder.setPositiveButton("Finalizar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SeguroItem seguro=new SeguroItem();
                seguro.setPolicy(ePoliza.getText().toString());
                seguro.setRefname(eNombre.getText().toString());
                seguro.setName(eAseguradora.getText().toString());
                seguro.setDescription(eNombre.getText().toString());
                seguro.setExpiration(eRenovacion.getText().toString());
                seguro.setEmergency(eTelefono.getText().toString());
                seguro.setInsured_name(eAsegurado.getText().toString());
                seguro.setId(arraySeguros.get(posicionActual).getId());
                inkPageIndicator.setVisibility(View.GONE);
                inkPageIndicator=null;
                pagerSeguros.setAdapter(null);
                CargarDatos.saveSeguro(SegurosActivity.this,seguro);
                adapter=new SegurosPagerAdapter(SegurosActivity.this,arraySeguros);
                onSegurosPulled(adapter);

                dialog.dismiss();
            }
        });
        adBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        adBuilder.show();
    }
}