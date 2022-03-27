package posteAltoMovile.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;


import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import android.util.Base64;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.regex.Pattern;

import posteAlto.postealtomovile.R;
import posteAltoMovile.dao.UsuarioDAO;
import posteAltoMovile.model.Usuario;
import posteAltoMovile.model.ResponseBackend;
import posteAltoMovile.retroFitClient.RestClient;
import posteAltoMovile.watcher.EmailWatcher;
import posteAltoMovile.watcher.VarificacionWatcher;
import retrofit2.Call;
import retrofit2.Response;

public class NuevoUsuario extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE= 123;
    private static final String patronEmail= "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    private static final int USUARIO_CREADO_OK= 1;
    private static final int USUARIO_YA_EXISTE= 2;
    private static final int FECHA_INCORRECTA=3;
    private static final int ERROR_DE_SERVIDOR=4;

    private EditText editTextFecha;
    private Button btnSeleccionarImagen;
    private ImageView imageViewAvatar;
    private Button btnRegistrar;
    private EditText editTextApellido;
    private EditText editTextCorreo;
    private EditText editTextVerifCorreo;
    private EditText editTextContraseña;
    private EditText editTextVerifContraseña;
    private EditText editTextNombre;

    private int anio, mes, dia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_usuario);
        setTitle("Nuevo Usuario");

         editTextFecha= findViewById(R.id.editTextFecha);
         btnSeleccionarImagen= findViewById(R.id.buttonSelecImagen);
         imageViewAvatar= findViewById(R.id.imageViewAvatar);
         btnRegistrar= findViewById(R.id.buttonRegistrar);
         editTextApellido= findViewById(R.id.editTextApellido);
         editTextCorreo= findViewById(R.id.editTextCorreo);
         editTextVerifCorreo= findViewById(R.id.editTextVerifCorreo);
         editTextContraseña= findViewById(R.id.editTextContraseña);
         editTextVerifContraseña= findViewById(R.id.editTextVerifContraseña);
         editTextNombre= findViewById(R.id.editTextNombre);

         editTextCorreo.addTextChangedListener(new EmailWatcher(editTextCorreo));
         editTextVerifCorreo.addTextChangedListener(new VarificacionWatcher(editTextCorreo, editTextVerifCorreo));
         editTextVerifContraseña.addTextChangedListener(new VarificacionWatcher(editTextContraseña, editTextVerifContraseña));

         editTextFecha.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 final Calendar c= Calendar.getInstance();
                 anio= c.get(Calendar.YEAR);
                 mes= c.get(Calendar.MONTH);
                 dia= c.get(Calendar.DAY_OF_MONTH);

                 DatePickerDialog datePickerDialog= new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
                     @Override
                     public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                         int cantidadCifrasMes= contarCifras(month);
                         int cantidadCifrasdia= contarCifras(day);
                         String mes;
                         String dia;

                         if(cantidadCifrasMes<2)
                             mes="0"+(month+1);
                         else
                             mes=String.valueOf(month+1);

                         if(cantidadCifrasdia<2)
                             dia="0"+day;
                         else
                             dia= String.valueOf(day);

                         editTextFecha.setText(dia+"-"+mes+"-"+year);
                     }
                 }
                 , anio, mes, dia);
                 datePickerDialog.show();
             }
         });

         btnSeleccionarImagen.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent intent= new Intent();
                 intent.setType("image/*");
                 intent.setAction(Intent.ACTION_GET_CONTENT);
                 startActivityForResult(Intent.createChooser(intent, "Elige una imagen"), GALLERY_REQUEST_CODE);
             }
         });

         btnRegistrar.setOnClickListener(new View.OnClickListener() {
             @RequiresApi(api = Build.VERSION_CODES.O)
             @Override
             public void onClick(View view) {
                 if(validar()){

                     byte[] pass= editTextContraseña.getText().toString().trim().getBytes(StandardCharsets.UTF_8);
                     String passEncode= Base64.encodeToString(pass, Base64.NO_WRAP);

                     System.out.println(passEncode);

                     Usuario usuario= new Usuario();
                     usuario.setNombre(editTextNombre.getText().toString().trim());
                     usuario.setApellido(editTextApellido.getText().toString().trim());
                     usuario.setEmail(editTextCorreo.getText().toString().trim());
                     usuario.setFechaNacimiento(editTextFecha.getText().toString().trim());
                     usuario.setPassword(passEncode);

                     guardarUsuario(usuario);
                 }
             }
         });
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri image = data.getData();
            imageViewAvatar.setImageURI(image);

        }
    }

    private int contarCifras(int numero){
        int cifras=0;
        int n= numero;

        while (n!=0){
            n= n/10;
            cifras ++;
        }

        return cifras;
    }

    private boolean validar(){

        if(editTextNombre.getText() == null || editTextNombre.getText().toString().trim().equals("")){
            editTextNombre.setError("El campo es obligatorio");
            return false;
        }

        if(editTextApellido.getText() == null || editTextApellido.getText().toString().trim().equals("")){
            editTextApellido.setError("El campo es obligatorio");
            return false;
        }

        if(editTextFecha.getText() == null || editTextFecha.getText().toString().trim().equals("")){
            editTextFecha.setError("El campo es obligatorio");
            return false;
        }

        if(editTextCorreo.getText() == null || editTextCorreo.getText().toString().trim().equals("")){
            editTextCorreo.setError("El campo es obligatorio");
            return false;
        }

        if(editTextVerifCorreo.getText() == null || editTextVerifCorreo.getText().toString().trim().equals("")){
            editTextVerifCorreo.setError("El campo es obligatorio");
            return false;
        }

        if(editTextContraseña.getText() == null || editTextContraseña.getText().toString().trim().equals("")){
            editTextContraseña.setError("El campo es obligatorio");
            return false;
        }

        if(editTextVerifContraseña.getText() == null || editTextVerifContraseña.getText().toString().trim().equals("")){
            editTextVerifContraseña.setError("El campo es obligatorio");
            return false;
        }

        if(!Pattern.matches(patronEmail, editTextCorreo.getText().toString())){
            editTextCorreo.setError("Correo invalido");
            return false;
        }

        if(!editTextCorreo.getText().toString().trim().equals(editTextVerifCorreo.getText().toString().trim())){
            editTextVerifCorreo.setError("Los campos no coinciden");
            return false;
        }

       if(!editTextContraseña.getText().toString().trim().equals(editTextVerifContraseña.getText().toString().trim())){
            editTextVerifContraseña.setError("Los campos no coinciden");
            return false;
        }

        return true;
    }

    private void guardarUsuario(Usuario usuario){
        Runnable r= new Runnable() {
            @Override
            public void run() {
                UsuarioDAO usuarioDAO= RestClient.getInstance().getRetrofit().create(UsuarioDAO.class);
                Call<ResponseBackend> callUsuario= usuarioDAO.nuevoUsuario(usuario);
                try{
                    String mensajeServidor;
                    Message mensaje;
                    Response<ResponseBackend> response= callUsuario.execute();
                    switch(response.code()){
                        case 200:
                            mensajeServidor= response.body().getMensaje();
                            mensaje= handler.obtainMessage(USUARIO_CREADO_OK, mensajeServidor);
                            mensaje.sendToTarget();
                            break;
                        case 422:
                            mensajeServidor= response.errorBody().string();
                            mensaje= handler.obtainMessage(USUARIO_YA_EXISTE, mensajeServidor);
                            mensaje.sendToTarget();
                            break;
                        case 400:
                            mensajeServidor= response.errorBody().string();
                            mensaje= handler.obtainMessage(FECHA_INCORRECTA, mensajeServidor);
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
        Thread t= new Thread(r);
        t.start();
    }

    Handler handler= new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            AlertDialog alertDialog;
            switch(msg.what){
                case USUARIO_CREADO_OK:
                    alertDialog= new AlertDialog.Builder(NuevoUsuario.this).create();
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
                case FECHA_INCORRECTA:
                    alertDialog= new AlertDialog.Builder(NuevoUsuario.this).create();
                    alertDialog.setTitle("Fecha incorrecta");
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
                case USUARIO_YA_EXISTE:
                    alertDialog= new AlertDialog.Builder(NuevoUsuario.this).create();
                    alertDialog.setTitle("Usuario ya existe");
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
                    alertDialog= new AlertDialog.Builder(NuevoUsuario.this).create();
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