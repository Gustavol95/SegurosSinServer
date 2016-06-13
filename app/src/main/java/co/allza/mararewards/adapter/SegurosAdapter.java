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
        TextView titulo;
        TextView poliza;
        TextView polizaTexto;
        TextView aseguradora;
        TextView aseguradoraTexto;
        TextView cobertura;
        TextView coberturaTexto;
        Button boton;

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
            viewHolder.titulo=(TextView)row.findViewById(R.id.cardTitulo);
            viewHolder.poliza=(TextView)row.findViewById(R.id.cardPoliza);
            viewHolder.polizaTexto=(TextView)row.findViewById(R.id.cardPolizaTexto);
            viewHolder.aseguradora=(TextView)row.findViewById(R.id.cardAseguradora);
            viewHolder.aseguradoraTexto=(TextView)row.findViewById(R.id.cardAseguradoraTexto);
            viewHolder.cobertura=(TextView)row.findViewById(R.id.cardCobertura);
            viewHolder.coberturaTexto=(TextView)row.findViewById(R.id.cardCoberturaTexto);
            viewHolder.boton=(Button)row.findViewById(R.id.cardBoton);
            row.setTag(viewHolder);

        } else {
            viewHolder = (CardViewHolder3)row.getTag();
        }
        SeguroItem item = getItem(position);
        viewHolder.titulo.setText(item.getNombre());
        viewHolder.polizaTexto.setText(item.getPoliza());
        viewHolder.aseguradoraTexto.setText(item.getAseguradora());
        viewHolder.coberturaTexto.setText(item.getCobertura());

        viewHolder.titulo.setTypeface(CargarFuentes.getTypeface(getContext(),CargarFuentes.RUBIK_MEDIUM));
        viewHolder.poliza.setTypeface(CargarFuentes.getTypeface(getContext(),CargarFuentes.RUBIK_MEDIUM));
        viewHolder.polizaTexto.setTypeface(CargarFuentes.getTypeface(getContext(),CargarFuentes.RUBIK_MEDIUM));
        viewHolder.aseguradora.setTypeface(CargarFuentes.getTypeface(getContext(),CargarFuentes.RUBIK_MEDIUM));
        viewHolder.aseguradoraTexto.setTypeface(CargarFuentes.getTypeface(getContext(),CargarFuentes.RUBIK_MEDIUM));
        viewHolder.cobertura.setTypeface(CargarFuentes.getTypeface(getContext(),CargarFuentes.RUBIK_MEDIUM));
        viewHolder.coberturaTexto.setTypeface(CargarFuentes.getTypeface(getContext(),CargarFuentes.RUBIK_MEDIUM));
        viewHolder.boton.setTypeface(CargarFuentes.getTypeface(getContext(),CargarFuentes.RUBIK_MEDIUM));

        return row;
    }
}
