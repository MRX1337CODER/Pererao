package br.com.pererao.notifications;

public class Sender {
    public Data data;
    public String to;

    public Sender() {
    }

    public Sender(Data data, String to) {
        this.data = data;
        this.to = to;
    }
}
