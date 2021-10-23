package by.pavka.march;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.HexagonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class PlayScreen implements Screen {
    public static final String MAP = "map/map4.tmx";

    private Game game;
    private Stage stage;
    private TextureAtlas atlas;
    private Skin skin;
    private TiledMap map;
    private HexagonalTiledMapRenderer hexagonalTiledMapRenderer;
    //private Viewport viewport;
    private OrthographicCamera camera;

    private Table group;

    public PlayScreen(Game game) {
        this.game = game;
        map = new TmxMapLoader().load(MAP);
        atlas = new TextureAtlas("skin/golden-ui-skin.atlas");
        skin = new Skin(Gdx.files.internal("skin/golden-ui-skin.json"), atlas);
        hexagonalTiledMapRenderer = new HexagonalTiledMapRenderer(map);
        //viewport = new ExtendViewport(800, 450);
        camera = new OrthographicCamera(800, 450);
        stage = new Stage(new ExtendViewport(800, 450)) {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.LEFT) {
                    camera.translate(-32, 0);
                }
                if (keycode == Input.Keys.RIGHT) {
                    camera.translate(32, 0);
                }
                if (keycode == Input.Keys.UP)
                    camera.translate(0, 32, 0);
                if (keycode == Input.Keys.DOWN) {
                    camera.translate(0, -32, 0);
                }
                camera.update();
                hexagonalTiledMapRenderer.setView(camera);
                return false;
            }
        };
        stage.addListener(new DragListener() {
            float x;
            float y;

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                this.x = x;
                this.y = y;
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                camera.translate(this.x - x, this.y - y);
                camera.update();
                hexagonalTiledMapRenderer.setView(camera);
                this.x = x;
                this.y = y;
            }
        });
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        group = new Table();
        stage.addActor(group);
        TextButton back = new TextButton("Back", skin);
        back.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });
        group.add(back);
        TextButton forward = new TextButton("Forward", skin);
        group.add(forward);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 1, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        hexagonalTiledMapRenderer.render();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        //viewport.update(width, height, true);
        camera.setToOrtho(false, width, height);
        //hexagonalTiledMapRenderer.setView((OrthographicCamera) viewport.getCamera());
        hexagonalTiledMapRenderer.setView(camera);
        group.setBounds(0, stage.getHeight() * 0.9f, stage.getWidth(), stage.getHeight() * .1f);
        group.left();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        atlas.dispose();
        map.dispose();
        hexagonalTiledMapRenderer.dispose();
    }

//    @Override
//    public boolean keyDown(int keycode) {
//        if (keycode == Input.Keys.LEFT) {
//            camera.translate(-32, 0);
//        }
//        if (keycode == Input.Keys.RIGHT) {
//            camera.translate(32, 0);
//        }
//        if (keycode == Input.Keys.UP)
//            camera.translate(0, 32, 0);
//        if (keycode == Input.Keys.DOWN) {
//            camera.translate(0, -32, 0);
//        }
//        camera.update();
//        hexagonalTiledMapRenderer.setView(camera);
//        return false;
//    }

}
