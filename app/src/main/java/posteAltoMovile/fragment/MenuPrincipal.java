package posteAltoMovile.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import posteAlto.postealtomovile.R;

public class MenuPrincipal extends Fragment{

    private Button btnSuscripcion;
    private Button btnFixture;
    private Button btnPosiciones;
    private Button btnNoticias;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_menu_principal, container, false);

        btnSuscripcion = v.findViewById(R.id.btnSuscripcion);
        btnFixture = v.findViewById(R.id.btnFixture);
        btnPosiciones = v.findViewById(R.id.btnPosicion);
        btnNoticias = v.findViewById(R.id.btnNoticias);

        btnSuscripcion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //agregar comportamiento cuando se crea la pantalla
            }
        });

        btnFixture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //agregar comportamiento cuando se crea la pantalla
            }
        });

        btnPosiciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //agregar comportamiento cuando se crea la pantalla
            }
        });

        btnNoticias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //agregar comportamiento cuando se crea la pantalla
            }
        });

        return v;
    }


}