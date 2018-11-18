package project.puzzungeon;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;

import project.puzzungeon.screens.LoadingScreen;
import project.puzzungeon.screens.MainMenuScreen;


//main game class
public class Puzzungeon extends Game {
	
	public static final int WIDTH = 1920;
	public static final int HEIGHT = 1080;
	
	public Skin skin;
	public Client client;
	public AssetLoader assetLoader;
	
	public SpriteBatch batch;

	public String serverAddress = "localhost";
	public int serverPort = 6789;
	
	public Boolean showDebugLine = true;

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
	}
	
}
