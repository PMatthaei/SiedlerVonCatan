package viewfx.client.menu.elements;

import javafx.animation.Animation;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;
import viewfx.utilities.SpriteAnimation;

public class LoadingSprite {
    private static final Image IMAGE = new Image(LoadingSprite.class.getResourceAsStream("/res/symbols/loading_sprite.png"));

    private static final int COLUMNS  =   9;
    private static final int COUNT    =  9;
    private static final int OFFSET_X =  0;
    private static final int OFFSET_Y =  0;
    private static final int WIDTH    = 81;
    private static final int HEIGHT   = 80;
    
    final Animation animation;
    final ImageView imageView;
    
    public LoadingSprite(){
        this.imageView = new ImageView(IMAGE);
        this.imageView.setViewport(new Rectangle2D(OFFSET_X, OFFSET_Y, WIDTH, HEIGHT));

        this.animation = new SpriteAnimation(
                imageView,
                Duration.millis(2000),
                COUNT, COLUMNS,
                OFFSET_X, OFFSET_Y,
                WIDTH, HEIGHT
        );
    }
    
    public void playAnimation(){
        animation.setCycleCount(Animation.INDEFINITE);
        animation.play();
    }
    
    public Animation getAnimation() {
		return animation;
	}
    
    public ImageView getSprite(){
    	return imageView;
    }
}
