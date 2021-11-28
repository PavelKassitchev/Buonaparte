package by.pavka.march;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class BuonaparteGame extends Game {
	public static final int TILE_SIZE_PX = 72;
	public static final int HEX_SIZE_M = 3500;
	private TextureAtlas textureAtlas;
	
	@Override
	public void create () {
		setScreen(new MenuScreen(this));
		textureAtlas = new TextureAtlas("unit/friend.txt");
	}

//	@Override
//	public void render () {
//		super.render();
//	}
//
//	@Override
//	public void dispose () {
//		super.dispose();
//	}

	public TextureRegion getTextureRegion(String name) {
		return textureAtlas.findRegion(name);
	}
}
