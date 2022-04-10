package posteAltoMovile.dao;

import java.util.List;

import posteAltoMovile.model.Equipo;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface EquipoDAO {

    @GET("equipo")
    Call<List<Equipo>> getAll(@Header("Authorization") String auth);
}
