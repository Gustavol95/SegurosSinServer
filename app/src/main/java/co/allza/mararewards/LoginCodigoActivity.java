package co.allza.mararewards;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Tavo on 10/06/2016.
 */
public class LoginCodigoActivity extends Activity implements View.OnClickListener {
    TextView introducir;
    TextView footer;
    EditText editTextCodigo;
    Button botonEntrar;
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
                Intent i = new Intent(LoginCodigoActivity.this, SegurosActivity.class);
                startActivity(i);
                finish();
                break;
        }

    }
}
