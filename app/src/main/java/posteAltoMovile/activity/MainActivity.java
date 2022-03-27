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

    private Button btnNuevoUsuario;
    private Button btnModificarUsuario;
    private Button btnAutenticar;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnNuevoUsuario= (Button) findViewById(R.id.buttonNuevoUsuario);
        btnModificarUsuario= findViewById(R.id.button2);
        btnAutenticar= findViewById(R.id.buttonAutenticar);

        usuario= new Usuario();
        usuario.setId(2);
        usuario.setEmail("maxi2@ejemplo.com");
        usuario.setFechaNacimiento("04-01-2022");
        usuario.setNombre("maxi");
        usuario.setApellido("reichert");

        btnNuevoUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), NuevoUsuario.class);
                startActivity(i);

            }
        });

        btnModificarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), ModificarUsuario.class);
                i.putExtra("usuario", usuario);
                startActivity(i);
            }
        });

        btnAutenticar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), Autenticar.class);
                startActivity(i);
            }
        });
    }


}