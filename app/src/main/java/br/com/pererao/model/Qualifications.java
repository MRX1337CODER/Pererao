package br.com.pererao.model;

public class Qualifications {

    private String desc;
    private boolean isSelected;

    public Qualifications() {}

    public Qualifications(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}