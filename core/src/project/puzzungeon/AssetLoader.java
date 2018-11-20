package project.puzzungeon;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader.SkinParameter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class AssetLoader {
	public final AssetManager manager = new AssetManager();
	
	//skin
	private final String skin = "pixungeon.json";
	
	//images pack
	private final String imgs = "sprites.txt";
	
	//sound effects
	private final String buttonPress = "sound/rightlocation.mp3";
	private final String rightLoc = "sound/buton1.mp3";
	private final String swoosh = "sound/swoosh1.mp3";
	private final String alert = "sound/alert1.mp3";
	
	public void loadSoundEffects() {
		manager.load(buttonPress, Sound.class);
		manager.load(rightLoc, Sound.class);
		manager.load(swoosh, Sound.class);
		manager.load(alert, Sound.class);
	}
	
	public void loadMusic() {
		
	}
	
	public void loadSkin() {
		SkinParameter params = new SkinParameter("pixungeon.atlas");
		manager.load(skin, Skin.class, params);
	}
	
	public void loadAtlas() {
		manager.load(imgs, TextureAtlas.class);
	}
}
