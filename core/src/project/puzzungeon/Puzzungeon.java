package project.puzzungeon;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;

import project.puzzungeon.screens.MainMenuScreen;


//main game class
public class Puzzungeon extends Game {
	
	public static final int WIDTH = 1920;
	public static final int HEIGHT = 1080;
	
	public Skin skin;
	public Client client;
	public AssetLoader assetLoader;
	
	//pre-loading stuff
	Label loading;
	Stage loading_stage;

	public String serverAddress = "localhost";
	public int serverPort = 6789;
	
	public Boolean showDebugLine = true;

	//loads assets and calls first screen
	@Override
	public void create () {
		assetLoader = new AssetLoader();
		client = new Client(serverAddress, serverPort);
		
		//load pre-loading assets
		assetLoader.loadSkin();
		assetLoader.manager.finishLoading();
		skin = assetLoader.manager.get("uiskin.json", Skin.class);
				
		//queue loading for other assets
		assetLoader.loadAtlas();
		
		//draw pre-loading stuff
		loading_stage = new Stage(new FitViewport(WIDTH, HEIGHT));
		loading = new Label("Loading...", skin);
		loading_stage.addActor(loading);
	}

	@Override
	public void render () {
		super.render();
		if (assetLoader.manager.update()) {
			//if inside here, assets are done loading
			this.setScreen(new MainMenuScreen(this));
		} else {
			loading_stage.act();
			loading_stage.draw();
		}
	}
	
	//disposes visual assets in order to free up memory 
	//(called when switching screens and at end of program).
	//Just a wrapper for AssetLoader’s dispose()
	@Override
	public void dispose () {

	}
	
}
