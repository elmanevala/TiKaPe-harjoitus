/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tietokanta;

import java.util.Scanner;
import java.sql.*;

/**
 *
 * @author elmaneva
 */
public class Kaskyt {

    private Connection db;
    private Scanner lukija;

    public Kaskyt() throws SQLException {
        this.lukija = new Scanner(System.in);
        this.db = DriverManager.getConnection("jdbc:sqlite:testi.db");
    }

    public void luoTaulut() throws SQLException {
        Statement s = db.createStatement();

        s.execute("PRAGMA foreign_keys = ON");

        s.execute("CREATE TABLE IF NOT EXISTS Asiakkaat (id INTEGER PRIMARY KEY, nimi TEXT NOT NULL, UNIQUE(nimi))");
        s.execute("CREATE TABLE IF NOT EXISTS Paketit (id INTEGER PRIMARY KEY, numero INTEGER, asiakas_id INTEGER REFRENCES Asiakkaat NOT NULL, UNIQUE(numero))");
        s.execute("CREATE TABLE IF NOT EXISTS Paikat (id INTEGER PRIMARY KEY, paikka TEXT, UNIQUE(paikka))");
        s.execute("CREATE TABLE IF NOT EXISTS Tapahtumat (id INTEGER PRIMARY KEY, kuvaus TEXT, paketti_id INTEGER REFRENCES Paketit, paikka_id INTEGER REFRECES Paikat NOT NULL, pvm TEXT, aika TEXT)");

        System.out.println("    Tietokanta olemassa");
        // luodaan tarvittavat taulut
        // tauluja neljä: asiakkaat, paketit, tapahtumat, paikat

    }

    public void uusiPaikka() throws SQLException {
        Statement s = db.createStatement();

        System.out.println("Uuden paikan lisäys");

        System.out.print("    Anna paikan nimi: ");
        String paikka = this.lukija.nextLine();

        try {
            PreparedStatement p1 = db.prepareStatement("INSERT OR ABORT INTO Paikat(paikka) VALUES (?)");
            p1.setString(1, paikka);

            p1.executeUpdate();
            System.out.println("    Paikka lisätty");

        } catch (SQLException e) {
            System.out.println("    VIRHE: Paikka on jo tietokannassa");
        }

    }

    public void uusiAsiakas() throws SQLException {
        System.out.println("Uuden asiakkaan lisäys");

        System.out.print("    Anna asiakkaan nimi: ");
        String asiakas = lukija.nextLine();

        try {
            PreparedStatement p1 = db.prepareStatement("INSERT OR ABORT INTO Asiakkaat(nimi) VALUES (?)");
            p1.setString(1, asiakas);

            p1.executeUpdate();
            System.out.println("    Asiakas lisätty");
        } catch (SQLException e) {
            System.out.println("    VIRHE: Asiakas on jo tietokannassa");
        }

    }

    public void uusiPaketti() {
        System.out.println("Uuden paketin lisäys");

        System.out.print("    Anna paketin seurantakoodi: ");
        String paketti = lukija.nextLine();

        System.out.print("    Anna asiakkaan nimi: ");
        String asiakas = lukija.nextLine();

        try {
            PreparedStatement p1 = db.prepareStatement("INSERT OR ABORT INTO Paketit(numero, asiakas_id) VALUES (?, (SELECT id FROM Asiakkaat WHERE nimi=?))");
            p1.setString(1, paketti);
            p1.setString(2, asiakas);

            p1.executeUpdate();
            System.out.println("    Paketti lisätty");
        } catch (SQLException e) {
            System.out.println("    VIRHE: Asiakas ei ole tietokannassa tai paketti on jo kirjattu tietokantaan");
        }

    }

    public void uusiTapahtuma() {
        System.out.println("Uuden tapahtuman lisäys");

        System.out.print("    Anna paketin seurantakoodi: ");
        String paketti = lukija.nextLine();

        System.out.print("    Anna tapahtuman paikka: ");
        String paikka = lukija.nextLine();

        System.out.print("    Anna tapahtuman kuvaus: ");
        String kuvaus = lukija.nextLine();

        long d = System.currentTimeMillis();
        Date pvm = new Date(d);
        String aika = String.valueOf(pvm);

        Time kello = new Time(d);
        String kellonAika = String.valueOf(kello);

        try {
            PreparedStatement p1 = db.prepareStatement("INSERT OR ABORT INTO Tapahtumat(kuvaus, paketti_id, paikka_id, pvm, aika) \n"
                    + "VALUES (?, (SELECT id FROM Paketit WHERE numero=?), (SELECT id FROM Paikat WHERE paikka=?), ?, ?)");
            p1.setString(1, kuvaus);
            p1.setString(2, paketti);
            p1.setString(3, paikka);
            p1.setString(4, aika);
            p1.setString(5, kellonAika);

            p1.executeUpdate();
            System.out.println("    Tapahtuma lisätty");
        } catch (SQLException e) {
            System.out.println("    VIRHE: Jotain tapahtui, en tiedä mitä ://");
            System.out.println("    " + e);
        }

    }

    public void paketinTapahtumat() throws SQLException {
        System.out.println("Haetaan paketin tapahtumat");

        System.out.print("    Anna paketin seurantakoodi: ");
        String paketti = lukija.nextLine();

        PreparedStatement p = db.prepareStatement("SELECT T.pvm, T.aika, T.kuvaus, P.paikka FROM Paikat P JOIN Tapahtumat T ON T.paikka_id = P.id AND T.paketti_id = (SELECT id FROM Paketit WHERE numero=?)");
        p.setString(1, paketti);

        ResultSet r = p.executeQuery();

        if (r.isBeforeFirst()) {
            while (r.next()) {
                System.out.println("    " + r.getString("pvm") + " " + r.getString("aika") + ", " + r.getString("kuvaus") + ", " + r.getString("paikka"));
            }
        } else {
            System.out.println("    Paketilla ei ole tapahtumia.");
        }
    }

    public void asiakkaanPaketit() {
        System.out.println("Haetaan asiakkaan paketit");

        System.out.print("    Anna asiakkaan nimi: ");
        String asiakas = lukija.nextLine();

        try {
            PreparedStatement p = db.prepareStatement("SELECT Paketit.numero, \n"
                    + "(SELECT COUNT(T.paketti_id) FROM Paketit P LEFT JOIN Tapahtumat T ON P.id=T.paketti_id AND P.numero=Paketit.numero) maara \n"
                    + "FROM Paketit JOIN Asiakkaat ON Asiakkaat.id = Paketit.asiakas_id AND Asiakkaat.nimi=?");
            p.setString(1, asiakas);

            ResultSet r = p.executeQuery();

            if (r.isBeforeFirst()) {
                System.out.println("    Paketin seurantakoodi ja tapahtumien määrä:");
                while (r.next()) {
                    System.out.println("    " + r.getString("numero") + ", " + r.getString("maara"));
                }
            } else {
                System.out.println("    Asiakasta ei ole olemassa tai hänellä ei ole paketteja");
            }
        } catch (SQLException e) {
            System.out.println("    VIRHE:");
            System.out.println("    " + e);
        }
    }

    public void paikanTapahtumat() {
        System.out.println("Haetaan paikan tapahtumien määrä");

        System.out.print("    Anna paikan nimi: ");
        String paikka = lukija.nextLine();

        System.out.print("    Anna päivämäärä muodossa yyyy-mm-dd: ");
        String pvm = lukija.nextLine();

        try {
            PreparedStatement p = db.prepareStatement("SELECT COUNT(*) maara FROM Tapahtumat WHERE paikka_id=(SELECT id FROM Paikat WHERE paikka=?) AND pvm=?");
            p.setString(1, paikka);
            p.setString(2, pvm);

            ResultSet r = p.executeQuery();

            if (r.isBeforeFirst()) {
                System.out.println("    Tapahtumia paikassa " + paikka + " päivämääränä " + pvm + ": ");
                while (r.next()) {
                    if (r.getString("maara").equals("0")) {
                        System.out.println("    Paikkaa ei ole tietokannassa tai siellä ei ole yhtään tapahtumaa annettuna päivänä");
                    } else {
                        System.out.println("    " + r.getString("maara"));
                    }
                }
            } else {
                System.out.println("    Paikkaa ei ole tietokannassa tai siellä ei ole yhtään tapahtumaa annettuna päivänä");
            }
        } catch (SQLException e) {
            System.out.println("    VIRHE:");
            System.out.println("    " + e);
        }

    }

}