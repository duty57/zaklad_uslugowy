import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.util.Pair;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.Semaphore;

public class Receptionist extends Thread {

    public String name;
    public int id;
    public float speedRate = 1;
    private float currentSpeedRate = 1;
    private Service service;
    private Equipment equipment;
    private Semaphore semaphore;
    private int goToStorageTime;
    private int writeDownAddressTime;
    private int stepForwardTime;
    private int clientExitTime;
    private boolean semState = false;
    private Group root;
    private ImageView mesh;//image of receptionist
    private Pair<Integer, Integer> defaultPosition;//position of receptionist
    private int iterator = 0;
    private int positionOnStorage = 0;//position of equipment in storage
    private VBox SliderBox;
    Label lengthOfQueue = null;
    private int clientsInQueueToMove = 9;
    private int clientsInQueueToDraw = 9;

    public Receptionist(int id, Service service, Semaphore semaphore, Group root, int goToStorageTime, int writeDownAddressTime, int stepForwardTime, int clientExitTime) {//constructor
        this.id = id;
        this.service = service;
        this.semaphore = semaphore;
        this.name = "Receptionist_" + id;
        this.goToStorageTime = goToStorageTime;
        this.writeDownAddressTime = writeDownAddressTime;
        this.stepForwardTime = stepForwardTime;
        this.clientExitTime = clientExitTime;
        this.root = root;
        defaultPosition = new Pair<>(700, 275);
        Platform.runLater(this::draw);
        Platform.runLater(this::drawSlider);
        Platform.runLater(this::drawButton);
        Platform.runLater(this::drawLengthOfQueue);
    }

    public void run() {
        while (service.getQueueSize() > 0) {//while thread is not interrupted

                if (iterator == 0) {//draw clients only once
                    Platform.runLater(this::drawClients);
                }
                try {
                    semaphore.acquire();//acquire semaphore

                    speedRate = currentSpeedRate;
                    if(service.getQueueSize() > 10 || clientsInQueueToDraw == 0){
                        Platform.runLater(this::drawClient);
                        clientsInQueueToMove = (clientsInQueueToDraw == 0) ? 0 : 9;
                    }else{
                        clientsInQueueToMove--;
                        clientsInQueueToDraw = (clientsInQueueToDraw > 0) ? clientsInQueueToDraw-1 : 0;
                    }
                    addEquipment();
                    System.out.println("Amount of equipment in storage: " + service.getEquipmentSize());
                    System.out.println("Length of queue: " + service.getQueueSize());
                    Platform.runLater(this::drawLengthOfQueue);
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                iterator++;
                positionOnStorage++;
                positionOnStorage = positionOnStorage % 30;
            }
        }


    public void addEquipment() {//add equipment to storage
        try {
            if (service.getEquipmentSize() <= service.MAX_SIZE) {//if storage is not full
                this.equipment = service.takeFromQueue();
                System.out.println("Equipment: " + this.equipment.id + " was added to storage");
                writeDownAddress();
                service.removeFromQueue(equipment);

                if(service.getQueueSize() > 0){
                    if (this.equipment.getPosition().getKey() > 730) {
                        this.equipment.goToFirstPosition((int) (2 * goToStorageTime / speedRate));
                        Thread.sleep((long) (2 * goToStorageTime / speedRate));
                    }
                        moveClient();
                        goToStorage();
                        Thread.sleep((long) (goToStorageTime / speedRate));
                        goBack();
                        Thread.sleep((long) (goToStorageTime / speedRate));
                        service.addEquipment(equipment);

                }
            } else {
                service.removeFromQueue(equipment);
                service.addToQueue(equipment);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void writeDownAddress() {//write down address of equipment
        Platform.runLater(this::writeDownAdress_d);
        try {
            Thread.sleep((long) (writeDownAddressTime / speedRate));
            this.equipment.setAddress(this.equipment.getOwner().getAddress());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void draw() {//draw receptionist
        //draw png with name and color
        Image image = null;
        try {
            image = new Image(new FileInputStream("images/actor.png"));//image of receptionist
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.mesh = new ImageView(image);
        this.mesh.setX(defaultPosition.getKey());
        this.mesh.setY(defaultPosition.getValue());
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

    public void goToStorage() {//go to storage
        //translate imageView to another position and back as animation
        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(goToStorageTime / speedRate), mesh);
        translateTransition.setByX(320 - defaultPosition.getKey());
        translateTransition.setByY(-7 * positionOnStorage - 20);
        this.equipment.goToReceptionist(defaultPosition, (int) (goToStorageTime / speedRate), clientExitTime);
        try {
            Thread.sleep((long) (goToStorageTime / speedRate));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.equipment.goToStorage((int) (goToStorageTime / speedRate));
        translateTransition.play();

    }

    public void goBack() {//go back to receptionist
        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(goToStorageTime / speedRate), mesh);
        translateTransition.setToX(0);
        translateTransition.setToY(0);
        translateTransition.play();
    }

    public void drawClients() {//draw clients in queue
        for (int i = 0; i < 10; i++) {
            Service.queue.get(i).draw(new Pair<>(700 + i * 75, 350));
        }
    }
    public void drawClient(){
        Service.queue.get(clientsInQueueToDraw).draw(new Pair<>(700 + 9 * 75, 350));//not 9
    }

    public void moveClient() {//move clients in queue
        for (int i = 0; i < clientsInQueueToMove; i++) {
            Service.queue.get(i).stepForward((int) (stepForwardTime / speedRate));
        }
    }

    public void writeDownAdress_d() {//write down address of equipment
        Rectangle note = new Rectangle(defaultPosition.getKey() + 25, defaultPosition.getValue() + 25, 15, 5);
        note.setFill(Color.YELLOW);
        root.getChildren().add(note);
        this.equipment.setNote(note, new Pair<>(defaultPosition.getKey() + 25, defaultPosition.getValue() + 25));
    }


    public void drawSlider() {//draw slider

        SliderBox = new VBox();
        SliderBox.setLayoutX(100);
        SliderBox.setLayoutY(500);
        SliderBox.setSpacing(10);

        Label label = new Label("Receptionist's speed rate:\n" + currentSpeedRate);

        Slider slider = new Slider();
        slider.setMin(0.25);
        slider.setMax(4);
        slider.setValue(1);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(10);
        slider.setBlockIncrement(0.5);
        slider.setLayoutX(50);
        slider.setLayoutY(50);

        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            currentSpeedRate = newValue.floatValue();
            label.setText("Receptionist's speed rate:\n" + currentSpeedRate);

        });

        SliderBox.getChildren().add(label);
        SliderBox.getChildren().add(slider);

        root.getChildren().add(SliderBox);
    }

    public void drawButton() {
        Button button = new Button("Add more clients");
        button.setLayoutX(250);
        button.setLayoutY(600);
        button.setOnAction(e -> addMoreEquipment());
        root.getChildren().add(button);
    }

    public void addMoreEquipment() {
        int n = 15;

        service.createEquipment(n, root);
    }
    public void drawLengthOfQueue(){

        if(lengthOfQueue != null){
            root.getChildren().remove(lengthOfQueue);
        }

        lengthOfQueue = new Label("Length of queue: " + service.getQueueSize());
        lengthOfQueue.setLayoutX(1050);
        lengthOfQueue.setLayoutY(500);
        root.getChildren().add(lengthOfQueue);
    }
}

