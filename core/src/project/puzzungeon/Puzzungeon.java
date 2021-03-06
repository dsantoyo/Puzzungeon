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

import com.badlogic.gdx.Game;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import project.puzzungeon.screens.LoadingScreen;

//main game class
public class Puzzungeon extends Game {
	
	public static final int WIDTH = 1920;
	public static final int HEIGHT = 1080;
	
	public Skin skin;
	public Client client;
	public AssetLoader assetLoader;
	
	public SpriteBatch batch;
	
	public Music menuMusic;
	public Music gameMusic;

	public String serverAddress = "localhost";
	public int serverPort = 6789;
	
	public Boolean showDebugLine = false;
	public Boolean playMusic = true;

	public Boolean randomPuzzle = false;
	
	//loads assets and calls first screen
	@Override
	public void create () {
		assetLoader = new AssetLoader();
		client = new Client(serverAddress, serverPort);
		batch = new SpriteBatch();
		
		//load pre-loading assets
		assetLoader.loadSkin();
		assetLoader.manager.finishLoading();
		skin = assetLoader.manager.get("pixungeon.json", Skin.class);
				
		//queue loading for other assets
		assetLoader.loadAtlas();
		assetLoader.loadSoundEffects();
		assetLoader.loadMusic();
		this.setScreen(new LoadingScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	//disposes visual assets in order to free up memory 
	//(called when switching screens and at end of program).
	@Override
	public void dispose () {
		batch.dispose();
		assetLoader.manager.dispose();
	}
	
}
