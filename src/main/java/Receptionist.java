import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.util.Pair;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.Semaphore;

public class Receptionist extends Thread{

    public String name;
    public int id;

    private Service service;
    private Equipment equipment;
    private Semaphore semaphore;
    private int goToStorageTime;
    private int writeDownAddressTime;
    private int stepForwardTime;
    private int clientExitTime;
    private Group root;
    private ImageView mesh;//image of receptionist
    private Pair<Integer, Integer> position;//position of receptionist
    private int iterator = 0;
    private int positionOnStorage = 0;//position of equipment in storage

    public Receptionist(int id, Service service, Semaphore semaphore, Group root, int goToStorageTime, int writeDownAddressTime, int stepForwardTime, int clientExitTime){//constructor
        this.id = id;
        this.service = service;
        this.semaphore = semaphore;
        this.name = "Receptionist_" + id;
        this.goToStorageTime = goToStorageTime;
        this.writeDownAddressTime = writeDownAddressTime;
        this.stepForwardTime = stepForwardTime;
        this.clientExitTime = clientExitTime;
        this.root = root;
        position = new Pair<>(700, 275);
        Platform.runLater(this::draw);
    }

    public void run(){
        while(service.getQueueSize() > 0){//while there are clients in queue
            if(iterator == 0){//draw clients only once
                Platform.runLater(this::drawClients);
            }
            try{
                semaphore.acquire();//acquire semaphore
                addEquipment();
                System.out.println("Amount of equipment in storage: " + service.getEquipment());
                System.out.println("Length of queue: " + service.getQueueSize());
                semaphore.release();
            }catch(InterruptedException e){
                e.printStackTrace();
            }
            iterator++;
            positionOnStorage++;
            positionOnStorage = positionOnStorage % 30;
        }
    }

    public void addEquipment(){//add equipment to storage
        try {
            if(service.getEquipment() <= service.MAX_SIZE){//if storage is not full
                this.equipment = service.takeFromQueue();
                System.out.println("Sprzet: " + equipment.id + " dodany do magazynu");
                writeDownAddress();
                service.removeFromQueue(equipment);
                moveClient();
                goToStorage();
                Thread.sleep(goToStorageTime);
                goBack();
                Thread.sleep(goToStorageTime);
                service.addEquipment(equipment);
            }else {
                service.removeFromQueue(equipment);
                service.addToQueue(equipment);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void writeDownAddress(){//write down address of equipment
        Platform.runLater(this::writeDownAdress_d);
        try {
            Thread.sleep(writeDownAddressTime);
            this.equipment.setAddress(this.equipment.getOwner().getAddress());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void draw(){//draw receptionist
        //draw png with name and color
        Image image = null;
        try {
            image = new Image(new FileInputStream("images/actor.png"));//image of receptionist
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.mesh = new ImageView(image);
        this.mesh.setX(position.getKey());
        this.mesh.setY(position.getValue());
        this.mesh.setFitHeight(50);
        this.mesh.setFitWidth(50);
        this.mesh.toBack();

        Rectangle adminTable = new Rectangle(600, 300, 600, 50);
        adminTable.setStroke(Color.BLACK);
        adminTable.setStrokeWidth(2);
        adminTable.setFill(Color.WHITE);

        root.getChildren().add(this.mesh);
        root.getChildren().add(adminTable);


    }

    public void goToStorage(){//go to storage
        //translate imageView to another position and back as animation
        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(goToStorageTime), mesh);
        translateTransition.byXProperty().set(320-position.getKey());
        translateTransition.byYProperty().set(-7*positionOnStorage);
        this.equipment.goToReceptionist(position, goToStorageTime, clientExitTime);
        try{
            Thread.sleep(goToStorageTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.equipment.goToStorage(goToStorageTime);

        translateTransition.play();
    }

    public void drawClients(){//draw clients in queue
        for(int i = 0; i < Service.queue.size(); i++) {
            Service.queue.get(i).draw(new Pair<>(700 + i * 75, 350));
        }
    }

    public void moveClient(){//move clients in queue
        for(int i = 0; i < Service.queue.size(); i++){
            Service.queue.get(i).stepForward(stepForwardTime);
        }
    }

    public void writeDownAdress_d(){//write down address of equipment
        Rectangle note = new Rectangle(position.getKey()+25, position.getValue()+25, 15, 5);
        note.setFill(Color.YELLOW);
        root.getChildren().add(note);
        this.equipment.setNote(note, new Pair<>(position.getKey()+25, position.getValue()+25));
    }
    public void goBack(){//go back to receptionist
        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(goToStorageTime), mesh);
        translateTransition.byXProperty().set(-320+position.getKey());
        translateTransition.byYProperty().set(7*positionOnStorage);
        translateTransition.play();

    }
}

