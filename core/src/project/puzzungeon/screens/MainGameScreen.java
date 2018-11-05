package project.puzzungeon.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;

import project.puzzungeon.Puzzungeon;

//Main Gameplay screen
public class MainGameScreen implements Screen{

	Puzzungeon game; //reference to the game
	private Stage stage;
	
	//constructor
	public MainGameScreen(Puzzungeon game) {
		this.game = game;
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
	}
	
	//updates actors
	public void act() {
		
	}
	
	//draw actors
	public void draw() {
		//create the actors
		Label gameTitle = new Label("Main Game Screen", game.skin);
		VerticalGroup vg1 = new VerticalGroup();
		vg1.setFillParent(true);
		vg1.addActor(gameTitle);
		stage.addActor(vg1);
		
	}
	
	@Override
	public void show() {
		this.draw();
	}

	@Override
	public void render(float delta) {
		
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
		
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
		
	}
	

}
