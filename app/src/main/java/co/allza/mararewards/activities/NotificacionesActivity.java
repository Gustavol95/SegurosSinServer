package co.allza.mararewards.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
    SharedPreferences settings;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_notificaciones);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        int anchoViejo=toolbar.getLayoutParams().height;
        toolbar.getLayoutParams().height=getStatusBarHeight()+anchoViejo;
        toolbar.setTitle("Notificaciones");
        setSupportActionBar(toolbar);
        adapter = new NotificacionesAdapter(this, R.layout.listview_notificaciones);
        final ArrayList<NotificacionItem> hola=CargarDatos.getNotifAdapter();
        lista=(ListView)findViewById(R.id.listViewNotificaciones);
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i=new Intent(NotificacionesActivity.this,SegurosActivity.class);
                i.putExtra("id",hola.get(position).getId());
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
    protected void onResume() {
        super.onResume();
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
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
            case R.id.detenerNotif:
                stopService(new Intent(NotificacionesActivity.this, SegurosService.class));
                menu.findItem(R.id.detenerNotif).setVisible(false);
                menu.findItem(R.id.iniciarNotif).setVisible(true);
                editor = settings.edit();
                editor.putBoolean("servicio" , false);
                editor.commit();
                return true;
            case R.id.iniciarNotif:
                startService(new Intent(NotificacionesActivity.this, SegurosService.class));
                menu.findItem(R.id.detenerNotif).setVisible(true);
                menu.findItem(R.id.iniciarNotif).setVisible(false);
                editor = settings.edit();
                editor.putBoolean("servicio" , true);
                editor.commit();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notificaciones, menu);
        this.menu=menu;
        if(settings.getBoolean("servicio",true))
        {
            menu.findItem(R.id.detenerNotif).setVisible(false);
            menu.findItem(R.id.iniciarNotif).setVisible(true);
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
}
