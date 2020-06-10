package br.com.pererao.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"codibge", "codestado", "unidade", "ibge", "gia", "complemento"})

public class Cep {
    private String cep;
    private String logradouro;
    private String bairro;
    private String localidade;
    private String uf;

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getLocalidade() {
        return localidade;
    }

    public void setLocalidade(String localidade) {
        this.localidade = localidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    @Override
    public String toString() {
        return "CEP: " + getCep()
                + "\nLogradouro: " + getLogradouro()
                + "\nBairro: " + getBairro()
                + "\nCidade:" + getLocalidade()
                + "\nEstado: " + getUf();
    }
}
