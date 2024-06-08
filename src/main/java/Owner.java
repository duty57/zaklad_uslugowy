import javafx.animation.TranslateTransition;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Pair;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

import static java.lang.Thread.sleep;


public class Owner {


    public static String names[];
    public static String surnames[];
    public static String cities[];
    public static String streets[];
    private String name;
    private String surname;
    private String address;
    private Group root;
    private Pair<Integer, Integer> position;//position of owner
    private ImageView Mesh;//owner mesh

    public Owner(Group root, Pair<Integer, Integer> pos) {//constructor
        Properties prop = readProperties();
        names = prop.getProperty("names").split(",");
        surnames = prop.getProperty("surnames").split(",");
        cities = prop.getProperty("cities").split(",");
        streets = prop.getProperty("streets").split(",");
        generateName();
        generateSurname();
        generateAddress();
        this.root = root;
    }

    public void generateName() {
        Random rand = new Random();
        name = names[rand.nextInt(names.length)];
    }

    public void generateSurname() {
        Random rand = new Random();
        surname = surnames[rand.nextInt(surnames.length)];
    }

    public void generateAddress() {
        Random city = new Random();
        Random street = new Random();
        Random number = new Random();
        Random flat = new Random();

        address = Owner.cities[city.nextInt(names.length)] + " " + Owner.streets[street.nextInt(Owner.streets.length)] + " " + number.nextInt(200) + "/" + flat.nextInt(60);
    }

    public String getAddress() {//metoda zwracajaca adres Wlasciciela
        return address;
    }

    public void draw(Pair<Integer, Integer> pos) {//method drawing owner
        Image image = null;
        this.position = pos;
        try {
            image = new Image(new FileInputStream("images/actor.png"));//loading image
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.Mesh = new ImageView(image);
        this.Mesh.setX(position.getKey() - 5);
        this.Mesh.setY(position.getValue() - 10);
        this.Mesh.setFitHeight(50);
        this.Mesh.setFitWidth(50);
        this.Mesh.toBack();

        root.getChildren().add(this.Mesh);//adding owner to root
    }

    public void stepForward(int time) {//method moving owner forward
        TranslateTransition tt = new TranslateTransition();
        tt.setNode(Mesh);
        tt.byXProperty().set(-75);
        tt.byYProperty().set(0);
        tt.setDuration(javafx.util.Duration.millis(time));
        tt.play();
        position = new Pair<>(position.getKey() - 75, position.getValue());//changing position
    }

    public void exit(int time) {//method moving owner out of the screen
        TranslateTransition tt = new TranslateTransition();
        tt.setNode(Mesh);
        tt.byXProperty().set(-this.position.getKey() - 200);
        tt.byYProperty().set(0);
        tt.setDuration(javafx.util.Duration.millis(time));
        tt.play();
        position = new Pair<>(-this.position.getKey(), this.position.getValue());//changing position
    }

    public ImageView getMesh() {
        return Mesh;
    }

    public Properties readProperties() {
        Properties prop = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream("config.properties");
            prop.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return prop;
    }
}
