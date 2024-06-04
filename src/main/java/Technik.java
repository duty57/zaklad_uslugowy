import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.util.Pair;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Technik extends Thread{


    private String imie;
    private int id;
    private int []czasNaprawy = {380, 800, 1700};
    private Zaklad zaklad;
    private Sprzet sprzet;
    private Semaphore semafor;
    private Semaphore accessToSprzet;
    private Group root;
    private ImageView imageView;
    private Pair<Integer, Integer> position;
    private Rectangle progressBar;
    public Technik(int id, Zaklad zaklad, Semaphore semafor, Semaphore accessToSprzet, Group root){
        this.id = id;
        this.zaklad = zaklad;
        this.semafor = semafor;
        this.accessToSprzet = accessToSprzet;
        this.imie = "Technik_" + id;
        this.root = root;
        position = new Pair<>(850 + 100*id, 25);
        Platform.runLater(this::draw);
    }

    public void run(){



        while(zaklad.getSprzetSize() > 0 || zaklad.getKolejkaSize() > 0){
            try{
                semafor.acquire();
                accessToSprzet.acquire();
                boolean czyJestSprzet = wezSprzet();
                if (czyJestSprzet) {
                    accessToSprzet.release();
                    this.naprawSprzet();
                    this.opakujSprzet();
                    this.naklejNaklejke();
                    this.oddajSprzet();
                }else{
                    accessToSprzet.release();
                }

                semafor.release();
            } catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    public boolean wezSprzet(){
        //System.out.println("Technik " + this.id + " pobiera sprzet");
        try {
            if(zaklad.getSprzetSize() == 0){
                return false;
            }
            this.sprzet = zaklad.wezSprzet();
            if (this.sprzet != null) {
                System.out.println("Technik " + this.id + " naprawia sprzet nr " + this.sprzet.id);
                zaklad.usunSprzet(sprzet);
                goForSprzet();
                Thread.sleep(400);
                goBack();
                return true;
            } else {
                return false;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("Technik " + this.id + " nie moze naprawic sprzetu");
            return false;
        }
    }

    public void naprawSprzet(){
        try {
            Random rand = new Random();
            switch (this.sprzet.getStan()) {
                case Sprzet.State.SLABO_USZKODZONY:
                    Thread.sleep(rand.nextInt(czasNaprawy[0]) + czasNaprawy[0]);
                    break;
                case Sprzet.State.USZKODZONY:
                    Thread.sleep(rand.nextInt(czasNaprawy[1]) + czasNaprawy[1]);
                    break;
                case Sprzet.State.NIE_DZIALA:
                    Thread.sleep(rand.nextInt(czasNaprawy[2]) + czasNaprawy[2]);
                    break;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void opakujSprzet(){
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void naklejNaklejke(){
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void oddajSprzet(){
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void draw(){

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

        root.getChildren().add(this.imageView);
    }

    public void goForSprzet(){
        TranslateTransition tt = new TranslateTransition(Duration.millis(400), imageView);
        tt.byXProperty().set(350-position.getKey());
        tt.byYProperty().set(250-position.getValue());
        tt.play();
    }

    public void goBack(){
        this.sprzet.goToTechnik(new Pair<>(350, 250));
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        TranslateTransition tt = new TranslateTransition(Duration.millis(400), imageView);
        tt.byXProperty().set(-350+position.getKey());
        tt.byYProperty().set(-250+position.getValue());
        tt.play();
        this.sprzet.goToWorkPlace(new Pair<>(850 + 100*id, 25));
    }
    public void packSprzet(){

    }

    public void oddajSprzed_d(){
        TranslateTransition tt = new TranslateTransition();
        tt.setNode(imageView);
        tt.byXProperty().set(450-position.getKey());
        tt.setByY(-position.getValue());
        tt.setDuration(Duration.millis(200));
        tt.play();
    }
    public void wroc(){
        TranslateTransition tt = new TranslateTransition();
        tt.setNode(imageView);
        tt.byXProperty().set(-450+position.getKey());
        tt.byYProperty().set(position.getValue());
        tt.setDuration(Duration.millis(100));
        tt.play();
    }
}
