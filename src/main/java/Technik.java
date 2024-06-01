import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
                Thread.sleep(200);
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
            Thread.sleep(150);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void naklejNaklejke(){
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void oddajSprzet(){
        try {
            Thread.sleep(200);
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
}
