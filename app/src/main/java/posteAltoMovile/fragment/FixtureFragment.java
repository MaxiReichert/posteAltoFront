package posteAltoMovile.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import posteAlto.postealtomovile.R;
import posteAltoMovile.activity.NuevoPartido;
import posteAltoMovile.activity.PrincipalActivity;
import posteAltoMovile.adapter.FechaAdapter;
import posteAltoMovile.dao.PartidoDAO;
import posteAltoMovile.model.Equipo;
import posteAltoMovile.model.Partido;
import posteAltoMovile.model.ResponseBackend;
import posteAltoMovile.retroFitClient.Constants;
import posteAltoMovile.retroFitClient.RestClient;
import retrofit2.Call;
import retrofit2.Response;

public class FixtureFragment extends Fragment {

    private final static int PARTIDOS_OBTENIDOS= 1;
    private final static int FECHAS_OBTENIDAS= 2;
    private final static int ERROR_DE_SERVIDOR= 3;

    private String accessToken;
    private TextView txtFecha;
    private TextView txtEquipoLibre;
    private RecyclerView rvFecha;
    private ImageView ivPrevious;
    private ImageView ivNext;
    private List<Partido> partidos;
    private Equipo equipoLibre;
    private FechaAdapter fechaAdapter;
    private Integer cantFechas;
    private Button btnAgrearPartido;
    private JSONObject datosUsuarioJSON=null;
    private Integer idCompetencia;

    public FixtureFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        idCompetencia= getArguments().getInt("idCompetencia");
        accessToken= getArguments().getString("accessToken");

        View v= inflater.inflate(R.layout.fragment_fixture, container, false);
        btnAgrearPartido= v.findViewById(R.id.buttonAgrearPartido);
        String[] datosUsuario= accessToken.split("\\.");
        String body= new String(Base64.decode(datosUsuario[1], Base64.NO_WRAP));
        try {
            datosUsuarioJSON= new JSONObject(body);
            if(!datosUsuarioJSON.get("rol").equals(Constants.ROL_ADMIN)){
                btnAgrearPartido.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        txtFecha= v.findViewById(R.id.txtFecha);
        txtEquipoLibre= v.findViewById(R.id.txtEquipoLibre);
        rvFecha= v.findViewById(R.id.rvFecha);
        ivPrevious= v.findViewById(R.id.ivPrevious);
        ivNext= v.findViewById(R.id.ivNext);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvFecha.setLayoutManager(llm);
        buscarPartidos(idCompetencia, 1);
        buscarCantFechas(idCompetencia);

        ivPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buscarPartidos(idCompetencia, partidos.get(0).getFechaCompetencia()-1);
            }
        });

        ivNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buscarPartidos(idCompetencia, partidos.get(0).getFechaCompetencia()+1);
            }
        });

        btnAgrearPartido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(getContext(), NuevoPartido.class);
                i.putExtra("idCompetencia", idCompetencia);
                i.putExtra("accessToken", accessToken);
                startActivity(i);
            }
        });

        return v;
    }

    private void determinarEquipoLibre(){

        Optional<Partido> partidoDummy= partidos.stream().filter(partido ->
                partido.getLocal()== null || partido.getVisitante() == null).findAny();

        if(partidoDummy.isPresent()){
            partidos.remove(partidoDummy.get());
            if(partidoDummy.get().getLocal() == null)
                equipoLibre= partidoDummy.get().getVisitante();
            else
                equipoLibre= partidoDummy.get().getLocal();
        }
        else{
            equipoLibre= null;
        }


        finalizarCreacionDeCompoente();
    }

    private void finalizarCreacionDeCompoente(){
        if(equipoLibre == null)
            txtEquipoLibre.setText("");
        else
            txtEquipoLibre.setText("LIBRE: "+equipoLibre.getNombre());

        if(partidos.get(0).getFechaCompetencia() == 1){
            ivPrevious.setEnabled(false);
        }
        else if(partidos.get(0).getFechaCompetencia() == cantFechas){
            ivNext.setEnabled(false);
        }
        else{
            ivPrevious.setEnabled(true);
            ivNext.setEnabled(true);
        }

        fechaAdapter= new FechaAdapter(partidos);
        rvFecha.setAdapter(fechaAdapter);
    }

    private void buscarPartidos(Integer idCompetencia, Integer idFecha){
        Runnable r= new Runnable() {
            @Override
            public void run() {
                PartidoDAO partidoDAO= RestClient.getInstance().getRetrofit().create(PartidoDAO.class);
                Call<List<Partido>> callGetPartidos= partidoDAO.buscarPorFechaYCompetencia(idFecha, idCompetencia, "Bearer "+accessToken);
                try{
                    Message mensaje;
                    Response<List<Partido>> response= callGetPartidos.execute();
                    switch (response.code()){
                        case 200:
                            mensaje= handler.obtainMessage(PARTIDOS_OBTENIDOS, response.body());
                            mensaje.sendToTarget();
                            break;
                        case 404:
                        case 500:
                            mensaje= handler.obtainMessage(ERROR_DE_SERVIDOR);
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

    private void buscarCantFechas(Integer idCompetencia){
        Runnable r= new Runnable() {
            @Override
            public void run() {
                PartidoDAO partidoDAO= RestClient.getInstance().getRetrofit().create(PartidoDAO.class);
                Call<ResponseBackend> callGetFechas= partidoDAO.buscarCantFechas(idCompetencia, "Bearer "+accessToken);
                try{
                    Message mensaje;
                    Response<ResponseBackend> response= callGetFechas.execute();
                    switch (response.code()){
                        case 200:
                            mensaje= handler.obtainMessage(FECHAS_OBTENIDAS, response.body());
                            mensaje.sendToTarget();
                            break;
                        case 404:
                        case 500:
                            mensaje= handler.obtainMessage(ERROR_DE_SERVIDOR);
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
                case PARTIDOS_OBTENIDOS:
                    partidos= (List<Partido>) msg.obj;
                    if(partidos.size() == 0){
                        alertDialog= new AlertDialog.Builder(getContext()).create();
                        alertDialog.setTitle("No se ha fixturado aún");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            if(!datosUsuarioJSON.get("rol").equals(Constants.ROL_ADMIN)){
                                                Intent i = new Intent(getContext(), PrincipalActivity.class);
                                                i.putExtra("accessToken", accessToken);
                                                i.putExtra("refreshToken", "");
                                                startActivity(i);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                        break;
                    }
                    else {
                        txtFecha.setText("FECHA " + partidos.get(0).getFechaCompetencia());
                        determinarEquipoLibre();
                    }
                    break;
                case FECHAS_OBTENIDAS:
                    ResponseBackend responseBackend= (ResponseBackend) msg.obj;
                    cantFechas= Integer.parseInt(responseBackend.getMensaje());
                    break;
                case ERROR_DE_SERVIDOR:
                    alertDialog= new AlertDialog.Builder(getContext()).create();
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