package posteAltoMovile.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.regex.Pattern;

import posteAlto.postealtomovile.R;
import posteAltoMovile.watcher.EmailWatcher;

public class OlvideContrasenia extends AppCompatActivity {

    private final static String patronEmail= "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

    EditText editTextSms;
    Button btnEnviarEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_olvide_contrasenia);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},1);
        editTextSms= findViewById(R.id.editTextSms);
        btnEnviarEmail= findViewById(R.id.buttonEnviarEmail);

        btnEnviarEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double numeroAleatorio= 100000 + Math.random() * 900000;
                int codigoRecuperacion= (int) numeroAleatorio;
                String mensaje= "Nadie de Poste Alto te solicitará este dato. " +
                        "No lo compartas. Tu codigo de seguridad es "+codigoRecuperacion;

                try {

                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(editTextSms.getText().toString(), null, mensaje, null, null);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                /*String email= editTextCorreo.getText().toString().trim();
                if(email.isEmpty() || !Pattern.matches(patronEmail, email)){
                    editTextCorreo.setError("Correo invalido");
                }
                else{
                    editTextCorreo.setError(null);
                    double numeroAleatorio= 100000 + Math.random() * 900000;
                    int codigoRecuperacion= (int) numeroAleatorio;
                    System.out.print(codigoRecuperacion);


                }*/
            }
        });

    }
}