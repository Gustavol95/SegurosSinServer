package co.allza.mararewards.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import co.allza.mararewards.CargarFuentes;
import co.allza.mararewards.R;
import co.allza.mararewards.items.LoginItem;
import co.allza.mararewards.items.SeguroItem;

/**
 * Created by Tavo on 13/06/2016.
 */
public class SegurosAdapter  extends ArrayAdapter<SeguroItem>
{
    private static final String TAG = "SegurosAdapter";
    private List<SeguroItem> lista = new ArrayList<SeguroItem>();
    private Context contexto;
    static class CardViewHolder3 {
        TextView poliza,aseguradora,seguro,beneficiario,renovacion,emergencia;
        ImageView info,aseguradoraIcono,seguroIcono,beneficiarioIcono,renovacionIcono,emergenciaIcono;


    }
    public SegurosAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.contexto=context;
    }

    @Override
    public void add(SeguroItem object) {
        super.add(object);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public SeguroItem getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        CardViewHolder3 viewHolder;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.listview_seguros, parent, false);
            viewHolder = new CardViewHolder3();
            viewHolder.poliza=(TextView)row.findViewById(R.id.cardPoliza);
            viewHolder.aseguradora=(TextView)row.findViewById(R.id.cardAseguradora);
            viewHolder.seguro=(TextView)row.findViewById(R.id.cardNombreSeguro);
            viewHolder.beneficiario=(TextView)row.findViewById(R.id.cardBeneficiario);
            viewHolder.renovacion=(TextView)row.findViewById(R.id.cardRenovacion);
            viewHolder.emergencia=(TextView)row.findViewById(R.id.cardEmergencia);
            viewHolder.info=(ImageView)row.findViewById(R.id.cardInfoIcono);
            viewHolder.aseguradoraIcono=(ImageView)row.findViewById(R.id.cardAseguradoraIcono);
            viewHolder.seguroIcono=(ImageView)row.findViewById(R.id.cardNombreSeguroIcono);
            viewHolder.beneficiarioIcono=(ImageView)row.findViewById(R.id.cardBeneficiarioIcono);
            viewHolder.renovacionIcono=(ImageView)row.findViewById(R.id.cardRenovacionIcono);
            viewHolder.emergenciaIcono=(ImageView)row.findViewById(R.id.cardEmergenciaIcono);

            row.setTag(viewHolder);

        } else {
            viewHolder = (CardViewHolder3)row.getTag();
        }
        SeguroItem item = getItem(position);
        viewHolder.poliza.setText("Póliza: "+item.getPoliza());
        viewHolder.aseguradora.setText(item.getAseguradora());
        viewHolder.seguro.setText(item.getSeguro());
        viewHolder.beneficiario.setText(item.getBeneficiario());
        viewHolder.renovacion.setText("Renovación el "+item.getRenovacion());
        viewHolder.emergencia.setText("Emergencia al "+item.getEmergencia());



        viewHolder.poliza.setTypeface(CargarFuentes.getTypeface(getContext(),CargarFuentes.RUBIK_REGULAR));
        viewHolder.aseguradora.setTypeface(CargarFuentes.getTypeface(getContext(),CargarFuentes.RUBIK_REGULAR));
        viewHolder.seguro.setTypeface(CargarFuentes.getTypeface(getContext(),CargarFuentes.RUBIK_REGULAR));
        viewHolder.beneficiario.setTypeface(CargarFuentes.getTypeface(getContext(),CargarFuentes.RUBIK_REGULAR));
        viewHolder.renovacion.setTypeface(CargarFuentes.getTypeface(getContext(),CargarFuentes.RUBIK_REGULAR));
        viewHolder.emergencia.setTypeface(CargarFuentes.getTypeface(getContext(),CargarFuentes.RUBIK_MEDIUM));
        return row;

    }
}
