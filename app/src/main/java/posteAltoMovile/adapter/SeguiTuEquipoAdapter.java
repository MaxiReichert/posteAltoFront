package posteAltoMovile.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import posteAlto.postealtomovile.R;
import posteAltoMovile.holder.SeguiTuEquipoHolder;
import posteAltoMovile.model.Equipo;

public class SeguiTuEquipoAdapter extends ArrayAdapter<Equipo> {

    private Context context;
    private List<Equipo> equipos;
    SharedPreferences  preferences;

    public SeguiTuEquipoAdapter(Context context, List<Equipo> equipos){
        super(context, R.layout.fragment_segui_tu_equipo, equipos);
        this.context= context;
        this.equipos= equipos;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater= LayoutInflater.from(this.getContext());
        preferences= PreferenceManager.getDefaultSharedPreferences(context);

        View fila= convertView;
        if(fila == null){
            fila= inflater.inflate(R.layout.fila_segui_equipo, null);
        }

        SeguiTuEquipoHolder holder= (SeguiTuEquipoHolder) fila.getTag();
        if(holder == null){
            holder= new SeguiTuEquipoHolder(fila);
            fila.setTag(holder);
        }

        Equipo filaEquipo= super.getItem(position);
        holder.tvEquipo.setText(filaEquipo.getNombre());
        byte[] escudo= Base64.decode(filaEquipo.getEscudo(), Base64.DEFAULT);
        holder.ivImagen.setImageBitmap(BitmapFactory.decodeByteArray(escudo, 0, escudo.length));
        holder.cbSigue.setChecked(preferences.getBoolean(filaEquipo.getNombre(), false));
        holder.cbSigue.setTag(position);

        SeguiTuEquipoHolder finalHolder = holder;
        holder.cbSigue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editorPreferences= preferences.edit();
                editorPreferences.putBoolean(filaEquipo.getNombre(), finalHolder.cbSigue.isChecked());
                editorPreferences.apply();
            }
        });

        return fila;
    }
}
