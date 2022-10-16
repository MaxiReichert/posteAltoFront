package posteAltoMovile.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import java.io.IOException;
import java.util.List;

import posteAlto.postealtomovile.R;
import posteAltoMovile.adapter.TablaDePosicionesAdapter;
import posteAltoMovile.dao.TablaDePosicionesDAO;
import posteAltoMovile.model.FilaTabla;
import posteAltoMovile.retroFitClient.RestClient;
import retrofit2.Call;
import retrofit2.Response;


public class TablaDePosicionesFragment extends Fragment {

    private final int FILAS_OBTENIDAS= 1;
    private final int ERROR_DE_SERVIDOR= 2;

    private String accessToken;
    private Integer idCompetencia;
    private TableLayout lvTablaPosiciones;
    private List<FilaTabla> filaTablaList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_tabla_de_posiciones, container, false);

        accessToken= getArguments().getString("accessToken");
        Integer idCompetencia= getArguments().getInt("idCompetencia");

        //lvTablaPosiciones= v.findViewById(R.id.lvTabla);
        lvTablaPosiciones= v.findViewById(R.id.TablaPosiciones);
        getTablaDePosiciones(idCompetencia);

        return v;
    }

    private void cargarTabla(){
        TablaDePosicionesAdapter adapter= new TablaDePosicionesAdapter();
        for(int position=0; position<filaTablaList.size(); position++){
            View fila= adapter.getView(filaTablaList.get(position), position, getContext());
            lvTablaPosiciones.addView(fila);
        }
    }

    private void getTablaDePosiciones(Integer idCompetencia){
        Runnable r= new Runnable() {
            @Override
            public void run() {
                TablaDePosicionesDAO tablaDePosicionesDAO= RestClient.getInstance().getRetrofit().create(TablaDePosicionesDAO.class);
                Call<List<FilaTabla>> callGetTablaDePosiciones= tablaDePosicionesDAO.getTablaDePosiciones(idCompetencia,
                        "Bearer "+accessToken);
                try {
                    Message mensaje;
                    Response<List<FilaTabla>> response = callGetTablaDePosiciones.execute();

                    switch (response.code()){
                        case 200:
                            mensaje= handler.obtainMessage(FILAS_OBTENIDAS, response.body());
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

    Handler handler= new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            AlertDialog alertDialog;
            switch (msg.what) {
                case FILAS_OBTENIDAS:
                    filaTablaList = (List<FilaTabla>) msg.obj;
                    cargarTabla();
                    break;
                case ERROR_DE_SERVIDOR:
                    alertDialog = new AlertDialog.Builder(getContext()).create();
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