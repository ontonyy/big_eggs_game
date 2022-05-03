package com.bigeggs.client.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bigeggs.client.gameInfo.GameClient;

import javax.swing.*;

import static com.badlogic.gdx.Gdx.gl;

public class ConnectScreen implements Screen {
    private final Stage stage;
    private final TextField username;
    private SpriteBatch batch;
    private SpriteBatch batch2;
    private BitmapFont font;
    private BitmapFont font2;
    private final TextButton connectButton;
    private final TextButton quitButton;
    private final TextButton tutorialButton;
    private GameClient gameClient;

    private Texture tex;
    private Texture tex2;
    private Texture background;
    private Sprite sprite;
    private Sprite sprite2;
    private Sprite sprite3;

    private Music music;
    protected Sound buttonSound;


    private final Table root;

    private boolean flag = false;

    /**
     * Constructor with setting all needed variables
     */
    public ConnectScreen() {

        final Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        batch = new SpriteBatch();
        batch2 = new SpriteBatch();

        stage = new Stage();
        root = new Table();
        root.setBounds(0, -100, 1000, 800);
        font = new BitmapFont();
        font2 = new BitmapFont();
        font2.getData().setScale(2f);
        font.getData().scale(5f);
        username = new TextField("Username", skin);
        connectButton = new TextButton("Connect", skin);
        quitButton = new TextButton("Quit", skin);
        tutorialButton = new TextButton("Controls", skin);
        music = Gdx.audio.newMusic(Gdx.files.internal("SoundEffects/menuMusic.mp3"));
        music.setVolume(0.05f);
        music.setLooping(true);
        music.play();

        buttonSound = Gdx.audio.newSound(Gdx.files.internal("SoundEffects/buttonSound.mp3"));

        listenButtons();

        stage.addActor(this.root);

        root.add(username).width(250).padTop(100).row();
        root.add(connectButton).size(250, 50).padTop(50).row();
        root.add(tutorialButton).size(250, 50).padTop(10).row();
        root.add(quitButton).size(250, 50).padTop(10).row();

        tex = new Texture(Gdx.files.internal("controls/wasd.png"));
        tex2 = new Texture(Gdx.files.internal("controls/mouse2.png"));
        sprite = new Sprite(tex, 0, 0, 800, 700);
        sprite2 = new Sprite(tex2, 0, 0, 800, 700);
        sprite.setPosition(-300, 25);
        sprite2.setPosition(-250, -100);
        sprite.setScale(0.2f);
        sprite2.setScale(0.2f);
        background = new Texture(Gdx.files.internal("nuclear_background.jpg"));
        sprite3 = new Sprite(background, 0, 0, 1920, 1080);
        sprite3.setPosition(195, -420);
        sprite3.setScale(1.3f);
    }

    /**
     * Listen all buttons in ConnectScreen (connect, quit, tutorial)
     */
    public void listenButtons() {
        connectButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                String name = username.getText();
                if (name.replaceAll("\\s", "").equals("") || name.equals("Username")) {
                    JOptionPane.showMessageDialog(null, "Please write your player name");
                    return false;
                } else {
                    buttonSound.play();
                    music.stop();
                    gameClient.startGame(name);
                    return super.touchDown(event, x, y, pointer, button);
                }
            }
        });

        quitButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                buttonSound.play();
                Gdx.app.exit();
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        tutorialButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                buttonSound.play();
                flag = !flag;
                return super.touchDown(event, x, y, pointer, button);
            }
        });
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Main loop method, with drawing stage and if need tutorial
     */
    @Override
    public void render(float delta) {
        gl.glClearColor(0, 0, 0, 1);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        sprite3.draw(batch);
        //font.draw(batch, "Cold line Tallinn", 300, 500);
        batch.end();
        stage.draw();
        stage.act(delta);


        if (flag) {
            batch2.begin();
            sprite.draw(batch2);
            sprite2.draw(batch2);
            font2.draw(batch2, "move", 75, 325);
            font2.draw(batch2, "shoot", 68, 155);
            font2.draw(batch2, "[R] - reload", 35, 115);
            font2.draw(batch2, "[E] - change fire mode", 35, 80);
            font2.draw(batch2, "[TAB] - score table", 35, 45);
            batch2.end();
        }
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
        tex.dispose();
    }

    public GameClient getGameClient() {
        return gameClient;
    }

    public void setGameClient(GameClient gameClient) {
        this.gameClient = gameClient;
    }
}
