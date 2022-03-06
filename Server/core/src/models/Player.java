package models;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.math.Rectangle;

public class Player extends GameCharacter {
    private final BitmapFont font;
    private static MapLayer map;
    private Rectangle res;

    public Player(float x, float y, float angle, String name, MapLayer tiledMapTileLayer) {
        super(x, y, 64, angle, name, tiledMapTileLayer, "player1.png");
        font = new BitmapFont();
        map = tiledMapTileLayer;
        res = new Rectangle(x, y, 32, 32);
    }

    @Override
    public void move() {
        super.move();
    }

    @Override
    public void render(Batch batch) {
        super.render(batch);
        font.draw(batch, name, position.x - 35, position.y + 45);
        for (Bullet bullet : bullets) {
            bullet.render(batch);
        }
    }

    public static Player createPlayer(float x, float y, float angle, String name) {
        return new Player(x, y, angle, name, (MapLayer) map) ;
    }

}
