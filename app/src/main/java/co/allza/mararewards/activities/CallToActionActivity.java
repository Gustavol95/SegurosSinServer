package co.allza.mararewards.activities;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import co.allza.mararewards.CargarDatos;
import co.allza.mararewards.R;
import co.allza.mararewards.adapter.CallToActionAdapter;
import co.allza.mararewards.items.LoginItem;
/**
 * Created by Tavo on 10/06/2016.
 */
public class CallToActionActivity extends Activity implements View.OnClickListener {

    private TextView texto;
    private ListView lista;
    private FloatingActionButton fab;
    private CallToActionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.StatusBarColorCallToAction));
        }
        setContentView(R.layout.activity_call_to_action);

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
        texto.setTypeface(CargarDatos.getTypeface(getApplicationContext(), CargarDatos.RUBIK_REGULAR));

        fab.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:+526677614428"));
                        startActivity(intent);
                        break;




        }
    }
}
