package co.allza.mararewards.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import co.allza.mararewards.CargarDatos;
import co.allza.mararewards.R;
import co.allza.mararewards.adapter.NotificacionesAdapter;
import co.allza.mararewards.adapter.SegurosPagerAdapter;
import co.allza.mararewards.items.NotificacionItem;
import io.realm.Realm;
import io.realm.RealmResults;

public class NotificacionesActivity extends AppCompatActivity implements CargarDatos.VolleyCallback {
    ListView lista;
    Toolbar toolbar;
    NotificacionesAdapter adapter;

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
        lista=(ListView)findViewById(R.id.listViewNotificaciones);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Pedir Adapter , meter el swipe layout

        adapter = new NotificacionesAdapter(this, R.layout.listview_notificaciones);
        ArrayList<NotificacionItem> hola=CargarDatos.getNotifAdapter();
        for(int i=0;i<hola.size();i++)
        {
            adapter.add(hola.get(i));
        }

        lista.setAdapter(adapter);

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
                } else {
                    // This activity is part of this app's task, so simply
                    // navigate up to the logical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
            case R.id.borrarNotif:
                Realm realm=CargarDatos.getRealm(this);
                RealmResults<NotificacionItem> borrarNotif=realm.where(NotificacionItem.class).findAll();
                realm.beginTransaction();
                borrarNotif.deleteAllFromRealm();
                realm.commitTransaction();
                if(!lista.getAdapter().isEmpty())
                {
                    adapter.clear();

                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notificaciones, menu);
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
