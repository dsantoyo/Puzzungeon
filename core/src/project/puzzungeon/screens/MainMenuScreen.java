package project.puzzungeon.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;

import project.puzzungeon.Client;
import project.puzzungeon.PuzzlePiece;
import project.puzzungeon.Puzzungeon;
import project.server.LoginRegister;
import project.server.Password;
import project.server.Username;

//First screen; 

public class MainMenuScreen implements Screen{

	Puzzungeon game; //reference to the game
	private Stage stage;
	FitViewport viewport;
	
	//asset references
	TextureAtlas atlas;
	Sprite background;
	
	//actor references
	private Label gameTitle;
	private TextButton loginButton;
	private TextButton newUserButton;
	private TextButton guestButton;
	private TextButton exitButton;
	
	//shared by different methods
	private Boolean displayDialog;
	private Dialog gameFullDialog;
	private Dialog connectionFailDialog;
	private Dialog databaseFailDialog;
	
	//constructor
	public MainMenuScreen(Puzzungeon game) {
		this.game = game;
		viewport = new FitViewport(Puzzungeon.WIDTH, Puzzungeon.HEIGHT);
		stage = new Stage(viewport);
		Gdx.input.setInputProcessor(stage);
		displayDialog = false;
		
		//setup background
		atlas = game.assetLoader.manager.get("sprites.txt");
		background = atlas.createSprite("dungeon");
		background.setOrigin(0, 0);
		background.setScale(9f);
	}
	
	//construct stage
	@Override
	public void show() {
/****************************************************************************************
*                             start: actors functionality
****************************************************************************************/

		gameTitle = new Label("Puzzungeon", game.skin);
		
		loginButton = new TextButton("Login", game.skin, "default");
			loginButton.addListener(new ClickListener(){
				@Override 
		            public void clicked(InputEvent event, float x, float y){
						game.setScreen(new LoginScreen(game));
		            }
		        });
		
		newUserButton = new TextButton("New User", game.skin, "default");
			newUserButton.addListener(new ClickListener(){
				@Override 
	            	public void clicked(InputEvent event, float x, float y){
						game.setScreen(new RegisterScreen(game));
	            	}
	        	});

		guestButton = new TextButton("Login as Guest", game.skin, "default");
			guestButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
							game.client.clientUsername = "Guest";
					if(!game.client.connectState) {
						//set up connection to the server
						System.out.println("Trying to connect...");
						if(!game.client.connect()) {
							System.out.println("Unable to connect to the server");
							displayDialog = true;
							game.client = new Client(game.serverAddress, game.serverPort);
						}
						else {
							game.client.sendUsername(new Username("guest"));
							game.client.sendPassword(new Password("guest"));
							game.client.sendLoginRegister(new LoginRegister("guest"));
							displayDialog = true;
						}
					}
					else {
						game.client.sendUsername(new Username("guest"));
						game.client.sendPassword(new Password("guest"));
						game.client.sendLoginRegister(new LoginRegister("guest"));
						displayDialog = true;
					}
				}
			});

		exitButton = new TextButton("Exit", game.skin, "default");

			exitButton.addListener(new ClickListener(){
				@Override 
				public void clicked(InputEvent event, float x, float y){
					Gdx.app.exit();
				}
			});
					
		gameFullDialog = new Dialog("Error", game.skin, "dialog") {
			public void result(Object obj) {}};
		gameFullDialog.text("We already have 2 players.");
		gameFullDialog.button("Got it", false); //sends "false" as the result
		
		connectionFailDialog = new Dialog("Error", game.skin, "dialog") {
		    public void result(Object obj) {}};
		connectionFailDialog.text("Couldn't connect to the server");
		connectionFailDialog.button("Got it", false); //sends "false" as the result
		
		
		databaseFailDialog = new Dialog("Error", game.skin, "dialog") {
		    public void result(Object obj) {}};
		databaseFailDialog.text("Couldn't connect to the database");
		databaseFailDialog.button("Got it", false); //sends "false" as the result
			
/****************************************************************************************
*                             end: actors functionality
****************************************************************************************/
		
/****************************************************************************************
*                             start: actors layout
****************************************************************************************/
		
		
/****************************************************************************************
*                             start: Main Menu UI
****************************************************************************************/
		
		//set label color and size
		gameTitle.setColor(Color.GREEN);
		
		Table mainMenuTable = new Table();
		mainMenuTable.setFillParent(true);
		mainMenuTable.add(gameTitle).colspan(3);
		mainMenuTable.row();
		
		mainMenuTable.add(loginButton).width(Puzzungeon.WIDTH*0.2f).pad(0.3f);
		mainMenuTable.add(newUserButton).width(Puzzungeon.WIDTH*0.2f).pad(0.3f);
		mainMenuTable.add(guestButton).width(Puzzungeon.WIDTH*0.3f).pad(0.3f);
		mainMenuTable.row();
			
/****************************************************************************************
*                             end: Main Menu UI
****************************************************************************************/

		
/****************************************************************************************
*                             start: Exit Button
****************************************************************************************/
		Table exitButtonTable = new Table().bottom().right();
		exitButtonTable.setFillParent(true);
		exitButtonTable.add(exitButton).width(Puzzungeon.WIDTH*0.2f).pad(10);
		
/****************************************************************************************
*                             end: Exit Button
****************************************************************************************/
		
		
		//add actors onto the stage
		stage.addActor(mainMenuTable);
		stage.addActor(exitButtonTable);
		
		//draw debugline to see the boundary of each actor
		if(game.showDebugLine) {
			stage.setDebugAll(true);
		}
		
/****************************************************************************************
*                             end: actors layout
****************************************************************************************/
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//draw background
		viewport.apply();
		game.batch.begin();
		background.draw(game.batch);
		game.batch.end();
		
		//update and draw stage
		stage.getViewport().apply();
		stage.act(Gdx.graphics.getDeltaTime());
		checkClientLoginState();
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height);
		//table.setWidth(Puzzungeon.WIDTH);
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
	
	public void checkClientLoginState() {

		if(displayDialog == true) {
			
			if(game.client.loginState) {
				game.setScreen(new WaitingScreen(game));
			}
			
			if(!game.client.loginState) {
				System.out.println(game.client.loginStateMessage);
				if(game.client.loginStateMessage.equals("Game is Full.")) {
					game.client.loginStateMessage = "";
					gameFullDialog.show(stage);
					displayDialog = false;
				}
				if(game.client.loginStateMessage.equals("Connection to the database failed")) {
					
					game.client.loginStateMessage = "";
					databaseFailDialog.show(stage);
					displayDialog = false;
				}
				
				if(!game.client.connectState) {
					connectionFailDialog.show(stage);
					displayDialog = false;
				}
			}
		}
	}
	
}
