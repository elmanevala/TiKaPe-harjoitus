package tietokanta;

import java.sql.*;
import java.util.*;
import java.util.Scanner;

public class Tehokkuustesti {

    private Connection db;
    private Scanner lukija;

    public Tehokkuustesti() throws SQLException {
        this.lukija = new Scanner(System.in);
        this.db = DriverManager.getConnection("jdbc:sqlite:tehokkuustestit.db");
    }

    public void testienSuoritus() throws SQLException {
        // Testaa tietokannan tehokkuutta tehtävänannon ohjeiden perusteella.
        // Luo ensin samat taulut kuin sovelluksessa, mutta eri tiedostoon, ettei
        // testit vaikuta päätietokantaan.
        // Suorittaa vaiheet 1–4 saman transaktion sisällä ja muut erikseen.
        // Lopulta poistaa kaikki taulut, jotta testit voidaan suorittaa useasti.
        
        System.out.println("Testataan tietokannan tehokkuutta");
        
        Statement s = db.createStatement();

        s.execute("PRAGMA foreign_keys = ON");

        s.execute("CREATE TABLE IF NOT EXISTS Asiakkaat (id INTEGER PRIMARY KEY, nimi TEXT NOT NULL, UNIQUE(nimi))");
        s.execute("CREATE TABLE IF NOT EXISTS Paketit (id INTEGER PRIMARY KEY, seurantakoodi TEXT, asiakas_id INTEGER REFRENCES Asiakkaat NOT NULL, UNIQUE(seurantakoodi))");
        s.execute("CREATE TABLE IF NOT EXISTS Paikat (id INTEGER PRIMARY KEY, paikka TEXT, UNIQUE(paikka))");
        s.execute("CREATE TABLE IF NOT EXISTS Tapahtumat (id INTEGER PRIMARY KEY, kuvaus TEXT, paketti_id INTEGER REFRENCES Paketit NOT NULL, paikka_id INTEGER REFRECES Paikat NOT NULL, pvm TEXT, aika TEXT)");

        s.execute("BEGIN TRANSACTION");

        PreparedStatement p = db.prepareStatement("INSERT INTO Paikat (paikka) VALUES (?)");
        PreparedStatement p1 = db.prepareStatement("INSERT INTO Asiakkaat (nimi) VALUES (?)");
        PreparedStatement p2 = db.prepareStatement("INSERT INTO Paketit (seurantakoodi, asiakas_id) VALUES (?,?)");
        PreparedStatement p3 = db.prepareStatement("INSERT INTO Tapahtumat (paketti_id, paikka_id) VALUES (?, 1)");

        long aloitus = System.nanoTime();

        for (int i = 1; i <= 10000; i++) {
            p.setString(1, "P" + i);
            p.executeUpdate();
        }

        long ekanJalkeen = System.nanoTime();

        for (int i = 1; i <= 10000; i++) {
            p1.setString(1, "A" + i);
            p1.executeUpdate();
        }

        long tokanJalkeen = System.nanoTime();

        for (int i = 1; i <= 10000; i++) {
            p2.setString(1, String.valueOf(i));
            p2.setString(2, String.valueOf(i));
            p2.executeUpdate();
        }

        long kolmannenJalkeen = System.nanoTime();

        for (int i = 1; i <= 1000000; i++) {
            p3.setString(1, String.valueOf(i));
            p3.executeUpdate();
        }

        long neljannenJalkeen = System.nanoTime();

        s.execute("COMMIT");

        PreparedStatement p4 = db.prepareStatement("CREATE INDEX idx_asiakas_id ON Paketit (asiakas_id);");
        p4.executeUpdate();
        PreparedStatement p41 = db.prepareStatement("SELECT COUNT(*) FROM Paketit WHERE asiakas_id = ?");

        for (int i = 1; i <= 1000; i++) {
            p41.setString(1, String.valueOf(i));
            p41.executeQuery();
        }

        long viidennenJalkeen = System.nanoTime();

        PreparedStatement p5 = db.prepareStatement("CREATE INDEX idx_paketti_id ON Tapahtumat (paketti_id);");
        p5.executeUpdate();
        PreparedStatement p51 = db.prepareStatement("SELECT COUNT(*) FROM Tapahtumat WHERE paketti_id = ?");

        for (int i = 1; i <= 1000; i++) {
            p51.setString(1, String.valueOf(i));
            p51.executeQuery();
        }

        long kuudennenJalkeen = System.nanoTime();

        System.out.println("    Ensimmäiseen vaiheeseen aikaa kului: " + (ekanJalkeen - aloitus) / 1e9 + " sekuntia");
        System.out.println("    Toiseen vaiheeseen aikaa kului: " + (tokanJalkeen - aloitus) / 1e9 + " sekuntia");
        System.out.println("    Kolmanteen vaiheeseen aikaa kului: " + (kolmannenJalkeen - aloitus) / 1e9 + " sekuntia");
        System.out.println("    Neljänteen vaiheeseen aikaa kului: " + (neljannenJalkeen - aloitus) / 1e9 + " sekuntia");
        System.out.println("    Viidenteen vaiheeseen aikaa kului: " + (viidennenJalkeen - aloitus) / 1e9 + " sekuntia");
        System.out.println("    Kuudenteen vaiheeseen aikaa kului: " + (kuudennenJalkeen - aloitus) / 1e9 + " sekuntia");

        s.close();
        p.close();
        p1.close();
        p2.close();
        p3.close();
        p4.close();
        p41.close();
        p5.close();
        p51.close();

        Statement s1 = db.createStatement();

        s1.execute("DROP TABLE Paikat");
        s1.execute("DROP TABLE Asiakkaat");
        s1.execute("DROP TABLE Paketit");
        s1.execute("DROP TABLE Tapahtumat");

    }

}
