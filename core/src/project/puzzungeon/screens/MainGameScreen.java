package project.puzzungeon.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
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
	
	private Dialog connectionLostDialog;
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
		showMessage1 = new Label("",game.skin);
		showMessage2 = new Label("",game.skin);
		showMessage3 = new Label("",game.skin);
		Label showDivider = new Label("-------------------------------------",game.skin);
		
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
		VerticalGroup vg1 = new VerticalGroup();
		vg1.setFillParent(true);
		vg1.addActor(gameTitle);
		stage.addActor(vg1);
		
		//chatroom UI
		//5 rows for the bottom bar.
		HorizontalGroup chatRow0 = new HorizontalGroup().bottom().left();
		HorizontalGroup chatRow1 = new HorizontalGroup().left();
		HorizontalGroup chatRow2 = new HorizontalGroup().left();
		HorizontalGroup chatRow3 = new HorizontalGroup().left();
		HorizontalGroup chatRow4 = new HorizontalGroup().left();
		
		VerticalGroup Chatroom = new VerticalGroup().bottom().left();
		
		chatRow0.addActor(inputBox);
		chatRow0.addActor(sendButton);
		
		chatRow4.addActor(showDivider);
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
		showMessage1.setText(game.client.messageVec.get(2).getUsername()+" " + game.client.messageVec.get(2).getMessage());
		showMessage2.setText(game.client.messageVec.get(1).getUsername()+" " + game.client.messageVec.get(1).getMessage());
		showMessage3.setText(game.client.messageVec.get(0).getUsername()+" " + game.client.messageVec.get(0).getMessage());
		
		if(!game.client.connectState & displayDialog) {
			System.out.println("MainGameScreen: connection lost.");
			connectionLostDialog.show(stage);
			displayDialog = false;
		}
	}
}
