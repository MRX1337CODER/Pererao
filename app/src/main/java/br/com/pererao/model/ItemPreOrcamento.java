package br.com.pererao.model;

public class ItemPreOrcamento {

    private String receiver;
    private String sender;
    private String item;
    private String keyPreOrc;
    private int qtdeItem;
    private double valorItem;
    private double subTotal;

    public ItemPreOrcamento() {
    }

    public ItemPreOrcamento(String item, int qtdeItem, double valorItem, String keyPreOrc, double subTotal, String receiver, String sender) {
        this.item = item;
        this.qtdeItem = qtdeItem;
        this.valorItem = valorItem;
        this.keyPreOrc = keyPreOrc;
        this.subTotal = subTotal;
        this.receiver = receiver;
        this.sender = sender;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public int getQtdeItem() {
        return qtdeItem;
    }

    public void setQtdeItem(int qtdeItem) {
        this.qtdeItem = qtdeItem;
    }

    public double getValorItem() {
        return valorItem;
    }

    public void setValorItem(double valorItem) {
        this.valorItem = valorItem;
    }

    public String getKeyPreOrc() {
        return keyPreOrc;
    }

    public void setKeyPreOrc(String keyPreOrc) {
        this.keyPreOrc = keyPreOrc;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }


    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}