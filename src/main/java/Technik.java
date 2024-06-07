import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
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
    private int time;
    private Semaphore semafor;
    private Semaphore accessToSprzet;
    private Group root;
    private ImageView imageView;
    private Pair<Integer, Integer> position;
    private Circle progressBar;
    private Rectangle pack;
    private Pair<Integer, Integer> packPos;
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
                    //this.naklejNaklejke();
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
                Thread.sleep(400);
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
                    time = rand.nextInt(czasNaprawy[0]) + czasNaprawy[0];
                    fix_d();
                    Thread.sleep(time);
                    break;
                case Sprzet.State.USZKODZONY:
                    time = rand.nextInt(czasNaprawy[1]) + czasNaprawy[1];
                    fix_d();
                    Thread.sleep(time);
                    break;
                case Sprzet.State.NIE_DZIALA:
                    time = rand.nextInt(czasNaprawy[2]) + czasNaprawy[2];
                    fix_d();
                    Thread.sleep(time);
                    break;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void opakujSprzet(){
        try {
            Platform.runLater(this::pack_d);
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    public void oddajSprzet(){
        try {
            putAside_d();
            Thread.sleep(400);
            goBack2();
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

        this.progressBar = new Circle(position.getKey()+25, position.getValue()+60, 2, Color.GREEN);
        this.progressBar.toFront();
        this.progressBar.setVisible(false);

        root.getChildren().add(this.imageView);
        root.getChildren().add(this.progressBar);
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
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        TranslateTransition tt = new TranslateTransition(Duration.millis(400), imageView);
        tt.byXProperty().set(-350+position.getKey());
        tt.byYProperty().set(-250+position.getValue());
        tt.play();
        this.sprzet.goToWorkPlace(new Pair<>(850 + 100*id, 25));
    }


    public void fix_d(){
        progressBar.setVisible(true);
        ScaleTransition st = new ScaleTransition();
        st.setNode(progressBar);
        st.setByX(2);
        st.setByY(2);
        st.setDuration(Duration.millis(time));
        st.play();
    }

        public void pack_d(){
        progressBar.setVisible(false);
        sprzet.getMesh().setVisible(false);
        ScaleTransition st = new ScaleTransition();
        st.setNode(progressBar);
        st.setByX(-2);
        st.setByY(-2);
        st.setDuration(Duration.millis(100));
        st.play();
        pack = new Rectangle(25,25);
        pack.setFill(Color.SANDYBROWN);
        pack.setX(sprzet.getPosition().getKey()-10);
        pack.setY(sprzet.getPosition().getValue()+10);
        root.getChildren().add(pack);
        packPos = new Pair<>(sprzet.getPosition().getKey()-10, sprzet.getPosition().getValue()+10);
        sprzet.putNoteOnBox_d(packPos.getKey(), packPos.getValue());

    }
    public void putAside_d(){
        TranslateTransition tt = new TranslateTransition();
        tt.setNode(imageView);
        tt.byXProperty().set(1180-position.getKey());
        tt.setByY(175-position.getValue());
        tt.setDuration(Duration.millis(400));

        TranslateTransition ttp = new TranslateTransition();
        ttp.setNode(pack);
        ttp.byXProperty().set(1205-packPos.getKey());
        ttp.byYProperty().set(175-packPos.getValue());
        ttp.setDuration(Duration.millis(400));

        TranslateTransition ttn = new TranslateTransition();
        ttn.setNode(sprzet.getNoteMesh());
        ttn.byXProperty().set(1205-packPos.getKey());
        ttn.byYProperty().set(175-packPos.getValue());
        ttn.setDuration(Duration.millis(400));

        tt.play();
        ttp.play();
        ttn.play();
    }
    public void goBack2(){
        TranslateTransition tt = new TranslateTransition();
        tt.setNode(imageView);
        tt.byXProperty().set(-1180+position.getKey());
        tt.byYProperty().set(-175+position.getValue());
        tt.setDuration(Duration.millis(400));
        tt.play();


    }
}
