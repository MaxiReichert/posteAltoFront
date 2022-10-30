package posteAltoMovile.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

import io.github.novacrypto.hashing.Sha256;
import posteAlto.postealtomovile.R;
import posteAltoMovile.dao.UsuarioDAO;
import posteAltoMovile.model.ModificarPasswordRequest;
import posteAltoMovile.model.ResponseBackend;
import posteAltoMovile.retroFitClient.RestClient;
import retrofit2.Call;
import retrofit2.Response;

public class CargarCodigoRecuperacion extends AppCompatActivity {

    TextView textViewCodigoRecuperacion;
    EditText editTextCodigoRecuperacion;
    TextView textViewContraseña;
    EditText editTextContraseña;
    TextView textViewVerifContreseña;
    EditText editTextVerifContraseña;
    Button btnCodigoRecuperacion;
    Button btnModificarContraseña;

    private static final int CONTRASEÑA_MODIFICADA_OK= 1;
    private static final int USUARIO_NO_ENCONTRADO= 2;
    private static final int TOKEN_VACIO= 3;
    private static final int ERROR_DE_SERVIDOR= 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cargar_codigo_recuperacion);

        textViewCodigoRecuperacion= findViewById(R.id.textViewCodigoRecuperacion);
        editTextCodigoRecuperacion= findViewById(R.id.editTextCodigoRecuperacion);
        textViewContraseña= findViewById(R.id.textViewContraseña);
        editTextContraseña= findViewById(R.id.editTextContraseña);
        textViewVerifContreseña= findViewById(R.id.textViewVerfiContraseña);
        editTextVerifContraseña= findViewById(R.id.editTextVerifContraseña);
        btnCodigoRecuperacion= findViewById(R.id.buttonCodigoRecuperacion);
        btnModificarContraseña= findViewById(R.id.buttonModifContraseña);

        textViewContraseña.setVisibility(View.GONE);
        editTextContraseña.setVisibility(View.GONE);
        textViewVerifContreseña.setVisibility(View.GONE);
        editTextVerifContraseña.setVisibility(View.GONE);
        btnModificarContraseña.setVisibility(View.GONE);

        Integer codigoRecuperacion= (Integer) getIntent().getSerializableExtra("codigoRecuperacion");
        String token= (String) getIntent().getSerializableExtra("token");

        btnCodigoRecuperacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer codigoRecuperacionIngresado= Integer.parseInt(editTextCodigoRecuperacion.getText().toString());
                if(codigoRecuperacion.equals(codigoRecuperacionIngresado)){

                    textViewCodigoRecuperacion.setVisibility(View.GONE);
                    editTextCodigoRecuperacion.setVisibility(View.GONE);
                    btnCodigoRecuperacion.setVisibility(View.GONE);

                    textViewContraseña.setVisibility(View.VISIBLE);
                    editTextContraseña.setVisibility(View.VISIBLE);
                    textViewVerifContreseña.setVisibility(View.VISIBLE);
                    editTextVerifContraseña.setVisibility(View.VISIBLE);
                    btnModificarContraseña.setVisibility(View.VISIBLE);

                }
                else {
                    editTextCodigoRecuperacion.setError("Código de recuperación incorrecto");
                }
            }
        });

        btnModificarContraseña.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(validarContraseña()){
                    ModificarPasswordRequest request= new ModificarPasswordRequest();
                    byte[] pass= editTextContraseña.getText().toString().trim().getBytes(StandardCharsets.UTF_8);
                    byte[] passEncript= Sha256.sha256(pass);
                    String passEncode= Base64.encodeToString(passEncript, Base64.NO_WRAP);
                    request.setNuevaContraseña(passEncode);
                    request.setResetToken(token);
                    modificarContraseña(request);
                }
            }
        });

    }

    private boolean validarContraseña(){
        if(editTextContraseña.getText() == null || editTextContraseña.getText().toString().trim().equals("")){
            editTextContraseña.setError("El campo es obligatorio");
            return false;
        }

        if(editTextVerifContraseña.getText() == null || editTextVerifContraseña.getText().toString().trim().equals("")){
            editTextVerifContraseña.setError("El campo es obligatorio");
            return false;
        }

        if(!editTextContraseña.getText().toString().trim().equals(editTextVerifContraseña.getText().toString().trim())){
            editTextVerifContraseña.setError("Los campos no coinciden");
            return false;
        }

        return true;
    }

    private void modificarContraseña(ModificarPasswordRequest request){
        Runnable r = new Runnable() {
            @Override
            public void run() {
                UsuarioDAO usuarioDAO= RestClient.getInstance().getRetrofit().create(UsuarioDAO.class);
                Call<ResponseBackend> callUsuario= usuarioDAO.modificarPassword(request);
                try{
                    String mensajeServidor;
                    Message mensaje;
                    Response<ResponseBackend> response= callUsuario.execute();
                    switch(response.code()){
                        case 200:
                            mensajeServidor= response.body().getMensaje();
                            mensaje= handler.obtainMessage(CONTRASEÑA_MODIFICADA_OK, mensajeServidor);
                            mensaje.sendToTarget();
                            break;
                        case 422:
                            mensajeServidor= response.errorBody().string();
                            mensaje= handler.obtainMessage(USUARIO_NO_ENCONTRADO, mensajeServidor);
                            mensaje.sendToTarget();
                            break;
                        case 403:
                            mensajeServidor= response.errorBody().string();
                            mensaje= handler.obtainMessage(TOKEN_VACIO, mensajeServidor);
                            mensaje.sendToTarget();
                            break;
                        case 404:
                        case 500:
                            mensajeServidor= response.errorBody().string();
                            mensaje= handler.obtainMessage(ERROR_DE_SERVIDOR, mensajeServidor);
                            mensaje.sendToTarget();
                            break;
                    }
                } catch(SocketTimeoutException timeoutException){
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
                case CONTRASEÑA_MODIFICADA_OK:
                    alertDialog= new AlertDialog.Builder(CargarCodigoRecuperacion.this).create();
                    alertDialog.setTitle("EXITO");
                    alertDialog.setMessage((String) msg.obj);
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent i= new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(i);
                                }
                            });
                    alertDialog.show();
                    break;
                case USUARIO_NO_ENCONTRADO:
                    alertDialog= new AlertDialog.Builder(CargarCodigoRecuperacion.this).create();
                    alertDialog.setTitle("Usuario no encontrado");
                    alertDialog.setMessage((String) msg.obj);
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent i= new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(i);
                                }
                            });
                    alertDialog.show();
                    break;
                case TOKEN_VACIO:
                    alertDialog= new AlertDialog.Builder(CargarCodigoRecuperacion.this).create();
                    alertDialog.setTitle("ERROR AL IDENTIFICAR USUARIO");
                    alertDialog.setMessage((String) msg.obj);
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent i= new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(i);
                                }
                            });
                    alertDialog.show();
                    break;
                case ERROR_DE_SERVIDOR:
                    alertDialog= new AlertDialog.Builder(CargarCodigoRecuperacion.this).create();
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