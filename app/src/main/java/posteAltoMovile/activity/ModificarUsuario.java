package posteAltoMovile.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.util.regex.Pattern;

import posteAlto.postealtomovile.R;
import posteAltoMovile.dao.UsuarioDAO;
import posteAltoMovile.model.ResponseBackend;
import posteAltoMovile.model.Usuario;
import posteAltoMovile.retroFitClient.RestClient;
import posteAltoMovile.watcher.EmailWatcher;
import posteAltoMovile.watcher.VarificacionWatcher;
import retrofit2.Call;
import retrofit2.Response;

public class ModificarUsuario extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE= 123;
    private static final String patronEmail= "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    private static final int USUARIO_MODIFICADO_OK= 1;
    private static final int USUARIO_NO_ENCONTRADO= 2;
    private static final int FECHA_INCORRECTA= 3;
    private static final int USUARIO_ELIMINADO_OK= 4;
    private static final int ERROR_DE_SERVIDOR= 5;


    private EditText editTextNombre, editTextApellido, editTextCorreo, editTextVerifCorreo, editTextFecha;
    private ImageView imageViewAvatar;
    private Button btnSeleccionarImagen, btnGuardarCambios, btnEliminarUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_usuario);

        Usuario usuario= (Usuario) getIntent().getSerializableExtra("usuario");
        String accessToken= (String) getIntent().getSerializableExtra("accessToken");
        String refreshToken= (String) getIntent().getSerializableExtra("refreshToken");

        if(usuario == null){
            Toast.makeText(getApplicationContext(), "ERROR AL CARGAR EL USUARIO", Toast.LENGTH_LONG);
            Intent i= new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        }
        editTextNombre= findViewById(R.id.editTextNombre);
        editTextApellido= findViewById(R.id.editTextApellido);
        editTextCorreo= findViewById(R.id.editTextCorreo);
        editTextVerifCorreo= findViewById(R.id.editTextVerifCorreo);
        editTextFecha= findViewById(R.id.editTextFecha);
        btnGuardarCambios= findViewById(R.id.buttonGuardarCambios);
        btnEliminarUsuario= findViewById(R.id.buttonEliminarUsuario);
        btnSeleccionarImagen= findViewById(R.id.buttonSelecImagen);
        imageViewAvatar= findViewById(R.id.imageViewAvatar);

        editTextNombre.setText(usuario.getNombre());
        editTextApellido.setText(usuario.getApellido());
        editTextCorreo.setText(usuario.getEmail());
        editTextVerifCorreo.setText(usuario.getEmail());
        editTextFecha.setText(usuario.getFechaNacimiento());

        editTextCorreo.addTextChangedListener(new EmailWatcher(editTextCorreo));
        editTextVerifCorreo.addTextChangedListener(new VarificacionWatcher(editTextCorreo, editTextVerifCorreo));

        btnSeleccionarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Elige una imagen"), GALLERY_REQUEST_CODE);
            }
        });

        btnGuardarCambios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validar()){
                    usuario.setNombre(editTextNombre.getText().toString().trim());
                    usuario.setApellido(editTextApellido.getText().toString().trim());
                    usuario.setEmail(editTextCorreo.getText().toString().trim());

                    actualizarUsuario(usuario, accessToken, refreshToken);
                }
            }
        });

        btnEliminarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog confirmDialog= new AlertDialog.Builder(ModificarUsuario.this).create();
                confirmDialog.setTitle("ELIMINAR USUARIO");
                confirmDialog.setMessage("¿Desea eliminar el usuario?");
                confirmDialog.setCancelable(true);
                confirmDialog.setButton(AlertDialog.BUTTON_POSITIVE, "ACEPTAR",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                borrarUsuario(usuario.getId(), accessToken, refreshToken);
                            }
                        });
                confirmDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCELAR",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                confirmDialog.show();
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

    private boolean validar(){
        if(editTextNombre.getText() == null || editTextNombre.getText().toString().trim().equals("")){
            editTextNombre.setError("El campo es obligatorio");
            return false;
        }

        if(editTextApellido.getText() == null || editTextApellido.getText().toString().trim().equals("")){
            editTextApellido.setError("El campo es obligatorio");
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

        if(!Pattern.matches(patronEmail, editTextCorreo.getText().toString())){
            editTextCorreo.setError("Correo invalido");
            return false;
        }

        if(!editTextCorreo.getText().toString().trim().equals(editTextVerifCorreo.getText().toString().trim())){
            editTextVerifCorreo.setError("Los campos no coinciden");
            return false;
        }

        return true;
    }

    private void actualizarUsuario(Usuario u, String accessToken, String refrehsToken){
        Runnable r= new Runnable() {
            @Override
            public void run() {
                UsuarioDAO usuarioDAO= RestClient.getInstance().getRetrofit().create(UsuarioDAO.class);
                Call<ResponseBackend> callActualizar= usuarioDAO.actualizarUsuario(u, accessToken);
                try{
                    String mensajeServidor;
                    Message mensaje;
                    Response<ResponseBackend> response= callActualizar.execute();
                    switch(response.code()){
                        case 200:
                            mensajeServidor= response.body().getMensaje();
                            mensaje= handler.obtainMessage(USUARIO_MODIFICADO_OK, mensajeServidor);
                            mensaje.sendToTarget();
                            break;
                        case 422:
                            mensajeServidor= response.errorBody().string();
                            mensaje= handler.obtainMessage(USUARIO_NO_ENCONTRADO, mensajeServidor);
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread t= new Thread(r);
        t.start();
    }

    private void borrarUsuario(Integer id, String accessToken, String refrehsToken){
        Runnable r= new Runnable() {
            @Override
            public void run() {
                UsuarioDAO usuarioDAO= RestClient.getInstance().getRetrofit().create(UsuarioDAO.class);
                Call<ResponseBackend> callBorrar= usuarioDAO.borrarUsuario(id, accessToken);
                try{
                    String mensajeServidor;
                    Message mensaje;
                    Response<ResponseBackend> response= callBorrar.execute();
                    switch(response.code()){
                        case 200:
                            mensajeServidor= response.body().getMensaje();
                            mensaje= handler.obtainMessage(USUARIO_ELIMINADO_OK, mensajeServidor);
                            mensaje.sendToTarget();
                            break;
                        case 422:
                            mensajeServidor= response.errorBody().string();
                            mensaje= handler.obtainMessage(USUARIO_NO_ENCONTRADO, mensajeServidor);
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
                case USUARIO_ELIMINADO_OK:
                case USUARIO_MODIFICADO_OK:
                    alertDialog= new AlertDialog.Builder(ModificarUsuario.this).create();
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
                    alertDialog= new AlertDialog.Builder(ModificarUsuario.this).create();
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
                case USUARIO_NO_ENCONTRADO:
                    alertDialog= new AlertDialog.Builder(ModificarUsuario.this).create();
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
                    alertDialog= new AlertDialog.Builder(ModificarUsuario.this).create();
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