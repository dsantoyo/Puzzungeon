package project.puzzungeon.screens;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
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
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

import project.puzzungeon.Client;
import project.puzzungeon.PuzzlePiece;
import project.puzzungeon.Puzzungeon;
import project.server.ChatMessage;

//Main Gameplay screen
public class MainGameScreen implements Screen{

	Puzzungeon game; //reference to the game
	private Stage stage;
	FitViewport viewport;
	private int count1 = 0;
	private int count2 = 0;
	
	//background references
	TextureAtlas atlas;
	Sprite background;
	
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
	private long startTime;
	ShapeRenderer shapeRenderer;
	
	
	public int correctPieceCount;
	
	public int totalPieces;
	
	public ArrayList<PuzzlePiece> pieceList;
	

	public DragAndDrop dragAndDrop;
	
	
	//constructor
	public MainGameScreen(Puzzungeon game) {
		this.game = game;
		viewport = new FitViewport(Puzzungeon.WIDTH, Puzzungeon.HEIGHT);
		stage = new Stage(viewport);
		displayDialog = true;
		startTime = System.nanoTime();
		Gdx.input.setInputProcessor(stage);
		

		atlas = game.assetLoader.manager.get("sprites.txt");
		background = atlas.createSprite("dungeon-wall");
		background.setOrigin(0, 0);
		background.setScale(9f);
		totalPieces = 4;
	}

	@Override
	public void show() {
				
/****************************************************************************************
*                             start: actors functionality
****************************************************************************************/
				
		//create the actors
		Label chatTitle = new Label("Chat",game.skin);
		showMessage1 = new Label("",game.skin);
		showMessage2 = new Label("",game.skin);
		showMessage3 = new Label("",game.skin);
		showMessage4 = new Label("",game.skin);
		
		showLocalPlayerName = new Label("Player1: " + game.client.localPlayer.playerName, game.skin);
		showOtherPlayerName = new Label("Player2: " + game.client.otherPlayer.playerName, game.skin);
		
		showLocalPlayerPC = new Label(" Pieces Completed: " + game.client.localPlayer.correctPieceCount + "/16", game.skin);
		showOtherPlayerPC = new Label(" Pieces Completed: " + game.client.otherPlayer.correctPieceCount + "/16", game.skin);
		showGameTime = new Label(" Time: 10:10", game.skin, "subtitle");
				
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
			
		connectionLostDialog = new Dialog("Error", game.skin, "dialog") {
		    public void result(Object obj) {
		    	game.setScreen(new MainMenuScreen(game));
		    }};
		connectionLostDialog.text("Connection lost.");
		connectionLostDialog.button("Got it", false); //sends "false" as the result
		
		player2LeftDialog = new Dialog("Error", game.skin, "dialog") {
		    public void result(Object obj) {
		    	game.client.localPlayer.readyState = false;
		    	game.client.updatePlayer();
		    	game.setScreen(new WaitingScreen(game));
		    }};
		player2LeftDialog.text("Player2 has left.");
		player2LeftDialog.button("Got it", false); //sends "false" as the result
		
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
*                             start: game logic functionality
****************************************************************************************/	
	
		pieceList = new ArrayList<PuzzlePiece>(32);
		//generate all 32 puzzle pieces
		shapeRenderer=new ShapeRenderer();
		
		for(int id = 0; id < 2; id++) {
			int k = (id*16) + 1;
			
			for(int i = 500; i <= 800; i+=100) {
				
				for(int j = 1400; j<= 1700; j+=100, k++) {
					final PuzzlePiece temp = new PuzzlePiece(new Texture(Gdx.files.internal("testImage/test"+k+".png")), k, j , i, id);
					pieceList.add(temp);
			
					temp.setPosition(new Random().nextInt((300)+1)+700,new Random().nextInt((300)+1)+450);
					temp.setSize(100, 100);
					temp.addListener(new DragListener() {
						public void drag(InputEvent event, float x, float y, int pointer) {		
							//if(!temp.checkrightLocation()) {
								temp.moveBy(x - temp.getWidth()/2, y - temp.getHeight()/2);
							//}
						}
						
						public void dragStop(InputEvent event, float x, float y, int pointer) {
							System.out.println("temp: " + temp.getX() + "," + temp.getY());
							if(((temp.getX()+50) >= (temp.getPieceCorrectLocX()-50) && (temp.getX()+50) <( temp.getPieceCorrectLocX() + 50))
									&& ((temp.getY()+50) >= (temp.getPieceCorrectLocY()-50) && (temp.playerID == game.client.localPlayer.playerID) &&
							(temp.getY()+50) < (temp.getPieceCorrectLocY() + 50))){
								temp.setPosition(temp.getPieceCorrectLocX()-50 , temp.getPieceCorrectLocY()-50);
								temp.setrightLocation();
								game.client.localPlayer.correctPieceCount++;
								game.client.updatePlayer();
							}
							
							if((((temp.getX()+50) >= 350) && (temp.getX()+50)<550) 
									&& (((temp.getY()+50) >= 350) &&
										(temp.getY()+50) < 550)){
								temp.setrightLocation();
								game.client.sendPiece(temp.getPieceID());
								temp.setVisible(false);				
							}
						}
					});
				}
			}
		}
		
		for(int i = 0; i < 32; i++) {
			if(!game.client.localPlayer.playerPieceSet.contains(pieceList.get(i).pieceID)) {
				pieceList.get(i).setVisible(false);
			}
			stage.addActor(pieceList.get(i));
		}
			
/****************************************************************************************
*                             end: game logic functionality
****************************************************************************************/
			
/****************************************************************************************
*                             end: actors functionality
****************************************************************************************/
	
/****************************************************************************************
*                             start: actors layout
****************************************************************************************/
			

/****************************************************************************************
*                             start: bottom bar UI
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
		chatRoomCol1.add(chatTitle).width((Puzzungeon.WIDTH / 2) + 100).colspan(2).fillX().align(Align.left);
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
		chatRoomCol1.add(inputBox).fillX().padLeft(15).minWidth(Puzzungeon.WIDTH / 4 + 30);
		chatRoomCol1.add(sendButton).align(Align.left);
		chatRoom.add(chatRoomCol1);
		
		Table chatRoomCol2 = new Table();
		if (chatRoomBackground != null) {
			chatRoomCol2.setBackground(chatRoomBackground);
		} else {
			System.out.println("Drawable of name 'window' not found.");
		}
		chatRoomCol2.pad(0);
		chatRoomCol2.add(showLocalPlayerName).align(Align.left).width(370).pad(15);
		chatRoomCol2.row();
		chatRoomCol2.add(showLocalPlayerPC).align(Align.left).pad(15);
		chatRoomCol2.row();
		chatRoomCol2.add(showOtherPlayerName).align(Align.left).pad(15);
		chatRoomCol2.row();
		chatRoomCol2.add(showOtherPlayerPC).align(Align.left).pad(15);
		chatRoom.add(chatRoomCol2);
		
		Table chatRoomCol3 = new Table();
		Table timerCell = new Table().pad(0);
		Drawable timerBackground = game.skin.getDrawable("textfield");
		timerCell.add(showGameTime);
		if (timerBackground != null) {
			timerCell.setBackground(timerBackground);
		}
		chatRoomCol3.add(timerCell).width(450).padBottom(10);
		chatRoomCol3.row();
		chatRoomCol3.add(backButton).width(220).height(90);
		chatRoomCol3.row();
		chatRoomCol3.add(exitButton).width(220).height(90);
		chatRoom.add(chatRoomCol3);
		
/****************************************************************************************
*                             end: bottom bar UI
****************************************************************************************/

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
		viewport.apply();
		game.batch.begin();
		background.draw(game.batch);
		game.batch.end();
	
		shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
			shapeRenderer.begin(ShapeType.Line);
		
		for(int i=450; i<=750;i+=100)
		{
			for( int j=1350; j<=1650;j+=100)
			{
				shapeRenderer.rect(j,i,100,100);
			}
		}
		
		shapeRenderer.rect(700,450,400,400);
		shapeRenderer.rect(350,650,200,200);
		shapeRenderer.rect(350,350,200,200);

  /*
		shapeRenderer.begin(ShapeType.Line);
		
		for(int i=150; i<=450;i+=100)
		{
			for( int j=450; j<=750;j+=100)
			{
				shapeRenderer.rect(j,i,100,100);
			}
		}
    */

		shapeRenderer.end();
		
		//update and draw stage
		stage.getViewport().apply();
		stage.act(Gdx.graphics.getDeltaTime());
		update();
		stage.draw();
		
//		//draw background
//		viewport.apply();
//		game.batch.begin();
//		background.draw(game.batch);
//		game.batch.end();
//		
//		//draw stage
//		stage.getViewport().apply();
//		stage.act(Gdx.graphics.getDeltaTime());
//		update();
//		stage.draw();
		
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
		
		//update piececount display
		showLocalPlayerPC.setText(" Pieces Completed: " + game.client.localPlayer.correctPieceCount + "/16");
		showOtherPlayerPC.setText(" Pieces Completed: " + game.client.otherPlayer.correctPieceCount + "/16");
		
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
		
		//update elapsed time. should change this to be updated by the server.
		Long currentTime = (System.nanoTime()-startTime)/1000000000;
		showGameTime.setText("Time: " + Long.toString(currentTime));
		
		//if connection is lost
		if(!game.client.connectState & displayDialog) {
			System.out.println("MainGameScreen: connection lost.");
			connectionLostDialog.show(stage);
			displayDialog = false;
		}
		
		//if player2 has left
		else if((game.client.otherPlayer.playerID == -1) & displayDialog) {
			System.out.println("MainGameScreen: player2 has left.");
			player2LeftDialog.show(stage);
			displayDialog = false;
		}
		if (game.client.incomingPieceID!=-1){
			System.out.println("receiving a piece id = " + game.client.incomingPieceID);
			pieceList.get(game.client.incomingPieceID-1).setVisible(true);
			pieceList.get(game.client.incomingPieceID-1).setPosition(375, 700);
			pieceList.get(game.client.incomingPieceID-1).setSize(100, 100);
			game.client.incomingPieceID = -1;
		}
	}
}
