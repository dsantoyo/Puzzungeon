package project.puzzungeon.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import project.puzzungeon.Client;
import project.puzzungeon.Puzzungeon;
import project.server.ChatMessage;

//waitroom screen
public class WaitingScreen implements Screen{

	Puzzungeon game; //reference to the game
	private Stage stage;
	
	//shared by different methods
	private Label showMessage1;
	private Label showMessage2;
	private Label showMessage3;
	private Label showMessage4;
	private Label waitingState;
	private TextButton readyButton;
	private Dialog connectionLostDialog;
	private Boolean displayDialog;
	
	//constructor
	public WaitingScreen(Puzzungeon game) {
		this.game = game;
		stage = new Stage();
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
		Label gameTitle = new Label("Waiting Room", game.skin);
		Label localPlayerUsername = new Label("Player1: " + game.client.clientUsername, game.skin);
		waitingState = new Label("Waiting for another player...", game.skin);
		Label chatTitle = new Label("Chat",game.skin);
		
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
		Label instruct1 = new Label(instructions1Str, game.skin, "default");
		Label instruct2 = new Label(instructions2Str, game.skin, "default");
		Label instruct3 = new Label(instructions3Str, game.skin, "default");
		Image mouse = new Image(new Texture(Gdx.files.internal("mouse-cursor-icon.png")));
		mouse.setScale(0.5f);
		Image teleporter = new Image(new Texture(Gdx.files.internal("teleporter.png")));
		instruct1.setWrap(true);
		instruct3.setWrap(true);

		connectionLostDialog = new Dialog("Error", game.skin, "dialog") {
		    public void result(Object obj) {
		    	game.setScreen(new MainMenuScreen(game));
		    }};
		connectionLostDialog.text("Connection lost.");
		connectionLostDialog.button("Got it", false); //sends "false" as the result

		
		
		TextButton backButton = new TextButton("Back", game.skin, "default");
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
		
		Table waitingTable = new Table().top();
		waitingTable.setFillParent(true);
		waitingTable.add(gameTitle);
		waitingTable.row();
		waitingTable.add(localPlayerUsername);
		waitingTable.row();
		waitingTable.add(waitingState);
		waitingTable.row();
		waitingTable.add(readyButton);
			
/****************************************************************************************
*                             end: Waiting state UI
****************************************************************************************/

/****************************************************************************************
*                             start: instructions UI
****************************************************************************************/
		
		Table instructionTable = new Table().left();
		instructionTable.setFillParent(true);
		instructionTable.add(instruct1);
		instructionTable.row();
		instructionTable.add(mouse);
		instructionTable.add(instruct2);
		instructionTable.row();
		instructionTable.add(instruct3);
		instructionTable.row();
		instructionTable.add(teleporter);
		
		
		stage.addActor(instructionTable);
		
		/*
		//controls instructions widget setup
		VerticalGroup instructs = new VerticalGroup();
		HorizontalGroup line2 = new HorizontalGroup();
		line2.rowAlign(Align.center);
		line2.addActor(mouse);
		line2.addActor(instruct2);
		instructs.addActor(instruct1);
		instructs.addActor(line2);
		instructs.addActor(instruct3);
		instructs.addActor(teleporter);
		stage.addActor(instructs);
		*/
		
/****************************************************************************************
*                             end: instructions UI
****************************************************************************************/
				
/****************************************************************************************
*                             start: chatroom UI
****************************************************************************************/	
		
		//set colors of the labels
		chatTitle.setColor(Color.GREEN);
		
		
		Table chatRoom = new Table().bottom().left();
		chatRoom.pad(0);
		chatRoom.add(chatTitle).width(game.WIDTH).colspan(3);
		chatRoom.row();
		chatRoom.add(showMessage4).width(game.WIDTH).colspan(3);
		chatRoom.row();
		chatRoom.add(showMessage3).width(game.WIDTH).colspan(3);
		chatRoom.row();
		chatRoom.add(showMessage2).width(game.WIDTH).colspan(3);
		chatRoom.row();
		chatRoom.add(showMessage1).width(game.WIDTH).colspan(3);
		chatRoom.row();
		
		chatRoom.add(inputBox).width(game.WIDTH*0.7f);
		chatRoom.add(sendButton).width(game.WIDTH*0.3f).colspan(2);
		chatRoom.row();
		
		chatRoom.add(new Label("",game.skin)).width(game.WIDTH*0.7f);
		chatRoom.add(backButton).width(game.WIDTH*0.15f).pad(0);
		chatRoom.add(exitButton).width(game.WIDTH*0.15f).pad(0);
		
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
		stage.act(Gdx.graphics.getDeltaTime());
		update();
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

	}
	
	
	public void update() {
		
		if(game.client.otherPlayer.playerID != -1) {
			
			//update the visibility of Ready button
			readyButton.setVisible(true);
			
			//update waiting state
			waitingState.setText("Player2: " + game.client.otherPlayer.playerName);
		}
		
		if(game.client.otherPlayer.playerID == -1) {
			
			//update the visibility of Ready button
			readyButton.setVisible(false);
			
			//update waiting state
			waitingState.setText("Waiting for another player...");
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
			game.setScreen(new MainGameScreen(game));
		}
		
		if(!game.client.connectState & displayDialog) {
			connectionLostDialog.show(stage);
			displayDialog = false;
			System.out.println("waitingScreen: connection lost.");
		}
	}
}
