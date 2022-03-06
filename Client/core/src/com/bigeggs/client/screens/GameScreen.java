package com.bigeggs.client.screens;

import ClientConnection.ClientConnection;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.bigeggs.client.models.EnemyAI;
import com.bigeggs.client.models.Player;
import com.bigeggs.client.models.Weapon;
import com.bigeggs.client.world.ClientWorld;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class GameScreen implements Screen, InputProcessor {
    private Player player;
    private ClientWorld world;
    private ClientConnection clientConnection;
    private OrthographicCamera camera;
    SpriteBatch batch;
    private TextureRegion backgroundTexture;
    private OrthogonalTiledMapRenderer tmr;
    private TiledMap map;

    public GameScreen(ClientWorld world) {
        this.world = world;
        backgroundTexture = new TextureRegion(new Texture("background.jpg"), 0, 0, 1920, 1080);
        batch = new SpriteBatch();
        map = new TmxMapLoader().load("tiledMap1.tmx");
        tmr = new OrthogonalTiledMapRenderer(map, 1);

        player = new Player(440f,455f, 0f, "kamikadze", (MapLayer) map.getLayers().get("Objects"));
        player.setWeapon(new Weapon());

        camera = new OrthographicCamera(1000, 800);
    }

    @Override
    public void render (float delta) {
        ScreenUtils.clear(1, 1, 1, 1);

        // update player, player rotate and move

        player.move();
        player.rotate(getMousePosInGameWorld());
        player.shoot();

        // rotate and move enemy to player
        moveAndRotateEnemiesAI();

        // update camera(follow player) and set it to batch
        camera.position.set(player.getPosition().x, player.getPosition().y, 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        tmr.setView(camera);
        tmr.render();

        batch.begin();

        player.render(batch);
        drawEnemiesAI();
        drawOtherPlayers();

        batch.end();

    }

    public Vector3 getMousePosInGameWorld() {
        // special mouse position because of Orthographic Camera
        return camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
    }

    public void drawOtherPlayers() {
        List<Player> players = new ArrayList<>(world.getPlayers().values());
        for (Player player1 : players) {
            player1.render(batch);
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