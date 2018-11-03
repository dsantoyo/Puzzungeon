package project.puzzungeon;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import project.puzzungeon.screens.MainMenuScreen;


//main game class
public class Puzzungeon extends Game {
	
	public SpriteBatch batch;

	//loads assets and calls first screen
	@Override
	public void create () {
		batch = new SpriteBatch();
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
