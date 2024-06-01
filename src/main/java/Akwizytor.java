import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.util.Pair;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.Semaphore;

public class Akwizytor extends Thread{

    private String imie;
    private int id;

    private Zaklad zaklad;
    private Sprzet sprzet;
    private Semaphore semafor;
    private Group root;
    private ImageView imageView;
    private Pair<Integer, Integer> position;


    public Akwizytor(int id, Zaklad zaklad,  Semaphore semafor, Group root){
        this.id = id;
        this.zaklad = zaklad;
        this.semafor = semafor;
        this.imie = "Akwizytor_" + id;
        this.root = root;
        position = new Pair<>(700, 275);
        Platform.runLater(this::draw);

    }

    public void run(){
        while(zaklad.getKolejkaSize() > 0){
            try{
                semafor.acquire();
                dodajSprzet();
                System.out.println("Ilosc sprzetu w magazynie: " + zaklad.getSprzetSize());
                System.out.println("Dlugosc kolejki: " + zaklad.getKolejkaSize());
                semafor.release();
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    public void dodajSprzet(){
        try {
            if(zaklad.getSprzetSize() <= zaklad.MAX_SIZE){
                this.sprzet = zaklad.wezZKolejki();
                zapiszAdres();
                zaklad.usunZKolejki(sprzet);
                goToStorage();
                Thread.sleep(800);
                zaklad.dodajSprzet(sprzet);
            }else {
                zaklad.usunZKolejki(sprzet);
                zaklad.dodajDoKolejki(sprzet);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void zapiszAdres(){
        try {
            Thread.sleep(100);
            this.sprzet.setAdres(this.sprzet.getWlasciciel().getAdres());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void draw(){
        //draw png with name and color
        Image image = null;
        try {
            image = new Image(new FileInputStream("images/actor.png"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.imageView = new ImageView(image);
        this.imageView.setX(position.getKey());
        this.imageView.setY(position.getValue());
        this.imageView.setFitHeight(50);
        this.imageView.setFitWidth(50);
        this.imageView.toBack();

        Rectangle adminTable = new Rectangle(600, 300, 600, 50);
        adminTable.setStroke(Color.BLACK);
        adminTable.setStrokeWidth(2);
        adminTable.setFill(Color.WHITE);

        root.getChildren().add(this.imageView);
        root.getChildren().add(adminTable);


    }

    public void goToStorage(){
        //translate imageView to another position and back as animation
        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(400), imageView);
        translateTransition.byXProperty().set(200);
        translateTransition.byYProperty().set(0);
        translateTransition.setCycleCount(2);
        translateTransition.setAutoReverse(true);
        translateTransition.play();
    }
}
