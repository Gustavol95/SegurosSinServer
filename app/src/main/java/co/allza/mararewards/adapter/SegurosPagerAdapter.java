package co.allza.mararewards.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import co.allza.mararewards.CallToActionActivity;
import co.allza.mararewards.CargarFuentes;
import co.allza.mararewards.NotificacionesActivity;
import co.allza.mararewards.R;
import co.allza.mararewards.items.SeguroItem;

/**
 * Created by Tavo on 17/06/2016.
 */
public class SegurosPagerAdapter extends PagerAdapter
{
    private boolean doNotifyDataSetChangedOnce = false;
    Context context;
    LayoutInflater inflater;
    ArrayList<SeguroItem> pages = new ArrayList<>();
    TextView poliza,aseguradora,seguro,beneficiario,renovacion,emergencia;
    ImageView info,aseguradoraIcono,seguroIcono,beneficiarioIcono,renovacionIcono,emergenciaIcono;
    Calendar calendar;
    Date fechaActual;
    Date fechaSeguro;
    SimpleDateFormat parserFecha;

    public SegurosPagerAdapter(Context context, ArrayList<SeguroItem> list)
    {
        this.context=context;
        inflater=LayoutInflater.from(context);
        this.pages=list;
        parserFecha=new SimpleDateFormat("dd/MMM/yyyy");
        calendar=Calendar.getInstance();
        fechaActual=calendar.getTime();
    }
    @Override
    public int getCount() {


        return pages.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
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

        SeguroItem item = pages.get(position);
        poliza.setText("Póliza: "+item.getPolicy());
        aseguradora.setText(item.getName());
        seguro.setText(item.getDescription());
        beneficiario.setText(item.getInsured_name());
        renovacion.setText("Renovación el "+item.getExpiration());
        emergencia.setText("Emergencia al "+item.getEmergency());

        poliza.setTypeface(CargarFuentes.getTypeface(context,CargarFuentes.RUBIK_REGULAR));
        aseguradora.setTypeface(CargarFuentes.getTypeface(context,CargarFuentes.RUBIK_REGULAR));
        seguro.setTypeface(CargarFuentes.getTypeface(context,CargarFuentes.RUBIK_REGULAR));
        beneficiario.setTypeface(CargarFuentes.getTypeface(context,CargarFuentes.RUBIK_REGULAR));
        renovacion.setTypeface(CargarFuentes.getTypeface(context,CargarFuentes.RUBIK_REGULAR));
        emergencia.setTypeface(CargarFuentes.getTypeface(context,CargarFuentes.RUBIK_MEDIUM));

        final int pos=position;
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
                renovacion.setTypeface(CargarFuentes.getTypeface(context,CargarFuentes.RUBIK_MEDIUM));
                renovacion.setTextColor(context.getResources().getColor(R.color.rectanguloSplash));
                info.setImageResource(R.drawable.info_vencido);
                aseguradoraIcono.setImageResource(R.drawable.briefcase_vencido);
                seguroIcono.setImageResource(R.drawable.verified_vencido);
                beneficiarioIcono.setImageResource(R.drawable.account_vencido);
                renovacionIcono.setImageResource(R.drawable.history_vencido);
                emergenciaIcono.setImageResource(R.drawable.ring_vencido);

                return row;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return row;
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
