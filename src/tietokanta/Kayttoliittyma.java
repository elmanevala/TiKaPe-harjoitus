package tietokanta;

import java.util.Scanner;
import java.sql.*;
import java.util.*;

public class Kayttoliittyma {

    private Kaskyt kaskyt;
    private Scanner lukija;

    public Kayttoliittyma() throws SQLException {
        this.kaskyt = new Kaskyt();
        this.lukija = new Scanner(System.in);
    }

    public void kaynnista() throws SQLException {
        // Tulostaa käytettävissä olevat toiminnot ja 
        // suorittaa toiminnon käyttäjän syöttämän luvun mukaan.
        // Tulostaa virheviestin, jos toimintoa ei ole olemassa.

        System.out.println("Tervetuloa käyttämään tietokantatunkeroa!");

        System.out.println("Käytettävissä olevat komennot: ");
        System.out.println("");

        System.out.println("1: Luo tietokanta");
        System.out.println("2: Lisää uusi paikka");
        System.out.println("3: Lisää uusi asiakas");
        System.out.println("4: Lisää uusi paketti");
        System.out.println("5: Lisää uusi tapahtuma");
        System.out.println("6: Hae tietyn paketin tapahtumat");
        System.out.println("7: Hae tietyn asiakkaan paketit");
        System.out.println("8: Hae tietyn paikan tapahtumien määrä");
        System.out.println("9: Suorita tehokkuustesti ilman indeksejä");
        System.out.println("10: Suorita tehokkuustesti indekseillä");
        System.out.println("lopeta: Lopettaa ohjelman");
        System.out.println("");

        while (true) {
            System.out.print("Valitse toiminto (1–9 tai lopeta): ");
            String kasky = this.lukija.nextLine();

            if (kasky.equals("1")) {
                this.kaskyt.luoTaulut();
            } else if (kasky.equals("2")) {
                this.kaskyt.uusiPaikka();
            } else if (kasky.equals("3")) {
                this.kaskyt.uusiAsiakas();
            } else if (kasky.equals("4")) {
                this.kaskyt.uusiPaketti();
            } else if (kasky.equals("5")) {
                this.kaskyt.uusiTapahtuma();
            } else if (kasky.equals("6")) {
                this.kaskyt.paketinTapahtumat();
            } else if (kasky.equals("7")) {
                this.kaskyt.asiakkaanPaketit();
            } else if (kasky.equals("8")) {
                this.kaskyt.paikanTapahtumat();
            } else if (kasky.equals("9")) {
                Tehokkuustesti tehokkuustesti = new Tehokkuustesti();
                tehokkuustesti.testienSuoritus(false);
            } else if (kasky.equals("10")) {
                Tehokkuustesti tehokkuustesti = new Tehokkuustesti();
                tehokkuustesti.testienSuoritus(true);
            } else if (kasky.equals("lopeta")) {
                break;
            } else {
                System.out.println("    VIRHE: toimintoa ei olemassa");
            }

            System.out.println("");

        }

        System.out.println("");
        System.out.println("Kiitos käynnistä ja tervetuloa uudelleen!");

    }

}
