package model;

public class Person {

    private String personalNr;
    private String kreis;
    private String nachName;
    private String vorName;
    private String kostenstelle;

    public Person(String personalNr, String kreis, String nachName, String vorName, String kostenstelle) {
        this.personalNr = personalNr;
        this.kreis = kreis;
        this.nachName = nachName;
        this.vorName = vorName;
        this.kostenstelle = kostenstelle;
    }

    public String getPersonalNr() {
        return personalNr;
    }

    public void setPersonalNr(String personalNr) {
        this.personalNr = personalNr;
    }

    public String getKreis() {
        return kreis;
    }

    public void setKreis(String kreis) {
        this.kreis = kreis;
    }

    public String getNachName() {
        return nachName;
    }

    public void setNachName(String nachName) {
        this.nachName = nachName;
    }

    public String getVorName() {
        return vorName;
    }

    public void setVorName(String vorName) {
        this.vorName = vorName;
    }

    public String getKostenstelle() {
        return kostenstelle;
    }

    public void setKostenstelle(String kostenstelle) {
        this.kostenstelle = kostenstelle;
    }
}
