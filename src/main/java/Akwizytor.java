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

    private int iterator = 0;

    public Akwizytor(int id, Zaklad zaklad,  Semaphore semafor, Group root){
        this.id = id;
        this.zaklad = zaklad;
        this.semafor = semafor;
        this.imie = "Akwizytor_" + id;
        this.root = root;
        position = new Pair<>(700, 275);
        Platform.runLater(this::draw);

//        Platform.runLater(this::drawClients);
    }

    public void run(){
        while(zaklad.getKolejkaSize() > 0){
            if(iterator == 0){
                Platform.runLater(this::drawClients);
            }

            try{
                semafor.acquire();
                dodajSprzet();
                System.out.println("Ilosc sprzetu w magazynie: " + zaklad.getSprzetSize());
                System.out.println("Dlugosc kolejki: " + zaklad.getKolejkaSize());
                semafor.release();
            }catch(InterruptedException e){
                e.printStackTrace();
            }
            iterator++;
        }
    }

    public void dodajSprzet(){
        try {
            if(zaklad.getSprzetSize() <= zaklad.MAX_SIZE){
                this.sprzet = zaklad.wezZKolejki();
                System.out.println("Sprzet: " + sprzet.id + " dodany do magazynu");
                zapiszAdres();
                zaklad.usunZKolejki(sprzet);
                moveClient();
                goToStorage();
                Thread.sleep(400);
                goBack();
                Thread.sleep(400);
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
        Platform.runLater(this::writeDownAdress_d);
        try {
            Thread.sleep(400);
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
        translateTransition.byXProperty().set(300-position.getKey());
        translateTransition.byYProperty().set(0);
        this.sprzet.goToAkwizytor(position);
        try{
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.sprzet.goToStorage();

        translateTransition.play();
    }

    public void drawClients(){
        for(int i = 0; i < Zaklad.kolejka.size(); i++) {
            Zaklad.kolejka.get(i).draw(new Pair<>(700 + i * 75, 350));
        }
    }

    public void moveClient(){
        for(int i = 0; i < Zaklad.kolejka.size(); i++){
            Zaklad.kolejka.get(i).stepForward();
        }
    }

    public void writeDownAdress_d(){
        Rectangle note = new Rectangle(position.getKey()+25, position.getValue()+25, 15, 5);
        note.setFill(Color.YELLOW);
        root.getChildren().add(note);
        this.sprzet.setNote(note, new Pair<>(position.getKey()+25, position.getValue()+25));
    }
    public void goBack(){
        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(400), imageView);
        translateTransition.byXProperty().set(-300+position.getKey());
        translateTransition.byYProperty().set(0);
        translateTransition.play();

    }
}

