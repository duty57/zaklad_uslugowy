
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;

import java.awt.*;
import java.io.*;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.Semaphore;

import static javafx.application.Application.launch;

public class Main extends Application {

    private boolean isRunning = false;
    private Properties prop = readProperties();
    public static void main(String[] args) {
        launch(args);

    }

    @Override
    public void start(Stage stage) throws Exception {

        Group root = new Group();//new Pane();

        Rectangle workPlace = new Rectangle(800, 0, 400, 50);//work place where technicians work
        workPlace.setFill(Color.GRAY);
        workPlace.setStroke(Color.BLACK);
        workPlace.setStrokeWidth(2);

        Rectangle road = new Rectangle(600, 300, 600, 100);//road where visitors come
        road.setFill(Color.INDIANRED);

        Rectangle storage = new Rectangle(300, 0, 50, 300);//storage for the equipment
        storage.setFill(Color.GRAY);
        storage.setStroke(Color.BLACK);
        storage.setStrokeWidth(2);

        Rectangle readyForSend = new Rectangle(1175, 150, 25, 100);//place where equipment is ready for send
        readyForSend.setFill(Color.LIGHTYELLOW);
        readyForSend.setStroke(Color.BLACK);
        readyForSend.setStrokeWidth(2);

        Rectangle adminTable = new Rectangle(600, 300, 600, 50);//admin table
        adminTable.setStroke(Color.BLACK);
        adminTable.setStrokeWidth(2);
        adminTable.setFill(Color.WHITE);

        VBox setQueueSize = new VBox();//set queue size
        setQueueSize.setSpacing(10);
        setQueueSize.setLayoutX(200);
        setQueueSize.setLayoutY(500);

        Label queueSizeLabel = new Label("Set queue size:");//label for queue size
        TextField queueSize = new TextField();
        queueSize.setText(prop.getProperty("numberOfEquipment"));
        queueSize.setMaxWidth(50);
        setQueueSize.getChildren().addAll(queueSizeLabel, queueSize);


        VBox setStorageSize = new VBox();//set storage size
        setStorageSize.setSpacing(10);
        setStorageSize.setLayoutX(300);
        setStorageSize.setLayoutY(500);

        Label storageSizeLabel = new Label("Set storage size:");//label for storage size
        TextField storageSize = new TextField();
        storageSize.setText(prop.getProperty("storageCapacity"));
        storageSize.setMaxWidth(50);
        setStorageSize.getChildren().addAll(storageSizeLabel, storageSize);

        Button button = new Button("Start");
        button.setLayoutX(250);
        button.setLayoutY(650);
        button.setOnAction(e -> {
            if (!isRunning) {
                Simulation sim = new Simulation(root, adminTable, prop);
                sim.start();
                isRunning = true;
                prop.setProperty("numberOfEquipment", queueSize.getText());
                prop.setProperty("storageCapacity", storageSize.getText());
                OutputStream output = null;
                try {
                    output = new FileOutputStream("config.properties");

                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
                try {
                    prop.store(output, null);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                root.getChildren().remove(setQueueSize);
                root.getChildren().remove(setStorageSize);
            }
        });

        Label amountOfEquipment = new Label("Amount of equipment in Storage:");//label for amount of equipment

        Text entry = new Text(1050, 450, "Entry");//entry for visitors
        entry.fontProperty().set(javafx.scene.text.Font.font(24));

        Text exit = new Text(0, 400, "Exit");// exit for visitors
        exit.fontProperty().set(javafx.scene.text.Font.font(24));

        //add all elements to root
        root.getChildren().add(entry);
        root.getChildren().add(exit);
        root.getChildren().add(workPlace);
        root.getChildren().add(road);
        root.getChildren().add(storage);
        root.getChildren().add(readyForSend);
        root.getChildren().add(adminTable);
        root.getChildren().add(button);
        root.getChildren().add(setQueueSize);
        root.getChildren().add(setStorageSize);

        stage.setTitle("Canvas");
        Scene scene = new Scene(root, 1200, 800, Color.WHITE);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
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
