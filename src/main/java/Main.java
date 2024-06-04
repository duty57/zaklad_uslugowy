
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.Semaphore;

import static javafx.application.Application.launch;

public class Main extends Application {



    public static void main(String[] args) {
        //prop = readProperties();
        launch(args);

    }

    @Override
    public void start(Stage stage) throws Exception {

        Group root = new Group();

        Rectangle workPlace = new Rectangle(800, 0, 400, 50);
        workPlace.setFill(Color.GRAY);
        workPlace.setStroke(Color.BLACK);
        workPlace.setStrokeWidth(2);

        Rectangle road = new Rectangle(600, 300, 600, 100);
        road.setFill(Color.INDIANRED);

        Rectangle storage = new Rectangle(300, 0, 50, 300);
        storage.setFill(Color.GRAY);
        storage.setStroke(Color.BLACK);
        storage.setStrokeWidth(2);

        Rectangle readyForSend = new Rectangle(1175, 150, 25, 100);
        readyForSend.setFill(Color.LIGHTYELLOW);
        readyForSend.setStroke(Color.BLACK);
        readyForSend.setStrokeWidth(2);


        Text entry = new Text(1050, 450, "Entry");
        entry.fontProperty().set(javafx.scene.text.Font.font(24));

        Text exit = new Text(0, 400, "Exit");
        exit.fontProperty().set(javafx.scene.text.Font.font(24));

        root.getChildren().add(entry);
        root.getChildren().add(exit);
        root.getChildren().add(workPlace);
        root.getChildren().add(road);
        root.getChildren().add(storage);
        root.getChildren().add(readyForSend);

        stage.setTitle("Canvas");
        Scene scene = new Scene(root, 1200, 800, Color.WHITE);
        stage.setScene(scene);
        stage.show();

        Simulation sim = new Simulation(root);
        sim.start();

    }
}
