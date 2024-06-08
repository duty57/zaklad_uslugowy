import javafx.animation.TranslateTransition;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Pair;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Random;

import static java.lang.Thread.sleep;


public class Owner {


    public static String names[] = {"Jan", "Andrzej", "Piotr", "Krzysztof", "Stanislaw", "Tomasz", "Pawel", "Michal", "Jozef", "Marek", "Mariusz", "Adam", "Zbigniew", "Jerzy", "Tadeusz", "Lukasz", "Robert", "Wojciech", "Dariusz", "Henryk"};//20
    public static String surnames[] = {"Nowak", "Kowalski", "Wisniewski", "Wojcik", "Kowalczyk", "Kaminski", "Lewandowski", "Zielinski", "Szymanski", "Wozniak", "Dabrowski", "Kozlowski", "Jankowski", "Mazur", "Kwiatkowski", "Wojciechowski", "Krawczyk", "Kaczmarek", "Piotrowski", "Grabowski"};//20
    public static String cities[] = {"Warszawa", "Krakow", "Gdansk", "Wroclaw", "Poznan", "Lodz", "Szczecin", "Lublin", "Katowice", "Bialystok"};//10
    public static String street[] = {"Kwiatowa", "Klonowa", "Szkolna", "Lesna", "Polna", "Koscielna", "Mickiewicza", "Sienkiewicza", "Wolnosci", "Pilsudskiego", "Jana Pawla II", "Kopernika", "Kosciuszki", "Wyspianskiego", "Reymonta"};//15
    private String name;
    private String surname;
    private String address;
    private Group root;
    private Pair<Integer, Integer> position;//position of owner
    private ImageView Mesh;//owner mesh

    public Owner(Group root, Pair<Integer, Integer> pos) {//constructor
        generateName();
        generateSurname();
        generateAddress();
        this.root = root;

    }

    public void generateName(){
        Random rand = new Random();
        name = names[rand.nextInt(20)];
    }

    public void generateSurname(){
        Random rand = new Random();
        surname = surnames[rand.nextInt(20)];
    }

    public void generateAddress(){
        Random miasto = new Random();
        Random ulica = new Random();
        Random numer = new Random();
        Random lokal = new Random();

        address = Owner.cities[miasto.nextInt(10)] + " " + Owner.street[ulica.nextInt(15)] + " " + numer.nextInt(200) + "/" + lokal.nextInt(60);
    }

    public String getAddress() {//metoda zwracajaca adres Wlasciciela
        return address;
    }

    public void draw(Pair<Integer, Integer> pos){//method drawing owner
        Image image = null;
        this.position = pos;
        try {
            image = new Image(new FileInputStream("images/actor.png"));//loading image
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.Mesh = new ImageView(image);
        this.Mesh.setX(position.getKey()-5);
        this.Mesh.setY(position.getValue()-10);
        this.Mesh.setFitHeight(50);
        this.Mesh.setFitWidth(50);
        this.Mesh.toBack();

        root.getChildren().add(this.Mesh);//adding owner to root
    }

    public void stepForward(int time){//method moving owner forward
        TranslateTransition tt = new TranslateTransition();
        tt.setNode(Mesh);
        tt.byXProperty().set(-75);
        tt.byYProperty().set(0);
        tt.setDuration(javafx.util.Duration.millis(time));
        tt.play();
        position = new Pair<>(position.getKey()-75, position.getValue());//changing position
    }
    public void exit(int time){//method moving owner out of the screen
        TranslateTransition tt = new TranslateTransition();
        tt.setNode(Mesh);
        tt.byXProperty().set(-this.position.getKey()-200);
        tt.byYProperty().set(0);
        tt.setDuration(javafx.util.Duration.millis(time));
        tt.play();
        position = new Pair<>(-this.position.getKey(), this.position.getValue());//changing position
    }

    public ImageView getMesh() {
        return Mesh;
    }
}
