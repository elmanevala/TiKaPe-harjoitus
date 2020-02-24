package tietokanta;

import java.sql.*;
import java.util.*;

public class Tietokanta {

    public static void main(String[] args) throws SQLException {
        
        Kayttoliittyma kayttis = new Kayttoliittyma();

        kayttis.kaynnista();
    }

}
