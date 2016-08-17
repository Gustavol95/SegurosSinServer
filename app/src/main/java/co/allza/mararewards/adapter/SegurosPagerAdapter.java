package co.allza.mararewards.adapter;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.PopupMenu;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import co.allza.mararewards.CargarDatos;
import co.allza.mararewards.R;
import co.allza.mararewards.interfaces.OnInfoClicked;
import co.allza.mararewards.items.SeguroItem;
/**
 * Created by Tavo on 17/06/2016.
 */
public class SegurosPagerAdapter extends PagerAdapter  implements PopupMenu.OnMenuItemClickListener {
    Context context;



    LayoutInflater inflater;
    ArrayList<SeguroItem> pages = new ArrayList<>();
    TextView poliza,aseguradora,seguro,beneficiario,renovacion,emergencia;
    ImageView info,aseguradoraIcono,seguroIcono,beneficiarioIcono,renovacionIcono,emergenciaIcono;
    Calendar calendar;
    Date fechaActual;
    Date fechaSeguro;
    SimpleDateFormat parserFecha;
    int pos;
    OnInfoClicked onInfoClicked;


    public SegurosPagerAdapter(Context context, ArrayList<SeguroItem> list) {
        this.context=context;
        inflater=LayoutInflater.from(context);
        this.pages=list;
        parserFecha=new SimpleDateFormat("dd/MMM/yyyy");
        calendar=Calendar.getInstance();
        fechaActual=calendar.getTime();
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public int getCount() {
        return pages.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        onInfoClicked=CargarDatos.getOnInfoClicked();
        View row = inflater.inflate(R.layout.listview_seguros, container, false);
        poliza=(TextView)row.findViewById(R.id.cardPoliza);
        aseguradora=(TextView)row.findViewById(R.id.cardAseguradora);
        seguro=(TextView)row.findViewById(R.id.cardNombreSeguro);
        beneficiario=(TextView)row.findViewById(R.id.cardBeneficiario);
        renovacion=(TextView)row.findViewById(R.id.cardRenovacion);
        emergencia=(TextView)row.findViewById(R.id.cardEmergencia);
        info=(ImageView)row.findViewById(R.id.cardInfoIcono);
        aseguradoraIcono=(ImageView)row.findViewById(R.id.cardAseguradoraIcono);
        seguroIcono=(ImageView)row.findViewById(R.id.cardNombreSeguroIcono);
        beneficiarioIcono=(ImageView)row.findViewById(R.id.cardBeneficiarioIcono);
        renovacionIcono=(ImageView)row.findViewById(R.id.cardRenovacionIcono);
        emergenciaIcono=(ImageView)row.findViewById(R.id.cardEmergenciaIcono);

        final SeguroItem item = pages.get(position);
        poliza.setText("Póliza: "+item.getPolicy());
        aseguradora.setText(item.getName());
        seguro.setText(item.getDescription());
        beneficiario.setText(item.getInsured_name());
        renovacion.setText("Renovación el "+item.getExpiration());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            emergencia.setText("Emergencia al "+ PhoneNumberUtils.formatNumber(item.getEmergency(),"MX"));
        }
        else{
        emergencia.setText("Emergencia al "+ PhoneNumberUtils.formatNumber(item.getEmergency()));}


        poliza.setTypeface(CargarDatos.getTypeface(context,CargarDatos.RUBIK_REGULAR));
        aseguradora.setTypeface(CargarDatos.getTypeface(context,CargarDatos.RUBIK_REGULAR));
        seguro.setTypeface(CargarDatos.getTypeface(context,CargarDatos.RUBIK_REGULAR));
        beneficiario.setTypeface(CargarDatos.getTypeface(context,CargarDatos.RUBIK_REGULAR));
        renovacion.setTypeface(CargarDatos.getTypeface(context,CargarDatos.RUBIK_REGULAR));
        emergencia.setTypeface(CargarDatos.getTypeface(context,CargarDatos.RUBIK_MEDIUM));
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(CargarDatos.getContext(), info);

                // This activity implements OnMenuItemClickListener
                popup.setOnMenuItemClickListener(SegurosPagerAdapter.this);
                popup.inflate(R.menu.menu_info);
                popup.show();
            }
        });

         pos=position;
        emergencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("tel:"+pages.get(pos).getEmergency().toString()));
                context.startActivity(intent);

            }
        });

        container.addView(row);

        try {
            fechaSeguro= parserFecha.parse(item.getExpiration());
            if(fechaActual.after(fechaSeguro))
            {
                poliza.setTextColor(context.getResources().getColor(R.color.grisVencido));
                aseguradora.setTextColor(context.getResources().getColor(R.color.grisVencido));
                seguro.setTextColor(context.getResources().getColor(R.color.grisVencido));
                beneficiario.setTextColor(context.getResources().getColor(R.color.grisVencido));
                emergencia.setTextColor(context.getResources().getColor(R.color.grisVencido));
                renovacion.setText("Venció el "+item.getExpiration());
                renovacion.setTypeface(CargarDatos.getTypeface(context,CargarDatos.RUBIK_MEDIUM));
                renovacion.setTextColor(context.getResources().getColor(R.color.rectanguloSplash));
                info.setImageResource(R.drawable.info_vencido);
                aseguradoraIcono.setImageResource(R.drawable.briefcase_vencido);
                seguroIcono.setImageResource(R.drawable.verified_vencido);
                beneficiarioIcono.setImageResource(R.drawable.account_vencido);
                renovacionIcono.setImageResource(R.drawable.history_vencido);
                emergenciaIcono.setImageResource(R.drawable.ring_vencido);



                return row;
            }
        } catch (ParseException e) {  e.printStackTrace();        }

        return row;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.editarSeg:
                onInfoClicked.editarSeguro(pos);
                return true;
            case R.id.borrarSeg:
                onInfoClicked.borrarSeguro(pos);
                return true;
        }

        return false;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public ArrayList<SeguroItem> getArrayList()
    {
        return pages;
    }


}
