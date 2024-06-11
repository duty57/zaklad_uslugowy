import javafx.scene.Group;
import javafx.scene.shape.Rectangle;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Semaphore;

public class Simulation extends Thread {

    private Semaphore accessToEquipment = new Semaphore(1);//semaphore for equipment
    public Service service;
    private Technician[] technician = new Technician[3];
    private Group root;
    private Rectangle adminTable;
    public Receptionist receptionist;
    Properties prop;


    public Simulation(Group root, Rectangle adminTable, Properties prop) {
        this.root = root;
        this.adminTable = adminTable;
        this.prop = prop;
    }

    public void run() {

        int numberOfTechnicians = Integer.parseInt(prop.getProperty("numberOfTechnicians"));
        int numberOfReceptionists = Integer.parseInt(prop.getProperty("numberOfReceptionists"));
        int numberOfEquipment = Math.max(Integer.parseInt(prop.getProperty("numberOfEquipment")), 10);
        int storageCapacity = Math.min(Integer.parseInt(prop.getProperty("storageCapacity")),100);

        service = new Service(storageCapacity);

        receptionist = new Receptionist(0, service, root, adminTable, Integer.parseInt(prop.getProperty("r_goToStorageTime")), Integer.parseInt(prop.getProperty("r_writeDownAddressTime")), Integer.parseInt(prop.getProperty("r_stepForwardTime")), Integer.parseInt(prop.getProperty("r_clientExitTime")));

        //convert string array to int
        int[] reparationTime = new int[3];
        String[] reparationTimeStr = prop.getProperty("t_reparationTime").split(",");
        reparationTimeStr[0] = reparationTimeStr[0].substring(1);
        reparationTimeStr[reparationTimeStr.length-1] = reparationTimeStr[reparationTimeStr.length-1].substring(0, reparationTimeStr[reparationTimeStr.length-1].length() - 1);
        for (int i = 0; i < reparationTimeStr.length; i++) {
            reparationTime[i] = Integer.parseInt(reparationTimeStr[i]);
        }

        for (int i = 0; i < numberOfTechnicians; i++) {//creating technicians
            technician[i] = new Technician(i, service, accessToEquipment, root, Integer.parseInt(prop.getProperty("t_goToStorageTime")), reparationTime, Integer.parseInt(prop.getProperty("t_packTime")), Integer.parseInt(prop.getProperty("t_putAsideTime")));
        }
        for (int i = 0; i < numberOfEquipment; i++) {//creating equipment
            Equipment equipment = new Equipment(root);
            service.addToQueue(equipment);
        }

        receptionist.start();
        for (int i = 0; i < numberOfTechnicians; i++) {
            technician[i].start();
        }

        try {
            receptionist.join();
            for (int i = 0; i < numberOfTechnicians; i++) {
                technician[i].join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
