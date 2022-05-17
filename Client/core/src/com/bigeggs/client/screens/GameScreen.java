package com.bigeggs.client.screens;

import ClientConnection.ClientConnection;
import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.bigeggs.client.models.Bullet;
import com.bigeggs.client.models.EnemyAI;
import com.bigeggs.client.models.Player;
import com.bigeggs.client.models.Weapon;
import com.bigeggs.client.models.boosts.*;
import com.bigeggs.client.world.ClientWorld;

import java.util.*;

public class GameScreen implements Screen, InputProcessor {
    private Player player;
    private ClientWorld world;
    private ClientConnection clientConnection;
    private OrthographicCamera camera;
    SpriteBatch batch;
    ShapeRenderer shapeRenderer;
    private TextureRegion backgroundTexture;
    private MapLayer collisionLayer;
    private long time = System.currentTimeMillis();

    private OrthogonalTiledMapRenderer tmr;
    private TiledMap map;
    private final Vector2 mouseInWorld2D = new Vector2();
    private Map<Integer, Boost> boosts;
    //text
    private FreeTypeFontGenerator fontGenerator;
    private FreeTypeFontGenerator.FreeTypeFontParameter fontParameter;
    private BitmapFont font, boostFont, scoreFont; //or use alex answer to use custom font

    //health bars
    private Texture tex, liveTex;
    private Sprite sprite, liveSprite;
    private String healthbar = "healthBars/100hp.png";
    private Integer previousHealth = 100;


    private Random rand = new Random();
    protected Sound ammoBoostSound;
    protected Sound healthBoostSound;
    protected Sound speedBoostSound;
    protected Sound invisBoostSound;


    /**
     * Constructor with setting all variables (generate fonts, sounds, textures)
     * @param world for hold all variables from server (players, enemies, boosts)
     */
    public GameScreen(ClientWorld world) {
        this.world = world;
        backgroundTexture = new TextureRegion(new Texture("bluegrey.png"), 0, 0, 3840, 2500);
        batch = new SpriteBatch();
        map = new TmxMapLoader().load("newTiledMap.tmx");
        tmr = new OrthogonalTiledMapRenderer(map, 1);
        collisionLayer = map.getLayers().get("Objects");
        int value = rand.nextInt(10 + 1) + 1;
        player = new Player(750f + value * 5f, 850f, 0f, "kamikadze", (MapLayer) map.getLayers().get("Objects"), "playerIcons/1.png");
        player.setWeapon(new Weapon());
        player.setWorld(world);

        camera = new OrthographicCamera(1000, 800);
        boosts = world.getBoosts();

        shapeRenderer = new ShapeRenderer();
        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/ARCADE.TTF"));
        fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.size = 60;
        fontParameter.color = Color.WHITE;
        font = generateFont(60, Color.WHITE);
        boostFont = generateFont(45, Color.LIGHT_GRAY);
        scoreFont = generateFont(50, Color.ORANGE);

        tex = new Texture(Gdx.files.internal(healthbar));
        sprite = new Sprite(tex, 0, 0, 2000, 2000);
        sprite.setSize(98, 76);

        liveTex = new Texture(Gdx.files.internal("boosts/live.png"));
        liveSprite = new Sprite(liveTex);
        liveSprite.setSize(40, 40);

        healthBoostSound = Gdx.audio.newSound(Gdx.files.internal("SoundEffects/healSound.mp3"));
        speedBoostSound = Gdx.audio.newSound(Gdx.files.internal("SoundEffects/speedBoostSound.mp3"));
        ammoBoostSound = Gdx.audio.newSound(Gdx.files.internal("SoundEffects/ammoBoostSound.mp3"));
        invisBoostSound = Gdx.audio.newSound(Gdx.files.internal("SoundEffects/invisSound.mp3"));
        Pixmap pixmap = new Pixmap(Gdx.files.internal("target.png"));
        Gdx.graphics.setCursor(Gdx.graphics.newCursor(pixmap, 0, 0));

    }

    /**
     * Health bar sprite updating (displaying)
     */
    public void updateHealthBar() {
        if (player.health < 90 && player.health >= 70) {
            healthbar = "healthBars/90hp.png";

        } else if (player.health < 70 && player.health >= 50) {
            healthbar = "healthBars/70hp.png";

        } else if (player.health < 50 && player.health >= 30) {
            healthbar = "healthBars/50hp.png";

        } else if (player.health < 30 && player.health >= 10) {
            healthbar = "healthBars/30hp.png";

        } else if (player.health < 10) {
            healthbar = "healthBars/10hp.png";
        } else {
            healthbar = "healthBars/100hp.png";
        }
        tex = new Texture(Gdx.files.internal(healthbar));
        sprite = new Sprite(tex, 0, 0, 2000, 2000);
        sprite.setSize(98, 76);
    }

    /**
     * Main loop method of class, make all needed methods (drawing, moving, updating)
     */
    @Override
    public void render(float delta) {
        ScreenUtils.clear(1, 1, 1, 1);
        if (player.health != previousHealth) { // if HP have changed, update health bar image
            previousHealth = player.health;
            updateHealthBar();
        }

        // update player, player rotate and move
        player.move();
        player.rotate(getMousePosInGameWorld());
        player.changeFireMode();
        player.reload();
        player.createBullet(new Vector2(getMousePosInGameWorld().x - player.getPosition().x,
                getMousePosInGameWorld().y - player.getPosition().y).nor(), player.name);
        botShooting();

        // rotate and move enemy to player
        moveAndRotateEnemiesAI();

        // update camera(follow player) and set it to batch
        camera.position.set(player.getPosition().x, player.getPosition().y, 0);
        camera.update();
        updateBullets();
        checkCollisions();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(backgroundTexture, -500, -500);
        batch.end();


        tmr.setView(camera);
        tmr.render();
        batch.begin();


        player.render(batch);
        player.displayScore(batch, scoreFont);

        // Different boosts collide with players
        boostsAction();
        player.actWithBoosts(batch, boostFont);
        drawEnemiesAI();
        drawOtherPlayers();
        drawBoosts();
        drawScreenMessages();
        drawLives();

        batch.end();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(Color.BLUE);
        drawBullets(shapeRenderer);
        shapeRenderer.end();

    }

    /**
     * Draw UI, all messages (username, HP, boosts messages, server messages)
     */
    private void drawScreenMessages() {
        sprite.setPosition(player.getPosition().x + 220, player.getPosition().y + 275);
        sprite.draw(batch);
        String ammoMessage = "AMMO:" + player.weapon.getLoadedAmmo() + "/" + player.weapon.getAmmo();
        String hpMessage = "HP:" + player.health;
        font.draw(batch, new String(new char[11 - ammoMessage.length()]).replace("\0", " ") + ammoMessage, player.getPosition().x + 170, player.getPosition().y + 370);
        font.draw(batch, hpMessage, player.getPosition().x + 330, player.getPosition().y + 320);
        if (world.getGameMessage() != null) {
            font.draw(batch, world.getGameMessage(), player.getPosition().x - 470, player.getPosition().y - 350);
        }
        font.draw(batch, player.name, player.getPosition().x - 470, player.getPosition().y + 370);
        int y = 320;
        for (String playersMessage : world.getPlayersMessages()) {
            font.draw(batch, playersMessage, player.getPosition().x - 470, player.getPosition().y + y);
            y -= 50;
        }
        world.checkTime();
    }

    /**
     * Draw lives texture like a sprite
     */
    private void drawLives() {
        for (int i = 0; i < player.lives; i++) {
            liveSprite.setPosition(player.getPosition().x + 440 - (i * 40), player.getPosition().y + 250);
            liveSprite.draw(batch);
        }
    }

    /**
     * @return 3D global position of mouse
     */
    public Vector3 getMousePosInGameWorld() {
        // special mouse position because of Orthographic Camera
        return camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
    }

    /**
     * Check collision with others players in server, and send info to server
     * If collide play sound, and make all actions with boosts
     */
    public void boostsAction() {
        for (Map.Entry<Integer, Boost> integerBoostEntry : boosts.entrySet()) {
            Boost boost = integerBoostEntry.getValue();
            int id = integerBoostEntry.getKey();

            if (boost.collisionWithPlayer(player) && !world.getRemoveBoostIds().contains(id)) {
                if (boost instanceof SpeedBoost) {
                    player.speedBoost = true;
                    speedBoostSound.play(0.5f);
                } else if (boost instanceof HealthBoost) {
                    player.hpBoost = true;
                    healthBoostSound.play(0.5f);
                } else if (boost instanceof InvisibleBoost) {
                    player.invisBoost = true;
                    invisBoostSound.play(0.5f);
                }
                world.removeBoostId(id);
                clientConnection.sendPacketRemoveBoost(id);
            }
        }
        world.removeBoostIdList();
    }

    public void drawBoosts() {
        for (Boost boost : boosts.values()) {
            boost.render(batch);
        }
    }

    /**
     * Draw players and scale based on it health
     */
    public void drawOtherPlayers() {
        List<Player> players = new ArrayList<>(world.getPlayers().values());
        for (Player player1 : players) {
            if (!player1.isInvisible()) { // draw other players
                if (player1.health >= 180) { // check HP and set image size and abstract box parameters  based on HP
                    player1.playerModelScale = 1.6f;
                    player1.abstractX = player1.position.x - 24;
                    player1.abstractY = player1.position.y - 19.2f;
                    player1.abstractWidth = 59f;
                    player1.abstractHeight = 64f;
                    // Move bullets source point outside abstract box, so that bullet does not collide with it's own player
                    player1.barrelOffset = new Vector2(83.0f, -48.0f).scl(0.5f);
                } else if (player1.health > 140 && player1.health < 171) {
                    player1.playerModelScale = 1.4f;
                    player1.abstractX = player1.position.x - 21;
                    player1.abstractY = player1.position.y - 16.8f;
                    player1.abstractWidth = 51.8f;
                    player1.abstractHeight = 56f;
                    player1.barrelOffset = new Vector2(70.0f, -40.0f).scl(0.5f);

                } else if (player1.health > 101 && player1.health < 141) {
                    player1.playerModelScale = 1.2f;
                    player1.abstractX = player1.position.x - 18;
                    player1.abstractY = player1.position.y - 14.4f;
                    player1.abstractWidth = 44.4f;
                    player1.abstractHeight = 48f;
                    player1.barrelOffset = new Vector2(60.0f, -37.0f).scl(0.5f);
                } else if (player1.health == 100) { //37 40
                    player1.playerModelScale = 1f;
                    player1.abstractX = player1.position.x - 15;
                    player1.abstractY = player1.position.y - 12;
                    player1.barrelOffset = new Vector2(45.0f, -27.0f).scl(0.5f);

                } else if (player1.health < 91 && player1.health > 70) {
                    player1.playerModelScale = 0.95f;
                    player1.abstractX = player1.position.x - 14;
                    player1.abstractY = player1.position.y - 13.5f;
                    player1.abstractWidth = 36f;
                    player1.abstractHeight = 39f;
                    player1.barrelOffset = new Vector2(45.0f, -27.0f).scl(0.5f);
                } else if (player1.health < 71 && player1.health > 50) {
                    player1.playerModelScale = 0.8f;
                    player1.abstractX = player1.position.x - 12;
                    player1.abstractY = player1.position.y - 9.6f;
                    player1.abstractWidth = 34f;
                    player1.abstractHeight = 37f;
                    player1.barrelOffset = new Vector2(45.0f, -27.0f).scl(0.5f);

                } else if (player1.health < 51 && player1.health > 30) {
                    player1.playerModelScale = 0.65f;
                    player1.abstractX = player1.position.x - 9.75f;
                    player1.abstractY = player1.position.y - 7.8f;
                    player1.abstractWidth = 33f;
                    player1.abstractHeight = 35f;
                    player1.barrelOffset = new Vector2(45.0f, -27.0f).scl(0.5f);

                } else if (player1.health < 31 && player1.health > 10) {
                    player1.playerModelScale = 0.5f;
                    player1.abstractX = player1.position.x - 7.5f;
                    player1.abstractY = player1.position.y - 6;
                    player1.abstractWidth = 32f;
                    player1.abstractHeight = 33f;
                    player1.barrelOffset = new Vector2(45.0f, -27.0f).scl(0.5f);
                } else {
                    player1.playerModelScale = 0.35f;
                    player1.abstractX = player1.position.x - 5.25f;
                    player1.abstractY = player1.position.y - 4.2f;
                    player1.abstractWidth = 20f;
                    player1.abstractHeight = 20f;
                    player1.barrelOffset = new Vector2(45.0f, -27.0f).scl(0.5f);

                }
                player1.render(batch);
            }
        }
    }

    /**
     * Update position of all existing bullets
     */
    public void updateBullets() {
        List<Bullet> bullets = new ArrayList<>(world.getBullets());
        for (Bullet b : bullets) {
            if (b != null) {
                b.update(Gdx.graphics.getDeltaTime());
            }
        }
    }

    /**
     * Check bullet collision with (walls, players, enemies)
     */
    public void checkCollisions() {
        List<Bullet> bullets = new ArrayList<>(world.getBullets());
        for (Bullet b : bullets) {
            Rectangle rect = new Rectangle(b.position.x, b.position.y, 1, 1);
            isCollisionWithPlayer(rect, b);
            isCollisionWithWall(rect, b);
            isCollisionWithEnemyAI(rect, b);
        }
    }

    /**
     * Check bullet collision with player.
     * @param r bullet's abstract box
     * @param b bullet
     */
    public void isCollisionWithPlayer(Rectangle r, Bullet b) {
        if (player.abstractBox.overlaps(r)) {
            player.damage(b.playerName);
            world.getBullets().remove(b);
            clientConnection.removeBullet(b.playerName, b.objectId);
        }
    }

    /**
     * Check if bullet collides with bot
     * @param r bullet's abstract box
     * @param b bullet
     */
    public void isCollisionWithEnemyAI (Rectangle r, Bullet b) {
        for (EnemyAI e : world.getEnemyAIList().values()) {
            if (e.abstractBox.overlaps(r)) {
                e.setHealth(e.health - 10);
                if (e.health < 10 && player.name.equals(b.playerName)) {
                    e.dead = true;
                }
                world.getBullets().remove(b);
                clientConnection.removeBullet(b.playerName, b.objectId);
            }
        }
    }

    /**
     * Draw bullets on the game screen
     * @param shapeRenderer renders bullets on game screen
     */
    public void drawBullets(ShapeRenderer shapeRenderer) {
        List<Bullet> bullets = new ArrayList<>(world.getBullets());
        for (Bullet b : bullets) {
            if (b != null) {
                shapeRenderer.circle(b.position.x, b.position.y, 03f, 32);
            }
        }
    }


    /**
     * All enemies moving and sending info to server
     */
    public void moveAndRotateEnemiesAI() {
        List<EnemyAI> enemyAIS = new ArrayList<>(world.getEnemyAIList().values());
        for (EnemyAI enemyAI : enemyAIS) {
            enemyAI.setCollisionLayer((MapLayer) map.getLayers().get("Objects"));
            enemyAI.move(player);
        }
    }

    public void drawEnemiesAI() {
        Collection<EnemyAI> enemyAIS = world.getEnemyAIList().values();
        List<Integer> sendDeadInfo = new LinkedList<>();
        for (EnemyAI enemyAI : enemyAIS) {
            enemyAI.render(batch);
            if (!enemyAI.dead) {
                clientConnection.updateEnemy(enemyAI.getPosition().x, enemyAI.getPosition().y, enemyAI.angle, enemyAI.health, enemyAI.getId(), enemyAI.getFollowPlayer());
            } else {
                sendDeadInfo.add(enemyAI.getId());
            }
        }
        if (sendDeadInfo.size() > 0) {
            for (Integer id : sendDeadInfo) {
                clientConnection.sendPacketRemoveAI(id);
                clientConnection.sendPacketAddScore(70  , clientConnection.getClient().getID(), 5, player.name);
                world.removeEnemy(id);
            }
            sendDeadInfo.clear();
        }
    }

    public ClientConnection getClientConnection() {
        return clientConnection;
    }

    public Player getPlayer() {
        return player;
    }

    public void setClientConnection(ClientConnection clientConnection) {
        this.clientConnection = clientConnection;
        player.setClientConnection(this.clientConnection);
        player.setTexture("playerIcons/" + world.getSkinId() + ".png");
    }

    /**
     * Check if bullet collides with wall
     * @param abstractBox Bullet's abstract box
     * @param b bullet
     * @return boolean(true, if bullet collides with wall)
     */
    public boolean isCollisionWithWall(Rectangle abstractBox, Bullet b) {
        MapObjects objects = collisionLayer.getObjects(); //get objects
        for (RectangleMapObject rectangleObject : objects.getByType(RectangleMapObject.class)) {
            Rectangle rectangle = rectangleObject.getRectangle();
            if (rectangle.overlaps(abstractBox)) {
                world.getBullets().remove(b);
            }
        }
        return false;
    }

    /**
     * Generate font for different things in UI (score, boosts messages, username)
     * @param size of font
     * @param color of font
     * @return generated font
     */
    public BitmapFont generateFont(int size, Color color) {
        fontParameter.size = size;
        fontParameter.color = color;
        return fontGenerator.generateFont(fontParameter);
    }

    /**
     * Bots shoot if see player
     */
    public void botShooting() {
        Map<Integer, EnemyAI> bots = new HashMap<>(world.getEnemyAIList());
        for (EnemyAI bot : bots.values()) {
            String name = Integer.toString(bot.getId());
            if (bot.followPlayer.equals(player.name)) {
                Bullet bullet = bot.shoot(player, name, time);
                if (bullet != null) {
                    time = System.currentTimeMillis();
                    world.getBullets().add(bullet);
                    clientConnection.addBullet(bullet.position.x, bullet.position.y, bullet.direction.x,
                            bullet.direction.y, name, bullet.objectId);
                }
            }
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        tmr.dispose();
        map.dispose();
        font.dispose();
        tex.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
