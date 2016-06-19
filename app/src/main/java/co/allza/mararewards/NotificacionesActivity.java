package co.allza.mararewards;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import co.allza.mararewards.adapter.NotificacionesAdapter;
import co.allza.mararewards.items.NotificacionItem;

public class NotificacionesActivity extends AppCompatActivity {
    ListView lista;
    Toolbar toolbar;
    NotificacionesAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
           // getWindow().setStatusBarColor(getResources().getColor(R.color.statusbarSeguros));
        }
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
        adapter = new NotificacionesAdapter(getApplicationContext(), R.layout.listview_notificaciones);
        adapter.add(new NotificacionItem(R.drawable.verified,"Seguro de vida - 123467890","Ha sido renovado exitosamente.",
                "12/Feb/2016"));
        adapter.add(new NotificacionItem(R.drawable.information,"Seguro de vida - 123467890","Ha sido renovado exitosamente.",
                "12/Ene/2016"));
        adapter.add(new NotificacionItem(R.drawable.verified,"Seguro de vida - 123467890","Ha sido renovado exitosamente.",
                "12/Feb/2016"));
        adapter.add(new NotificacionItem(R.drawable.information,"Seguro de vida - 123467890","Ha sido renovado exitosamente.",
                "12/Ene/2016"));
        adapter.add(new NotificacionItem(R.drawable.verified,"Seguro de vida - 123467890","Ha sido renovado exitosamente.",
                "12/Feb/2016"));
        adapter.add(new NotificacionItem(R.drawable.information,"Seguro de vida - 123467890","Ha sido renovado exitosamente.",
                "12/Ene/2016"));
        adapter.add(new NotificacionItem(R.drawable.verified,"Seguro de vida - 123467890","Ha sido renovado exitosamente.",
                "12/Feb/2016"));
        adapter.add(new NotificacionItem(R.drawable.information,"Seguro de vida - 123467890","Ha sido renovado exitosamente.",
                "12/Ene/2016"));
        adapter.add(new NotificacionItem(R.drawable.verified,"Seguro de vida - 123467890","Ha sido renovado exitosamente.",
                "12/Feb/2016"));
        adapter.add(new NotificacionItem(R.drawable.information,"Seguro de vida - 123467890","Ha sido renovado exitosamente.",
                "12/Ene/2016"));
        adapter.add(new NotificacionItem(R.drawable.verified,"Seguro de vida - 123467890","Ha sido renovado exitosamente.",
                "12/Feb/2016"));
        adapter.add(new NotificacionItem(R.drawable.information,"Seguro de vida - 123467890","Ha sido renovado exitosamente.",
                "12/Ene/2016"));
        adapter.add(new NotificacionItem(R.drawable.verified,"Seguro de vida - 123467890","Ha sido renovado exitosamente.",
                "12/Feb/2016"));
        adapter.add(new NotificacionItem(R.drawable.information,"Seguro de vida - 123467890","Ha sido renovado exitosamente.",
                "12/Ene/2016"));
        adapter.add(new NotificacionItem(R.drawable.verified,"Seguro de vida - 123467890","Ha sido renovado exitosamente.",
                "12/Feb/2016"));
        adapter.add(new NotificacionItem(R.drawable.information,"Seguro de vida - 123467890","Ha sido renovado exitosamente.",
                "12/Ene/2016"));
        adapter.add(new NotificacionItem(R.drawable.verified,"Seguro de vida - 123467890","Ha sido renovado exitosamente.",
                "12/Feb/2016"));
        adapter.add(new NotificacionItem(R.drawable.information,"Seguro de vida - 123467890","Ha sido renovado exitosamente.",
                "12/Ene/2016"));
        adapter.add(new NotificacionItem(R.drawable.verified,"Seguro de vida - 123467890","Ha sido renovado exitosamente.",
                "12/Feb/2016"));
        adapter.add(new NotificacionItem(R.drawable.information,"Seguro de vida - 123467890","Ha sido renovado exitosamente.",
                "12/Ene/2016"));
        adapter.add(new NotificacionItem(R.drawable.verified,"Seguro de vida - 123467890","Ha sido renovado exitosamente.",
                "12/Feb/2016"));
        adapter.add(new NotificacionItem(R.drawable.information,"Seguro de vida - 123467890","Ha sido renovado exitosamente.",
                "12/Ene/2016"));
        adapter.add(new NotificacionItem(R.drawable.verified,"Seguro de vida - 123467890","Ha sido renovado exitosamente.",
                "12/Feb/2016"));
        adapter.add(new NotificacionItem(R.drawable.information,"Seguro de vida - 123467890","Ha sido renovado exitosamente.",
                "12/Ene/2016"));
        adapter.add(new NotificacionItem(R.drawable.verified,"Seguro de vida - 123467890","Ha sido renovado exitosamente.",
                "12/Feb/2016"));
        adapter.add(new NotificacionItem(R.drawable.information,"Seguro de vida - 123467890","Ha sido renovado exitosamente.",
                "12/Ene/2016"));
        adapter.add(new NotificacionItem(R.drawable.verified,"Seguro de vida - 123467890","Ha sido renovado exitosamente.",
                "12/Feb/2016"));
        adapter.add(new NotificacionItem(R.drawable.information,"Seguro de vida - 123467890","Ha sido renovado exitosamente.",
                "12/Ene/2016"));
        adapter.add(new NotificacionItem(R.drawable.verified,"Seguro de vida - 123467890","Ha sido renovado exitosamente.",
                "12/Feb/2016"));
        adapter.add(new NotificacionItem(R.drawable.information,"Seguro de vida - 123467890","Ha sido renovado exitosamente.",
                "12/Ene/2016"));
        adapter.add(new NotificacionItem(R.drawable.verified,"Seguro de vida - 123467890","Ha sido renovado exitosamente.",
                "12/Feb/2016"));
        adapter.add(new NotificacionItem(R.drawable.information,"Seguro de vida - 123467890","Ha sido renovado exitosamente.",
                "12/Ene/2016"));
        adapter.add(new NotificacionItem(R.drawable.verified,"Seguro de vida - 123467890","Ha sido renovado exitosamente.",
                "12/Feb/2016"));
        adapter.add(new NotificacionItem(R.drawable.information,"Seguro de vida - 123467890","Ha sido renovado exitosamente.",
                "12/Ene/2016"));
        adapter.add(new NotificacionItem(R.drawable.verified,"Seguro de vida - 123467890","Ha sido renovado exitosamente.",
                "12/Feb/2016"));
        adapter.add(new NotificacionItem(R.drawable.information,"Seguro de vida - 123467890","Ha sido renovado exitosamente.",
                "12/Ene/2016"));
        adapter.add(new NotificacionItem(R.drawable.verified,"Seguro de vida - 123467890","Ha sido renovado exitosamente.",
                "12/Feb/2016"));
        adapter.add(new NotificacionItem(R.drawable.information,"Seguro de vida - 123467890","Ha sido renovado exitosamente.",
                "12/Ene/2016"));
        adapter.add(new NotificacionItem(R.drawable.verified,"Seguro de vida - 123467890","Ha sido renovado exitosamente.",
                "12/Feb/2016"));
        adapter.add(new NotificacionItem(R.drawable.information,"Seguro de vida - 123467890","Ha sido renovado exitosamente.",
                "12/Ene/2016"));
        adapter.add(new NotificacionItem(R.drawable.verified,"Seguro de vida - 123467890","Ha sido renovado exitosamente.",
                "12/Feb/2016"));
        adapter.add(new NotificacionItem(R.drawable.information,"Seguro de vida - 123467890","Ha sido renovado exitosamente.",
                "12/Ene/2016"));


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
}
