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

public class Technician extends Thread{


    public String name;
    private int id;
    private int [] reparationTime = {380, 800, 1700};
    private Service service;
    private Equipment equipment;
    private int time;
    private float speedRate = 1;
    private float currentSpeedRate = 2;
    private int goToStorageTime;
    private int repairTime;
    private int packTime;
    private int putAsideTime;
    private Semaphore semaphore;
    private Semaphore accessToSprzet;
    private Group root;
    private ImageView Mesh;// mesh of technician
    private Pair<Integer, Integer> position;// position of technician
    private Circle progressBar;// progress bar of fixing equipment
    private Rectangle packMesh;// mesh of packed equipment
    private Pair<Integer, Integer> packPos;// position of packed equipment
    public Technician(int id, Service service, Semaphore semaphore, Semaphore accessToSprzet, Group root, int goToStorageTime, int repairTime, int packTime, int putAsideTime){// constructor
        this.id = id;
        this.service = service;
        this.semaphore = semaphore;
        this.accessToSprzet = accessToSprzet;
        this.name = "Technician_" + id;
        this.goToStorageTime = goToStorageTime;
        this.repairTime = repairTime;
        this.packTime = packTime;
        this.putAsideTime = putAsideTime;
        this.root = root;
        position = new Pair<>(850 + 100*id, 25);
        Platform.runLater(this::draw);
        Platform.runLater(this::drawSlider);
    }
    public void run(){

        while(service.getEquipment() > 0 || service.getQueueSize() > 0){// while there are equipment to fix
            try{
                speedRate = currentSpeedRate;
                semaphore.acquire();
                accessToSprzet.acquire();
                boolean czyJestSprzet = takeEquipment();
                if (czyJestSprzet) {
                    accessToSprzet.release();
                    this.fix();
                    this.packEquipment();
                    this.putAside();
                }else{
                    accessToSprzet.release();
                }

                semaphore.release();
            } catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    public boolean takeEquipment(){// take equipment from service
        try {
            if(service.getEquipment() == 0){
                return false;
            }
            this.equipment = service.takeEquipment();
            if (this.equipment != null) {// if there is equipment to fix
                System.out.println("Technician " + this.id + " fixing equipment nr " + this.equipment.id);
                service.removeEquipment(equipment);
                goForEquipment();
                Thread.sleep((long) (goToStorageTime/speedRate));
                goBack();
                Thread.sleep((long) (goToStorageTime/speedRate));
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



    public void fix(){// fix equipment
        try {

            Random rand = new Random();
            switch (this.equipment.getState()) {
                case Equipment.State.WEAK_DAMAGED:
                    time = (int) ((rand.nextInt(reparationTime[0]) + reparationTime[0])/speedRate);
                    fix_d();
                    Thread.sleep((long) (time/speedRate));
                    break;
                case Equipment.State.DAMAGED:
                    time = (int) ((rand.nextInt(reparationTime[1]) + reparationTime[1])/speedRate);
                    fix_d();
                    Thread.sleep((long) (time/speedRate));
                    break;
                case Equipment.State.NOT_WORKING:
                    time = (int) ((rand.nextInt(reparationTime[2]) + reparationTime[2])/speedRate);
                    fix_d();
                    Thread.sleep((long) (time/speedRate));
                    break;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void packEquipment(){// pack equipment
        try {
            Platform.runLater(this::pack_d);
            Thread.sleep((long) (packTime/speedRate));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void putAside(){// put aside equipment
        try {
            putAside_d();
            Thread.sleep((long) (putAsideTime/speedRate));
            goBack2();
            Thread.sleep((long) (putAsideTime/speedRate));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void draw(){// draw technician

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

        this.progressBar = new Circle(position.getKey()+25, position.getValue()+60, 2, Color.GREEN);// progress bar
        this.progressBar.toFront();
        this.progressBar.setVisible(false);

        root.getChildren().add(this.Mesh);
        root.getChildren().add(this.progressBar);
    }

    public void goForEquipment(){// go for equipment
        TranslateTransition tt = new TranslateTransition(Duration.millis((int) (goToStorageTime/(speedRate))), Mesh);
        tt.byXProperty().set(350-position.getKey());
        tt.byYProperty().set(250-position.getValue());
        tt.play();
    }

    public void goBack(){// go back
        this.equipment.goToTechnician(new Pair<>(350, 250), (int) (goToStorageTime/(4*speedRate)));
        try {
            Thread.sleep((long) (goToStorageTime/(4*speedRate)));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        TranslateTransition tt = new TranslateTransition(Duration.millis((int) (goToStorageTime/(4*speedRate))), Mesh);
        tt.setToX(0);
        tt.setToY(0);
        tt.play();
        this.equipment.goToWorkPlace(new Pair<>(850 + 100*id, 25), (int) (goToStorageTime/speedRate));
    }


    public void fix_d(){// fix equipment
        progressBar.setVisible(true);
        ScaleTransition st = new ScaleTransition();
        st.setNode(progressBar);
        st.setByX(2);
        st.setByY(2);
        st.setDuration(Duration.millis(time/speedRate));
        st.play();
    }

        public void pack_d(){// pack equipment
        progressBar.setVisible(false);
        equipment.getMesh().setVisible(false);
        ScaleTransition st = new ScaleTransition();
        st.setNode(progressBar);
        st.setByX(-2);
        st.setByY(-2);
        st.setDuration(Duration.millis(packTime/speedRate));
        st.play();
        packMesh = new Rectangle(25,25);
        packMesh.setFill(Color.SANDYBROWN);
        packMesh.setX(equipment.getPosition().getKey()-10);
        packMesh.setY(equipment.getPosition().getValue()+10);
        root.getChildren().add(packMesh);
        packPos = new Pair<>(equipment.getPosition().getKey()-10, equipment.getPosition().getValue()+10);
        equipment.putNoteOnBox_d(packPos.getKey(), packPos.getValue(), (int)(packTime/speedRate));

    }
    public void putAside_d(){// put aside equipment
        TranslateTransition tt = new TranslateTransition();
        tt.setNode(Mesh);
        tt.byXProperty().set(1180-position.getKey());
        tt.setByY(175-position.getValue());
        tt.setDuration(Duration.millis(putAsideTime/speedRate));

        TranslateTransition ttp = new TranslateTransition();
        ttp.setNode(packMesh);
        ttp.byXProperty().set(1205-packPos.getKey());
        ttp.byYProperty().set(175-packPos.getValue());
        ttp.setDuration(Duration.millis(putAsideTime/speedRate));

        TranslateTransition ttn = new TranslateTransition();
        ttn.setNode(equipment.getNoteMesh());
        ttn.byXProperty().set(1205-packPos.getKey());
        ttn.byYProperty().set(175-packPos.getValue());
        ttn.setDuration(Duration.millis(putAsideTime/speedRate));

        tt.play();
        ttp.play();
        ttn.play();
    }
    public void goBack2(){// go back
        TranslateTransition tt = new TranslateTransition();
        tt.setNode(Mesh);
        tt.setToX(0);
        tt.setToY(0);
        tt.setDuration(Duration.millis(putAsideTime/speedRate));
        tt.play();
    }
    public void drawSlider(){//draw slider

        VBox SliderBox = new VBox();
        SliderBox.setLayoutX(100+150*(id+1));
        SliderBox.setLayoutY(500);
        SliderBox.setSpacing(10);

        Label label = new Label("Technician's "+ id + " speed rate:\n" + currentSpeedRate);

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
            label.setText("Technician's "+ id + " speed rate:\n" + currentSpeedRate);

        });

        SliderBox.getChildren().add(label);
        SliderBox.getChildren().add(slider);

        root.getChildren().add(SliderBox);

    }
}
