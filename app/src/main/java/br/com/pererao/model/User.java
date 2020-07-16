package br.com.pererao.model;

import java.util.List;

public class User {

    private String id;
    private String nomeUser;
    private String emailUser;
    private String userUrl;
    private String status;
    private String search;
    private float rating;
    private boolean isPrestador;
    private int size;
    private List<String> list;

    public User() {
    }

    public User(String id, String nomeUser, String emailUser, String userUrl, String status, String search, float rating, boolean isPrestador, int size, List<String> list) {
        this.id = id;
        this.nomeUser = nomeUser;
        this.emailUser = emailUser;
        this.userUrl = userUrl;
        this.status = status;
        this.search = search;
        this.isPrestador = isPrestador;
        this.rating = rating;
        this.size = size;
        this.list = list;
    }

    public String getNomeUser() {
        return nomeUser;
    }

    public void setNomeUser(String nomeUser) {
        this.nomeUser = nomeUser;
    }

    public String getEmailUser() {
        return emailUser;
    }

    public void setEmailUser(String emailUser) {
        this.emailUser = emailUser;
    }

    public String getUserUrl() {
        return userUrl;
    }

    public void setUserUrl(String userUrl) {
        this.userUrl = userUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public boolean isPrestador() {
        return isPrestador;
    }

    public void setPrestador(boolean prestador) {
        isPrestador = prestador;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }
}
