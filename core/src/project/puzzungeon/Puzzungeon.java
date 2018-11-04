package project.puzzungeon;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import project.puzzungeon.screens.MainMenuScreen;
import project.server.Server;


//main game class
public class Puzzungeon extends Game {
	
	public SpriteBatch batch;
	public Skin skin;
	public Client client;

	//loads assets and calls first screen
	@Override
	public void create () {
		batch = new SpriteBatch();
		skin = new Skin(Gdx.files.internal("uiskin.json"));
		client = new Client("10.26.190.11", 6789);
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
