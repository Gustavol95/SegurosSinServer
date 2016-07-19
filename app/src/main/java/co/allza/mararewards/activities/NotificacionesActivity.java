package co.allza.mararewards.activities;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import java.util.ArrayList;
import co.allza.mararewards.CargarDatos;
import co.allza.mararewards.R;
import co.allza.mararewards.adapter.NotificacionesAdapter;
import co.allza.mararewards.adapter.SegurosPagerAdapter;
import co.allza.mararewards.interfaces.VolleyCallback;
import co.allza.mararewards.items.NotificacionItem;
import co.allza.mararewards.services.SegurosService;
import io.realm.Realm;
import io.realm.RealmResults;

public class NotificacionesActivity extends AppCompatActivity implements VolleyCallback {
    ListView lista;
    Toolbar toolbar;
    NotificacionesAdapter adapter;
    Menu menu;
    MenuItem checkNotif;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    ArrayList<NotificacionItem> hola;
    SwipeRefreshLayout swipe;
    CoordinatorLayout coordinator;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_notificaciones);

        coordinator=(CoordinatorLayout)findViewById(R.id.coordinatorNotif);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        int anchoViejo=toolbar.getLayoutParams().height;
        toolbar.getLayoutParams().height=getStatusBarHeight()+anchoViejo;
        toolbar.setTitle("Notificaciones");
        setSupportActionBar(toolbar);
        adapter = new NotificacionesAdapter(this, R.layout.listview_notificaciones);
        CargarDatos.getNotificacionesFromDatabase(this);
        hola=CargarDatos.getNotifAdapter();
        swipe=(SwipeRefreshLayout)findViewById(R.id.swipeNotif);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(CargarDatos.getNotifAdapter().size()!=0){
                    for(int i=0;i<CargarDatos.getNotifAdapter().size();i++)
                    {
                        adapter.remove(CargarDatos.getNotifAdapter().get(i));
                    }
                    lista.setAdapter(adapter);
                }
                CargarDatos.getNotifAdapter().clear();
                CargarDatos.getNotificacionesFromDatabase(NotificacionesActivity.this);
                hola=CargarDatos.getNotifAdapter();
                for(int i=0;i<hola.size();i++)
                {
                    adapter.add(hola.get(i));
                }
                swipe.setRefreshing(false);
            }
        });
        swipe.setColorSchemeResources(R.color.rectanguloSplash,R.color.toolbarSeguros);
        lista=(ListView)findViewById(R.id.listViewNotificaciones);
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent i=new Intent(NotificacionesActivity.this,SegurosActivity.class);
                i.putExtra("goTo",  adapter.getItem(position).getId());
                i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(i);
                finish();
            }
        });

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if(hola.size()!=0){
        for(int i=0;i<hola.size();i++)
        {
            adapter.add(hola.get(i));
        }

        lista.setAdapter(adapter);
        }

        settings = getSharedPreferences(SegurosService.PREFS_NAME, 0);
        editor = settings.edit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // This activity is NOT part of this app's task, so create a new task
                    // when navigating up, with a synthesized back stack.
                    TaskStackBuilder.create(this)
                            // Add all of this activity's parents to the back stack
                            .addNextIntentWithParentStack(upIntent)
                            // Navigate up to the closest parent
                            .startActivities();
                    finish();
                } else {
                    // This activity is part of this app's task, so simply
                    // navigate up to the logical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                    finish();
                }
                return true;
            case R.id.borrarNotif:
                if(CargarDatos.getNotifAdapter().size()!=0){
                    for(int i=0;i<CargarDatos.getNotifAdapter().size();i++)
                    {
                        adapter.remove(CargarDatos.getNotifAdapter().get(i));
                    }
                    lista.setAdapter(adapter);
                }
                CargarDatos.getNotifAdapter().clear();
                Realm realm=CargarDatos.getRealm(this);
                RealmResults<NotificacionItem> borrarNotif=realm.where(NotificacionItem.class).findAll();
                realm.beginTransaction();
                borrarNotif.deleteAllFromRealm();
                realm.commitTransaction();
                return true;

            case R.id.iniciarNotif:
                if(checkNotif.isChecked())
                {
                    checkNotif.setChecked(false);
                    stopService(new Intent(NotificacionesActivity.this, SegurosService.class));
                    editor = settings.edit();
                    editor.putBoolean("servicio" , false);
                    editor.commit();
                    Snackbar snackbar = Snackbar
                            .make(coordinator, "Notificaciones Desactivadas", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    return true;
                }
                else
                {
                    checkNotif.setChecked(true);
                    startService(new Intent(NotificacionesActivity.this, SegurosService.class));
                    editor = settings.edit();
                    editor.remove("servicio");
                    editor.commit();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            if(CargarDatos.getNotifAdapter().size()!=0){
                                for(int i=0;i<CargarDatos.getNotifAdapter().size();i++)
                                {
                                    adapter.remove(CargarDatos.getNotifAdapter().get(i));
                                }
                            }
                            CargarDatos.getNotificacionesFromDatabase(NotificacionesActivity.this);
                            hola=CargarDatos.getNotifAdapter();
                            if(CargarDatos.getNotifAdapter().size()!=0){
                                for(int i=0;i<CargarDatos.getNotifAdapter().size();i++)
                                {
                                    adapter.add(CargarDatos.getNotifAdapter().get(i));
                                }
                                lista.setAdapter(adapter);
                            }
                        }
                    },1000);
                    Snackbar snackbar = Snackbar
                            .make(coordinator, "Notificaciones Activadas", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    return true;
                }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notificaciones, menu);
        this.menu=menu;
        checkNotif=menu.findItem(R.id.iniciarNotif);
        if(settings.getBoolean("servicio",true))
        {
          checkNotif.setChecked(true);
        }
        return true;

    }

    @Override
    public void onSuccess(SegurosPagerAdapter result) {

    }

    @Override
    public void onFailure(String error) {

    }

    @Override
    public void onTokenReceived(String token) {

    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
