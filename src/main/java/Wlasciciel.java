import javafx.animation.TranslateTransition;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Pair;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Random;

import static java.lang.Thread.sleep;


public class Wlasciciel {


    public static String imiona [] = {"Jan", "Andrzej", "Piotr", "Krzysztof", "Stanislaw", "Tomasz", "Pawel", "Michal", "Jozef", "Marek", "Mariusz", "Adam", "Zbigniew", "Jerzy", "Tadeusz", "Lukasz", "Robert", "Wojciech", "Dariusz", "Henryk"};//20
    public static String nazwiska [] = {"Nowak", "Kowalski", "Wisniewski", "Wojcik", "Kowalczyk", "Kaminski", "Lewandowski", "Zielinski", "Szymanski", "Wozniak", "Dabrowski", "Kozlowski", "Jankowski", "Mazur", "Kwiatkowski", "Wojciechowski", "Krawczyk", "Kaczmarek", "Piotrowski", "Grabowski"};//20
    public static String miasto[] = {"Warszawa", "Krakow", "Gdansk", "Wroclaw", "Poznan", "Lodz", "Szczecin", "Lublin", "Katowice", "Bialystok"};//10
    public static String ulica[] = {"Kwiatowa", "Klonowa", "Szkolna", "Lesna", "Polna", "Koscielna", "Mickiewicza", "Sienkiewicza", "Wolnosci", "Pilsudskiego", "Jana Pawla II", "Kopernika", "Kosciuszki", "Wyspianskiego", "Reymonta"};//15
    private String imie;
    private String nazwisko;
    private String adres;
    private Group root;
    private Pair<Integer, Integer> position;
    private ImageView imageView;

    public Wlasciciel(Group root, Pair<Integer, Integer> pos) {//konstruktor klasy Wlasciciel
        wygenerujImie();
        wygenerujNazwisko();
        wygenerujAdres();
        this.root = root;
       // this.position = new Pair<>(pos.getKey()+10, pos.getValue()-15);

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

    public void draw(Pair<Integer, Integer> pos){//metoda rysujaca Wlasciciela
        Image image = null;
        this.position = pos;
        try {
            image = new Image(new FileInputStream("images/actor.png"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.imageView = new ImageView(image);
        this.imageView.setX(position.getKey()-5);
        this.imageView.setY(position.getValue()-10);
        this.imageView.setFitHeight(50);
        this.imageView.setFitWidth(50);
        this.imageView.toBack();

        root.getChildren().add(this.imageView);
    }

    public void stepForward(){
        TranslateTransition tt = new TranslateTransition();
        tt.setNode(imageView);
        tt.byXProperty().set(-75);
        tt.byYProperty().set(0);
        tt.setDuration(javafx.util.Duration.millis(400));
        tt.play();
        position = new Pair<>(position.getKey()-75, position.getValue());
    }
    public void exit(){
        TranslateTransition tt = new TranslateTransition();
        tt.setNode(imageView);
        tt.byXProperty().set(-this.position.getKey()-200);
        tt.byYProperty().set(0);
        tt.setDuration(javafx.util.Duration.millis(1000));
        tt.play();
        position = new Pair<>(-this.position.getKey(), this.position.getValue());
    }

    public ImageView getImageView() {
        return imageView;
    }
}
