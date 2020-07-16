package br.com.pererao.model;

public class Qualifications {

    private String desc;
    private String cod;
    private boolean isSelected;

    public Qualifications() {}

    public Qualifications(String desc, String cod) {
        this.desc = desc;
        this.cod = cod;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}