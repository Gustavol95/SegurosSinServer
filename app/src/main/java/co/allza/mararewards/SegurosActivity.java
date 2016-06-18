package co.allza.mararewards;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pixelcan.inkpageindicator.InkPageIndicator;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import co.allza.mararewards.adapter.SegurosAdapter;
import co.allza.mararewards.adapter.SegurosPagerAdapter;
import co.allza.mararewards.items.DepthPageTransformer;
import co.allza.mararewards.items.SeguroItem;

public class SegurosActivity extends AppCompatActivity  {

    ViewPager pagerSeguros;
    SegurosPagerAdapter adapter;
    ArrayList<SeguroItem> arraySeguros = new ArrayList<>();
    TextView seguroActual;
    Button botonCallToAction;
    SimpleDateFormat parserFecha;
    Calendar calendar;
    Date fechaActual;
    Date fechaSeguro;
    InkPageIndicator inkPageIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seguros);
        //para Lollipop
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.statusbarSeguros));
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Seguros");
        toolbar.setNavigationIcon(R.drawable.ic_notifications_active_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(SegurosActivity.this);
                Toast t= Toast.makeText(getApplicationContext(),"fgas",Toast.LENGTH_LONG);
            }
        });


        pagerSeguros=(ViewPager) findViewById(R.id.viewPagerSeguros);
        arraySeguros.add(new SeguroItem("0123423356789","MAPFRE Tepeyac","Plan Educacional Superación",
                "José Eduardo Quintana Rodriguez","24/Ago/2016","01 800 062 7373"));
        arraySeguros.add(new SeguroItem("0123456789"," Tepeyac","Plan Educacional Superación",
                "José Eduardo Quintana Rodriguez","24/Ago/2016","01 800 062 7373"));
        arraySeguros.add(new SeguroItem("0123456789","MAPFRE Tepeyac"," Educacional Superación",
                "José Eduardo Quintana Rodriguez","24/Ago/2016","01 800 062 7373"));
        arraySeguros.add(new SeguroItem("0123456789","MAPFRE Tepeyac","Plan Educacional Superación",
                "José Eduardo Quintana Rodriguez","24/Jan/2016","01 800 062 7373"));
        arraySeguros.add(new SeguroItem("0123456789","MAPFRE Tepeyac","Plan Educacional ",
                "José Eduardo Quintana Rodriguez","24/Ene/2016","7450982"));
        adapter = new SegurosPagerAdapter(getApplicationContext(), arraySeguros);
        pagerSeguros.setAdapter(adapter);
        pagerSeguros.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                seguroActual.setText(arraySeguros.get(position).getSeguro());


            }

            @Override
            public void onPageSelected(int position) {
                try {
                    fechaSeguro= parserFecha.parse(arraySeguros.get(position).getRenovacion());
                    if(fechaActual.after(fechaSeguro))
                    Toast.makeText(SegurosActivity.this, "ESTA ANTES", Toast.LENGTH_SHORT).show();
                    if(fechaActual.before(fechaSeguro))
                    Toast.makeText(SegurosActivity.this, "ESTA DESPUES", Toast.LENGTH_SHORT).show();
                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(SegurosActivity.this, ""+e.toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        pagerSeguros.setPageTransformer(true,new DepthPageTransformer());
        seguroActual=(TextView)findViewById(R.id.tituloSeguroActual);
        seguroActual.setTypeface(CargarFuentes.getTypeface(getApplicationContext(),CargarFuentes.RUBIK_MEDIUM));

        botonCallToAction=(Button)findViewById(R.id.botonCallToAction);
        botonCallToAction.setTypeface(CargarFuentes.getTypeface(getApplicationContext(),CargarFuentes.ROBOTO_MEDIUM));
        botonCallToAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(SegurosActivity.this,CallToActionActivity.class);
                startActivity(i);
            }
        });

        parserFecha=new SimpleDateFormat("dd/MMM/yyyy");
        calendar=Calendar.getInstance();
        fechaActual=calendar.getTime();

         inkPageIndicator = (InkPageIndicator) findViewById(R.id.indicator);
        inkPageIndicator.setViewPager(pagerSeguros);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_seguros, menu);
        return true;
        
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.desactivarNotif) {
            Toast.makeText(SegurosActivity.this, "", Toast.LENGTH_SHORT).show();
            return true;
        }

      
        return super.onOptionsItemSelected(item);
    }
}
