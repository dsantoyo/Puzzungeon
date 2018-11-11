package project.puzzungeon;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import project.puzzungeon.screens.MainMenuScreen;


//main game class
public class Puzzungeon extends Game {
	
	public final int WIDTH = 1000;
 	public final int HEIGHT = 800;
	
	public SpriteBatch batch;
	public Skin skin;
	public Client client;
	public AssetLoader assetLoader;
	
	public static final int DEFAULT_WIDTH = 1000;
	public static final int DEFAULT_HEIGHT = 800;

	public String serverAddress = "localhost";
	public int serverPort = 6789;
	
	public Boolean showDebugLine = true;

	//loads assets and calls first screen
	@Override
	public void create () {
		assetLoader = new AssetLoader();
		batch = new SpriteBatch();

		//pre-loading assets
		assetLoader.loadSkin();
		assetLoader.manager.finishLoading();
		skin = assetLoader.manager.get("uiskin.json", Skin.class);

		client = new Client(serverAddress, serverPort);
		//move on the the main Menu screen
		this.setScreen(new MainMenuScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	//disposes visual assets in order to free up memory 
	//(called when switching screens and at end of program).
	//Just a wrapper for AssetLoader’s dispose()
	@Override
	public void dispose () {

	}
	
}
