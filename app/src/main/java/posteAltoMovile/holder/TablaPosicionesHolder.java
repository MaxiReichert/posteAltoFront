package posteAltoMovile.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import posteAlto.postealtomovile.R;

public class TablaPosicionesHolder {

    public ImageView ivImagen;
    public TextView tvEquipo;
    public TextView tvPG;
    public TextView tvPP;
    public TextView tvTF;
    public TextView tvTC;
    public TextView tvD;



    public TablaPosicionesHolder(View base){
        ivImagen = (android.widget.ImageView) base.findViewById(R.id.imImagen);
        tvEquipo = (TextView) base.findViewById(R.id.tvEquipo);
        tvPG = (TextView) base.findViewById(R.id.tvPG);
        tvPP = (TextView) base.findViewById(R.id.tvPP);
        tvTF = (TextView) base.findViewById(R.id.tvTF);
        tvTC = (TextView) base.findViewById(R.id.tvTC);
        tvD = (TextView) base.findViewById(R.id.tvD);
    }
}
