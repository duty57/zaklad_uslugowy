import javafx.scene.Group;

import java.util.LinkedList;

public class Service {

    public int MAX_SIZE;
    public static LinkedList<Equipment> equipment = new LinkedList<Equipment>();//list of equipment in storage
    public static LinkedList<Equipment> queue = new LinkedList<Equipment>();//list of equipment in queue

    public Service(int max_size) {
        this.MAX_SIZE = max_size;
    }//constructor

    public void addEquipment(Equipment equipment) {
        this.equipment.add(equipment);
    }

    public void removeEquipment(Equipment equipment) {//metoda usuwajaca sprzet
        this.equipment.remove(equipment);
    }

    public void addToQueue(Equipment equipment) {//metoda dodajaca do kolejki
        this.queue.add(equipment);
    }

    public void removeFromQueue(Equipment equipment) {//metoda usuwajaca z kolejki
        this.queue.remove(equipment);
    }

    public Equipment takeFromQueue() {//metoda pobierajaca z kolejki
        return this.queue.getFirst();
    }

    public Equipment takeEquipment() {//metoda pobierajaca sprzet
        return this.equipment.getFirst();
    }

    public int getEquipment() {//metoda zwracajaca rozmiar sprzetu
        return this.equipment.size();
    }

    public int getQueueSize() {//metoda zwracajaca rozmiar kolejki
        return this.queue.size();
    }

    public static int getPostion(Equipment s) {
        return queue.indexOf(s);
    }

    public void createEquipment(int id, int n, Group root) {//generating equipment
        for (int i = 0; i < n; i++) {
            Equipment equipment = new Equipment(id, root);
            addToQueue(equipment);
        }
    }
}
