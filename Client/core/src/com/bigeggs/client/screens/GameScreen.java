package com.bigeggs.client.screens;

import ClientConnection.ClientConnection;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.bigeggs.client.models.Bullet;
import com.bigeggs.client.models.EnemyAI;
import com.bigeggs.client.models.Player;
import com.bigeggs.client.models.Weapon;
import com.bigeggs.client.models.boosts.AmmoBoost;
import com.bigeggs.client.models.boosts.Boost;
import com.bigeggs.client.models.boosts.HealthBoost;
import com.bigeggs.client.models.boosts.SpeedBoost;
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
    private SpriteBatch spriteBatch;

    private OrthogonalTiledMapRenderer tmr;
    private TiledMap map;
    private EnemyAI enemy;
    private final Vector2 mouseInWorld2D = new Vector2();
    private List<Boost> boosts;

    public GameScreen(ClientWorld world) {
        this.world = world;
        backgroundTexture = new TextureRegion(new Texture("bluegrey.png"), 0, 0, 3840, 2500);
        batch = new SpriteBatch();
        map = new TmxMapLoader().load("newTiledMap.tmx");
        tmr = new OrthogonalTiledMapRenderer(map, 1);

        player = new Player(750f,850f, 0f, "kamikadze", (MapLayer) map.getLayers().get("Objects"), "playerIcons/1.png");
        player.setWeapon(new Weapon());
        player.setWorld(world);
        enemy = new EnemyAI(200f, 1500f, 0f, (MapLayer) map.getLayers().get("Objects"));

        camera = new OrthographicCamera(1000, 800);
        boosts = world.getBoosts();

        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void render (float delta) {
        ScreenUtils.clear(1, 1, 1, 1);

        // update player, player rotate and move
        player.move();
        player.rotate(getMousePosInGameWorld());
        player.createBullet(new Vector2(getMousePosInGameWorld().x - player.getPosition().x, getMousePosInGameWorld().y - player.getPosition().y).nor());

        // rotate and move enemy to player
        moveAndRotateEnemiesAI();

        // update camera(follow player) and set it to batch
        camera.position.set(player.getPosition().x, player.getPosition().y, 0);
        camera.update();
        updateBullets();
        batch.setProjectionMatrix(camera.combined);


        batch.begin();
        batch.draw(backgroundTexture, -500, -500);
        batch.end();



        tmr.setView(camera);
        tmr.render();
        batch.begin();

        player.render(batch);

        // Different boosts collide with players
        boostsCollisionWithPlayers();
        player.actWithBoosts(batch);

        drawBoosts();
        drawEnemiesAI();
        drawOtherPlayers();

        batch.end();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(Color.BLUE);
        drawBullets(shapeRenderer);
        shapeRenderer.end();

    }

    public Vector3 getMousePosInGameWorld() {
        // special mouse position because of Orthographic Camera
        return camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
    }

    public void boostsCollisionWithPlayers() {
        List<Boost> boostsRemove = new ArrayList<>();
        for (Boost boost : boosts) {
            if (boost.collisionWithPlayer(player)) {
                boostsRemove.add(boost);
                if (boost instanceof SpeedBoost) {
                    player.speedBoost = true;
                } else if (boost instanceof HealthBoost) {
                    player.hpBoost = true;
                } else if (boost instanceof AmmoBoost) {
                    player.ammoBoost = true;
                }
                clientConnection.sendPacketRemoveBoost(boost.getPosition().x, boost.getPosition().y);
            }
        }
        boosts.removeAll(boostsRemove);
    }

    public void shoot(Vector2 mousePos) {
        Bullet b = player.createBullet(mousePos);
        if (b != null) {
            world.addBullet(b);
            clientConnection.addBullet(b.position.x, b.position.y, b.direction.x, b.direction.y);
        }
    }

    public Vector2 getMouseInWorld2D() {
        Vector3 mouse = getMousePosInGameWorld();
        mouseInWorld2D.x = mouse.x;
        mouseInWorld2D.y = mouse.y;
        return new Vector2(mouse.x - player.getPosition().x, mouse.y - player.getPosition().y).nor();
    }

    public void drawOtherPlayers() {
        List<Player> players = new ArrayList<>(world.getPlayers().values());
        for (Player player1 : players) {
            player1.render(batch);
        }
    }

    public void drawBoosts() {
        for (Boost boost : boosts) {
            boost.render(batch);
        }
    }

    public void updateBullets() {
        List<Bullet> bullets = new ArrayList<>(world.getBullets());
        for (Bullet b : bullets) {
            b.update(Gdx.graphics.getDeltaTime());
        }
    }

    public void drawBullets(ShapeRenderer shapeRenderer) {
        List<Bullet> bullets = new ArrayList<>(world.getBullets());
        for (Bullet b : bullets) {
            shapeRenderer.circle(b.position.x, b.position.y, 03f, 32);
        }
    }

    public void moveAndRotateEnemiesAI() {
        Collection<EnemyAI> enemyAIS = world.getEnemyAIList().values();
        for (EnemyAI enemyAI : enemyAIS) {
            enemyAI.setCollisionLayer((MapLayer) map.getLayers().get("Objects"));
            enemyAI.move(player);
            clientConnection.updateEnemy(enemyAI.getPosition().x, enemyAI.getPosition().y, enemyAI.angle, enemyAI.health, enemyAI.getId(), enemyAI.getFollowPlayer());
        }
    }

    public void drawEnemiesAI() {
        Collection<EnemyAI> enemyAIS = world.getEnemyAIList().values();
        for (EnemyAI enemyAI : enemyAIS) {
            enemyAI.render(batch);
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
        player.setTexture("playerIcons/" + clientConnection.getSkinId() + ".png");
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
    public void dispose () {
        batch.dispose();
        tmr.dispose();
        map.dispose();
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
