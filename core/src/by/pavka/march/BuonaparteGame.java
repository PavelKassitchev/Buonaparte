package by.pavka.march;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import by.pavka.march.configuration.Nation;

public class BuonaparteGame extends Game {
	public static final int TILE_SIZE_PX = 72;
	public static final int HEX_SIZE_M = 3500;
//	public static final Nation nation = FRANCE;
	private TextureAtlas textureAtlas;
	private Skin skin;

	public Nation nation;
	
	@Override
	public void create () {
		textureAtlas = new TextureAtlas("unit/friend.txt");
		TextureAtlas atlas = new TextureAtlas("skin/my-war/my-war.atlas");
		skin = new Skin(Gdx.files.internal("skin/my-war/my-war.json"), atlas);
//		TextureAtlas atlas = new TextureAtlas("skin/clean-crispy/clean-crispy-ui.atlas");
//		skin = new Skin(Gdx.files.internal("skin/clean-crispy/clean-crispy-ui.json"), atlas);
//		TextureAtlas atlas = new TextureAtlas("skin/golden-ui-skin.atlas");
//		skin = new Skin(Gdx.files.internal("skin/golden-ui-skin.json"), atlas);
		setScreen(new MenuScreen(this));
	}

	public TextureRegion getTextureRegion(String name) {
		return textureAtlas.findRegion(name);
	}

	public Skin getSkin() {
		return skin;
	}

	@Override
	public void dispose() {
		super.dispose();
		skin.dispose();
		textureAtlas.dispose();
	}
}
