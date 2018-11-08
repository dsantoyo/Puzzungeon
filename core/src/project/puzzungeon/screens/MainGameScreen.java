package project.puzzungeon.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import project.puzzungeon.Puzzungeon;
import project.server.ChatMessage;

//Main Gameplay screen
public class MainGameScreen implements Screen{

	Puzzungeon game; //reference to the game
	private Stage stage;
	
	//shared by different methods
	private Label showMessage1;
	private Label showMessage2;
	private Label showMessage3;
	private Label showMessage4;
	
	private Label showLocalPlayerName;
	private Label showOtherPlayerName;
	
	private Label showLocalPlayerPC;
	private Label showOtherPlayerPC;
	
	private Label showGameTime;
	
	private Dialog connectionLostDialog;
	private Dialog player2LeftDialog;
	private Boolean displayDialog;
	
	
	//constructor
	public MainGameScreen(Puzzungeon game) {
		this.game = game;
		stage = new Stage();
		displayDialog = true;
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void show() {
		
/****************************************************************************************
*                             start: actors functionality
****************************************************************************************/
				
		//create the actors
		Label gameTitle = new Label("Main Game Screen", game.skin);
		
		Label chatTitle = new Label("Chat",game.skin);
		showMessage1 = new Label("",game.skin);
		showMessage2 = new Label("",game.skin);
		showMessage3 = new Label("",game.skin);
		showMessage4 = new Label("",game.skin);
		
		showLocalPlayerName = new Label("Player1: " + game.client.localPlayer.playerName, game.skin);
		showOtherPlayerName = new Label("Player2: " + game.client.otherPlayer.playerName, game.skin);
		
		showLocalPlayerPC = new Label(" Pieces Completed: 1/10", game.skin);
		showOtherPlayerPC = new Label(" Pieces Completed: 2/10", game.skin);
		showGameTime = new Label(" Time: 10:10", game.skin);
				
		final TextArea inputBox = new TextArea("",game.skin);
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
	                ChatMessage cm = new ChatMessage(game.client.clientUsername+":", messageStr);
	                game.client.sendMessage(cm);
	            }
	        });
			
		connectionLostDialog = new Dialog("Connection Lost", game.skin, "dialog") {
		    public void result(Object obj) {
		    	game.setScreen(new MainMenuScreen(game));
		    }};
		connectionLostDialog.text("Connection to sever lost.");
		connectionLostDialog.button("Got it", false); //sends "false" as the result
		
		player2LeftDialog = new Dialog("Player2 has left", game.skin, "dialog") {
		    public void result(Object obj) {
		    	game.client.localPlayer.readyState = false;
		    	game.client.updatePlayer();
		    	game.setScreen(new WaitingScreen(game));
		    }};
		player2LeftDialog.text("Player2 has left.");
		player2LeftDialog.button("Got it", false); //sends "false" as the result
		
/****************************************************************************************
*                             end: actors functionality
****************************************************************************************/
		
/****************************************************************************************
*                             start: actors layout
****************************************************************************************/
		
		
		//use vg and hg to group the actors now. changes should be made to make it look better
		VerticalGroup topbar = new VerticalGroup().left();
		topbar.setFillParent(true);
		topbar.addActor(gameTitle);
		topbar.addActor(showGameTime);
		HorizontalGroup topRow1 = new HorizontalGroup();
		HorizontalGroup topRow2 = new HorizontalGroup();
		
		topRow1.addActor(showLocalPlayerName);
		topRow1.addActor(showLocalPlayerPC);
		topbar.addActor(topRow1);
		topRow2.addActor(showOtherPlayerName);
		topRow2.addActor(showOtherPlayerPC);
		topbar.addActor(topRow2);			
		stage.addActor(topbar);
		
		
		
/****************************************************************************************
*                             start: chatroom UI
****************************************************************************************/	
		
		//set colors of the labels
		chatTitle.setColor(Color.GREEN);
		
		Table chatRoom = new Table().bottom().left();
		chatRoom.pad(0);
		chatRoom.add(chatTitle).width(game.WIDTH).colspan(2);
		chatRoom.row();
		chatRoom.add(showMessage4).width(game.WIDTH).colspan(2);
		chatRoom.row();
		chatRoom.add(showMessage3).width(game.WIDTH).colspan(2);
		chatRoom.row();
		chatRoom.add(showMessage2).width(game.WIDTH).colspan(2);
		chatRoom.row();
		chatRoom.add(showMessage1).width(game.WIDTH).colspan(2);
		chatRoom.row();
		chatRoom.add(inputBox).width(game.WIDTH*0.7f);
		chatRoom.add(sendButton).width(game.WIDTH*0.3f);
		
/****************************************************************************************
*                             end: chatroom UI
****************************************************************************************/

		//add bottom bar to the stage
		stage.addActor(chatRoom);
		
		//draw debugline to see the boundary of each actor
		stage.setDebugAll(true);
		
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
		
		game.batch.dispose();
		
	}
	
	public void update() {
		
		//update chatroom
		showMessage1.setText(game.client.messageVec.get(3).getUsername()+" " + game.client.messageVec.get(3).getMessage());
		showMessage2.setText(game.client.messageVec.get(2).getUsername()+" " + game.client.messageVec.get(2).getMessage());
		showMessage3.setText(game.client.messageVec.get(1).getUsername()+" " + game.client.messageVec.get(1).getMessage());
		showMessage4.setText(game.client.messageVec.get(0).getUsername()+" " + game.client.messageVec.get(0).getMessage());
		
		
		//if connection is lost
		if(!game.client.connectState & displayDialog) {
			System.out.println("MainGameScreen: connection lost.");
			connectionLostDialog.show(stage);
			displayDialog = false;
		}
		
		//if player2 has left
		if((game.client.otherPlayer.playerID == -1) & displayDialog) {
			System.out.println("MainGameScreen: player2 has left.");
			player2LeftDialog.show(stage);
			displayDialog = false;
		}
	}
}
