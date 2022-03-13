package posteAltoMovile.dao;

import java.util.List;

import posteAltoMovile.model.Equipo;
import retrofit2.Call;
import retrofit2.http.GET;

public interface EquipoDAO {

    @GET("equipo")
    Call<List<Equipo>> getAll();
}
