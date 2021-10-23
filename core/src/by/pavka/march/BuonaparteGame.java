package by.pavka.march;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;

public class BuonaparteGame extends Game {
	SpriteBatch batch;
	Texture img;
	Camera camera;
	Viewport viewport;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("default.png");
//		camera = new OrthographicCamera();
//		//camera.viewportHeight = 900;
//		viewport = new FitViewport(900, 200);
		setScreen(new MenuScreen(this));
	}

	@Override
	public void render () {
//		ScreenUtils.clear(1, 0, 0, 1);
//		//viewport.apply();
//		//batch.setProjectionMatrix(camera.projection);
//		batch.begin();
//		batch.draw(img, 0, 0);
//		batch.end();
//		batch.setProjectionMatrix(camera.projection);
		super.render();
	}
	
	@Override
	public void dispose () {
		super.dispose();
		batch.dispose();
		img.dispose();
	}
}
