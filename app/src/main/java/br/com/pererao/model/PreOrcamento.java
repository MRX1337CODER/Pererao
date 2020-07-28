package br.com.pererao.model;

public class PreOrcamento {

    private String cliente;
    private String prestador;
    private String obeservation;
    private double dataInicio;
    private double dataFinal;
    private double totalValor;

    public PreOrcamento() {}

    public PreOrcamento(String cliente, String prestador, double dataInicio, double dataFinal, double totalValor, String obeservation) {
        this.cliente = cliente;
        this.prestador = prestador;
        this.dataInicio = dataInicio;
        this.dataFinal = dataFinal;
        this.totalValor = totalValor;
        this.obeservation = obeservation;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getPrestador() {
        return prestador;
    }

    public void setPrestador(String prestador) {
        this.prestador = prestador;
    }

    public double getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(double dataInicio) {
        this.dataInicio = dataInicio;
    }

    public double getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(double dataFinal) {
        this.dataFinal = dataFinal;
    }

    public double getTotalValor() {
        return totalValor;
    }

    public void setTotalValor(double totalValor) {
        this.totalValor = totalValor;
    }

    public String getObeservation() {
        return obeservation;
    }

    public void setObeservation(String obeservation) {
        this.obeservation = obeservation;
    }

}