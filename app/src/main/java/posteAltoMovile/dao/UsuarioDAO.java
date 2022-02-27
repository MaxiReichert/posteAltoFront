package posteAltoMovile.dao;

import posteAltoMovile.model.ResponseBackend;
import posteAltoMovile.model.Usuario;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UsuarioDAO {

    @POST("usuario")
    Call<ResponseBackend> nuevoUsuario(@Body Usuario usuario);

    @PATCH("usuario")
    Call<ResponseBackend> actualizarUsuario(@Body Usuario usuario, @Header("Authorization") String auth);

    @DELETE("usuario/{id}")
    Call<ResponseBackend> borrarUsuario(@Path("id") Integer id, @Header("Authorization") String auth);

    @GET("usuario/buscarEmail/{email}")
    Call<ResponseBackend> buscarPorEmail(@Path("email") String email);
}
