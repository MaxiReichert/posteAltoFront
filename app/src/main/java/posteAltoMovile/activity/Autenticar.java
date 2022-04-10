package posteAltoMovile.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import posteAlto.postealtomovile.R;
import posteAltoMovile.dao.LoginDAO;
import posteAltoMovile.model.ResponseLogin;
import posteAltoMovile.retroFitClient.RestClient;
import retrofit2.Call;
import retrofit2.Response;

public class Autenticar extends AppCompatActivity {

    private static final int FALLA_AUTENTICACION= 2;
    private static final int ERROR_SERVIDOR= 3;
    private static final int AUTENTICACION_OK= 1;

    Button btnInciarSesion;
    EditText editTextCorreo;
    EditText editTextContraseña;
    TextView textViewError;
    TextView textViewOlvidasteContraseña;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autenticar);

        btnInciarSesion= findViewById(R.id.buttonIniciarSesion);
        editTextCorreo= findViewById(R.id.editTextCorreo);
        editTextContraseña= findViewById(R.id.editTextContraseña);
        textViewError= findViewById(R.id.textViewErrorSesión);
        textViewOlvidasteContraseña= findViewById(R.id.textViewOlvidasteContraseña);


        btnInciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                autenticar(editTextCorreo.getText().toString().trim(),
                        editTextContraseña.getText().toString().trim());
            }
        });

        textViewOlvidasteContraseña.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(getApplicationContext(), OlvideContrasenia.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void autenticar(String username, String password){
        Runnable r= new Runnable() {
            @Override
            public void run() {
                LoginDAO loginDAO= RestClient.getInstance().getRetrofit().create(LoginDAO.class);
                Call<ResponseLogin> callLogin= loginDAO.login(username, password);
                try{
                    Message mensaje;
                    Response<ResponseLogin> response= callLogin.execute();

                    switch(response.code()){
                        case 200:
                            mensaje= handler.obtainMessage(AUTENTICACION_OK, response.body());
                            mensaje.sendToTarget();
                            break;
                        case 403:
                            mensaje= handler.obtainMessage(FALLA_AUTENTICACION);
                            mensaje.sendToTarget();
                            break;
                        case 404:
                        case 500:
                            mensaje= handler.obtainMessage(ERROR_SERVIDOR);
                            mensaje.sendToTarget();
                            break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };
        Thread thread= new Thread(r);
        thread.start();
    }

    Handler handler= new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 1:
                    ResponseLogin responseLogin= (ResponseLogin) msg.obj;
                    Intent i = new Intent(getApplicationContext(), PrincipalActivity.class);
                    i.putExtra("accessToken", responseLogin.getAccessToken());
                    i.putExtra("refreshToken", responseLogin.getRefreshToken());
                    startActivity(i);
                    break;
                case 2:
                    textViewError.setText("Usuario o contraseña incorrectos");
                    break;
                case 3:
                    Toast.makeText(getApplicationContext(),
                            "Error del servidor, intentelo más tarde", Toast.LENGTH_LONG);
                    break;
            }
        }
    };
}