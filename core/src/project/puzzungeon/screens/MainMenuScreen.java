package project.puzzungeon.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import project.puzzungeon.Client;
import project.puzzungeon.PuzzlePiece;
import project.puzzungeon.Puzzungeon;
import project.server.LoginRegister;
import project.server.Password;
import project.server.Username;

//First screen; 

public class MainMenuScreen implements Screen{

	Puzzungeon game; //reference to the game
	private Stage stage;
	private Table table;
	
	//shared by different methods
	private Boolean displayDialog;
	private Dialog gameFullDialog;
	private Dialog connectionFailDialog;
	
	
	public PuzzlePiece piece1;
	public PuzzlePiece piece2;
	public PuzzlePiece piece3;
	public PuzzlePiece piece4;
	
	public ArrayList<PuzzlePiece> pieces;
	

	public DragAndDrop dragAndDrop;
	
	//constructor
	public MainMenuScreen(Puzzungeon game) {
		this.game = game;
		stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);
		displayDialog = false;
	}
	
	//construct stage
	@Override
	public void show() {
		
/****************************************************************************************
*                             start: actors functionality
****************************************************************************************/
		
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
			sourceTableNew.add(pieces.get(i)).width(100).height(100);
			sourceTables.add(sourceTableNew);
			dragAndDrop.addSource(new Source(sourceTableNew) {
				public Payload dragStart (InputEvent event, float x, float y, int pointer) {
					Payload payload = new Payload();
					payload.setObject(sourceTableNew.getCells().get(0).getActor());
					payload.setDragActor(sourceTableNew.getCells().get(0).getActor());
					//sourceTables.get(i).clearChildren();
					//sourceTables.get(i).add(new PuzzlePiece(new Texture(Gdx.files.internal("empty.png")),-1)).width(100).height(100);
					
					dragAndDrop.removeSource(this);
					return payload;
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
			targetTableNew.add(new PuzzlePiece(new Texture(Gdx.files.internal("empty.png")), -1)).width(100).height(100);
			targetTables.add(targetTableNew);
			dragAndDrop.addTarget(new Target(targetTableNew) {
				public boolean drag (Source source, Payload payload, float x, float y, int pointer) {
					return true;
				}

				public void drop (Source source, Payload payload, float x, float y, int pointer) {
					System.out.println("Accepted: " + payload.getObject() + " " + x + ", " + y);
					((Table) getActor()).clearChildren();
					//System.out.println("PuzzlePiece " + Integer.toString(((PuzzlePiece)payload.getObject()).getPieceID()) + " dropped on table1");
					((Table) getActor()).add((PuzzlePiece)payload.getObject()).width(100).height(100);
					
					int pieceID = ((PuzzlePiece)payload.getObject()).getPieceID();
					int targetCellID = index;
					System.out.println("piece " + pieceID + " dropped in cell " + targetCellID);
					
					if(pieceID == targetCellID) {
						dragAndDrop.removeTarget(this);
					}
					else {
						dragAndDrop.addSource(new Source(targetTables.get(index)) {
							public Payload dragStart (InputEvent event, float x, float y, int pointer) {
								Payload payload = new Payload();
								payload.setObject(targetTables.get(index).getCells().get(0).getActor());
								payload.setDragActor(targetTables.get(index).getCells().get(0).getActor());
								targetTables.get(index).clearChildren();
								targetTables.get(index).add(new PuzzlePiece(new Texture(Gdx.files.internal("empty.png")),-1)).width(100).height(100);
								
								dragAndDrop.removeSource(this);
								return payload;
							}
							
						});
					}
					
					
					
					
					/*
					dragAndDrop.addSource(new Source(table1) {
						public Payload dragStart (InputEvent event, float x, float y, int pointer) {
							
							System.out.println("dragging from table1");
							Payload payload = new Payload();
							
							payload.setObject(table1.getCells().get(0).getActor());

							payload.setDragActor(table1.getCells().get(0).getActor());
							table1.clearChildren();
							table1.add(new PuzzlePiece(new Texture(Gdx.files.internal("empty.png")),-1)).width(100).height(100);
							
							dragAndDrop.removeSource(this);
							return payload;
						}
					});
					*/
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
		
		
		
		
	
		
		

		
			
			
			
			/*
			public void dragStop(InputEvent event,
                    float x,
                    float y,
                    int pointer,
                    DragAndDrop.Payload payload,
                    DragAndDrop.Target target) {
				
				if(target == null) {
					System.out.println("drag from table1 failed.");
					table1.clearChildren();
					table1.add(((PuzzlePiece)payload.getObject())).width(100).height(100);
					dragAndDrop.addSource(this);
				}
			}
			*/
			
			
		
		/*
		
		dragAndDrop.addSource(new Source(table01) {
			public Payload dragStart (InputEvent event, float x, float y, int pointer) {
				
				System.out.println("dragging from table01");
				Payload payload = new Payload();
				
				payload.setObject(table01.getCells().get(0).getActor());

				payload.setDragActor(table01.getCells().get(0).getActor());
				table01.clearChildren();
				table01.add(new PuzzlePiece(new Texture(Gdx.files.internal("empty.png")),-1)).width(100).height(100);
				
				dragAndDrop.removeSource(this);
				return payload;
			}
		});
		
		
		dragAndDrop.addSource(new Source(table3) {
			public Payload dragStart (InputEvent event, float x, float y, int pointer) {
				
				System.out.println("dragging from table2");
				Payload payload = new Payload();
				
				payload.setObject(table2.getCells().get(0).getActor());

				payload.setDragActor(table2.getCells().get(0).getActor());
				table3.clearChildren();
				table3.add(new PuzzlePiece(new Texture(Gdx.files.internal("black.png")),-1)).width(100).height(100);
				
				dragAndDrop.removeSource(this);
				return payload;
			}
		});
		
		
		
		dragAndDrop.addTarget(new Target(table1) {
			public boolean drag (Source source, Payload payload, float x, float y, int pointer) {
				return true;
			}

			public void drop (Source source, Payload payload, float x, float y, int pointer) {
				System.out.println("table1 Accepted: " + payload.getObject() + " " + x + ", " + y);
				((Table) getActor()).clearChildren();
				System.out.println("PuzzlePiece " + Integer.toString(((PuzzlePiece)payload.getObject()).getPieceID()) + " dropped on table1");
				((Table) getActor()).add((PuzzlePiece)payload.getObject()).width(100).height(100);
				dragAndDrop.removeTarget(this);
				
				dragAndDrop.addSource(new Source(table1) {
					public Payload dragStart (InputEvent event, float x, float y, int pointer) {
						
						System.out.println("dragging from table1");
						Payload payload = new Payload();
						
						payload.setObject(table1.getCells().get(0).getActor());

						payload.setDragActor(table1.getCells().get(0).getActor());
						table1.clearChildren();
						table1.add(new PuzzlePiece(new Texture(Gdx.files.internal("empty.png")),-1)).width(100).height(100);
						
						dragAndDrop.removeSource(this);
						return payload;
					}
				});
			}
		});
		
		
		dragAndDrop.addTarget(new Target(table2) {
			public boolean drag (Source source, Payload payload, float x, float y, int pointer) {
				return true;
			}
			
			public void drop (Source source, Payload payload, float x, float y, int pointer) {
				System.out.println("table2 Accepted: " + payload.getObject() + " " + x + ", " + y);
				((Table) getActor()).clearChildren();
				System.out.println("PuzzlePiece " + Integer.toString(((PuzzlePiece)payload.getObject()).getPieceID()) + " dropped on table2");
				((Table) getActor()).add((PuzzlePiece)payload.getObject()).width(100).height(100);
				dragAndDrop.removeTarget(this);
				
				dragAndDrop.addSource(new Source(table2) {
					public Payload dragStart (InputEvent event, float x, float y, int pointer) {
						
						System.out.println("dragging from table2");
						Payload payload = new Payload();
						
						payload.setObject(table2.getCells().get(0).getActor());

						payload.setDragActor(table2.getCells().get(0).getActor());
						table2.clearChildren();
						table2.add(new PuzzlePiece(new Texture(Gdx.files.internal("empty.png")),-1)).width(100).height(100);
						
						dragAndDrop.removeSource(this);
						return payload;
					}
				});
			}
		});
		
		
		dragAndDrop.addTarget(new Target(table3) {
			public boolean drag (Source source, Payload payload, float x, float y, int pointer) {
				getActor().setColor(Color.GREEN);
				return true;
			}

			public void drop (Source source, Payload payload, float x, float y, int pointer) {
				System.out.println("table3 Accepted: " + payload.getObject() + " " + x + ", " + y);
				((Table) getActor()).clearChildren();
				System.out.println("PuzzlePiece " + Integer.toString(((PuzzlePiece)payload.getObject()).getPieceID()) + " dropped on table3");
				((Table) getActor()).add((PuzzlePiece)payload.getObject()).width(100).height(100);
				dragAndDrop.removeTarget(this);
				
				dragAndDrop.addSource(new Source(table3) {
					public Payload dragStart (InputEvent event, float x, float y, int pointer) {
						
						System.out.println("dragging from table3");
						Payload payload = new Payload();
						
						payload.setObject(table3.getCells().get(0).getActor());
						payload.setDragActor(table3.getCells().get(0).getActor());
						table3.clearChildren();
						table3.add(new PuzzlePiece(new Texture(Gdx.files.internal("empty.png")),-1)).width(100).height(100);
						
						dragAndDrop.removeSource(this);
						return payload;
					}
				});
			}
		});
		
		
		
	*/
///////////////////////////
		Label gameTitle = new Label("Puzzungeon", game.skin);
		
		TextButton loginButton = new TextButton("Login", game.skin, "default");
			loginButton.addListener(new ClickListener(){
				@Override 
		            public void clicked(InputEvent event, float x, float y){
						game.setScreen(new LoginScreen(game));
		            }
		        });
		
		TextButton newUserButton = new TextButton("New User", game.skin, "default");
			newUserButton.addListener(new ClickListener(){
				@Override 
	            	public void clicked(InputEvent event, float x, float y){
						game.setScreen(new RegisterScreen(game));
	            	}
	        	});
			
		TextButton guestButton = new TextButton("Login as Guest", game.skin, "default");
					guestButton.addListener(new ClickListener() {
						@Override
						public void clicked(InputEvent event, float x, float y) {
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
									game.client.sendUsername(new Username("guest"));
									game.client.sendPassword(new Password("guest"));
									game.client.sendLoginRegister(new LoginRegister("guest"));
									displayDialog = true;
								}
							}
							else {
								game.client.sendUsername(new Username("guest"));
								game.client.sendPassword(new Password("guest"));
								game.client.sendLoginRegister(new LoginRegister("guest"));
								displayDialog = true;
							}
						}
					});
					
		TextButton exitButton = new TextButton("Exit", game.skin, "default");
			exitButton.addListener(new ClickListener(){
				@Override 
				public void clicked(InputEvent event, float x, float y){
					Gdx.app.exit();
				}
			});
					
		gameFullDialog = new Dialog("Error", game.skin, "dialog") {
			public void result(Object obj) {}};
		gameFullDialog.text("We already have 2 players.");
		gameFullDialog.button("Got it", false); //sends "false" as the result
		
		connectionFailDialog = new Dialog("Error", game.skin, "dialog") {
		    public void result(Object obj) {}};
		connectionFailDialog.text("Couldn't connect to the server");
		connectionFailDialog.button("Got it", false); //sends "false" as the result
			
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
		gameTitle.setFontScale(2);
		
		Table mainMenuTable = new Table();
		mainMenuTable.setFillParent(true);
		mainMenuTable.add(gameTitle).colspan(3);
		mainMenuTable.row();
		
		mainMenuTable.add(loginButton).width(game.WIDTH*0.2f).pad(10);
		mainMenuTable.add(newUserButton).width(game.WIDTH*0.2f).pad(10);
		mainMenuTable.add(guestButton).width(game.WIDTH*0.3f).pad(10);
		mainMenuTable.row();
			
/****************************************************************************************
*                             end: Main Menu UI
****************************************************************************************/

		
/****************************************************************************************
*                             start: Exit Button
****************************************************************************************/
		
		Table exitButtonTable = new Table().bottom().right();
		exitButtonTable.setFillParent(true);
		exitButtonTable.add(exitButton).width(game.WIDTH*0.2f).pad(10);
		
/****************************************************************************************
*                             end: Exit Button
****************************************************************************************/
		
		
		//add actors onto the stage
		//stage.addActor(mainMenuTable);
		//stage.addActor(exitButtonTable);
		
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
		checkClientLoginState();
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
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
				game.setScreen(new WaitingScreen(game));
			}
			
			if(!game.client.loginState) {
				if(game.client.loginStateMessage.equals("Game is Full.")) {
					game.client.loginStateMessage = "";
					gameFullDialog.show(stage);
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
