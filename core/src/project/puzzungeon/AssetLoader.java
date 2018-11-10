package project.puzzungeon;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader.SkinParameter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class AssetLoader {
	public final AssetManager manager = new AssetManager();
	
	//skin
	public final String skin = "uiskin.json";
	
	public void loadSkin() {
		SkinParameter params = new SkinParameter("uiskin.atlas");
		manager.load(skin, Skin.class, params);
	}
}
