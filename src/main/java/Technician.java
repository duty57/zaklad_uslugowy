import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.util.Pair;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Technician extends Thread {


    public String name;
    private int id;
    private int[] reparationTime;
    private Service service;
    private Equipment equipment;
    private int time;
    private float speedRate = 1;
    private float currentSpeedRate = 1;
    private int goToStorageTime;
    private int packTime;
    private int putAsideTime;
    private Semaphore semaphore;
    private Semaphore accessToEquipment;
    private static Circle semaphoreState;
    private Group root;
    private ImageView Mesh;// mesh of technician
    private Pair<Integer, Integer> position;// position of technician
    private Circle progressBar;// progress bar of fixing equipment
    private Rectangle packMesh;// mesh of packed equipment
    private Pair<Integer, Integer> packPos;// position of packed equipment
    private VBox box;

    public Technician(int id, Service service, Semaphore semaphore, Semaphore accessToSprzet, Group root, int goToStorageTime, int [] repairTime, int packTime, int putAsideTime) {// constructor
        this.id = id;
        this.service = service;
        this.semaphore = semaphore;
        this.accessToEquipment = accessToSprzet;
        this.name = "Technician_" + id;
        this.goToStorageTime = goToStorageTime;
        this.reparationTime = repairTime;
        this.packTime = packTime;
        this.putAsideTime = putAsideTime;
        this.root = root;
        position = new Pair<>(850 + 100 * id, 25);
        Platform.runLater(this::draw);
        Platform.runLater(this::drawSlider);
    }

    public void run() {

        while (service.getEquipmentSize() > 0 || service.getQueueSize() > 0) {// while there are equipment to fix
            try {
                speedRate = currentSpeedRate;
                semaphore.acquire();
                accessToEquipment.acquire();
                boolean ifAvaliableEquipment = takeEquipment();
                if (ifAvaliableEquipment) {
                    accessToEquipment.release();
                    this.fix();
                    this.packEquipment();
                    this.putAside();
                } else {
                    accessToEquipment.release();
                }

                semaphore.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean takeEquipment() {// take equipment from service
        try {
            Receptionist.amountOfEquipmentInStorage--;
            if (service.getEquipmentSize() == 0) {
                return false;
            }
            this.equipment = service.takeEquipment();
            if (this.equipment != null) {// if there is equipment to fix
                System.out.println("Technician " + this.id + " fixing equipment nr " + this.equipment.id);
                service.removeEquipment(equipment);
                goForEquipment();
                Thread.sleep((long) (goToStorageTime / speedRate));
                goBack();
                Thread.sleep((long) (goToStorageTime / speedRate));
                return true;
            } else {
                return false;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("Technician " + this.id + " can not take equipment.");
            return false;
        }
    }


    public void fix() {// fix equipment
        try {

            Random rand = new Random();
            switch (this.equipment.getState()) {
                case Equipment.State.WEAK_DAMAGED:
                    time = (int) ((rand.nextInt(reparationTime[0]) + reparationTime[0]) / speedRate);
                    fix_d();
                    Thread.sleep((long) (time / speedRate));
                    break;
                case Equipment.State.DAMAGED:
                    time = (int) ((rand.nextInt(reparationTime[1]) + reparationTime[1]) / speedRate);
                    fix_d();
                    Thread.sleep((long) (time / speedRate));
                    break;
                case Equipment.State.NOT_WORKING:
                    time = (int) ((rand.nextInt(reparationTime[2]) + reparationTime[2]) / speedRate);
                    fix_d();
                    Thread.sleep((long) (time / speedRate));
                    break;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void packEquipment() {// pack equipment
        try {
            Platform.runLater(this::pack_d);
            Thread.sleep((long) (packTime / speedRate));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void putAside() {// put aside equipment
        try {
            Platform.runLater(this::putAside_d);
            Thread.sleep((long) (putAsideTime / speedRate));
            Platform.runLater(this::goBack2);
            Thread.sleep((long) (putAsideTime / speedRate));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void draw() {// draw technician

        Image image = null;
        try {
            image = new Image(new FileInputStream("images/actor.png"));// load image
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.Mesh = new ImageView(image);
        this.Mesh.setX(position.getKey());
        this.Mesh.setY(position.getValue());
        this.Mesh.setFitHeight(50);
        this.Mesh.setFitWidth(50);
        this.Mesh.toBack();

        this.semaphoreState = new Circle(365, 10, 5, Color.GREEN);// semaphore state

        this.progressBar = new Circle(position.getKey() + 25, position.getValue() + 60, 2, Color.GREEN);// progress bar
        this.progressBar.toFront();
        this.progressBar.setVisible(false);

        root.getChildren().add(this.Mesh);
        root.getChildren().add(this.progressBar);
        root.getChildren().add(this.semaphoreState);
    }

    public void goForEquipment() {// go for equipment
        semaphoreState.setFill(Color.RED);
        TranslateTransition tt = new TranslateTransition(Duration.millis((int) (goToStorageTime / (speedRate))), Mesh);
        tt.byXProperty().set(350 - position.getKey());
        tt.byYProperty().set(250 - position.getValue());
        tt.play();
    }

    public void goBack() {// go back
        this.equipment.goToTechnician(new Pair<>(350, 250), (int) (goToStorageTime / (4 * speedRate)));
        try {
            Thread.sleep((long) (goToStorageTime / (4 * speedRate)));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        TranslateTransition tt = new TranslateTransition(Duration.millis((int) (goToStorageTime / (speedRate))), Mesh);
        tt.setToX(0);
        tt.setToY(0);
        this.equipment.goToWorkPlace(new Pair<>(850 + 100 * id, 25), (int) (goToStorageTime / speedRate));
        tt.play();
        semaphoreState.setFill(Color.GREEN);

    }


    public void fix_d() {// fix equipment
        progressBar.setVisible(true);
        ScaleTransition st = new ScaleTransition();
        st.setNode(progressBar);
        st.setByX(2);
        st.setByY(2);
        st.setDuration(Duration.millis(time / speedRate));
        st.play();
    }

    public void pack_d() {// pack equipment
        progressBar.setVisible(false);// hide progress bar
        equipment.getMesh().setVisible(false);// hide equipment mesh
        ScaleTransition st = new ScaleTransition();// show progress bar
        st.setNode(progressBar);
        st.setByX(-2);
        st.setByY(-2);
        st.setDuration(Duration.millis(packTime / speedRate));
        st.play();

        packMesh = new Rectangle(25, 25);
        packMesh.setFill(Color.SANDYBROWN);
        packMesh.setX(equipment.getPosition().getKey() - 10);
        packMesh.setY(equipment.getPosition().getValue() + 10);
        packPos = new Pair<>(equipment.getPosition().getKey() - 10, equipment.getPosition().getValue() + 10);
        equipment.putNoteOnBox_d();

        box = new VBox();
        box.setLayoutX(position.getKey());
        box.setLayoutY(position.getValue());
        root.getChildren().remove(equipment.getNoteMesh());
        box.getChildren().add(packMesh);
        box.getChildren().addAll(equipment.getNoteMesh());
        root.getChildren().add(box);


    }

    public void putAside_d() {// put aside equipment
        TranslateTransition tt = new TranslateTransition();
        tt.setNode(Mesh);
        tt.byXProperty().set(1180 - position.getKey());
        tt.setByY(175 - position.getValue());
        tt.setDuration(Duration.millis(putAsideTime / speedRate));

        TranslateTransition ttb = new TranslateTransition();
        ttb.setNode(this.box);
        ttb.byXProperty().set(1205 - position.getKey());
        ttb.byYProperty().set(175 - position.getValue());
        ttb.setDuration(Duration.millis(putAsideTime / speedRate));

        tt.play();
        ttb.play();
        root.getChildren().remove(this.equipment.getOwner().getMesh());

    }

    public void goBack2() {// go back
        TranslateTransition tt = new TranslateTransition();
        tt.setNode(Mesh);
        tt.setToX(0);
        tt.setToY(0);
        tt.setDuration(Duration.millis(putAsideTime / speedRate));
        tt.play();
        root.getChildren().remove(box);
        root.getChildren().remove(packMesh);
        root.getChildren().removeAll(equipment.getNoteMesh());
        root.getChildren().remove(equipment.getMesh());
    }

    public void drawSlider() {//draw slider

        VBox SliderBox = new VBox();
        SliderBox.setLayoutX(100 + 150 * (id + 1));
        SliderBox.setLayoutY(500);
        SliderBox.setSpacing(10);

        Label label = new Label("Technician's " + id + " speed rate:\n" + currentSpeedRate);

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
            label.setText("Technician's " + id + " speed rate:\n" + currentSpeedRate);

        });

        SliderBox.getChildren().add(label);
        SliderBox.getChildren().add(slider);

        root.getChildren().add(SliderBox);

    }
}
