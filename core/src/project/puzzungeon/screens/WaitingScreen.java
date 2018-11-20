package project.puzzungeon.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;

import project.puzzungeon.Client;
import project.puzzungeon.Puzzungeon;
import project.server.ChatMessage;
import project.server.LoginRegister;
import project.server.Password;
import project.server.Username;

//waitroom screen
public class WaitingScreen implements Screen{

	Puzzungeon game; //reference to the game
	private Stage stage;
	FitViewport viewport;
	
	//asset reference
	private TextureAtlas atlas;
	private Sprite background;
	private Sprite mouseSprite;
	private Sprite teleporterSprite;

	//shared by different methods
	private Label showMessage1;
	private Label showMessage2;
	private Label showMessage3;
	private Label showMessage4;
	private Label otherPlayerUsername;
	private Label otherPlayerPastScore;
	private Label waitingState;
	private Label roomCode;
	private TextButton readyButton;
	private Dialog connectionLostDialog;
	private Boolean displayDialog;
	
	//global Table variable
	private Table waitingTable;
	
	//constructor
	public WaitingScreen(Puzzungeon game) {
		this.game = game;
		viewport = new FitViewport(Puzzungeon.WIDTH, Puzzungeon.HEIGHT);
		stage = new Stage(viewport);
		
		atlas = game.assetLoader.manager.get("sprites.txt");
		mouseSprite = atlas.createSprite("mouse-cursor-icon");
		teleporterSprite = atlas.createSprite("teleporter");
		background = atlas.createSprite("dungeon-wall");
		background.setOrigin(0, 0);
		background.setSize(Puzzungeon.WIDTH, Puzzungeon.HEIGHT);
		
		Gdx.input.setInputProcessor(stage);
		displayDialog = true;
		ChatMessage cm = new ChatMessage(game.client.clientUsername+" ", "has joined the chat.", true);
		game.client.sendMessage(cm);
	}
	
	@Override
	public void show() {
/****************************************************************************************
*                             start: actors functionality
****************************************************************************************/
		
		//create the actors
		Label gameTitle = new Label("Waiting Room", game.skin, "subtitle");
		Label localPlayerUsername = new Label("Player1: " + game.client.clientUsername, game.skin, "subtitle");
		localPlayerUsername.setColor(game.skin.getColor("Teal"));
		Label localPlayerPastScore = new Label("Best Time: " + Integer.toString(game.client.localPlayer.pastScore), game.skin, "subtitle");
		otherPlayerPastScore = new Label("", game.skin, "subtitle");
		otherPlayerUsername = new Label("", game.skin, "subtitle");
		otherPlayerUsername.setColor(game.skin.getColor("Red"));
		roomCode = new Label("Code:" + game.client.gameRoomCode, game.skin);
		waitingState = new Label("Waiting for another player...", game.skin);
		
		Label chatTitle = new Label("Chat",game.skin, "subtitle");
		
		showMessage1 = new Label("",game.skin);
		showMessage2 = new Label("",game.skin);
		showMessage3 = new Label("",game.skin);
		showMessage4 = new Label("",game.skin);
				
		final TextField inputBox = new TextArea("",game.skin);
			//when ENTER key is pressed, send message to the serverthread
			inputBox.setTextFieldListener(new TextFieldListener() {
				@Override
				public void keyTyped(TextField textField, char c) {
					if(Gdx.input.isKeyPressed(Keys.ENTER)) {
						String messageStr = new String();
		                //allow to send empty message
		                if(inputBox.getText().length() == 0) {
		                	messageStr = "";
		                }
		                else {
		                	messageStr = inputBox.getText();
		                	//remove newline character
		                	messageStr = messageStr.replace("\n", "");
		                }
		                //clear inputbox after new message is sent
		                inputBox.setText("");
		                ChatMessage cm = new ChatMessage(game.client.clientUsername+":", messageStr, false);
		                game.client.sendMessage(cm);
					}
				}
			});
		
		final TextButton sendButton  = new TextButton("Send Message", game.skin, "default");
			//when sendButton is clicked, send message to the serverthread
			sendButton.addListener(new ClickListener(){
	            @Override 
	            public void clicked(InputEvent event, float x, float y){
	                String messageStr = new String();
	                //allow to send empty message
	                if(inputBox.getText().length() == 0) {
	                	messageStr = "";
	                }
	                else {
	                	messageStr = inputBox.getText();
	                }
	                //clear inputbox after new message is sent
	                inputBox.setText("");
	                ChatMessage cm = new ChatMessage(game.client.clientUsername+":", messageStr, false);
	                game.client.sendMessage(cm);
	            }
	        });
			
		readyButton  = new TextButton("Ready?", game.skin, "default");
			//when readybutton is clicked, change localplayerstate(front-end)
			//and update the playerVec on the server(back-end)
			readyButton.addListener(new ClickListener(){
	            @Override 
	            public void clicked(InputEvent event, float x, float y){
	            	
	            	if(game.client.localPlayer.readyState == false) {
	            		game.client.localPlayer.readyState = true;
		                game.client.updatePlayer();
		                ChatMessage cm = new ChatMessage(game.client.clientUsername, "is ready", true);
		                game.client.sendMessage(cm);
		                readyButton.setText("Cancel?");
	            	}
	            	else {
	            		game.client.localPlayer.readyState = false;
		                game.client.updatePlayer();
		                ChatMessage cm = new ChatMessage(game.client.clientUsername, "isn't ready anymore", true);
		                game.client.sendMessage(cm);
		                readyButton.setText("Ready?");
	            	}
	            }
	        });
			

		//controls instructions
		String instructions1Str = "Each player has one half of the puzzle."
				+ " Complete both of your sides to escape the Puzzungeon!";
		String instructions2Str = "Drag and Drop the puzzle pieces.";
		String instructions3Str = "You may have some of the other player’s"
				+ " pieces, and vice versa. Use the teleporter to send puzzle"
				+ " pieces to the other player!";
		Label tutorial = new Label("Tutorial", game.skin, "subtitle");
		tutorial.setAlignment(Align.center);
		Label instruct1 = new Label(instructions1Str, game.skin, "default");
		instruct1.setAlignment(Align.center);
		instruct1.setWrap(true);
		Label instruct2 = new Label(instructions2Str, game.skin, "default");
		Label instruct3 = new Label(instructions3Str, game.skin, "default");
		instruct3.setWrap(true);
		Image mouse = new Image(new SpriteDrawable(mouseSprite));
		mouse.setScaling(Scaling.fit);
		Image teleporter = new Image(new SpriteDrawable(teleporterSprite));
		teleporter.setScaling(Scaling.fit);

		connectionLostDialog = new Dialog("", game.skin, "dialog") {
		    public void result(Object obj) {
		    	game.setScreen(new MainMenuScreen(game));
		    }};
		connectionLostDialog.text("Connection lost.");
		connectionLostDialog.button("Got it", false); //sends "false" as the result

		
		
		TextButton backButton = new TextButton("Back", game.skin, "default");
			backButton.addListener(new ClickListener(){
				@Override 
				public void clicked(InputEvent event, float x, float y){
					String username = new String(game.client.username);
					String password = new String(game.client.password);
					game.client.disconnect = true;
					game.client.localPlayer.disconnect = true;
					game.client.updatePlayer();
					game.client = new Client(game.serverAddress, game.serverPort);
					
					if(username.equals("guest")) {
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
					}
					else {
						game.client.clientUsername = username;
						if(!game.client.connectState) {
							//set up connection to the server
							System.out.println("Trying to connect...");
							if(!game.client.connect()) {
								System.out.println("Unable to connect to the server");
								displayDialog = true;
								game.client = new Client(game.serverAddress, game.serverPort);
							}
							else {
								//send username and password to back-end
								game.client.username = username;
								game.client.password = password;
								game.client.sendUsername(new Username(username));
								game.client.sendPassword(new Password(password));
								game.client.sendLoginRegister(new LoginRegister("login"));
								displayDialog = true;
							}
						}
					}
					game.setScreen(new GameLobbyScreen(game));
				}
			});
		
		TextButton exitButton = new TextButton("Exit", game.skin, "default");
			exitButton.addListener(new ClickListener(){
				@Override 
				public void clicked(InputEvent event, float x, float y){
					Gdx.app.exit();
				}
			});
		
/****************************************************************************************
*                             end: actors functionality
****************************************************************************************/
			
/****************************************************************************************
*                             start: actors layout
****************************************************************************************/
		
/****************************************************************************************
*                             start: Waiting state UI
****************************************************************************************/
		
		waitingTable = new Table().top().padTop(30);
		waitingTable.setFillParent(true);
		waitingTable.add(gameTitle).colspan(2);
		waitingTable.row();
		waitingTable.add(localPlayerUsername).padRight(10);
		waitingTable.add(localPlayerPastScore).padLeft(10);
		waitingTable.row();
		waitingTable.add(otherPlayerUsername).padRight(10);;
		waitingTable.add(otherPlayerPastScore).padLeft(10);;
		waitingTable.row();
		waitingTable.add(readyButton).colspan(2).height(0);
		waitingTable.row();
		waitingTable.add(waitingState).colspan(2).height(90);
		waitingTable.row();
		waitingTable.add(roomCode).colspan(2).padBottom(15);
		
			
/****************************************************************************************
*                             end: Waiting state UI
****************************************************************************************/

/****************************************************************************************
*                             start: instructions UI
****************************************************************************************/
		
		waitingTable.row();
		
		Table instructionTable = new Table();
		Drawable tableBackground = game.skin.getDrawable("window");
		if (tableBackground != null) {
			instructionTable.setBackground(tableBackground);
		} else {
			System.out.println("Drawable of name 'window' not found.");
		}
		instructionTable.pad(0).padTop(30);
		instructionTable.add(tutorial).colspan(2).width(800).align(Align.center).padBottom(15);
		instructionTable.row();
		instructionTable.add(instruct1).colspan(2).align(Align.center).fillX().padBottom(15);
		instructionTable.row();
		instructionTable.add(mouse).fill().padBottom(15).padRight(10);
		instructionTable.add(instruct2).fillX().height(70).padBottom(15);
		instructionTable.row();
		instructionTable.add(teleporter).fill().padRight(10).padBottom(15);
		instructionTable.add(instruct3).fillX().height(200).padBottom(15);
		
		waitingTable.add(instructionTable).fillX().align(Align.center).colspan(2);
		
/****************************************************************************************
*                             end: instructions UI
****************************************************************************************/
				
/****************************************************************************************
*                             start: chatroom UI
****************************************************************************************/			
		
		Table chatRoom = new Table().bottom().left();
		chatRoom.pad(0);
		Table chatRoomCol1 = new Table();
		Drawable chatRoomBackground = game.skin.getDrawable("window");
		if (chatRoomBackground != null) {
			chatRoomCol1.setBackground(chatRoomBackground);
		} else {
			System.out.println("Drawable of name 'window' not found.");
		}
		chatRoomCol1.pad(0);
		chatRoomCol1.add(chatTitle).width(Puzzungeon.WIDTH - 220).colspan(2).fillX().align(Align.left);
		chatRoomCol1.getCell(chatTitle).padTop(15).padLeft(15);
		chatRoomCol1.row();
		chatRoomCol1.add(showMessage4).colspan(2).fillX().align(Align.left).padLeft(15).padRight(15);
		chatRoomCol1.row();
		chatRoomCol1.add(showMessage3).colspan(2).fillX().align(Align.left).padLeft(15).padRight(15);
		chatRoomCol1.row();
		chatRoomCol1.add(showMessage2).colspan(2).fillX().align(Align.left).padLeft(15).padRight(15);
		chatRoomCol1.row();
		chatRoomCol1.add(showMessage1).colspan(2).fillX().align(Align.left).padLeft(15).padRight(15);
		chatRoomCol1.row();
		chatRoomCol1.add(inputBox).fillX().padLeft(15);
		chatRoomCol1.add(sendButton).align(Align.left);
		chatRoom.add(chatRoomCol1);
		
		Table chatRoomCol2 = new Table();
		chatRoomCol2.add(backButton);
		chatRoomCol2.row();
		chatRoomCol2.add(exitButton);
		chatRoom.add(chatRoomCol2);

/****************************************************************************************
*                             end: chatroom UI
****************************************************************************************/
				
		stage.addActor(waitingTable);
		
		//add chatroom to the stage
		stage.addActor(chatRoom);
		
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
				
		//draw stage
		stage.getViewport().apply();
		stage.act(Gdx.graphics.getDeltaTime());
		update();
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height);		
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
		
		if(game.client.otherPlayer.playerID != -1) {
			
			//update the visibility of Ready button
			waitingTable.getCell(readyButton).height(90);
			readyButton.setVisible(true);
			
			//update waiting state
			waitingState.setText("");
			waitingTable.getCell(waitingState).height(0);
			otherPlayerUsername.setText("Player2: " + game.client.otherPlayer.playerName);
			otherPlayerPastScore.setText("Best time: " + Integer.toString(game.client.otherPlayer.pastScore));
		}
		
		if(game.client.otherPlayer.playerID == -1) {
			
			//update the visibility of Ready button
			readyButton.setVisible(false);
			waitingTable.getCell(readyButton).height(0);
			
			//update waiting state
			otherPlayerUsername.setText("");
			otherPlayerPastScore.setText("");
			waitingState.setText("Waiting for another player...");
			waitingTable.getCell(waitingState).height(60);
		}
		
		//update chatroom
		showMessage1.setText(game.client.messageVec.get(3).getUsername()+" " + game.client.messageVec.get(3).getMessage());
		showMessage2.setText(game.client.messageVec.get(2).getUsername()+" " + game.client.messageVec.get(2).getMessage());
		showMessage3.setText(game.client.messageVec.get(1).getUsername()+" " + game.client.messageVec.get(1).getMessage());
		showMessage4.setText(game.client.messageVec.get(0).getUsername()+" " + game.client.messageVec.get(0).getMessage());
		if(game.client.messageVec.get(3).isSystemMessage()) {
			showMessage1.setColor(Color.RED);
			//showMessage1.setAlignment(Align.center);
		}
		else {
			showMessage1.setColor(Color.WHITE);
			//showMessage1.setAlignment(Align.left);
		}
		if(game.client.messageVec.get(2).isSystemMessage()) {
			showMessage2.setColor(Color.RED);
			//showMessage2.setAlignment(Align.center);
		}
		else {
			showMessage2.setColor(Color.WHITE);
			//showMessage2.setAlignment(Align.left);
		}
		if(game.client.messageVec.get(1).isSystemMessage()) {
			showMessage3.setColor(Color.RED);
			//showMessage3.setAlignment(Align.center);
		}
		else {
			showMessage3.setColor(Color.WHITE);
			showMessage3.setAlignment(Align.left);
		}
		if(game.client.messageVec.get(0).isSystemMessage()) {
			showMessage4.setColor(Color.RED);
			//showMessage4.setAlignment(Align.center);
		}
		else {
			showMessage4.setColor(Color.WHITE);
			//showMessage4.setAlignment(Align.left);
		}
		

		//check if every player is ready
		if(game.client.bothPlayerReady) {
			game.client.messageVec.remove(0);
			game.client.messageVec.add(new ChatMessage("The game is going to start!","", true));
			game.setScreen(new MainGameScreen(game,1));
		}
		
		if(!game.client.connectState & displayDialog) {
			connectionLostDialog.show(stage);
			displayDialog = false;
			System.out.println("waitingScreen: connection lost.");
		}
	}
}
