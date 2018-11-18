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
	
	public ArrayList<PuzzlePiece> pieces;
	

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

		correctPieceCount = 0;
		totalPieces = 4;
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
	/*		
			pieces = new ArrayList<PuzzlePiece>();
			
			for(int i = 0; i < 4; i++) {
				pieces.add(new PuzzlePiece(new Texture(Gdx.files.internal("image/pup"+(i+1)+".jpg")), i));
			}
			
			
			dragAndDrop = new DragAndDrop();
			
			final ArrayList<Table> sourceTables = new ArrayList<Table>();
			final Table sourceTable = new Table().right();
			sourceTable.setFillParent(true);
			
			for(int i = 0; i < 4; i++) {
				final Table sourceTableNew = new Table();
				sourceTableNew.add(pieces.get(i)).width(80).height(80);
				sourceTables.add(sourceTableNew);
				
				dragAndDrop.addSource(new Source(sourceTableNew) {
					public Payload dragStart (InputEvent event, float x, float y, int pointer) {
						Payload payload = new Payload();
						payload.setObject(sourceTableNew.getCells().get(0).getActor());
						payload.setDragActor(sourceTableNew.getCells().get(0).getActor());
						return payload;
					}
					
					public void dragStop(InputEvent event,
		                     float x,
		                     float y,
		                     int pointer,
		                     DragAndDrop.Payload payload,
		                     DragAndDrop.Target target) {
						
						if(target == null) {
							System.out.println("lose piece");
							sourceTableNew.clearChildren();
							sourceTableNew.add((PuzzlePiece)payload.getObject()).width(80).height(80);
							
						}
					}
					
				});
		
			}
			
			for(Table t : sourceTables) {
				sourceTable.add(t);
				sourceTable.row();
			}
				
			final ArrayList<Table> targetTables = new ArrayList<Table>();
			final Table targetTable = new Table();
			targetTable.setFillParent(true);
			
			for(int i = 0; i < 4; i++) {
				//final Table
				final int index = i;
				final Table targetTableNew = new Table();
				targetTableNew.add(new PuzzlePiece(new Texture(Gdx.files.internal("empty.png")), -1, )).width(80).height(80);
				targetTables.add(targetTableNew);
				dragAndDrop.addTarget(new Target(targetTableNew) {
					public boolean drag (Source source, Payload payload, float x, float y, int pointer) {
						return true;
					}

					public void drop (Source source, Payload payload, float x, float y, int pointer) {
						System.out.println("Accepted: " + payload.getObject() + " " + x + ", " + y);
						
						((Table)source.getActor()).clearChildren();
						((Table)source.getActor()).add(((Table)getActor()).getCells().get(0).getActor()).width(80).height(80);
						
						
						((Table) getActor()).clearChildren();
						//System.out.println("PuzzlePiece " + Integer.toString(((PuzzlePiece)payload.getObject()).getPieceID()) + " dropped on table1");
						((Table) getActor()).add((PuzzlePiece)payload.getObject()).width(80).height(80);
						
						//check if every piece is in the correct location
						for(int i = 0; i < targetTables.size(); i++) {
							
							System.out.println("checking piece:" + i);
							//get pieceIn every table
							int pieceID = ((PuzzlePiece)(targetTables.get(i).getCells().get(0).getActor())).getPieceID();
							
							if(i == pieceID) {
								//dragAndDrop.removeTarget( (Target)(targetTables.get(i)));
								correctPieceCount++;
								if(correctPieceCount == totalPieces) {
									System.out.println("All pieces are in correct location");
								
								}
							}
						}
						
						
						
						int pieceID = ((PuzzlePiece)payload.getObject()).getPieceID();
						int targetCellID = index;
						System.out.println("piece " + pieceID + " dropped in cell " + targetCellID);
						
						if(pieceID == targetCellID) {
							dragAndDrop.removeTarget(this);
							correctPieceCount++;
							if(correctPieceCount == totalPieces) {
								System.out.println("All pieces are in correct location");
							
							}
						}
						else {
							dragAndDrop.addSource(new Source(targetTables.get(index)) {
								public Payload dragStart (InputEvent event, float x, float y, int pointer) {
									Payload payload = new Payload();
									payload.setObject(targetTables.get(index).getCells().get(0).getActor());
									payload.setDragActor(targetTables.get(index).getCells().get(0).getActor());
									targetTables.get(index).clearChildren();
									targetTables.get(index).add(new PuzzlePiece(new Texture(Gdx.files.internal("empty.png")),-1)).width(80).height(80);
									
									//dragAndDrop.removeSource(this);
									return payload;
								}
								
								public void dragStop(InputEvent event,
					                     float x,
					                     float y,
					                     int pointer,
					                     DragAndDrop.Payload payload,
					                     DragAndDrop.Target target) {
									
									if(target == null) {
										System.out.println("lose piece");
										targetTableNew.clearChildren();
										targetTableNew.add((PuzzlePiece)payload.getObject()).width(80).height(80);
										
									}
								}
							});
						}
					}
				});
			}


			
			for(int i = 0; i < targetTables.size();i=i+2) {
				targetTable.add(targetTables.get(i));
				targetTable.add(targetTables.get(i+1));
				targetTable.row();
				
			}
			
			stage.addActor(sourceTable);
			stage.addActor(targetTable);
			
			
/****************************************************************************************
*                             end: game logic functionality
****************************************************************************************/
			
/****************************************************************************************
*                             end: actors functionality
****************************************************************************************/
	
/****************************************************************************************
*                             start: actors layout
****************************************************************************************/
			shapeRenderer=new ShapeRenderer();
			
			final ArrayList<PuzzlePiece> listOfPieces = new ArrayList<PuzzlePiece>();
			
			int k = (16*game.client.localPlayer.playerID)+1;
			System.out.println(1);
			for(int i = 500; i <= 800; i+=100) {
				
				for(int j = 1400; j<= 1700; j+=100) {
				
				//final PuzzlePiece temp = new PuzzlePiece(new Texture(Gdx.files.internal("image/pup" + i + ".jpg")), 1, 500, 600);
				final PuzzlePiece temp = new PuzzlePiece(new Texture(Gdx.files.internal("testImage/test"+k+".png")), k,j, i, true);
				k++;
				
				temp.setPosition(new Random().nextInt((300)+1)+700,new Random().nextInt((300)+1)+450);
				temp.setSize(100, 100);
				 
				temp.addListener(new DragListener() {
					public void drag(InputEvent event, float x, float y, int pointer) {
						
						if(!temp.checkrightLocation()) {
						temp.moveBy(x - temp.getWidth()/2, y - temp.getHeight()/2);
						}
  /*
			shapeRenderer = new ShapeRenderer();
			
			//final ArrayList<PuzzlePiece> listOfPieces = new ArrayList<PuzzlePiece>();
			
			final Set<PuzzlePiece> setOfPieces = new HashSet<PuzzlePiece>();
			
			int k = 0;
			System.out.println(k);
			for(int i = 200; i <= 500; i+=100) {
				
				for(int j = 500; j<= 800; j+=100) {
				
				//final PuzzlePiece temp = new PuzzlePiece(new Texture(Gdx.files.internal("image/pup" + i + ".jpg")), 1, 500, 600);
				final PuzzlePiece temp = new PuzzlePiece(new Texture(Gdx.files.internal("image/pup1.jpg")), k,j, i);
				k++;
				System.out.println(k);
				temp.setPosition(300, 300);
				temp.setSize(100, 100);
			
				temp.addListener(new DragListener() {
					public void drag(InputEvent event, float x, float y, int pointer) {
						if(!temp.checkRightLocation()) 
						{
								temp.moveBy(x - temp.getWidth()/2, y - temp.getHeight()/2);
					}
          */
					}
					
					public void dragStop(InputEvent event, float x, float y, int pointer) {
						System.out.println("temp: " + temp.getX() + "," + temp.getY());
						if(((temp.getX()+50) >= (temp.getPieceCorrectLocX()-50) && (temp.getX()+50) <( temp.getPieceCorrectLocX() + 50))
								&& ((temp.getY()+50) >= (temp.getPieceCorrectLocY()-50) &&
						(temp.getY()+50) < (temp.getPieceCorrectLocY() + 50)))
						{
							temp.setPosition(temp.getPieceCorrectLocX()-50 , temp.getPieceCorrectLocY()-50);
							temp.setrightLocation();
						}
						
						if((((temp.getX()+50) >= 350) && (temp.getX()+50)<550)
								&& (((temp.getY()+50) >= 350) &&
									(temp.getY()+50) < 550))
						{
							//temp.setPosition(temp.getPieceCorrectLocX()-50 , temp.getPieceCorrectLocY()-50);
							temp.setrightLocation();
							game.client.sendPiece(temp.getPieceID());
							temp.remove();
;							
						}
					}
				});
				listOfPieces.add(temp);

  /*
						if(((temp.getX() >= (temp.getPieceCorrectLocX()-50)) && temp.getX() <( temp.getPieceCorrectLocX() + 50))
								&& ((temp.getY() >= (temp.getPieceCorrectLocY()-50)) &&
						temp.getY() < (temp.getPieceCorrectLocY() + 50)))
						{
							temp.setPosition((temp.getPieceCorrectLocX())-50, temp.getPieceCorrectLocY()-35);
							temp.setRightLocation();
						}
					}
				});
				
				setOfPieces.add(temp);
        */

			
//				final PuzzlePiece test1 = new PuzzlePiece(new Texture(Gdx.files.internal("image/pup1.jpg")), 1, 500, 600);
//				test1.setPosition(300, 300);
//				test1.setSize(100, 100);
//				test1.addListener(new DragListener() {
//				    	public void drag(InputEvent event, float x, float y, int pointer) {
//				    		test1.moveBy(x - test1.getWidth() / 2, y - test1.getHeight() / 2);
//				    	
//				    	}
//				    
//				    	public void dragStop(InputEvent event, float x, float y, int pointer) {
//				    		if(x == test1.getPieceX() && y == test1.getPieceY()) {
//				    			count1++;
//				    			//System.out.println("count1: " + count1);
//				    		}
//				    		else {
//				    			count1--;
//				    			//System.out.println("count1: " + count1);
//				    		}
//				    		System.out.println("test1: " + 	test1.getX() +"," + test1.getY() );
//				    	}
//					});
//			
//			;
//				stage.addActor(test2);
				stage.addActor(temp);
		}
	}
		
/****************************************************************************************
*                             start: topbar UI
****************************************************************************************/
		
		Table topbar = new Table().top().left();
		topbar.setFillParent(true);
		topbar.pad(0);
		topbar.add(gameTitle);
		topbar.row();
		topbar.add(showLocalPlayerName);
		topbar.add(showLocalPlayerPC);
		topbar.row();
		topbar.add(showOtherPlayerName);
		topbar.add(showOtherPlayerPC);
		topbar.row();
		topbar.add(showGameTime);
	
/****************************************************************************************
*                             end: topbar UI
****************************************************************************************/
		
/****************************************************************************************
*                             start: chatroom UI
****************************************************************************************/	
		
		//set colors of the labels
		chatTitle.setColor(Color.GREEN);
		
		Table chatRoom = new Table().bottom().left();
		chatRoom.pad(0);
		chatRoom.add(chatTitle).width(Puzzungeon.WIDTH).colspan(3);
		chatRoom.row();
		chatRoom.add(showMessage4).width(Puzzungeon.WIDTH).colspan(3);
		chatRoom.row();
		chatRoom.add(showMessage3).width(Puzzungeon.WIDTH).colspan(3);
		chatRoom.row();
		chatRoom.add(showMessage2).width(Puzzungeon.WIDTH).colspan(3);
		chatRoom.row();
		chatRoom.add(showMessage1).width(Puzzungeon.WIDTH).colspan(3);
		chatRoom.row();
		
		chatRoom.add(inputBox).width(Puzzungeon.WIDTH*0.7f);
		chatRoom.add(sendButton).width(Puzzungeon.WIDTH*0.3f).colspan(2);
		chatRoom.row();
		
		chatRoom.add(new Label("",game.skin)).width(Puzzungeon.WIDTH*0.7f);
		chatRoom.add(backButton).width(Puzzungeon.WIDTH*0.15f).pad(0);
		chatRoom.add(exitButton).width(Puzzungeon.WIDTH*0.15f).pad(0);
		
/****************************************************************************************
*                             end: chatroom UI
****************************************************************************************/

		stage.addActor(topbar);
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
		if (game.client.incomingPieceID!=-1)
		{
			System.out.println("incoming piece id: " + game.client.incomingPieceID);
			//stage.addActor(actor);
			
			final PuzzlePiece temp = new PuzzlePiece(new Texture(Gdx.files.internal("image/pup1.jpg")), game.client.incomingPieceID,0, 0, true);
			//k++;
			
			temp.setPosition(375, 700);
			temp.setSize(100, 100);
			 
			temp.addListener(new DragListener() {
				public void drag(InputEvent event, float x, float y, int pointer) {
					
					if(!temp.checkrightLocation()) {
					temp.moveBy(x - temp.getWidth()/2, y - temp.getHeight()/2);
					}
				}
				
				public void dragStop(InputEvent event, float x, float y, int pointer) {
					System.out.println("temp: " + temp.getX() + "," + temp.getY());
					if(((temp.getX()+50) >= (temp.getPieceCorrectLocX()-50) && (temp.getX()+50) <( temp.getPieceCorrectLocX() + 50))
							&& ((temp.getY()+50) >= (temp.getPieceCorrectLocY()-50) &&
					(temp.getY()+50) < (temp.getPieceCorrectLocY() + 50)))
					{
						temp.setPosition(temp.getPieceCorrectLocX()-50 , temp.getPieceCorrectLocY()-50);
						temp.setrightLocation();
					}
					
					if((((temp.getX()+50) >= 350) && (temp.getX()+50)<550)
							&& (((temp.getY()+50) >= 350) &&
								(temp.getY()+50) < 550))
					{
						//temp.setPosition(temp.getPieceCorrectLocX()-50 , temp.getPieceCorrectLocY()-50);
						temp.setrightLocation();
						game.client.sendPiece(temp.getPieceID());
						temp.remove();
;							
					}
				}
			});
			stage.addActor(temp);
			game.client.incomingPieceID = -1;
		}
		
	}
}
