
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.Semaphore;

import static javafx.application.Application.launch;

public class Main extends Application {

    public static Properties prop = null;//readProperties();
    private static Semaphore semaforT = new Semaphore(3);
    private static Semaphore semaforA = new Semaphore(1);
    private static Semaphore accessToSprzet = new Semaphore(1);
    private static Zaklad zaklad = new Zaklad();
    private static Technik[] technik = new Technik[3];
    private static Akwizytor akwizytor = new Akwizytor(0, zaklad, semaforA);

    public static void main(String[] args) {

        int iloscTechnikow = 3;
        int iloscAkwizytorow = 1;
        int iloscSprzetu = 50;
        if(prop != null) {

            iloscTechnikow = Integer.parseInt(prop.getProperty("iloscTechnikow"));
            iloscAkwizytorow = Integer.parseInt(prop.getProperty("iloscAkwizytorow"));
            iloscSprzetu = Integer.parseInt(prop.getProperty("iloscSprzetu"));
            semaforT = new Semaphore(iloscTechnikow);
            semaforA = new Semaphore(iloscAkwizytorow);
            zaklad.MAX_SIZE = Integer.parseInt(prop.getProperty("pojemnoscMagazynu"));
        }

        launch(args);

        for(int i = 0; i < iloscTechnikow; i++) {
            technik[i] = new Technik(i, zaklad, semaforT, accessToSprzet);
        }
        for(int i = 0; i < iloscSprzetu; i++) {
            Sprzet sprzet = new Sprzet();
            zaklad.dodajDoKolejki(sprzet);
        }


        akwizytor.start();
        for(int i = 0; i < iloscTechnikow; i++) {
            technik[i].start();
        }

        try {
            akwizytor.join();
            for (int i = 0; i < iloscTechnikow; i++) {
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

    public static Properties readProperties() {
        File file = new File(".");
        for(String fileNames : file.list()) System.out.println(fileNames);
        try {
            Properties prop = new Properties();
            InputStream input = new FileInputStream("config.properties");
            prop.load(input);
            System.out.println("Properties file read successfully");
            System.out.println("Properties: " + prop);
            return prop;
        } catch (IOException e) {
            System.out.println("Error reading properties file");
            e.printStackTrace();
        }
        return null;
    }
}
