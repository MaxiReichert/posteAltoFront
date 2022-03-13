package posteAltoMovile.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import posteAlto.postealtomovile.R;
import posteAltoMovile.fragment.MenuPrincipal;
import posteAltoMovile.fragment.SeguiTuEquipo;

public class PrincipalActivity extends AppCompatActivity implements MenuPrincipal.OnSeguiTuEquipoListener {

    private DrawerLayout drawerLayout;
    private NavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navView = (NavigationView) findViewById(R.id.navview);
        navView.setItemIconTintList(null);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
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
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if(fragment == null){
            fragment = new SeguiTuEquipo();
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contenido, fragment, tag)
                .addToBackStack(null)
                .show(fragment)
                .commit();
    }
}