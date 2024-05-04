import java.util.Random;


public class Wlasciciel {


    public static String imiona [] = {"Jan", "Andrzej", "Piotr", "Krzysztof", "Stanislaw", "Tomasz", "Pawel", "Michal", "Jozef", "Marek", "Mariusz", "Adam", "Zbigniew", "Jerzy", "Tadeusz", "Lukasz", "Robert", "Wojciech", "Dariusz", "Henryk"};//20
    public static String nazwiska [] = {"Nowak", "Kowalski", "Wisniewski", "Wojcik", "Kowalczyk", "Kaminski", "Lewandowski", "Zielinski", "Szymanski", "Wozniak", "Dabrowski", "Kozlowski", "Jankowski", "Mazur", "Kwiatkowski", "Wojciechowski", "Krawczyk", "Kaczmarek", "Piotrowski", "Grabowski"};//20
    public static String miasto[] = {"Warszawa", "Krakow", "Gdansk", "Wroclaw", "Poznan", "Lodz", "Szczecin", "Lublin", "Katowice", "Bialystok"};//10
    public static String ulica[] = {"Kwiatowa", "Klonowa", "Szkolna", "Lesna", "Polna", "Koscielna", "Mickiewicza", "Sienkiewicza", "Wolnosci", "Pilsudskiego", "Jana Pawla II", "Kopernika", "Kosciuszki", "Wyspianskiego", "Reymonta"};//15



    private String imie;
    private String nazwisko;
    private String adres;

    public Wlasciciel() {//konstruktor klasy Wlasciciel
        wygenerujImie();
        wygenerujNazwisko();
        wygenerujAdres();
    }

    public void wygenerujImie(){//generowanie imienia Wlasciciela
        Random rand = new Random();
        imie = imiona[rand.nextInt(20)];
    }

    public void wygenerujNazwisko(){//generowanie nazwiska Wlasciciela
        Random rand = new Random();
        nazwisko = nazwiska[rand.nextInt(20)];
    }

    public void wygenerujAdres(){//generowanie adresu Wlasciciela
        Random miasto = new Random();
        Random ulica = new Random();
        Random numer = new Random();
        Random lokal = new Random();

        adres = Wlasciciel.miasto[miasto.nextInt(10)] + " " + Wlasciciel.ulica[ulica.nextInt(15)] + " " + numer.nextInt(200) + "/" + lokal.nextInt(60);
    }

    public String getAdres() {//metoda zwracajaca adres Wlasciciela
        return adres;
    }
}
