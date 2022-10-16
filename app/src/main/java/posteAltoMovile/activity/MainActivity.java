package posteAltoMovile.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;

import posteAlto.postealtomovile.R;
import posteAltoMovile.model.Usuario;

public class MainActivity extends AppCompatActivity {

    private Button btnRegistrarse;
    private Button btnIniciarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Pantalla Principal");

        btnRegistrarse= findViewById(R.id.btnRegistrarse);
        btnIniciarSesion= findViewById(R.id.btnIniciar);

        btnRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(getApplicationContext(), NuevoUsuario.class);
                startActivity(i);
            }
        });

        btnIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(getApplicationContext(), Autenticar.class);
                startActivity(i);
            }
        });
    }


}