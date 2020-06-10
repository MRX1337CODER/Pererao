package br.com.pererao.retrofit;

import br.com.pererao.services.CepService;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;


public class RetrofitInitializer {
    private final Retrofit retrofit;

    public RetrofitInitializer() {
        this.retrofit = new Retrofit.Builder()
                .baseUrl("https://viacep.com.br/ws/")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }

    public CepService getCepService() {
        return this.retrofit.create(CepService.class);
    }
}