package br.com.pererao.services;

import br.com.pererao.model.Cep;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CepService {

    @GET("{cep}/json")
    Call<Cep> buscarCEP(@Path("cep") String cep);

}

