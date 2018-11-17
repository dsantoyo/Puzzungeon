package project.puzzungeon.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

import project.puzzungeon.Client;
import project.puzzungeon.Puzzungeon;
import project.server.LobbyChoice;

//First screen; 

public class GameLobbyScreen implements Screen{

	Puzzungeon game; //reference to the game
	private Stage stage;
	FitViewport viewport;
	
	//asset references
	TextureAtlas atlas;
	Sprite background;
	
	//actor references
	private Label gameTitle;
	private TextButton newGameButton;
	private TextButton existGameButton;
	private TextField codeInputField;
	private TextButton randomGameButton;
	private TextButton backButton;
	private TextButton exitButton;
	
	private Dialog noEmptyRoomDialog;
	private Dialog roomNotAvailableDialog;
	
	//shared by different methods
	private Boolean displayDialog;

	
	//constructor
	public GameLobbyScreen(Puzzungeon game) {
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
		
		newGameButton = new TextButton("Create Game", game.skin, "default");
		newGameButton.addListener(new ClickListener(){
				@Override 
		            public void clicked(InputEvent event, float x, float y){
						game.client.sendLobbyChoice(new LobbyChoice("new game", ""));
						displayDialog = true;
		            }
		        });
		
		existGameButton = new TextButton("Use Code", game.skin, "default");
		existGameButton.addListener(new ClickListener(){
				@Override 
	            public void clicked(InputEvent event, float x, float y){
					String code = codeInputField.getText();
					game.client.sendLobbyChoice(new LobbyChoice("use code", code));
					displayDialog = true;
					
	            	}
	        	});
		
		codeInputField = new TextField("",game.skin);

		randomGameButton = new TextButton("Random Game", game.skin, "default");
		randomGameButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					game.client.sendLobbyChoice(new LobbyChoice("random game", ""));
					displayDialog = true;
				}
			});

		exitButton = new TextButton("Exit", game.skin, "default");
		exitButton.addListener(new ClickListener(){
				@Override 
				public void clicked(InputEvent event, float x, float y){
					Gdx.app.exit();
				}
			});
					
		backButton = new TextButton("Back", game.skin, "default");
		backButton.addListener(new ClickListener(){
			@Override 
			public void clicked(InputEvent event, float x, float y){
				game.client.disconnect = true;
				game.client.localPlayer.disconnect = true;
				game.client.updatePlayer();
				game.client = new Client(game.serverAddress, game.serverPort);
				game.setScreen(new MainMenuScreen(game));
			}
		});
		
		
		noEmptyRoomDialog = new Dialog("Error", game.skin, "dialog") {
		    public void result(Object obj) {}};
		noEmptyRoomDialog.text("We don't have any room available");
		noEmptyRoomDialog.button("Got it", false); //sends "false" as the result
		
		
		roomNotAvailableDialog = new Dialog("Error", game.skin, "dialog") {
		    public void result(Object obj) {}};
		roomNotAvailableDialog.text("The room is not available");
		roomNotAvailableDialog.button("Got it", false); //sends "false" as the result
			
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
		
		mainMenuTable.add(newGameButton).width(Puzzungeon.WIDTH*0.2f).pad(0.3f);
		mainMenuTable.row();
		mainMenuTable.add(existGameButton).width(Puzzungeon.WIDTH*0.2f).pad(0.3f);
		mainMenuTable.add(codeInputField).width(Puzzungeon.WIDTH*0.2f).pad(0.3f);
		mainMenuTable.row();
		mainMenuTable.add(randomGameButton).width(Puzzungeon.WIDTH*0.3f).pad(0.3f);
		mainMenuTable.row();
			
/****************************************************************************************
*                             end: Main Menu UI
****************************************************************************************/

		
/****************************************************************************************
*                             start: Exit Button
****************************************************************************************/
		Table exitButtonTable = new Table().bottom().right();
		exitButtonTable.setFillParent(true);
		exitButtonTable.add(backButton).width(Puzzungeon.WIDTH*0.2f).pad(10);
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
		
		//update
		update();
		
		//draw background
		viewport.apply();
		game.batch.begin();
		background.draw(game.batch);
		game.batch.end();
		
		//update and draw stage
		stage.getViewport().apply();
		stage.act(Gdx.graphics.getDeltaTime());
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
	
	public void update() {
		
		if(displayDialog == true) {
			if(game.client.gameRoomCode.equals("no empty room")){
				game.client.gameRoomCode = "";
				noEmptyRoomDialog.show(stage);
				displayDialog = false;
			}
			
			else if(game.client.gameRoomCode.equals("room not available")){
				game.client.gameRoomCode = "";
				roomNotAvailableDialog.show(stage);
				displayDialog = false;
			}
			
			else if(!game.client.gameRoomCode.equals("")) {
				game.setScreen(new WaitingScreen(game));
			}
		}
	}
}
