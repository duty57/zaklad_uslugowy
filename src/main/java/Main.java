
import java.util.concurrent.Semaphore;

public class Main {



    public static void main(String[] args) {

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
}
