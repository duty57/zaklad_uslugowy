
public class Sprzet {


    enum Shape{
        KULA, KWADRAT, PROSTOKAT, TROJKAT
    }
    enum State{
        SLABO_USZKODZONY, USZKODZONY, NIE_DZIALA
    }


    private final float[][] colors = {
            {1.0f, 0.0f, 0.0f, 1.0f}, // red
            {0.0f, 1.0f, 0.0f, 1.0f}, // green
            {0.0f, 0.0f, 1.0f, 1.0f}, // blue
            {1.0f, 1.0f, 0.0f, 1.0f}, // yellow
            {1.0f, 0.0f, 1.0f, 1.0f}, // magenta
            {0.0f, 1.0f, 1.0f, 1.0f}, // cyan
            {0.5f, 0.0f, 0.5f, 1.0f}, //purple
            {0.5f, 0.5f, 0.0f, 1.0f}, //olive
            {0.0f, 0.5f, 0.5f, 1.0f}, //teal
            {1.0f, 0.75294f, 0.79608f, 1.0f} //pink
    };
    private final String[] nazwaSprzetu = {"Laptop", "Monitor", "Klawiatura", "Myszka", "Drukarka", "Telefon", "Tablet", "Router", "Glosniki", "Kamera"};//10

    private static int counter = 0;
    private String nazwa;
    private double [] kolor = new double[4];
    private Shape ksztalt;
    private State stan;
    private Wlasciciel wlasciciel;

    public int id;

    Sprzet(){
        wygenerujNazwe();
        wygenerujKolor();
        wygenerujKsztalt();
        wygenerujStan();
        wlasciciel = new Wlasciciel();

    }

    public void wygenerujNazwe(){
        int i = (int)(Math.random()*10);
        nazwa = nazwaSprzetu[i];
    }
    public void wygenerujKolor(){
        int i = counter%10;
        kolor[0] = colors[i][0];
        kolor[1] = colors[i][1];
        kolor[2] = colors[i][2];
        kolor[3] = colors[i][3];
        counter++;
    }
    public void wygenerujKsztalt(){
        int i = (int)(Math.random()*4);
        ksztalt = Shape.values()[i];
    }
    public void wygenerujStan(){
        int i = (int)(Math.random()*3);
        stan = State.values()[i];
    }

    public void dodajWlasciciela(Wlasciciel wlasciciel){
        this.wlasciciel = wlasciciel;
    }

    public State getStan() {
        return stan;
    }
}
