package posteAltoMovile.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import posteAlto.postealtomovile.R;
import posteAltoMovile.adapter.SeguiTuEquipoAdapter;
import posteAltoMovile.dao.EquipoDAO;
import posteAltoMovile.model.Equipo;
import posteAltoMovile.retroFitClient.RestClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SeguiTuEquipo extends Fragment {

    static final int EQUIPOS_CARGADOS=1;
    static final int ERROR=2;
    ListView lvSeguiEquipo;
    List<Equipo> listaEquipos= new ArrayList<Equipo>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_segui_tu_equipo, container, false);

        buscarEquipos();
        lvSeguiEquipo = (ListView) v.findViewById(R.id.lvSegui);

        return v;
    }

    private void buscarEquipos(){
        Runnable r= new Runnable() {
            @Override
            public void run() {
                EquipoDAO equipoDAO= RestClient.getInstance().getRetrofit().create(EquipoDAO.class);
                Call<List<Equipo>> callEquipos= equipoDAO.getAll();
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

    Handler handler= new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case EQUIPOS_CARGADOS:
                    lvSeguiEquipo.setAdapter(new SeguiTuEquipoAdapter(getContext(),listaEquipos));
                    break;
                case ERROR:
                    AlertDialog alertDialog= new AlertDialog.Builder(getContext()).create();
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