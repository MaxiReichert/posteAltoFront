package posteAltoMovile.dao;

import java.util.List;

import posteAltoMovile.model.FilaTabla;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface TablaDePosicionesDAO {

    @GET("tablaDePosiciones/{idCompetencia}")
    Call<List<FilaTabla>> getTablaDePosiciones(@Path("idCompetencia") Integer idCompetencia,
                                                      @Header("Authorization") String auth);
}
