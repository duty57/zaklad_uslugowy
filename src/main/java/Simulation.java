import javafx.scene.Group;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Semaphore;

public class Simulation extends Thread{

    public static Properties prop = null;//readProperties();
    private static Semaphore semaphoreT = new Semaphore(3);//semaphore for technicians
    private static Semaphore semaphoreA = new Semaphore(1);//semaphore for receptionists
    private static Semaphore accessToSprzet = new Semaphore(1);//semaphore for equipment
    private static Service service = new Service();
    private static Technician[] technician = new Technician[3];
    private Group root;
    private static Receptionist receptionist;

    public Simulation(Group root) {
        this.root = root;
    }
    public void run(){
        int numberOfTechnicians = 3;
        int numberOfReceptionists = 1;
        int numberOfEquipment = 50;
        if(prop != null) {
            numberOfTechnicians = Integer.parseInt(prop.getProperty("numberOfTechnicians"));
            numberOfReceptionists = Integer.parseInt(prop.getProperty("numberOfReceptionists"));
            numberOfEquipment = Integer.parseInt(prop.getProperty("numberOfEuqipment"));
            semaphoreT = new Semaphore(numberOfTechnicians);
            semaphoreA = new Semaphore(numberOfReceptionists);
            service.MAX_SIZE = Integer.parseInt(prop.getProperty("storageCapacity"));
        }

        receptionist = new Receptionist(0, service, semaphoreA, root);

        for(int i = 0; i < numberOfTechnicians; i++) {//creating technicians
            technician[i] = new Technician(i, service, semaphoreT, accessToSprzet, root);
        }
        for(int i = 0; i < numberOfEquipment; i++) {//creating equipment
            Equipment sprzet = new Equipment(i, root);
            service.addToQueue(sprzet);
        }


        receptionist.start();
        for(int i = 0; i < numberOfTechnicians; i++) {
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
