import javafx.animation.TranslateTransition;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.util.Pair;

import static java.lang.Thread.sleep;

public class Sprzet {

//create list with free positions on table

    enum State{
        SLABO_USZKODZONY, USZKODZONY, NIE_DZIALA
    }


    private final Color[] colors = {
            Color.RED, // red
            Color.GREEN, // green
            Color.BLUE, // blue
            Color.YELLOW, // yellow
            Color.MAGENTA, // magenta
            Color.CYAN, // cyan
            Color.PURPLE, //purple
            Color.OLIVE, //olive
            Color.TEAL, //teal
            Color.PINK //pink
    };
    private final String[] nazwaSprzetu = {"Laptop", "Monitor", "Klawiatura", "Myszka", "Drukarka", "Telefon", "Tablet", "Router", "Glosniki", "Kamera"};//10

    private static int counter = 0;
    private String nazwa;
    private Color color;
    private State stan;
    private Wlasciciel wlasciciel;
    private String adres;
    private Group root;
    private Pair<Integer, Integer> position;
    private Rectangle note;
    private Pair<Integer, Integer> notePos;
    public int id;
    Circle mesh;

    public static int positionOnStorage = 0;
    Sprzet(int id, Group root){
        wygenerujNazwe();
        wygenerujKolor();
        wygenerujStan();
        wlasciciel = new Wlasciciel(root, position);
        this.id = id;
        this.root = root;
    }

    public void wygenerujNazwe(){
        int i = (int)(Math.random()*10);
        nazwa = nazwaSprzetu[i];
    }
    public void wygenerujKolor(){
        int i = counter%10;
        color = colors[i];
        counter++;
    }
    public void wygenerujStan(){
        int i = (int)(Math.random()*3);
        stan = State.values()[i];
    }

    public void dodajWlasciciela(Wlasciciel wlasciciel){
        this.wlasciciel = wlasciciel;
    }

    public State getStan() {
        return stan;
    }

    public Wlasciciel getWlasciciel(){
        return this.wlasciciel;
    }

    public void setAdres(String adres){
        this.adres = adres;
    }
    public void setNote(Rectangle note, Pair<Integer, Integer> pos){
        this.note = note;
        this.notePos = pos;
    }
    public void draw(Pair<Integer, Integer> pos){
        //System.out.println("Rysuje sprzet");
        position = pos;
        this.mesh = new Circle(pos.getKey(), pos.getValue(), 10, color);
        root.getChildren().add(this.mesh);
        wlasciciel.draw(new Pair<>(pos.getKey(), pos.getValue()));
    }

    public void stepForward(){
        TranslateTransition tt = new TranslateTransition();
        tt.setNode(this.mesh);
        tt.byXProperty().set(-75);
        tt.byYProperty().set(0);
        tt.setDuration(javafx.util.Duration.millis(300));
        this.wlasciciel.stepForward();
        tt.play();
        position = new Pair<>(position.getKey()-75, position.getValue());
    }

    public void goToAkwizytor(Pair<Integer, Integer> pos){
        TranslateTransition tt = new TranslateTransition(Duration.millis(400), this.mesh);
        tt.byXProperty().set(0);
        tt.byYProperty().set(-50);

        this.wlasciciel.exit();
        tt.play();
        position = new Pair<>(pos.getKey(), pos.getValue());
    }
    public void goToStorage(){
        TranslateTransition tt = new TranslateTransition(Duration.millis(400), this.mesh);
        tt.byXProperty().set(320-position.getKey());
        tt.byYProperty().set(250-position.getValue() - positionOnStorage*7);

        TranslateTransition ttn = new TranslateTransition(Duration.millis(400), this.note);
        ttn.byXProperty().set(310-position.getKey());
        ttn.byYProperty().set(250-position.getValue()- positionOnStorage*7);

        tt.play();
        ttn.play();
        position = new Pair<>(320, 250- positionOnStorage*7);
        notePos = new Pair<>(310, 250- positionOnStorage*7);
        positionOnStorage++;
    }

    public void goToTechnik(Pair<Integer, Integer> pos){
        TranslateTransition tt = new TranslateTransition(Duration.millis(400), this.mesh);
        tt.byXProperty().set(pos.getKey() - position.getKey());
        tt.byYProperty().set(pos.getValue() - position.getValue());

        System.out.println("note: " + note);
        TranslateTransition ttn = new TranslateTransition(Duration.millis(400), this.note);
        ttn.byYProperty().set(pos.getValue() - notePos.getValue());
        ttn.byXProperty().set(pos.getKey() - notePos.getKey());

        ttn.play();
        tt.play();
        position = pos;
        notePos = pos;
    }

    public void goToWorkPlace(Pair<Integer, Integer> pos){
        TranslateTransition tt = new TranslateTransition(Duration.millis(400), this.mesh);
        tt.byXProperty().set(pos.getKey() - position.getKey());
        tt.byYProperty().set(pos.getValue() - position.getValue());

        System.out.println("note: " + note);

        TranslateTransition ttn = new TranslateTransition(Duration.millis(400), this.note);
        ttn.byYProperty().set(pos.getValue() - notePos.getValue());
        ttn.byXProperty().set(pos.getKey() - notePos.getKey());

        tt.play();
        ttn.play();
        position = pos;
        notePos = pos;
    }
}
