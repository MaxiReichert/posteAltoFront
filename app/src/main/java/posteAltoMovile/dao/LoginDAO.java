package posteAltoMovile.dao;

import posteAltoMovile.model.ResponseLogin;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface LoginDAO {

    @FormUrlEncoded
    @POST("login")
    Call<ResponseLogin> login(@Field("username") String username, @Field("password") String password);
}
