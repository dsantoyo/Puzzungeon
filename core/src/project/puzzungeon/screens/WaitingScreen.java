package project.puzzungeon.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

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
		ChatMessage cm = new ChatMessage(game.client.clientUsername+" ", "has joined the chat.");
		game.client.sendMessage(cm);
	}
	
	@Override
	public void show() {
/****************************************************************************************
*                             start: actors functionality
****************************************************************************************/
		
		//create the actors
		Label gameTitle = new Label("Puzzungeon", game.skin);
		Label localPlayerUsername = new Label("Player1: " + game.client.clientUsername, game.skin);
		waitingState = new Label("Waiting for another player...", game.skin);
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
		                ChatMessage cm = new ChatMessage(game.client.clientUsername+":", messageStr);
		                game.client.sendMessage(cm);
					}
				}
			});
		
		final TextButton sendButton  = new TextButton("Send", game.skin, "default");
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
	                ChatMessage cm = new ChatMessage(game.client.clientUsername+":", messageStr);
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
		                ChatMessage cm = new ChatMessage(game.client.clientUsername, "is ready");
		                game.client.sendMessage(cm);
		                readyButton.setText("Cancel?");
	            	}
	            	else {
	            		game.client.localPlayer.readyState = false;
		                game.client.updatePlayer();
		                ChatMessage cm = new ChatMessage(game.client.clientUsername, "isn't ready anymore");
		                game.client.sendMessage(cm);
		                readyButton.setText("Ready?");
	            	}
	            }
	        });
			
		connectionLostDialog = new Dialog("Connection Lost", game.skin, "dialog") {
		    public void result(Object obj) {
		    	game.setScreen(new MainMenuScreen(game));
		    }};
		connectionLostDialog.text("Connection to sever lost.");
		connectionLostDialog.button("Got it", false); //sends "false" as the result
		
/****************************************************************************************
*                             end: actors functionality
****************************************************************************************/
			
/****************************************************************************************
*                             start: actors layout
****************************************************************************************/
		

		//use vg and hg to group the actors now. changes should be made to make it look better
		VerticalGroup vg1 = new VerticalGroup();
		vg1.setFillParent(true);
		vg1.addActor(gameTitle);
		vg1.addActor(localPlayerUsername);
		vg1.addActor(waitingState);
		
		readyButton.setVisible(false);
		vg1.addActor(readyButton);
		
		stage.addActor(vg1);
				
		
		//chatroom UI
		//5 rows for the bottom bar.
		VerticalGroup Chatroom = new VerticalGroup().bottom().left();
		HorizontalGroup chatRow0 = new HorizontalGroup();
		HorizontalGroup chatRow1 = new HorizontalGroup();
		HorizontalGroup chatRow2 = new HorizontalGroup();
		HorizontalGroup chatRow3 = new HorizontalGroup();
		HorizontalGroup chatRow4 = new HorizontalGroup();
		
		
		chatRow0.fill();
		chatRow0.addActor(inputBox);
		chatRow0.addActor(sendButton);
		
		chatRow4.addActor(showMessage4);
		chatRow3.addActor(showMessage3);
		chatRow2.addActor(showMessage2);
		chatRow1.addActor(showMessage1);
		
		Chatroom.addActor(chatRow4);
		Chatroom.addActor(chatRow3);
		Chatroom.addActor(chatRow2);
		Chatroom.addActor(chatRow1);
		Chatroom.addActor(chatRow0);
		
		//add bottom bar to the stage
		stage.addActor(Chatroom);
		
		//draw debugline to see the boundary of each actor
		stage.setDebugAll(true);
		
/****************************************************************************************
*                             end: actors functionality
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
		
		//check if every player is ready
		if(game.client.bothPlayerReady) {
			game.client.messageVec.remove(0);
			game.client.messageVec.add(new ChatMessage("","The game is going to start!"));
			game.setScreen(new MainGameScreen(game));
		}
		
		if(!game.client.connectState & displayDialog) {
			connectionLostDialog.show(stage);
			displayDialog = false;
			System.out.println("waitingScreen: connection lost.");
		}
	}
}
