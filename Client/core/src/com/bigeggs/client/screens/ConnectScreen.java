package com.bigeggs.client.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bigeggs.client.gameInfo.GameClient;

import javax.swing.*;

public class ConnectScreen implements Screen {
    private final Stage stage;
    private final TextField username;
    private SpriteBatch batch;
    private BitmapFont font;
    private final TextButton connectButton;
    private final TextButton quitButton;
    private GameClient gameClient;

    private final Table root;

    public ConnectScreen() {

        final Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        batch = new SpriteBatch();

        stage = new Stage();
        root = new Table();
        root.setBounds(0, -100, 1000, 800);
        font = new BitmapFont();
        font.getData().scale(3f);
        username = new TextField("Username", skin);
        connectButton = new TextButton("Connect", skin);
        quitButton = new TextButton("Quit", skin);

        listenButtons();

        stage.addActor(this.root);

        root.add(username).width(250).padTop(100).row();
        root.add(connectButton).size(250, 50).padTop(50).row();
        root.add(quitButton).size(250, 50).padTop(10).row();

    }

    public void listenButtons() {
        connectButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                String name = username.getText();
                if (name.replaceAll("\\s", "").equals("") || name.equals("Username")) {
                    JOptionPane.showMessageDialog(null, "Please write your player name");
                    return false;
                } else {
                    gameClient.startGame(name);
                    return super.touchDown(event, x, y, pointer, button);
                }
            }
        });

        quitButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.exit();
                return super.touchDown(event, x, y, pointer, button);
            }
        });
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        stage.draw();
        stage.act(delta);
        batch.begin();
        font.draw(batch, "Cold line Tallinn", 300, 500);
        batch.end();
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

    }

    public GameClient getGameClient() {
        return gameClient;
    }

    public void setGameClient(GameClient gameClient) {
        this.gameClient = gameClient;
    }
}
