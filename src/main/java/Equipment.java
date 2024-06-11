import javafx.animation.TranslateTransition;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.util.Pair;

import static java.lang.Thread.sleep;

public class Equipment {

    enum State {//state of equipment
        WEAK_DAMAGED, DAMAGED, NOT_WORKING
    }


    private final Color[] colors = {
            Color.RED, // red
            Color.GREEN, // green
            Color.BLUE, // blue
            Color.YELLOW, // yellow
            Color.MAGENTA, // magenta
            Color.CYAN, // cyan
            Color.PURPLE, //purple
            Color.OLIVE, //olive
            Color.TEAL, //teal
            Color.PINK //pink
    };
    private final String[] nameOfEquipment = {"Laptop", "Monitor", "Klawiatura", "Myszka", "Drukarka", "Telefon", "Tablet", "Router", "Glosniki", "Kamera"};//10

    private static int counter = 0;//counter for colors
    public String name;
    private Color color;
    private State state;//state of equipment
    public String address;
    private Owner owner;//owner of equipment
    private Group root;//root of scene
    private Pair<Integer, Integer> position;//position of equipment
    private Rectangle noteMesh;//note on equipment
    private Pair<Integer, Integer> notePos;//position of note
    public static int numberOfEquipment = 0;
    public int id;
    private Circle Mesh;//mesh of equipment

    public static int positionOnStorage = 0;//position on storage

    Equipment(Group root) {//constructor
        generateName();
        generateColor();
        generateState();
        owner = new Owner(root, position);
        this.numberOfEquipment++;
        id = numberOfEquipment;
        this.root = root;
    }

    public void generateName() {//generate name of equipment
        int i = (int) (Math.random() * 10);
        name = nameOfEquipment[i];
    }

    public void generateColor() {//generate color of equipment
        int i = counter % 10;
        color = colors[i];
        counter++;
    }

    public void generateState() {//generate state of equipment
        int i = (int) (Math.random() * 3);
        state = State.values()[i];
    }

    public State getState() {
        return state;
    }//get state of equipment

    public Owner getOwner() {
        return this.owner;
    }//get owner of equipment

    public void setAddress(String address) {
        this.address = address;
    }//set address of equipment

    public void setNote(Rectangle note, Pair<Integer, Integer> pos) {//set note on equipment
        this.noteMesh = note;
        this.notePos = pos;
    }

    public void draw(Pair<Integer, Integer> pos) {//draw equipment
        position = pos;
        this.Mesh = new Circle(pos.getKey(), pos.getValue(), 10, color);//create mesh of equipment
        root.getChildren().add(this.Mesh);//add mesh to root
        owner.draw(new Pair<>(pos.getKey(), pos.getValue()));
    }

    public void stepForward(int time) {//step forward of equipment
        TranslateTransition tt = new TranslateTransition();
        tt.setNode(this.Mesh);
        tt.byXProperty().set(-75);
        tt.byYProperty().set(0);
        tt.setDuration(javafx.util.Duration.millis(time));
        this.owner.stepForward(time);
        tt.play();//play animation
        if(position != null) {
            position = new Pair<>(position.getKey() - 75, position.getValue());
        }
    }

    public void goToReceptionist(Pair<Integer, Integer> pos, int time, int exitTime) {//set position of equipment to receptionist
        TranslateTransition tt = new TranslateTransition(Duration.millis(time), this.Mesh);
        tt.byXProperty().set(0);
        tt.byYProperty().set(-50);
        this.owner.exit(exitTime);
        tt.play();//play animation
        position = new Pair<>(pos.getKey(), pos.getValue());
    }

    public void goToStorage(int time) {//set position of equipment to storage
        TranslateTransition tt = new TranslateTransition(Duration.millis(time), this.Mesh);//create animation for equipment
        tt.byXProperty().set(320 - position.getKey());
        tt.byYProperty().set(250 - position.getValue() - positionOnStorage * 7);

        TranslateTransition ttn = new TranslateTransition(Duration.millis(time), this.noteMesh);//create animation for note
        ttn.byXProperty().set(310 - position.getKey());
        ttn.byYProperty().set(250 - position.getValue() - positionOnStorage * 7);

        tt.play();//play animation
        ttn.play();//play animation
        position = new Pair<>(320, 250 - positionOnStorage * 7);//set position of equipment
        notePos = new Pair<>(310, 250 - positionOnStorage * 7);//set position of note
        positionOnStorage++;
        positionOnStorage = positionOnStorage % 30;
    }

    public void goToTechnician(Pair<Integer, Integer> pos, int time) {//set position of equipment to technician
        TranslateTransition tt = new TranslateTransition(Duration.millis(time), this.Mesh);//create animation for equipment
        tt.byXProperty().set(pos.getKey() - position.getKey());
        tt.byYProperty().set(pos.getValue() - position.getValue());

        TranslateTransition ttn = new TranslateTransition(Duration.millis(time), this.noteMesh);//create animation for note
        ttn.byYProperty().set(pos.getValue() - notePos.getValue());
        ttn.byXProperty().set(pos.getKey() - notePos.getKey());

        ttn.play();//play animation
        tt.play();//play animation
        position = pos;
        notePos = pos;
    }

    public void goToWorkPlace(Pair<Integer, Integer> pos, int time) {//set position of equipment to work place
        TranslateTransition tt = new TranslateTransition(Duration.millis(time), this.Mesh);//create animation for equipment
        tt.byXProperty().set(pos.getKey() - position.getKey());
        tt.byYProperty().set(pos.getValue() - position.getValue());


        TranslateTransition ttn = new TranslateTransition(Duration.millis(time), this.noteMesh);//create animation for note
        ttn.byYProperty().set(pos.getValue() - notePos.getValue());
        ttn.byXProperty().set(pos.getKey() - notePos.getKey());

        tt.play();//play animation
        ttn.play();//play animation
        position = pos;
        notePos = pos;
    }
    public void putNoteOnBox_d() {//put note on box animation

        TranslateTransition ttn = new TranslateTransition(Duration.millis(1), this.noteMesh);//create animation for note
        ttn.setToX(0);
        ttn.setToY(-15);
        ttn.play();
    }

    public Pair<Integer, Integer> getPosition() {
        return position;
    }

    public Circle getMesh() {
        return this.Mesh;
    }

    public Rectangle getNoteMesh() {
        return this.noteMesh;
    }

    public Pair<Integer, Integer> getNotePos() {
        return notePos;
    }
}
