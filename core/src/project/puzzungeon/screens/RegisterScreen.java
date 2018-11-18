package project.puzzungeon.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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
import com.badlogic.gdx.utils.viewport.FitViewport;

import project.puzzungeon.Client;
import project.puzzungeon.Puzzungeon;
import project.server.LoginRegister;
import project.server.Password;
import project.server.Username;

//register screen
public class RegisterScreen implements Screen{

	Puzzungeon game; //reference to the game
	private Stage stage;
	private FitViewport viewport;
	
	//background references
	private TextureAtlas atlas;
	Sprite background;
	
	//shared by different methods
	private Boolean displayDialog;
	private Dialog gameFullDialog;
	private Dialog registerFailDialog;
	private Dialog connectionFailDialog;
	private Dialog databaseFailDialog;
	private TextArea passwordInput;
	
	//constructor
	public RegisterScreen(Puzzungeon game) {
		this.game = game;
		viewport = new FitViewport(Puzzungeon.WIDTH, Puzzungeon.HEIGHT);
		stage = new Stage(viewport);
		displayDialog = false;
		Gdx.input.setInputProcessor(stage);
		
		//set up background
		atlas = game.assetLoader.manager.get("sprites.txt");
		background = atlas.createSprite("dungeon");
		background.setOrigin(0, 0);
		background.setScale(9f);
	}
	
	@Override
	public void show() {

/****************************************************************************************
 *                             start: actors functionality
****************************************************************************************/				
		//create the actors
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
									game.client.sendLoginRegister(new LoginRegister("register"));
									displayDialog = true;
								}
							}
							else {
								//send username and password to back-end
								game.client.sendUsername(new Username(usernameStr));
								game.client.sendPassword(new Password(passwordStr));
								game.client.sendLoginRegister(new LoginRegister("register"));
								displayDialog = true;
							}
						} 
					}
				}
			});
			
		TextButton registerButton = new TextButton("Register", game.skin, "default");
			registerButton.addListener(new ClickListener(){
					@Override 
					public void clicked(InputEvent event, float x, float y){
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
									game.client.sendLoginRegister(new LoginRegister("register"));
									displayDialog = true;
								}
							}
							else {
								//send username and password to back-end
								game.client.sendUsername(new Username(usernameStr));
								game.client.sendPassword(new Password(passwordStr));
								game.client.sendLoginRegister(new LoginRegister("register"));
								displayDialog = true;
							}
						}
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
			
		TextButton backButton = new TextButton("Back", game.skin, "default");
			backButton.addListener(new ClickListener(){
				@Override 
				public void clicked(InputEvent event, float x, float y){
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
			
		registerFailDialog = new Dialog("", game.skin, "dialog") {
		    public void result(Object obj) {}};
		registerFailDialog.text("Use other username/password");
		registerFailDialog.button("Got it", false); //sends "false" as the result
		
		gameFullDialog = new Dialog("", game.skin, "dialog") {
		    public void result(Object obj) {}};
		gameFullDialog.text("We already have 2 players.");
		gameFullDialog.button("Got it", false); //sends "false" as the result
		
		connectionFailDialog = new Dialog("", game.skin, "dialog") {
		    public void result(Object obj) {}};
		connectionFailDialog.text("Couldn't connect to the server");
		connectionFailDialog.button("Got it", false); //sends "false" as the result
		
		databaseFailDialog = new Dialog("", game.skin, "dialog") {
		    public void result(Object obj) {}};
		databaseFailDialog.text("Couldn't connect to the database");
		databaseFailDialog.button("Got it", false); //sends "false" as the result
		
/****************************************************************************************
*                             end: actors functionality
****************************************************************************************/
		
/****************************************************************************************
*                             start: actors layout
****************************************************************************************/
			
/****************************************************************************************
*                             start: Register Menu UI
****************************************************************************************/
			
		//set label color and size
		error.setColor(Color.RED);
		
		Table registerMenuTable = new Table();
		registerMenuTable.setFillParent(true);
		registerMenuTable.add(gameTitle).colspan(4).padBottom(30);
		
		registerMenuTable.row();
		registerMenuTable.add().width(50).uniform();
		registerMenuTable.add(username).uniform();
		registerMenuTable.add(usernameInput).uniform().height(80).fillX();
		registerMenuTable.add().uniform();
		
		registerMenuTable.row();
		registerMenuTable.add().uniform();
		registerMenuTable.add(password).uniform();
		registerMenuTable.add(passwordInput).uniform().height(80).fillX();
		registerMenuTable.add().uniform();
		
		registerMenuTable.row();
		registerMenuTable.add(guestButton).colspan(2);
		registerMenuTable.add(registerButton).colspan(2);
		
		registerMenuTable.row();
		registerMenuTable.add(error).colspan(4);

			
/****************************************************************************************
*                             end: Register Menu UI
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
		stage.addActor(registerMenuTable);
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
				if(game.client.loginStateMessage.equals("Failed to register.")) {
					game.client.loginStateMessage = "";
					registerFailDialog.show(stage);
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
