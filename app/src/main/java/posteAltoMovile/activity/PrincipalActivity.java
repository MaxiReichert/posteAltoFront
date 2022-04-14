package posteAltoMovile.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import java.io.IOException;

import posteAlto.postealtomovile.R;
import posteAltoMovile.Listener.OnMostrarFixtureListener;
import posteAltoMovile.Listener.OnMostrarTablaDePosiconesListener;
import posteAltoMovile.Listener.OnSeguiTuEquipoListener;
import posteAltoMovile.dao.CompetenciaDAO;
import posteAltoMovile.fragment.FixtureFragment;
import posteAltoMovile.fragment.MenuPrincipal;
import posteAltoMovile.fragment.SeguiTuEquipo;
import posteAltoMovile.fragment.TablaDePosicionesFragment;
import posteAltoMovile.model.Competencia;
import posteAltoMovile.retroFitClient.RestClient;
import retrofit2.Call;
import retrofit2.Response;

public class PrincipalActivity extends AppCompatActivity implements OnSeguiTuEquipoListener,
        OnMostrarFixtureListener, OnMostrarTablaDePosiconesListener {

    private final static int COMPETENCIA_ENCONTADA=1;
    private final static int COMPETENCIA_NO_ENCONTRADA=2;
    private final static int ERROR_DE_SERVIDOR=3;

    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private Competencia competencia;
    private String accessToken;
    private String refreshToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        accessToken= (String) getIntent().getSerializableExtra("accessToken");
        refreshToken= (String) getIntent().getSerializableExtra("refreshToken");

        setContentView(R.layout.activity_principal);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navView = (NavigationView) findViewById(R.id.navview);
        navView.setItemIconTintList(null);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        buscarCompetencia();
        
        cargarMenuPrincipal();

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                boolean fragmentTransaction = false;
                Fragment fragment = null;
                String tag = "";

                switch(item.getItemId()){
                    case R.id.optInicio:
                        tag = "menu";
                        fragment = getSupportFragmentManager().findFragmentByTag(tag);
                        if (fragment == null) {
                            fragment = new MenuPrincipal();
                        }
                        fragmentTransaction = true;
                        break;
                    case R.id.optResultados:
                        tag = "fixture";
                        Bundle args= new Bundle();
                        args.putString("accessToken", accessToken);
                        args.putInt("idCompetencia", competencia.getId());
                        fragment = getSupportFragmentManager().findFragmentByTag(tag);
                        if (fragment == null) {
                            fragment = new FixtureFragment();
                        }
                        fragment.setArguments(args);
                        fragmentTransaction = true;
                }
                if (fragmentTransaction) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.contenido, fragment, tag)
                            .addToBackStack(null)
                            .show(fragment)
                            .commit();

                    item.setChecked(true);
                }
                drawerLayout.closeDrawers();

                return true;
            }
        });
    }

    private void cargarMenuPrincipal() {
        String tag = "menu";
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment == null) {
            fragment = new MenuPrincipal();
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contenido, fragment, tag)
                .addToBackStack(null)
                .show(fragment)
                .commit();
    }

    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();

        if(count > 1)
            getSupportFragmentManager().popBackStack();
    }


    @Override
    public void mostrarSeguiTuEquipo() {
        String tag = "seguiTuEquipo";
        Bundle args= new Bundle();
        args.putString("accessToken", accessToken);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if(fragment == null){
            fragment = new SeguiTuEquipo();
        }

        fragment.setArguments(args);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contenido, fragment, tag)
                .addToBackStack(null)
                .show(fragment)
                .commit();
    }

    @Override
    public void mostrarFixture() {
        String tag = "fixture";
        Bundle args= new Bundle();
        args.putString("accessToken", accessToken);
        args.putInt("idCompetencia", competencia.getId());
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if(fragment == null){
            fragment = new FixtureFragment();
        }

        fragment.setArguments(args);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contenido, fragment, tag)
                .addToBackStack(null)
                .show(fragment)
                .commit();
    }

    @Override
    public void mostrarTablaDePosicones() {
        String tag= "tablaPosiciones";
        Bundle args= new Bundle();
        args.putString("accessToken", accessToken);
        args.putInt("idCompetencia", competencia.getId());
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if(fragment == null){
            fragment = new TablaDePosicionesFragment();
        }

        fragment.setArguments(args);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contenido, fragment, tag)
                .addToBackStack(null)
                .show(fragment)
                .commit();
    }

    private void buscarCompetencia(){
        Runnable r= new Runnable() {
            @Override
            public void run() {
                CompetenciaDAO competenciaDAO= RestClient.getInstance().getRetrofit().create(CompetenciaDAO.class);
                Call<Competencia> callUltimaCompetencia= competenciaDAO.getUltimaCompetencia("Bearer "+accessToken);
                try{
                    Message mensaje;
                    Response<Competencia> response= callUltimaCompetencia.execute();
                    switch(response.code()){
                        case 200:
                            mensaje= handler.obtainMessage(COMPETENCIA_ENCONTADA, response.body());
                            mensaje.sendToTarget();
                            break;
                        case 422:
                            mensaje= handler.obtainMessage(COMPETENCIA_NO_ENCONTRADA);
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
                case COMPETENCIA_ENCONTADA:
                    competencia = (Competencia) msg.obj;
                    break;
                case COMPETENCIA_NO_ENCONTRADA:
                    alertDialog= new AlertDialog.Builder(getApplicationContext()).create();
                    alertDialog.setTitle("No hay competencias disponibles");
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
                    alertDialog= new AlertDialog.Builder(getApplicationContext()).create();
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