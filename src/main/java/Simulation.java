import javafx.scene.Group;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Semaphore;

public class Simulation extends Thread {

    public Properties prop = readProperties();
    private Semaphore semaphoreT = new Semaphore(3);//semaphore for technicians
    private Semaphore semaphoreR = new Semaphore(1);//semaphore for receptionists
    private Semaphore accessToEquipment = new Semaphore(1);//semaphore for equipment
    private Service service;
    private Technician[] technician = new Technician[3];
    private Group root;
    private Receptionist receptionist;

    public Simulation(Group root) {
        this.root = root;
    }

    public void run() {


        int numberOfTechnicians = Integer.parseInt(prop.getProperty("numberOfTechnicians"));
        int numberOfReceptionists = Integer.parseInt(prop.getProperty("numberOfReceptionists"));
        int numberOfEquipment = Integer.parseInt(prop.getProperty("numberOfEuqipment"));
        semaphoreT = new Semaphore(numberOfTechnicians);
        semaphoreR = new Semaphore(numberOfReceptionists);

        service = new Service(numberOfEquipment);

        receptionist = new Receptionist(0, service, semaphoreR, root, Integer.parseInt(prop.getProperty("r_goToStorageTime")), Integer.parseInt(prop.getProperty("r_writeDownAddressTime")), Integer.parseInt(prop.getProperty("r_stepForwardTime")), Integer.parseInt(prop.getProperty("r_clientExitTime")));

        for (int i = 0; i < numberOfTechnicians; i++) {//creating technicians
            technician[i] = new Technician(i, service, semaphoreT, accessToEquipment, root, Integer.parseInt(prop.getProperty("t_goToStorageTime")), Integer.parseInt(prop.getProperty("t_repairTime")), Integer.parseInt(prop.getProperty("t_packTime")), Integer.parseInt(prop.getProperty("t_putAsideTime")));
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

    public static Properties readProperties() {//reading properties file
        File file = new File(".");
        for (String fileNames : file.list()) System.out.println(fileNames);
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
