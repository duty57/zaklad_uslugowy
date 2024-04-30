import java.util.concurrent.Semaphore;

public class Akwizytor extends Thread{

    private String imie;
    private int id;

    private Zaklad zaklad;
    private Sprzet sprzet;

    private Semaphore semafor;

    public Akwizytor(int id, Zaklad zaklad,  Semaphore semafor){
        this.id = id;
        this.zaklad = zaklad;
        this.semafor = semafor;
        this.imie = "Akwizytor_" + id;
    }

    public void run(){
        while(zaklad.getKolejkaSize() > 0){
            try{
                semafor.acquire();
                dodajSprzet();
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
                zaklad.usunZKolejki(sprzet);
                Thread.sleep(200);
                zaklad.dodajSprzet(sprzet);
            }else {
                zaklad.usunZKolejki(sprzet);
                zaklad.dodajDoKolejki(sprzet);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



}
