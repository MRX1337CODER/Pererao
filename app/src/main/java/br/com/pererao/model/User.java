package br.com.pererao.model;

public class User {

    private String id;
    private String nomeUser;
    private String emailUser;
    private String senhaUser;
    private String userUrl;
    private String status;
    private String search;
    private float rating;

    public User() {}

    public User(String id, String nomeUser, String emailUser, String senhaUser, String userUrl, String status, String search, float rating) {
        this.id = id;
        this.nomeUser = nomeUser;
        this.emailUser = emailUser;
        this.senhaUser = senhaUser;
        this.userUrl = userUrl;
        this.status = status;
        this.search = search;
        this.rating = rating;
    }

    public User(String nomeUser, String emailUser) {
        this.nomeUser = nomeUser;
        this.emailUser = emailUser;
    }

    public User(String nomeUser, String emailUser, String id) {
        this.nomeUser = nomeUser;
        this.emailUser = emailUser;
        this.id = id;
    }

    //Configurar o construtor para o cliente e para o prestador depois... aqui está só o básico
    public User(String nomeUser, String emailUser, String senhaUser, String userUrl) {
        this.nomeUser = nomeUser;
        this.emailUser = emailUser;
        this.senhaUser = senhaUser;
        this.userUrl = userUrl;
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

    public String getSenhaUser() {
        return senhaUser;
    }

    public void setSenhaUser(String senhaUser) {
        this.senhaUser = senhaUser;
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
}
