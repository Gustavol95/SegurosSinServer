package co.allza.mararewards.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import co.allza.mararewards.CargarDatos;
import co.allza.mararewards.CargarFuentes;
import co.allza.mararewards.R;
import co.allza.mararewards.adapter.SegurosPagerAdapter;
import co.allza.mararewards.items.CustomerItem;
import io.realm.Realm;

/**
 * Created by Tavo on 10/06/2016.
 */
public class LoginCodigoActivity extends Activity implements View.OnClickListener, CargarDatos.VolleyCallback {
    TextView introducir;
    TextView footer;
    EditText editTextCodigo;
    Button botonEntrar;
    CustomerItem customer;
    ProgressBar progress;
    String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //para lolipop
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_login_codigo);
        introducir= (TextView)findViewById(R.id.textViewLoginCodigo);
        footer=(TextView)findViewById(R.id.textViewFooterLoginCodigo);
        editTextCodigo=(EditText)findViewById(R.id.editTextLoginCodigo);
        botonEntrar=(Button)findViewById(R.id.buttonLoginCodigo);
        progress=(ProgressBar) findViewById(R.id.progress);

        introducir.setTypeface(CargarFuentes.getTypeface(getApplicationContext(),CargarFuentes.ROBOTO_MEDIUM));
        footer.setTypeface(CargarFuentes.getTypeface(getApplicationContext(),CargarFuentes.RUBIK_LIGHT));
        editTextCodigo.setTypeface(CargarFuentes.getTypeface(getApplicationContext(),CargarFuentes.RUBIK_REGULAR));
        botonEntrar.setTypeface(CargarFuentes.getTypeface(getApplicationContext(),CargarFuentes.ROBOTO_MEDIUM));
        botonEntrar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonLoginCodigo:
                CargarDatos.getTokenFromServer(getApplicationContext(),editTextCodigo.getText().toString(),this);
                botonEntrar.setEnabled(false);

                progress.setVisibility(View.VISIBLE);
                progress.setProgress(15);


                break;

        }

    }

    @Override
    public void onSuccess(SegurosPagerAdapter result) {

        Realm realm =CargarDatos.getRealm(getApplicationContext());
        customer=new CustomerItem();
        customer.setUsertoken(editTextCodigo.getText().toString());
        customer.setToken(token);
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(customer);
        realm.commitTransaction();
        Intent i = new Intent(LoginCodigoActivity.this, SegurosActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onFailure(String error) {
        Toast.makeText(LoginCodigoActivity.this, "Algo está mal, intente de nuevo por favor", Toast.LENGTH_SHORT).show();
        botonEntrar.setEnabled(true);

        progress.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onTokenReceived(String token) {
        this.token=token;
        CargarDatos.pullSeguros(getApplicationContext(),editTextCodigo.getText().toString(),token,this);

    }
}
