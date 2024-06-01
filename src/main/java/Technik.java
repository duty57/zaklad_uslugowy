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

    public Technik(int id, Zaklad zaklad, Semaphore semafor, Semaphore accessToSprzet){
        this.id = id;
        this.zaklad = zaklad;
        this.semafor = semafor;
        this.accessToSprzet = accessToSprzet;
        this.imie = "Technik_" + id;
    }

    public void run(){

        try {
            Thread.sleep(300 + id*300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while(zaklad.getSprzetSize() > 0 || zaklad.getKolejkaSize() > 0){
            try{
                semafor.acquire();
                if (wezSprzet()) {
                    this.naprawSprzet();
                    this.opakujSprzet();
                    this.naklejNaklejke();
                    this.oddajSprzet();
                }
                semafor.release();
            } catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    public boolean wezSprzet(){
        System.out.println("Technik " + this.id + " pobiera sprzet");
        try {
            if(zaklad.getSprzetSize() == 0){
                return false;
            }
            this.sprzet = zaklad.wezSprzet();
            if (this.sprzet != null) {
                zaklad.usunSprzet(sprzet);
                Thread.sleep(200);
                return true;
            } else {
                return false;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
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
}
