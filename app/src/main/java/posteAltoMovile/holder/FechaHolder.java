package posteAltoMovile.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import posteAlto.postealtomovile.R;

public class FechaHolder extends RecyclerView.ViewHolder {
    public TextView txtFechaHora;
    public ImageView ivEquipo1;
    public ImageView ivEquipo2;
    public TextView txtEquipo1;
    public TextView txtEquipo2;
    public TextView txtRdo1;
    public TextView txtRdo2;
    public TextView txtClub;


    public FechaHolder(View base){
        super(base);
        this.txtFechaHora = (TextView) base.findViewById(R.id.txtFechaHora);
        this.ivEquipo1 = (ImageView) base.findViewById(R.id.ivEquipo1);
        this.ivEquipo2 = (ImageView) base.findViewById(R.id.ivEquipo2);
        this.txtEquipo1 = (TextView) base.findViewById(R.id.txtEquipo1);
        this.txtEquipo2 = (TextView) base.findViewById(R.id.txtEquipo2);
        this.txtRdo1 = (TextView) base.findViewById(R.id.txtRdo1);
        this.txtRdo2 = (TextView) base.findViewById(R.id.txtRdo2);
        this.txtClub = (TextView) base.findViewById(R.id.txtClub);
    }
}
