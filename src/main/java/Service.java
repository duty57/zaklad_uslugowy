import java.util.LinkedList;

public class Service {

    public int MAX_SIZE = 100;
    public static LinkedList<Equipment> equipment = new LinkedList<Equipment>();//lista sprzetu
    public static LinkedList<Equipment> queue = new LinkedList<Equipment>();//lista kolejki


    public void addEquipment(Equipment equipment) {
        this.equipment.add(equipment);
    }//metoda dodajaca sprzet
    public void removeEquipment(Equipment equipment) {//metoda usuwajaca sprzet
        this.equipment.remove(equipment);
    }//metoda usuwajaca sprzet

    public void addToQueue(Equipment equipment){//metoda dodajaca do kolejki
        this.queue.add(equipment);
    }//metoda dodajaca do kolejki

    public void removeFromQueue(Equipment equipment){//metoda usuwajaca z kolejki
        this.queue.remove(equipment);
    }//metoda usuwajaca z kolejki

    public Equipment takeFromQueue(){//metoda pobierajaca z kolejki
        return this.queue.getFirst();
    }//metoda pobierajaca z kolejki
    public Equipment takeEquipment(){//metoda pobierajaca sprzet
        return this.equipment.getFirst();
    }

    public int getEquipment(){//metoda zwracajaca rozmiar sprzetu
        return this.equipment.size();
    }// metoda zwracajaca rozmiar sprzetu

    public int getQueueSize(){//metoda zwracajaca rozmiar kolejki
        return this.queue.size();
    }//metoda zwracajaca rozmiar kolejki

    public static int getPostion(Equipment s){
        return queue.indexOf(s);
    }// metoda zwracajaca pozycje sprzetu w kolejce
}
