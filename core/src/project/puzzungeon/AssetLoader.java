/*CSCI201 Final Project

Project Name: Puzzungeon
Project Number: 7
Project Category: Game

Daniel Santoyo: dsantoyo@usc.edu USC ID: 6926712177
Hayley Pike: hpike@usc.edu USC ID: 8568149839
Yi(Ian) Sui: ysui@usc.edu USC ID: 2961712187
Ekta Gogri: egogri@usc.edu USC ID: 9607321862
*/

package project.puzzungeon;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader.SkinParameter;
import com.badlogic.gdx.audio.Music;
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
	private final String win = "sound/good-end.mp3";
	
	//music
	private final String menuMusic = "music/Inside-The-Tower.mp3";
	private final String gameMusic1 = "music/Final_Sacrifice.mp3";
	private final String gameMusic2 = "music/Epilogue.mp3";
	
	public void loadSoundEffects() {
		manager.load(buttonPress, Sound.class);
		manager.load(rightLoc, Sound.class);
		manager.load(swoosh, Sound.class);
		manager.load(alert, Sound.class);
		manager.load(win, Sound.class);
	}
	
	public void loadMusic() {
		manager.load(menuMusic, Music.class);
		manager.load(gameMusic1, Music.class);
		manager.load(gameMusic2, Music.class);
	}
	
	public void loadSkin() {
		SkinParameter params = new SkinParameter("pixungeon.atlas");
		manager.load(skin, Skin.class, params);
	}
	
	public void loadAtlas() {
		manager.load(imgs, TextureAtlas.class);
	}
}
