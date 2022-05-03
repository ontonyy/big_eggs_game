package com.bigeggs.client.gameInfo;

import ClientConnection.ClientConnection;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.bigeggs.client.screens.ConnectScreen;
import com.bigeggs.client.screens.GameScreen;
import com.bigeggs.client.world.ClientWorld;

public class GameClient extends Game {
    private GameScreen gameScreen;
    private ConnectScreen connectScreen;
    private ClientConnection clientConnection;
    private ClientWorld clientWorld;

    /**
     * Creates a client who connects to Server with GameScreen and ClientWorld
     * @param gameScreen adding to ClientConnection
     * @param clientWorld adding to ClientConnection
     */
    public void createClient(GameScreen gameScreen, ClientWorld clientWorld) {
        clientConnection = new ClientConnection();
        clientConnection.setGameScreen(gameScreen);
        clientConnection.setClientWorld(clientWorld);
        clientConnection.setPlayerName(gameScreen.getPlayer().name);
        clientConnection.setGameClient(this);
        clientConnection.sendPacketConnect();
        gameScreen.setClientConnection(clientConnection);
        clientWorld.setClientConnection(clientConnection);
    }

    /**
     * Starts first screen of game - ConnectScreen
     */
    public void startConnect() {
        connectScreen = new ConnectScreen();
        connectScreen.setGameClient(this);
        setScreen(connectScreen);
    }

    /**
     * Create ClientWorld and GameScreen and set it to main screen, and InputProcessor
     * @param name of player that was got in ConnectScreen
     */
    public void startGame(String name) {
        clientWorld = new ClientWorld();
        gameScreen = new GameScreen(clientWorld);
        gameScreen.getPlayer().setName(name);
        createClient(gameScreen, clientWorld);
        setScreen(gameScreen);
        Gdx.input.setInputProcessor(gameScreen);
    }

    /**
     * Main start method of Game class
     */
    @Override
    public void create() {
        startConnect();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        gameScreen.dispose();
    }

    public GameScreen getGameScreen() {
        return gameScreen;
    }
}
