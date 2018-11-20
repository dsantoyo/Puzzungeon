package project.puzzungeon.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.FitViewport;

import project.puzzungeon.Puzzungeon;

public class LoadingScreen implements Screen {
	Puzzungeon game;
	
	//pre-loading stuff
	Label loading;
	Stage loading_stage;
	
	public LoadingScreen(Puzzungeon game) {
		this.game = game;
		
		FitViewport viewport = new FitViewport(Puzzungeon.WIDTH, Puzzungeon.HEIGHT);
		loading_stage = new Stage(viewport);
		
		//draw pre-loading stuff
		loading_stage = new Stage(new FitViewport(Puzzungeon.WIDTH, Puzzungeon.HEIGHT));
		loading = new Label("Loading...", game.skin);
		loading.setPosition(Puzzungeon.WIDTH / 2, Puzzungeon.HEIGHT / 2);
		loading_stage.addActor(loading);
		
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(float delta) {
		if (game.assetLoader.manager.update()) {
			//if inside here, assets are done loading
			game.setScreen(new MainMenuScreen(game));
		} else {
			loading_stage.act();
			loading_stage.draw();
		}
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
}
