package viewfx.main.utilities;

import viewfx.ViewController;
import javafx.animation.Animation;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SandboxFX extends Application {

    private static final Image IMAGE = new Image(SandboxFX.class.getResourceAsStream("/res/symbols/loading_sprite.png"));

    private static final int COLUMNS  =   9;
    private static final int COUNT    =  9;
    private static final int OFFSET_X =  0;
    private static final int OFFSET_Y =  0;
    private static final int WIDTH    = 81;
    private static final int HEIGHT   = 80;

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {
        primaryStage.setTitle("The Horse in Motion");

        final ImageView imageView = new ImageView(IMAGE);
        System.out.println(imageView.getFitWidth());
        imageView.setViewport(new Rectangle2D(OFFSET_X, OFFSET_Y, WIDTH, HEIGHT));

        final Animation animation = new SpriteAnimation(
                imageView,
                Duration.millis(2000),
                COUNT, COLUMNS,
                OFFSET_X, OFFSET_Y,
                WIDTH, HEIGHT
        );
        animation.setCycleCount(Animation.INDEFINITE);
//        animation.play();

        primaryStage.setScene(new Scene(new Group(imageView)));
        primaryStage.show();
    }
}