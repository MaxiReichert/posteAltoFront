package posteAltoMovile.dao;

import posteAltoMovile.model.Competencia;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface CompetenciaDAO {

    @GET("competencia")
    Call<Competencia> getUltimaCompetencia(@Header("Authorization") String auth);
}
