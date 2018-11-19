package project.puzzungeon.screens;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

import project.puzzungeon.Client;
import project.puzzungeon.PuzzlePiece;
import project.puzzungeon.Puzzungeon;
import project.server.ChatMessage;
import project.server.LoginRegister;
import project.server.Password;
import project.server.Username;

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
	Sprite teleporter;
	Drawable puzzleBacking;
	Drawable startBacking;
	
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
	
	private Label teleporterLabel1;
	private Label teleporterLabel2;
	
	private Dialog connectionLostDialog;
	private Dialog player2LeftDialog;
	private Boolean displayDialog;
	private Dialog guestFinishDialog;
	private Dialog registeredFinishDialog;
	private Dialog waitPlayer2forNextPuzzleDialog;
	private long startTime;
	ShapeRenderer shapeRenderer;
	ShapeRenderer greenGridRenderer;
	
	//puzzle pieces
	public int correctPieceCount;
	public int totalPieces;
	public ArrayList<PuzzlePiece> pieceList;
	private int[][] greenGrid;
	private int greenGridCounter;
	
	private ArrayList<Sprite> puzzleSprites;
	public int puzzleID = 1;

	public Boolean update;
	
	//puzzle variables
	int numTilesHorizontal = 4;
	int numTilesVertical = 4;
	int sideLength = 130;
	
	//deposit/recieve square variables
	int sendX = 400;
	int sendY = 650;
	int sendLength = 200;
	int recieveX = 100;
	int recieveY = 650;
	int recieveLength = 200;
	
	//grid variables
	int gridY = 500;
	int gridX = 1300;
	int gridLengthX = sideLength * numTilesHorizontal;
	int gridLengthY = sideLength * numTilesVertical;
	
	//starting area variables
	int startX = 685;
	int startY = 500;
  
	public Boolean gameFinished; 
	
	//constructor
	public MainGameScreen(Puzzungeon game) {
		this.game = game;
		viewport = new FitViewport(Puzzungeon.WIDTH, Puzzungeon.HEIGHT);
		stage = new Stage(viewport);
		displayDialog = true;
		startTime = System.nanoTime();
		Gdx.input.setInputProcessor(stage);
		

		atlas = game.assetLoader.manager.get("sprites.txt");
		teleporter = atlas.createSprite("teleporter");
		teleporter.setPosition(250, 420);
		teleporter.setScale(9f);
		background = atlas.createSprite("dungeon-wall");
		background.setOrigin(0, 0);
		background.setScale(9f);
		puzzleBacking = game.skin.getDrawable("window");
		startBacking = game.skin.getDrawable("window");
		
		greenGridRenderer = new ShapeRenderer();
		totalPieces = 16;
		update = true;

		gameFinished = false;

		greenGrid = new int[16][3];
		greenGridCounter = 0;

	}

	@Override
	public void show() {
		
/****************************************************************************************
*                             start: actors functionality
****************************************************************************************/
				
		//create the actors
		Label chatTitle = new Label("Chat",game.skin);
		
		teleporterLabel1 = new Label("Send/Recieve", game.skin, "subtitle");
		teleporterLabel2 = new Label("Pieces Here", game.skin, "subtitle");
		teleporterLabel1.setAlignment(Align.center);
		teleporterLabel2.setAlignment(Align.center);
		
		showMessage1 = new Label("",game.skin);
		showMessage2 = new Label("",game.skin);
		showMessage3 = new Label("",game.skin);
		showMessage4 = new Label("",game.skin);
		
		showLocalPlayerName = new Label("Player1: " + game.client.localPlayer.playerName, game.skin);
		showOtherPlayerName = new Label("Player2: " + game.client.otherPlayer.playerName, game.skin);
		
		showLocalPlayerPC = new Label(" Pieces Completed: " + game.client.localPlayer.correctPieceCount + "/16", game.skin);
		showOtherPlayerPC = new Label(" Pieces Completed: " + game.client.otherPlayer.correctPieceCount + "/16", game.skin);
		showGameTime = new Label(" Time: 0", game.skin, "subtitle");
				
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
			
		connectionLostDialog = new Dialog("", game.skin, "dialog") {
		    public void result(Object obj) {
		    	game.setScreen(new MainMenuScreen(game));
		    }};
		connectionLostDialog.text("Connection lost.");
		connectionLostDialog.button("Got it", false); //sends "false" as the result
		
		player2LeftDialog = new Dialog("", game.skin, "dialog") {
		    public void result(Object obj) {
		    	//game.client.localPlayer.readyState = false;
		    	//game.client.updatePlayer();
		    	game.setScreen(new GameLobbyScreen(game));
		    	//game.setScreen(new WaitingScreen(game));
		    }};
		player2LeftDialog.text("Player2 has left.");
		player2LeftDialog.button("Got it", false); //sends "false" as the result
		
		
		guestFinishDialog = new Dialog("", game.skin, "dialog") {
		    public void result(Object obj) {
		    	backToLobby();
		    	game.setScreen(new GameLobbyScreen(game));
		    }};
		guestFinishDialog.text("You finished the puzzle!\nGuest can't play the next puzzle.");
		guestFinishDialog.button("Got it", false); //sends "false" as the result
		
		registeredFinishDialog = new Dialog("", game.skin, "dialog") {
		    public void result(Object obj) {
		    	
		    	/*
		    	Boolean result = (Boolean)obj;
		    	
		    	if(!result) {
		    		backToLobby();
			    	game.setScreen(new GameLobbyScreen(game));
		    	}
		    	else {
		    		waitPlayer2forNextPuzzleDialog.show(stage);
		    	}
		    	*/
		    }};
		registeredFinishDialog.text("You finished the puzzle!\n Do you want to play the next puzzle?");
		registeredFinishDialog.button("Yes", true); 
		registeredFinishDialog.button("No", false);
		
		waitPlayer2forNextPuzzleDialog = new Dialog("", game.skin, "dialog") {
		    public void result(Object obj) {
		    }};
		waitPlayer2forNextPuzzleDialog.text("Waiting for Player2...");
		    
		
		TextButton backButton = new TextButton("Back", game.skin, "default");
			backButton.addListener(new ClickListener(){
				@Override 
				public void clicked(InputEvent event, float x, float y){
					backToLobby();
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
*                             start: game logic functionality
****************************************************************************************/	
			
		//make puzzle from TextureRegion
		TextureRegion puzzle;
		if (puzzleID == 1) {
			puzzle = atlas.findRegion("dragon");
		} else {
			puzzle = atlas.findRegion("castle-small");
		}
		int imageWidth = puzzle.getRegionWidth();
	    int imageHeight = puzzle.getRegionHeight();
	    int pieceWidth = imageWidth / (numTilesHorizontal * 2);
	    int pieceHeight = imageHeight / numTilesVertical;
		TextureRegion[][] pieceRegions = puzzle.split(pieceWidth, pieceHeight);
		
		pieceList = new ArrayList<PuzzlePiece>(32);
		//generate all 32 puzzle pieces
		shapeRenderer=new ShapeRenderer();
		
		int gridEndX = gridY + (sideLength * (numTilesHorizontal - 1)) + (sideLength / 2);
		int gridEndY = gridX + (sideLength * (numTilesVertical - 1)) + (sideLength / 2);
		int half = sideLength / 2;
		for(int id = 0; id < 2; id++) {
			int k = (id*16) + 1;
			int regionX = id * 4;
			
			for(int i = gridY + half, y = 3; (i <= (gridEndX + half)) && y >= 0; i+=sideLength, y--) {
				regionX = id * 4;
				for(int j = gridX + half; j<= (gridEndY); j+=sideLength, k++, regionX++) {
					final PuzzlePiece temp = new PuzzlePiece(pieceRegions[y][regionX], k, j , i, id);
					pieceList.add(temp);
			
					temp.setPosition(new Random().nextInt((300)+1)+startX,new Random().nextInt((300)+1)+startY);
					temp.setSize(sideLength, sideLength);
					temp.addListener(new DragListener() {
						public void drag(InputEvent event, float x, float y, int pointer) {
							float distanceToMinY = temp.getY() - 300;
							float distanceToMinX = temp.getX() - 0;
							
							if(!temp.checkrightLocation()) {
								if(distanceToMinY > 0) {
									temp.moveBy(x - temp.getWidth()/2, y - temp.getHeight()/2);	
								}
								else if(distanceToMinY < 0){
									float Ymove = y - temp.getHeight()/2;
									
									if(Ymove < 0){
										temp.moveBy(x - temp.getWidth()/2, 0);
									}
									else {
										temp.moveBy(x - temp.getWidth()/2, Ymove);
									}
								}
							}
						}
						
						int half = sideLength / 2;
						public void dragStop(InputEvent event, float x, float y, int pointer) {
							if(((temp.getX()+half) >= (temp.getPieceCorrectLocX()-half) && (temp.getX()+half) <( temp.getPieceCorrectLocX() + half))
									&& ((temp.getY()+half) >= (temp.getPieceCorrectLocY()-half) && (temp.playerID == game.client.localPlayer.playerID) &&
							(temp.getY()+half) < (temp.getPieceCorrectLocY() + half))){
								temp.setPosition(temp.getPieceCorrectLocX()-half, temp.getPieceCorrectLocY()-half);
								
								if(!temp.checkrightLocation()) {
									game.client.localPlayer.correctPieceCount++;
									if (greenGridCounter != 16) {
										greenGrid[greenGridCounter][0] = temp.getPieceCorrectLocX()- half;
										greenGrid[greenGridCounter][1] = temp.getPieceCorrectLocY() - half;
										greenGrid[greenGridCounter][2] = sideLength;
										greenGridCounter++;
									}
								}
								temp.setrightLocation();
								game.client.updatePlayer();
							}
							
							if((((temp.getX()+50) >= sendX) && (temp.getX()+50)<sendX + sendLength) 
									&& (((temp.getY()+50) >= sendY) &&
										(temp.getY()+50) < sendY + sendLength)){
								//temp.setrightLocation();
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
*                             start: game instructions UI
****************************************************************************************/	

		Table teleportLabel = new Table().left().top();
		teleportLabel.setFillParent(true);
		teleportLabel.add(teleporterLabel1).padLeft(130).padTop(120).width(450).align(Align.center);
		teleportLabel.row();
		teleportLabel.add(teleporterLabel2).padLeft(130).width(450).align(Align.center);
		
/****************************************************************************************
*                             end: game instructions UI
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
		chatRoom.add(chatRoomCol2).fillY();
		
		Table chatRoomCol3 = new Table();
		Table timerCell = new Table().pad(0);
		Drawable timerBackground = game.skin.getDrawable("textfield");
		timerCell.add(showGameTime);
		if (timerBackground != null) {
			timerCell.setBackground(timerBackground);
		}
		chatRoomCol3.add(timerCell).width(450).padBottom(10).minHeight(90);
		chatRoomCol3.row();
		chatRoomCol3.add(backButton).width(220).minHeight(90);
		chatRoomCol3.row();
		chatRoomCol3.add(exitButton).width(220).minHeight(90);
		chatRoom.add(chatRoomCol3).fillY();
		
/****************************************************************************************
*                             end: bottom bar UI
****************************************************************************************/

		stage.addActor(teleportLabel);
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
		
		//draw background + puzzle backing
		
		viewport.apply();
		game.batch.begin();
		background.draw(game.batch);
		teleporter.draw(game.batch);
		puzzleBacking.draw(game.batch, gridX - 350, startY - 160, gridLengthX - 80, gridLengthY - 100);
		startBacking.draw(game.batch, startX - 200, startY - 160, gridLengthX - 80, gridLengthY - 100);
		game.batch.end();
		
		//draw white grid
		shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(game.skin.getColor("white"));
			
		int gridEndX = gridY + (sideLength * numTilesHorizontal);
		int gridEndY = gridX + (sideLength * numTilesVertical);
		for(int i=gridY; i< gridEndX;i+=sideLength) {
			for( int j=gridX; j<gridEndY;j+=sideLength) {
				shapeRenderer.rect(j,i,sideLength,sideLength);
			}
		}
			
		//deposit shapes
		shapeRenderer.rect(startX,startY,gridLengthX,gridLengthY);
		shapeRenderer.setColor(game.skin.getColor("Red"));
		shapeRenderer.rect(recieveX,recieveY,recieveLength,recieveLength);
		shapeRenderer.setColor(game.skin.getColor("Teal"));
		shapeRenderer.rect(sendX,sendY,sendLength,sendLength);
		
		shapeRenderer.end();
		
		//update and draw stage
		stage.getViewport().apply();
		stage.act(Gdx.graphics.getDeltaTime());
		update();
		stage.draw();

		//draw green grid
		greenGridRenderer.setProjectionMatrix(stage.getCamera().combined);
		greenGridRenderer.begin(ShapeType.Line);
		greenGridRenderer.setColor(game.skin.getColor("Teal"));
		for (int i = 0; i < greenGrid.length; i++) {
			greenGridRenderer.rect(greenGrid[i][0], greenGrid[i][1], greenGrid[i][2], greenGrid[i][2]);
		}
		greenGridRenderer.end();
	
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
		if(update) {
					
			//update piececount display
			showLocalPlayerPC.setText(" Pieces Completed: " + game.client.localPlayer.correctPieceCount + "/16");
			showOtherPlayerPC.setText(" Pieces Completed: " + game.client.otherPlayer.correctPieceCount + "/16");
			
			
			
			//if local player finishes the puzzle
			//if(game.client.localPlayer.correctPieceCount == 16 && !game.client.localPlayer.isFinished) {
			
			/*
			if(!game.client.localPlayer.isFinished) {
				game.client.localPlayer.isFinished = true;
				game.client.updatePlayer();
				//ChatMessage cm = new ChatMessage(game.client.clientUsername, "has finished half of puzzle!", true);
                //game.client.sendMessage(cm);
			}
			
			*/
			/*
			//if both player finishes the puzzle
			if(game.client.localPlayer.isFinished && game.client.otherPlayer.isFinished && !gameFinished) {
				System.out.println("the game is finished");
				gameFinished = true;
				game.client.messageVec.remove(0);
				game.client.messageVec.add(new ChatMessage("The puzzle is finished", "", true));
				
				
				//if one of the player is a guest
				if((game.client.localPlayer.playerName.equals("Guest"))||(game.client.otherPlayer.playerName.equals("Guest"))) {
					backToLobby();
					System.out.println("at least one Guest");
					guestFinishDialog.show(stage);
					displayDialog = false;
					update = false;
					return;
				}
				
			
				
				//if both players are registered user
				else {
					System.out.println("both registered user");
					registeredFinishDialog.show(stage);
					displayDialog = true;
				}
				
			}
			*/
			/*
			
			if(game.client.localPlayer.playNextPuzzle && game.client.otherPlayer.playNextPuzzle) {
				System.out.println("Play the next puzzle!");
			}
			
			*/
			
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
			if(!gameFinished) {
				showGameTime.setText("Time: " + Long.toString(currentTime));
			}
			
			//if connection is lost
			if(!game.client.connectState && displayDialog) {
				update = false;
				System.out.println("MainGameScreen: connection lost.");
				connectionLostDialog.show(stage);
				displayDialog = false;
			}
			
			//if player2 has left
			else if((game.client.otherPlayer.playerID == -1) & displayDialog) {
				System.out.println("MainGameScreen: player2 has left.");
				
				player2LeftDialog.show(stage);
				displayDialog = false;
				
				backToLobby();
			}
			if (game.client.incomingPieceID!=-1){
				System.out.println("receiving a piece id = " + game.client.incomingPieceID);
				pieceList.get(game.client.incomingPieceID-1).setVisible(true);
				int range = recieveLength / 2;
				pieceList.get(game.client.incomingPieceID-1).setPosition(new Random().nextInt((range)+1)+recieveX,new Random().nextInt((range)+1)+recieveY);
				pieceList.get(game.client.incomingPieceID-1).setSize(sideLength, sideLength);
				game.client.incomingPieceID = -1;
			}
		}
	}
	
	public void backToLobby() {
		update = false;
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
	}
}
