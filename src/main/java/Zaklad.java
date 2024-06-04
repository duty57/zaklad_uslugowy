import java.util.LinkedList;

public class Zaklad {

    public int MAX_SIZE = 100;
    public static LinkedList<Sprzet> sprzet = new LinkedList<Sprzet>();
//    private Technik[] technicy = new Technik[3];
//    private Akwizytor akwizytor = new Akwizytor();

    public static LinkedList<Sprzet> kolejka = new LinkedList<Sprzet>();//kolejka na sprzet
//    Zaklad(Technik[] technicy, Akwizytor akwizytor) {//konstruktor
//        sprzet = null;
//        this.technicy = technicy;
//        this.akwizytor = akwizytor;
//        kolejka = null;
//    }



    public void dodajSprzet(Sprzet sprzet) {//metoda dodajaca sprzet
        this.sprzet.add(sprzet);

    }

    public void usunSprzet(Sprzet sprzet) {//metoda usuwajaca sprzet
        this.sprzet.remove(sprzet);
    }

    public void dodajDoKolejki(Sprzet sprzet){//metoda dodajaca do kolejki
        this.kolejka.add(sprzet);
    }

    public void usunZKolejki(Sprzet sprzet){//metoda usuwajaca z kolejki
        this.kolejka.remove(sprzet);
    }

    public Sprzet wezZKolejki(){//metoda pobierajaca z kolejki
        return this.kolejka.getFirst();
    }
    public Sprzet wezSprzet(){//metoda pobierajaca sprzet
        return this.sprzet.getFirst();
    }

    public int getSprzetSize(){//metoda zwracajaca rozmiar sprzetu
        return this.sprzet.size();
    }

    public int getKolejkaSize(){//metoda zwracajaca rozmiar kolejki
        return this.kolejka.size();
    }

    public static int getPostion(Sprzet s){
        return kolejka.indexOf(s);
    }
}
