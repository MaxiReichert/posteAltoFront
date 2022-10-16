package posteAltoMovile.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Calendar;
import java.util.List;

import posteAlto.postealtomovile.R;
import posteAltoMovile.dao.EquipoDAO;
import posteAltoMovile.dao.PartidoDAO;
import posteAltoMovile.dao.UsuarioDAO;
import posteAltoMovile.model.Equipo;
import posteAltoMovile.model.Estado;
import posteAltoMovile.model.Partido;
import posteAltoMovile.model.ResponseBackend;
import posteAltoMovile.model.Usuario;
import posteAltoMovile.retroFitClient.RestClient;
import retrofit2.Call;
import retrofit2.Response;

public class NuevoPartido extends AppCompatActivity {

    private final int EQUIPOS_CARGADOS= 1;
    private final int ERROR= 2;
    private final int YA_EXISTE_PARTIDO= 3;
    private final int PARTIDO_CREADO_CON_EXITO= 4;

    private Integer idCompetencia;
    private String accessToken;
    private Spinner spiLocal;
    private Spinner spiVisitante;
    private List<Equipo> listaEquipos;
    private Button btnNuevoPartido;
    private EditText edtFechaJuego;
    private EditText edtHoraJuego;
    private EditText edtFechaCompetencia;
    private int anio, mes, dia, hora, minuto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_partido);

        idCompetencia= (Integer) getIntent().getSerializableExtra("idCompetencia");
        accessToken= (String) getIntent().getSerializableExtra("accessToken");

        spiLocal= findViewById(R.id.spiLocal);
        spiVisitante= findViewById(R.id.spiVisitante);
        edtFechaJuego= findViewById(R.id.etFechaJuego);
        edtHoraJuego= findViewById(R.id.etHoraDeJuego);
        edtFechaCompetencia= findViewById(R.id.etFechaCompetencia);
        btnNuevoPartido= findViewById(R.id.buttonNuevoPartido);

        cargarSpinners();

        edtFechaJuego.setOnClickListener(new View.OnClickListener() {
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

                        edtFechaJuego.setText(dia+"-"+mes+"-"+year);
                    }
                }
                        , anio, mes, dia);
                datePickerDialog.show();
            }
        });

        edtHoraJuego.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c= Calendar.getInstance();
                hora= c.get(Calendar.HOUR);
                minuto= c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog= new TimePickerDialog(view.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hora, int minuto) {
                        int cantCifrasHora= contarCifras(hora);
                        int cantCifrasMinuto= contarCifras(minuto);

                        String horaString, minutoString;

                        if(cantCifrasHora<2)
                            horaString= "0"+hora;
                        else
                            horaString= String.valueOf(hora);

                        if(cantCifrasMinuto<2)
                            minutoString= "0"+minuto;
                        else
                            minutoString= String.valueOf(minuto);

                        edtHoraJuego.setText(horaString+":"+minutoString);
                    }
                }, hora, minuto,true);

                timePickerDialog.show();
            }
        });

        btnNuevoPartido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Equipo local= (Equipo) spiLocal.getSelectedItem();
                Equipo visitante= (Equipo) spiVisitante.getSelectedItem();

                if(local.equals(visitante)){
                    visitante= null;
                }

                if(edtFechaCompetencia.getText() == null || edtFechaCompetencia.getText().toString().equals("")){
                    edtFechaCompetencia.setError("El campo es obligatorio");
                }

                Partido partido= new Partido();
                partido.setFechaCompetencia(Integer.parseInt(edtFechaCompetencia.getText().toString()));
                String fechaJuego= edtFechaJuego.getText().toString();
                String horaJuego=edtHoraJuego.getText().toString();
                if(edtFechaJuego.getText().toString().equals("") && edtHoraJuego.getText().toString().equals("")){
                    partido.setFechaJuego(null);
                }
                else{
                    partido.setFechaJuego(edtFechaJuego.getText().toString()+" "+edtHoraJuego.getText().toString());
                }

                partido.setIdCompetencia(idCompetencia);
                partido.setLocal(local);
                partido.setVisitante(visitante);

                crearPartido(partido);
            }
        });

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

    private void cargarSpinners(){
        Runnable r= new Runnable() {
            @Override
            public void run() {
                EquipoDAO equipoDAO= RestClient.getInstance().getRetrofit().create(EquipoDAO.class);
                Call<List<Equipo>> callEquipos= equipoDAO.getAll("Bearer "+accessToken);
                try{
                    Response<List<Equipo>> response= callEquipos.execute();
                    Message mensaje;
                    switch(response.code()){
                        case 200:
                            listaEquipos= response.body();
                            mensaje= handler.obtainMessage(EQUIPOS_CARGADOS);
                            mensaje.sendToTarget();
                            break;
                        default:
                            mensaje= handler.obtainMessage(ERROR);
                            mensaje.sendToTarget();
                            break;
                    }

                }
                catch (SocketTimeoutException e){
                    Message mensaje= handler.obtainMessage(EQUIPOS_CARGADOS);
                    mensaje.sendToTarget();
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
        };
        Thread t= new Thread(r);
        t.start();

    }

    private void crearPartido(Partido partido){
        Runnable r= new Runnable() {
            @Override
            public void run() {
                PartidoDAO partidoDAO= RestClient.getInstance().getRetrofit().create(PartidoDAO.class);
                Call<ResponseBackend> callPartido= partidoDAO.crearPartido(partido, "Bearer "+accessToken);
                try {
                    Response<ResponseBackend> response= callPartido.execute();

                    String mensajeServidor= "";
                    Message mensaje;
                    switch(response.code()){
                        case 200:
                            mensajeServidor= response.body().getMensaje();
                            mensaje= handler.obtainMessage(PARTIDO_CREADO_CON_EXITO, mensajeServidor);
                            mensaje.sendToTarget();
                            break;
                        case 403:
                            mensaje= handler.obtainMessage(PARTIDO_CREADO_CON_EXITO);
                            break;
                        case 422:
                            mensajeServidor= response.errorBody().string();
                            mensaje= handler.obtainMessage(YA_EXISTE_PARTIDO, mensajeServidor);
                            mensaje.sendToTarget();
                            break;
                        case 404:
                        case 500:
                            mensaje= handler.obtainMessage(ERROR);
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
        public void handleMessage(Message msg) {
            AlertDialog alertDialog= null;
            switch (msg.what){
                case EQUIPOS_CARGADOS:
                    ArrayAdapter<Equipo> arrayAdapterLocal= new ArrayAdapter<>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, listaEquipos);
                    ArrayAdapter<Equipo> arrayAdapterVisitante= new ArrayAdapter<>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, listaEquipos);
                    spiLocal.setAdapter(arrayAdapterLocal);
                    spiVisitante.setAdapter(arrayAdapterVisitante);
                    break;
                case PARTIDO_CREADO_CON_EXITO:
                    alertDialog= new AlertDialog.Builder(NuevoPartido.this).create();
                    alertDialog.setTitle("EXITO");
                    alertDialog.setMessage((String) msg.obj);
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent i= new Intent(getApplicationContext(), PrincipalActivity.class);
                                    i.putExtra("accessToken", accessToken);
                                    startActivity(i);
                                }
                            });
                    alertDialog.show();
                    break;
                case YA_EXISTE_PARTIDO:
                    alertDialog= new AlertDialog.Builder(NuevoPartido.this).create();
                    alertDialog.setTitle("Partido ya existe");
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
                case ERROR:
                    alertDialog= new AlertDialog.Builder(NuevoPartido.this).create();
                    alertDialog.setTitle("Error de Conexión");
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