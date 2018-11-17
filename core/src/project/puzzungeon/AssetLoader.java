package project.puzzungeon;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader.SkinParameter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class AssetLoader {
	public final AssetManager manager = new AssetManager();
	
	//skin
	public final String skin = "pixungeon.json";
	
	//images pack
	public final String imgs = "sprites.txt";
	
	public void loadSkin() {
		SkinParameter params = new SkinParameter("pixungeon.atlas");
		manager.load(skin, Skin.class, params);
	}
	
	public void loadAtlas() {
		manager.load(imgs, TextureAtlas.class);
	}
}
