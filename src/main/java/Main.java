
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;

import java.awt.*;
import java.util.Collection;
import java.util.concurrent.Semaphore;

import static javafx.application.Application.launch;

public class Main extends Application {



    public static void main(String[] args) {

        launch(args);
        Semaphore semaforT = new Semaphore(3);
        Semaphore semaforA = new Semaphore(1);
        Zaklad zaklad = new Zaklad();
        Technik[] technik = new Technik[3];
        for(int i = 0; i < 3; i++) {
            technik[i] = new Technik(i, zaklad, semaforT);
        }
        Akwizytor akwizytor = new Akwizytor(0, zaklad, semaforA);
        for(int i = 0; i < 50; i++) {
            Sprzet sprzet = new Sprzet();
            zaklad.dodajDoKolejki(sprzet);
        }


        akwizytor.start();
        for(int i = 0; i < 3; i++) {
            technik[i].start();
        }

        try {
            akwizytor.join();
            for (int i = 0; i < 3; i++) {
                technik[i].join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        Rectangle r = new Rectangle();
        r.setX(50);
        r.setY(50);
        r.setWidth(100);
        r.setHeight(100);
       // r.setFill();

        Group root = new Group(r);

       // stage.setFullScreen(true);
        stage.setTitle("Canvas");
        Scene scene = new Scene(root, 500, 500);
        stage.setScene(scene);
        stage.show();
    }
}
