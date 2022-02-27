package posteAltoMovile.retroFitClient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import posteAlto.postealtomovile.R;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {

    private static RestClient _INSTANCIA_UNICA = null;
    private Retrofit retrofit;

    public static RestClient getInstance() {
        if (_INSTANCIA_UNICA == null) _INSTANCIA_UNICA = new RestClient();
        return _INSTANCIA_UNICA;
    }

    private RestClient() {
        Gson gson = new GsonBuilder().setLenient().create();
        retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create(gson)).build();
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public void setRetrofit(Retrofit retrofit) {
        this.retrofit = retrofit;
    }
}
