package posteAltoMovile.dao;

import java.util.List;

import posteAltoMovile.model.Partido;
import posteAltoMovile.model.ResponseBackend;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PartidoDAO {

    @GET("partido/buscarPorFechaYCompetencia")
    Call<List<Partido>> buscarPorFechaYCompetencia(@Query("fecha") Integer fecha,
                                                   @Query("competencia") Integer competencia,
                                                   @Header("Authorization") String auth);

    @GET("partido/{idCompetencia}")
    Call<ResponseBackend> buscarCantFechas(@Path("idCompetencia") Integer idCompetencia,
                                       @Header("Authorization") String auth);

    @POST("partido")
    Call<ResponseBackend> crearPartido(@Body Partido partido,@Header("Authorization") String auth);
}
