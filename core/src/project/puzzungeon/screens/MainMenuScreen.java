package project.puzzungeon.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import project.puzzungeon.Puzzungeon;

//First screen; 

public class MainMenuScreen implements Screen{

	Puzzungeon game; //reference to the game
	private Stage stage;
	private Table table;
	
	//constructor
	public MainMenuScreen(Puzzungeon game) {
		this.game = game;
		stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);
	}
	
	//construct stage
	@Override
	public void show() {
		
		//simple layout:
		//       Game Title
		//
		//login   new user   guest
		//
		//                    exit
		
		//create the actors
		Label gameTitle = new Label("Puzzungeon", game.skin);
		
		TextButton loginButton = new TextButton("Login", game.skin, "default");
			loginButton.addListener(new ClickListener(){
				@Override 
		            public void clicked(InputEvent event, float x, float y){
						game.setScreen(new LoginScreen(game));
		            }
		        });
		
		TextButton newUserButton = new TextButton("New User", game.skin, "default");
			newUserButton.addListener(new ClickListener(){
				@Override 
	            	public void clicked(InputEvent event, float x, float y){
						game.setScreen(new RegisterScreen(game));
	            	}
	        	});
		TextButton guestButton = new TextButton("Login as Guest", game.skin, "default");
					guestButton.addListener(new ClickListener() {
						@Override
						public void clicked(InputEvent event, float x, float y) {
							game.client.clientUsername = "Guest";
							System.out.println("Trying to connect...");
							game.client.connect();
							System.out.println("connected!");
					System.out.println("switching screens...");
					game.setScreen(new WaitingScreen(game));
						}
					});
		TextButton exitButton = new TextButton("Exit", game.skin, "default");
			exitButton.addListener(new ClickListener(){
				@Override 
				public void clicked(InputEvent event, float x, float y){
					Gdx.app.exit();
					System.exit(0);
				}
			});
			
		/*	
		//use table to display actors.
		table = new Table();
		table.setBounds(0, 0, Puzzungeon.WIDTH, Puzzungeon.HEIGHT);
		
		table.add(gameTitle).expandX().colspan(3);
		table.row();
		table.add(loginButton);
		table.add(newUserButton);
		table.add(guestButton);
		table.add(exitButton).bottom().right();
		
		table.debug();
		stage.addActor(table);
		*/
		
		//use vg and hg to group the actors now. changes should be made to make it look better
		VerticalGroup vg = new VerticalGroup();
		vg.setFillParent(true);
		vg.addActor(gameTitle);
		
		HorizontalGroup buttonRow1 = new HorizontalGroup();
		buttonRow1.addActor(loginButton);
		buttonRow1.addActor(newUserButton);
		buttonRow1.addActor(guestButton);
		vg.addActor(buttonRow1);
		
		HorizontalGroup buttonRow2 = new HorizontalGroup();
		buttonRow2.addActor(exitButton);
		vg.addActor(buttonRow2);
		
		//add actors onto the stage
		stage.addActor(vg);
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
		table.setWidth(Puzzungeon.WIDTH);
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

	}
	
}
