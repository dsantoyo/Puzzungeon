package project.puzzungeon.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;

import project.puzzungeon.Puzzungeon;

//First screen; login/validation happens on this screen.

public class MainMenuScreen implements Screen{

	Puzzungeon game; //reference to the game
	Texture img;
	
	//constructor
	public MainMenuScreen(Puzzungeon game) {
		this.game = game;
	}
	
	//updates actors
	public void act() {
		
	}
	
	//draw actors
	public void draw() {
		
	}
	
	@Override
	public void show() {
		img = new Texture("MainMenuScreen.png");
	}

	@Override
	public void render(float delta) {
		
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		game.batch.begin();
		game.batch.draw(img, 0, 0);
		game.batch.end();
		
	}

	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void hide() {
		
	}

	@Override
	public void dispose() {
		
		game.batch.dispose();
		img.dispose();
		
	}
	

}
