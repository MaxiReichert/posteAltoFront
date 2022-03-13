package posteAltoMovile.holder;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import posteAlto.postealtomovile.R;

public class SeguiTuEquipoHolder {

    public ImageView ivImagen;
    public TextView tvEquipo;
    public CheckBox cbSigue;



    public SeguiTuEquipoHolder(View base) {
        ivImagen = (ImageView) base.findViewById(R.id.imImagen);
        tvEquipo = (TextView) base.findViewById(R.id.tvEquipo);
        cbSigue = (CheckBox) base.findViewById(R.id.cbSigue);

    }
}
