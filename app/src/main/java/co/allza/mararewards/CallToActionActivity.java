package co.allza.mararewards;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import co.allza.mararewards.adapter.CallToActionAdapter;
import co.allza.mararewards.items.LoginItem;

/**
 * Created by Tavo on 10/06/2016.
 */
public class CallToActionActivity extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private TextView texto;
    private ListView lista;
    private FloatingActionButton fab;
    private CallToActionAdapter adapter;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Para kitkat
        requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.StatusBarColorCallToAction));
        }
        setContentView(R.layout.activity_call_to_action);
        //para Lollipop


        fab = (FloatingActionButton) findViewById(R.id.fab);
        lista = (ListView) findViewById(R.id.listViewCallToAction);
        texto = (TextView) findViewById(R.id.textViewCallToAction);
        adapter = new CallToActionAdapter(getApplicationContext(), R.layout.listview_calltoaction);
        adapter.add(new LoginItem("Seguro de Vida", R.drawable.account_multiple));
        adapter.add(new LoginItem("Seguro de Hogar", R.drawable.home));
        adapter.add(new LoginItem("Seguro de Automóvil", R.drawable.taxi));
        adapter.add(new LoginItem("Seguro de Gastos Médicos", R.drawable.heart));
        adapter.add(new LoginItem("Seguro de Inversión", R.drawable.trending_up));
        adapter.add(new LoginItem("Seguro de Viaje", R.drawable.briefcase));

        lista.setAdapter(adapter);
        texto.setTypeface(CargarFuentes.getTypeface(getApplicationContext(), CargarFuentes.RUBIK_REGULAR));

        lista.setOnItemClickListener(this);
        fab.setOnClickListener(this);


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position)
        {
            case 0:
                Intent jota=new Intent(CallToActionActivity.this,SegurosActivity.class);
                startActivity(jota);


                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:018001010145"));
                        startActivity(intent);
                        break;




        }
    }
}
