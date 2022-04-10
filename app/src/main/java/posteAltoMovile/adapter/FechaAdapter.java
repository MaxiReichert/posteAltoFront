package posteAltoMovile.adapter;

import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import posteAlto.postealtomovile.R;
import posteAltoMovile.holder.FechaHolder;
import posteAltoMovile.model.Partido;

public class FechaAdapter extends RecyclerView.Adapter<FechaHolder> {

    private List<Partido> partidos;
    private ViewGroup context;

    public FechaAdapter(List<Partido> datos){ this.partidos= datos; }

    @NonNull
    @Override
    public FechaHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = (View) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.fila_fecha, viewGroup, false);
        context=viewGroup;
        FechaHolder vh = new FechaHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull FechaHolder holder, int position) {
        final Partido partido = partidos.get(position);
        if(partido.getLocal() != null && partido.getVisitante() != null){
            //set fecha-hora de partido
            holder.txtFechaHora.setText(partido.getFechaJuego());

            //set imagenes
            byte[] escudoL= Base64.decode(partido.getLocal().getEscudo(), Base64.DEFAULT);
            holder.ivEquipo1.setImageBitmap(BitmapFactory.decodeByteArray(escudoL, 0, escudoL.length));
            holder.txtFechaHora.setText(partido.getFechaJuego());
            byte[] escudoV= Base64.decode(partido.getVisitante().getEscudo(), Base64.DEFAULT);
            holder.ivEquipo2.setImageBitmap(BitmapFactory.decodeByteArray(escudoV, 0, escudoV.length));

            //set nombre
            holder.txtEquipo1.setText(partido.getLocal().getNombre());
            holder.txtEquipo2.setText(partido.getVisitante().getNombre());

            //set resultado
            holder.txtRdo1.setText(partido.getPuntosLocal().toString());
            holder.txtRdo2.setText(partido.getPuntosVisitante().toString());

            //set dirección equipo local
            holder.txtClub.setText(partido.getLocal().getCalle()+" "+partido.getLocal().getNumero());
        }

    }

    @Override
    public int getItemCount() {
        return partidos.size();
    }
}
