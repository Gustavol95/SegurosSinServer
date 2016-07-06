package co.allza.mararewards.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import co.allza.mararewards.R;

/**
 * Created by Tavo on 06/07/2016.
 */
public class CaracteristicasSeguroFragment extends DialogFragment {

    private String title;
    private String message;
    private static final String TAG = "CaracteristicasSeguroFragment";

    public CaracteristicasSeguroFragment() {
        title=getArguments().getString("title", "Titulo del Seguro");
        message=getArguments().getString("message","Aqui se muestran las características en general del seguro");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                    }
                })
               .setTitle(title);
        // Create the AlertDialog object and return it
        return builder.create();
    }


}