package project.puzzungeon.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

import project.puzzungeon.Client;
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
	//private Image gameTitle;
	private Label gameTitle;
	private TextButton loginButton;
	private TextButton newUserButton;
	private TextButton guestButton;
	private TextButton exitButton;
	private TextButton creditsButton;
	
	//shared by different methods
	private Boolean displayDialog;
	private Dialog gameFullDialog;
	private Dialog connectionFailDialog;
	private Dialog databaseFailDialog;
	private Dialog creditsDialog;
	
	//sound variables
	public Sound buttonpress;
	
	
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
		background.setSize(Puzzungeon.WIDTH, Puzzungeon.HEIGHT);
		
		buttonpress = game.assetLoader.manager.get("sound/rightlocation.mp3");
		if (game.menuMusic == null) {
			game.menuMusic = game.assetLoader.manager.get("music/Inside-The-Tower.mp3");
		}
		if (game.gameMusic == null) {
			game.gameMusic = game.assetLoader.manager.get("music/Final_Sacrifice.mp3");
		}
		if (game.gameMusic.isPlaying()) {
			game.gameMusic.stop();
		}
		if (!game.menuMusic.isPlaying() && game.playMusic == true) {
			game.menuMusic.play();
			game.menuMusic.setVolume(0.2f);
			game.menuMusic.setLooping(true);
		}
	}
	
	//construct stage
	@Override
	public void show() {
		
/****************************************************************************************
*                             start: actors functionality
****************************************************************************************/		
		gameTitle = new Label("Puzzungeon", game.skin, "title");
		
		loginButton = new TextButton("Login", game.skin, "default");
			loginButton.addListener(new ClickListener(){
				@Override 
		            public void clicked(InputEvent event, float x, float y){
					buttonpress.play();
						game.setScreen(new LoginScreen(game));
		            }
		        });
		
		newUserButton = new TextButton("New User", game.skin, "default");
			newUserButton.addListener(new ClickListener(){
				@Override 
	            	public void clicked(InputEvent event, float x, float y){
					buttonpress.play();
						game.setScreen(new RegisterScreen(game));
	            	}
	        	});

		guestButton = new TextButton("Login as Guest", game.skin, "default");
			guestButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					buttonpress.play();
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
							game.client.username = "guest";
							game.client.password = "guest";
							game.client.sendUsername(new Username("guest"));
							game.client.sendPassword(new Password("guest"));
							game.client.sendLoginRegister(new LoginRegister("guest"));
							displayDialog = true;
						}
					}
					else {
						game.client.username = "guest";
						game.client.password = "guest";
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
					buttonpress.play();
					Gdx.app.exit();
				}
			});
			
		//credits 
		String authorCreditStr = "By Ekta Gogri, Hayley Pike, Daniel Santoyo, and Ian Sui.";
		String skinCreditStr = "UI Styling derived from Libgdx Skin by Raymond \"Raeleus\" Buckley";
		String musicCreditStr = "Music by Visager";
		String ccLicenseStr = "Both are under a Creative Commons 4.0 license, "
				+ "http://creativecommons.org/licenses/by/4.0/";
		
		Label creditHeading = new Label("Credits", game.skin, "subtitle");
		creditHeading.setAlignment(Align.center);
		Label authorCredit = new Label(authorCreditStr, game.skin);
		authorCredit.setAlignment(Align.center);
		Label skinCredit = new Label(skinCreditStr, game.skin);
		skinCredit.setAlignment(Align.center);
		skinCredit.setWrap(true);
		Label musicCredit = new Label(musicCreditStr, game.skin);
		musicCredit.setAlignment(Align.center);
		Label ccLicense = new Label(ccLicenseStr, game.skin);
		ccLicense.setAlignment(Align.center);
		ccLicense.setWrap(true);
		
		creditsDialog = new Dialog("", game.skin, "dialog") {
			public void result(Object obj) {}};
		Table creditsTable = creditsDialog.getContentTable();
		creditsTable.add(creditHeading).padBottom(15).padTop(15);
		creditsTable.row();
		creditsTable.add(authorCredit).padBottom(10).padLeft(15).padRight(15);
		creditsTable.row();
		creditsTable.add(skinCredit).fillX().padBottom(10);
		creditsTable.row();
		creditsTable.add(musicCredit).padBottom(10);
		creditsTable.row();
		creditsTable.add(ccLicense).fillX().padBottom(10);
		creditsDialog.button("Back", false);
		
		creditsButton = new TextButton("Credits", game.skin, "default");
			creditsButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					creditsDialog.show(stage);
					buttonpress.play();
				}
			});			
		gameFullDialog = new Dialog("", game.skin, "dialog") {
			public void result(Object obj) {
				buttonpress.play();
			}};
		gameFullDialog.text("We already have 2 players.");
		gameFullDialog.button("Got it", false); //sends "false" as the result
		
		connectionFailDialog = new Dialog("", game.skin, "dialog") {
		    public void result(Object obj) {
		    	buttonpress.play();
		    }};
		connectionFailDialog.text("Couldn't connect to the server");
		connectionFailDialog.button("Got it", false); //sends "false" as the result
		
		
		databaseFailDialog = new Dialog("", game.skin, "dialog") {
		    public void result(Object obj) {
		    	buttonpress.play();
		    }};
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
		
		gameTitle.setFontScale(2);
		
		Table mainMenuTable = new Table();
		mainMenuTable.setFillParent(true);
		mainMenuTable.padTop(100);
		mainMenuTable.add(gameTitle).colspan(3).padBottom(20).prefHeight(200);
		mainMenuTable.row();
		
		mainMenuTable.add(loginButton).width(Puzzungeon.WIDTH*0.16f).pad(20);
		mainMenuTable.add(newUserButton).width(Puzzungeon.WIDTH*0.22f).pad(20);
		mainMenuTable.add(guestButton).width(Puzzungeon.WIDTH*0.34f).pad(20);
		mainMenuTable.row();
			
/****************************************************************************************
*                             end: Main Menu UI
****************************************************************************************/

		
/****************************************************************************************
*                             start: Exit Button
****************************************************************************************/
		Table exitButtonTable = new Table().bottom().right();
		exitButtonTable.setFillParent(true);
		exitButtonTable.add(creditsButton).width(Puzzungeon.WIDTH * 0.2f).pad(10);
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
		game.batch.setProjectionMatrix(stage.getCamera().combined);
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
				game.setScreen(new GameLobbyScreen(game));
			} else {
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
