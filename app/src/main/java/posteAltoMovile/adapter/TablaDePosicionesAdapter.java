package posteAltoMovile.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import posteAlto.postealtomovile.R;
import posteAltoMovile.holder.TablaPosicionesHolder;
import posteAltoMovile.model.FilaTabla;

public class TablaDePosicionesAdapter extends ArrayAdapter<FilaTabla> {

    private Context contexto;
    private List<FilaTabla> filaTablaList;

    public TablaDePosicionesAdapter(Context context, List<FilaTabla> filaTablaList){
        super(context, R.layout.fragment_tabla_de_posiciones, filaTablaList);
        this.contexto= context;
        this.filaTablaList= filaTablaList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(this.getContext());
        View fila = convertView;
        if (fila == null) {
            fila = inflater.inflate(R.layout.fila_tabla_posiciones, null);
        }
        TablaPosicionesHolder holder = (TablaPosicionesHolder) fila.getTag();

        if (holder == null) {
            holder = new TablaPosicionesHolder(fila);
            fila.setTag(holder);
        }

        FilaTabla filaEquipo = super.getItem(position);
        byte[] escudo= Base64.decode(filaEquipo.getEscudo(), Base64.DEFAULT);
        Bitmap imagen= BitmapFactory.decodeByteArray(escudo, 0, escudo.length);
        holder.ivImagen.setImageBitmap(BitmapFactory.decodeByteArray(escudo, 0, escudo.length));
        holder.tvEquipo.setText(filaEquipo.getNombreEquipo());
        holder.tvPG.setText(String.valueOf(filaEquipo.getPartidosGanados()));
        holder.tvPP.setText(String.valueOf(filaEquipo.getPartidosPerdidos()));
        holder.tvTF.setText(String.valueOf(filaEquipo.getPuntosAFavor()));
        holder.tvTC.setText(String.valueOf(filaEquipo.getPuntosEnContra()));
        holder.tvD.setText(String.valueOf(filaEquipo.getDiferenciaDePuntos()));
        if ((position)%2==0) {
            holder.tvEquipo.setTextColor(Color.parseColor("#000000"));
            holder.tvPG.setTextColor(Color.parseColor("#000000"));
            holder.tvPP.setTextColor(Color.parseColor("#000000"));
            holder.tvTF.setTextColor(Color.parseColor("#000000"));
            holder.tvTC.setTextColor(Color.parseColor("#000000"));
            holder.tvD.setTextColor(Color.parseColor("#000000"));
        }
        return fila;
    }
}
