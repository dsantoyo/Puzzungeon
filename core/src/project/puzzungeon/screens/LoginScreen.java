/*CSCI201 Final Project

Project Name: Puzzungeon
Project Number: 7
Project Category: Game

Daniel Santoyo: dsantoyo@usc.edu USC ID: 6926712177
Hayley Pike: hpike@usc.edu USC ID: 8568149839
Yi(Ian) Sui: ysui@usc.edu USC ID: 2961712187
Ekta Gogri: egogri@usc.edu USC ID: 9607321862
*/

package project.puzzungeon.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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
import com.badlogic.gdx.utils.viewport.FitViewport;

import project.puzzungeon.Client;
import project.puzzungeon.Puzzungeon;
import project.server.LoginRegister;
import project.server.Password;
import project.server.Username;

//login screen
public class LoginScreen implements Screen{

	Puzzungeon game; //reference to the game
	private Stage stage;
	private FitViewport viewport;
	
	//background references
	TextureAtlas atlas;
	Sprite background;
	
	//shared by different methods
	private Boolean displayDialog;
	private Dialog loginFailDialog;
	private Dialog gameFullDialog;
	private Dialog connectionFailDialog;
	private Dialog databaseFailDialog;
	private TextArea passwordInput;
	
	//sound variables
	public Sound buttonpress;
	
	//constructor
	public LoginScreen(Puzzungeon game) {
		this.game = game;
		viewport = new FitViewport(Puzzungeon.WIDTH, Puzzungeon.HEIGHT);
		stage = new Stage(viewport);
		Gdx.input.setInputProcessor(stage);
		displayDialog = false;
		
		//set up background
		atlas = game.assetLoader.manager.get("sprites.txt");
		background = atlas.createSprite("dungeon");
		background.setOrigin(0, 0);
		background.setSize(Puzzungeon.WIDTH, Puzzungeon.HEIGHT);
		
		buttonpress = game.assetLoader.manager.get("sound/rightlocation.mp3");
		
		if (game.gameMusic.isPlaying()) {
			game.gameMusic.stop();
		}
		if (!game.menuMusic.isPlaying() && game.playMusic == true) {
			game.menuMusic.play();
			game.menuMusic.setVolume(0.2f);
			game.menuMusic.setLooping(true);
		}
	}
	@Override
	public void show() {

/****************************************************************************************
 *                             start: actors functionality
 ****************************************************************************************/
		Label gameTitle = new Label("Puzzungeon", game.skin, "title");
		gameTitle.setFontScale(1.5f);
		Label username = new Label("Username: ", game.skin);
		Label password = new Label("Password: ", game.skin);
		final Label error = new Label("", game.skin);
		final TextArea usernameInput = new TextArea("",game.skin);
			//when ENTER key is pressed,
			usernameInput.setTextFieldListener(new TextFieldListener() {
				@Override
				public void keyTyped(TextField textField, char c) {
					if(Gdx.input.isKeyPressed(Keys.ENTER)) {
						buttonpress.play();
						String usernameInputStr = new String();
		                if(usernameInput.getText().length() == 0) {
		                	usernameInputStr = "";
		                }
		                else {
		                	usernameInputStr = usernameInput.getText();
		                	//remove newline character
		                	usernameInputStr = usernameInputStr.replace("\n", "");
		                }
		                usernameInput.setText(usernameInputStr);
		                stage.setKeyboardFocus(passwordInput);
					}
				}
			});
		
		passwordInput = new TextArea("",game.skin);
			passwordInput.setPasswordCharacter('*');
			passwordInput.setPasswordMode(true);
			//when ENTER key is pressed,
			passwordInput.setTextFieldListener(new TextFieldListener() {
				@Override
				public void keyTyped(TextField textField, char c) {
					if(Gdx.input.isKeyPressed(Keys.ENTER)) {
						buttonpress.play();
						String passwordInputStr = new String();
		                if(passwordInput.getText().length() == 0) {
		                	passwordInputStr = "";
		                }
		                else {
		                	passwordInputStr = passwordInput.getText();
		                	//remove newline character
		                	passwordInputStr = passwordInputStr.replace("\n", "");
		                }
		                passwordInput.setText(passwordInputStr);
		                String usernameStr = usernameInput.getText();
						String passwordStr = passwordInput.getText();
						
						game.client.username = usernameStr;
						game.client.password = passwordStr;
						
						//front-end input format validation
						if (usernameStr.trim().isEmpty() && passwordStr.trim().isEmpty()) {
							error.setText("Please enter a valid username and password.");
						} 
						else if (usernameStr.trim().isEmpty()) {
							error.setText("Please enter a valid username!");
						} 
						else if (passwordStr.trim().isEmpty()){
							error.setText("Please enter a valid password!");
						} 
						else {
							error.setText("");
							game.client.clientUsername = usernameStr;
							System.out.println("username: ." + usernameStr + ".");
							//set up connection to the server
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
									game.client.sendUsername(new Username(usernameStr));
									game.client.sendPassword(new Password(passwordStr));
									game.client.sendLoginRegister(new LoginRegister("login"));
									displayDialog = true;
								}
							}
							else {
								//send username and password to back-end
								game.client.sendUsername(new Username(usernameStr));
								game.client.sendPassword(new Password(passwordStr));
								game.client.sendLoginRegister(new LoginRegister("login"));
								displayDialog = true;
							}
						}
					}
				}
			});
		
		loginFailDialog = new Dialog("Error", game.skin, "dialog") {
		    public void result(Object obj) {
		    	buttonpress.play();
		    }};
		loginFailDialog.text("Check username/password.");
		loginFailDialog.button("Got it", false); //sends "false" as the result
		
		gameFullDialog = new Dialog("Error", game.skin, "dialog") {
		    public void result(Object obj) {
		    	buttonpress.play();
		    }};
		gameFullDialog.text("We already have 2 players.");
		gameFullDialog.button("Got it", false); //sends "false" as the result
		
		connectionFailDialog = new Dialog("Error", game.skin, "dialog") {
		    public void result(Object obj) {
		    	buttonpress.play();
		    }};
		connectionFailDialog.text("Couldn't connect to the server");
		connectionFailDialog.button("Got it", false); //sends "false" as the result
		
		databaseFailDialog = new Dialog("Error", game.skin, "dialog") {
		    public void result(Object obj) {
		    	buttonpress.play();
		    }};
		databaseFailDialog.text("Couldn't connect to the database");
		databaseFailDialog.button("Got it", false); //sends "false" as the result
		
		TextButton loginButton = new TextButton("Login", game.skin, "default");
			loginButton.addListener(new ClickListener(){
				@Override 
				public void clicked(InputEvent event, float x, float y){
					buttonpress.play();
					String usernameStr = usernameInput.getText();
					String passwordStr = passwordInput.getText();
					
					game.client.username = usernameStr;
					game.client.password = passwordStr;
					
					//front-end input format validation
					if (usernameStr.trim().isEmpty() && passwordStr.trim().isEmpty()) {
						error.setText("Please enter a valid username and password.");
					} 
					else if (usernameStr.trim().isEmpty()) {
						error.setText("Please enter a valid username!");
					} 
					else if (passwordStr.trim().isEmpty()){
						error.setText("Please enter a valid password!");
					} 
					else {
						error.setText("");
						game.client.clientUsername = usernameStr;
						System.out.println("username: ." + usernameStr + ".");
						//set up connection to the server
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
								game.client.sendUsername(new Username(usernameStr));
								game.client.sendPassword(new Password(passwordStr));
								game.client.sendLoginRegister(new LoginRegister("login"));
								displayDialog = true;
							}
						}
						else {
							//send username and password to back-end
							game.client.sendUsername(new Username(usernameStr));
							game.client.sendPassword(new Password(passwordStr));
							game.client.sendLoginRegister(new LoginRegister("login"));
							displayDialog = true;
						}
					}
				}
			});
			
		TextButton guestButton = new TextButton("Login as Guest", game.skin, "default");
				guestButton.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						buttonpress.play();
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
						else {
							game.client.username = "guest";
							game.client.password = "guest";
							game.client.sendUsername(new Username("guest"));
							game.client.sendPassword(new Password("guest"));
							game.client.sendLoginRegister(new LoginRegister("guest"));
							displayDialog = true;
						}
					}
				});
					
		TextButton backButton = new TextButton("Back", game.skin, "default");
			backButton.addListener(new ClickListener(){
				@Override 
				public void clicked(InputEvent event, float x, float y){
					buttonpress.play();
					game.setScreen(new MainMenuScreen(game));
				}
			});
			
		TextButton exitButton = new TextButton("Exit", game.skin, "default");
			exitButton.addListener(new ClickListener(){
				
				@Override 
				public void clicked(InputEvent event, float x, float y){
					buttonpress.play();
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
*                             start: Login Menu UI
****************************************************************************************/
			
			//set label color and size
			error.setColor(Color.RED);
			
			Table loginMenuTable = new Table();
			loginMenuTable.setFillParent(true);
			loginMenuTable.add(gameTitle).colspan(4).padBottom(30);
			
			loginMenuTable.row();
			loginMenuTable.add().width(50).uniform();
			loginMenuTable.add(username).uniform();
			loginMenuTable.add(usernameInput).uniform().height(80).fillX();
			loginMenuTable.add().uniform();
			
			loginMenuTable.row();
			loginMenuTable.add().uniform();
			loginMenuTable.add(password).uniform();
			loginMenuTable.add(passwordInput).pad(5).uniform().height(80).fillX();
			loginMenuTable.add().uniform();
			
			loginMenuTable.row();
			loginMenuTable.add(guestButton).colspan(2);
			loginMenuTable.add(loginButton).colspan(2);
			
			loginMenuTable.row();
			loginMenuTable.add(error).colspan(4);

			
				
/****************************************************************************************
*                             end: Login Menu UI
****************************************************************************************/
		
/****************************************************************************************
*                             start: Exit and Back Button
****************************************************************************************/
			
			Table exitButtonTable = new Table().bottom().right();
			exitButtonTable.setFillParent(true);
			exitButtonTable.add(backButton).width(Puzzungeon.WIDTH*0.2f).pad(10);
			exitButtonTable.add(exitButton).width(Puzzungeon.WIDTH*0.2f).pad(10);
			
/****************************************************************************************
*                             end: Exit and Back Button
****************************************************************************************/
	
		//add actors onto the stage
		stage.addActor(loginMenuTable);
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
		
		//draw background
		game.batch.setProjectionMatrix(stage.getCamera().combined);
		viewport.apply();
		game.batch.begin();
		background.draw(game.batch);
		game.batch.end();
		
		//update and draw stage
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
		
		if(displayDialog == true) {
			
			if(game.client.loginState) {
				game.setScreen(new GameLobbyScreen(game));
			}
			
			if(!game.client.loginState) {
				if(game.client.loginStateMessage.equals("Check username/password")) {
					game.client.loginStateMessage = "";
					loginFailDialog.show(stage);
					displayDialog = false;
				}
				if(game.client.loginStateMessage.equals("Game is Full.")) {
					game.client.loginStateMessage = "";
					gameFullDialog.show(stage);
					displayDialog = false;
				}
				
				if(game.client.loginStateMessage.equals("Connection to the database failed")) {
					
					game.client.loginStateMessage = "";
					databaseFailDialog.show(stage);
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
