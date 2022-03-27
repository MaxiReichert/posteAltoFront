package posteAltoMovile.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Random;
import java.util.regex.Pattern;

import posteAlto.postealtomovile.R;
import posteAltoMovile.dao.UsuarioDAO;
import posteAltoMovile.model.ResetPasswordRequest;
import posteAltoMovile.model.ResetPasswordResponse;
import posteAltoMovile.model.ResponseBackend;
import posteAltoMovile.retroFitClient.RestClient;
import posteAltoMovile.watcher.EmailWatcher;
import retrofit2.Call;
import retrofit2.Response;

public class OlvideContrasenia extends AppCompatActivity {

    private final static String patronEmail= "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    private final static int RESET_EXITO = 1;
    private final static int USUARIO_NO_ENCONTRADO= 2;
    private final static int ERROR_DE_SERVIDOR= 3;

    EditText editTextCorreo;
    Button btnEnviarEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_olvide_contrasenia);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},1);
        editTextCorreo= findViewById(R.id.editTextCorreo);
        btnEnviarEmail= findViewById(R.id.buttonEnviarEmail);

        btnEnviarEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email= editTextCorreo.getText().toString().trim();
                if(email.isEmpty() || !Pattern.matches(patronEmail, email)){
                    editTextCorreo.setError("Correo invalido");
                }
                else{
                    ResetPasswordRequest request= new ResetPasswordRequest();
                    request.setEmail(email);
                    resetPassword(request);
                }
            }
        });

    }

    private void resetPassword(ResetPasswordRequest request){
        Runnable r= new Runnable() {
            @Override
            public void run() {
                UsuarioDAO usuarioDAO= RestClient.getInstance().getRetrofit().create(UsuarioDAO.class);
                Call<ResetPasswordResponse> callUsuario= usuarioDAO.resetPassword(request);
                try{
                    String mensajeServidor;
                    Message mensaje;
                    Response<ResetPasswordResponse> response= callUsuario.execute();
                    switch(response.code()){
                        case 200:
                            mensaje= handler.obtainMessage(RESET_EXITO, response.body());
                            mensaje.sendToTarget();
                            break;
                        case 422:
                            mensajeServidor= response.errorBody().string();
                            mensaje= handler.obtainMessage(USUARIO_NO_ENCONTRADO, mensajeServidor);
                            mensaje.sendToTarget();
                            break;
                        case 404:
                        case 500:
                            mensajeServidor= response.errorBody().string();
                            mensaje= handler.obtainMessage(ERROR_DE_SERVIDOR, mensajeServidor);
                            mensaje.sendToTarget();
                            break;
                    }
                }
                catch(SocketTimeoutException timeoutException){
                    timeoutException.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread t = new Thread(r);
        t.start();
    }

    Handler handler= new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            AlertDialog alertDialog;
            switch(msg.what){
                case RESET_EXITO:
                    Intent i= new Intent(getApplicationContext(), CargarCodigoRecuperacion.class);
                    ResetPasswordResponse response= (ResetPasswordResponse) msg.obj;
                    i.putExtra("codigoRecuperacion", response.getCodigoRecuperacion());
                    i.putExtra("token", response.getResetToken());
                    startActivity(i);
                    break;
                case USUARIO_NO_ENCONTRADO:
                    alertDialog= new AlertDialog.Builder(OlvideContrasenia.this).create();
                    alertDialog.setTitle("Usuario no encontrado");
                    alertDialog.setMessage((String) msg.obj);
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();

                                }
                            });
                    alertDialog.show();
                    break;
                case ERROR_DE_SERVIDOR:
                    alertDialog= new AlertDialog.Builder(OlvideContrasenia.this).create();
                    alertDialog.setTitle("Se produjo un error");
                    alertDialog.setMessage("Revise su conexión de internet y vuelva a intentar");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                    break;
            }
        }
    };
}