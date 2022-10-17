package model;

public class Stempelung {

    private String datum;
    private String sollzeit;
    private String anwesend;
    private String saldo;
    private String diere;
    private String plan;

    private Person person;

    public Stempelung(String datum, String sollzeit, String anwesend, String saldo, String diere, String plan, Person person) {
        this.datum = datum;
        this.sollzeit = sollzeit;
        this.anwesend = anwesend;
        this.saldo = saldo;
        this.diere = diere;
        this.plan = plan;
        this.person = person;
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public String getSollzeit() {
        return sollzeit;
    }

    public void setSollzeit(String sollzeit) {
        this.sollzeit = sollzeit;
    }

    public String getAnwesend() {
        return anwesend;
    }

    public void setAnwesend(String anwesend) {
        this.anwesend = anwesend;
    }

    public String getSaldo() {
        return saldo;
    }

    public void setSaldo(String saldo) {
        this.saldo = saldo;
    }

    public String getDiere() {
        return diere;
    }

    public void setDiere(String diere) {
        this.diere = diere;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
