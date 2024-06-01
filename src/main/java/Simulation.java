import javafx.scene.Group;
import javafx.scene.Scene;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Semaphore;

public class Simulation extends Thread{

    public static Properties prop = null;//readProperties();
    private static Semaphore semaforT = new Semaphore(3);
    private static Semaphore semaforA = new Semaphore(1);
    private static Semaphore accessToSprzet = new Semaphore(1);
    private static Zaklad zaklad = new Zaklad();
    private static Technik[] technik = new Technik[3];
    private Group root;
    private static Akwizytor akwizytor;

    public Simulation(Group root) {
        this.root = root;
    }
    public void run(){
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

        akwizytor = new Akwizytor(0, zaklad, semaforA, root);

        for(int i = 0; i < iloscTechnikow; i++) {
            technik[i] = new Technik(i, zaklad, semaforT, accessToSprzet, root);
        }
        for(int i = 0; i < iloscSprzetu; i++) {
            Sprzet sprzet = new Sprzet(i, root);
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
