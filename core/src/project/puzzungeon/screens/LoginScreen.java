package project.puzzungeon.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import project.puzzungeon.Puzzungeon;
import project.server.LoginRegister;
import project.server.Password;
import project.server.Username;

//login screen
public class LoginScreen implements Screen{

	Puzzungeon game; //reference to the game
	private Stage stage;
	
	private Boolean displayDialog;
	
	private Dialog loginFailDialog;
	private Dialog gameFullDialog;
	
	
	//constructor
	public LoginScreen(Puzzungeon game) {
		this.game = game;
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		displayDialog = false;
		
	}
	@Override
	public void show() {

		//simple layout:
		//       Game Title
		//
		//  username:        
		//  password:     
		//        Login
		//                   exit
		
		//create the actors
		Label gameTitle = new Label("Puzzungeon", game.skin);
		Label username = new Label("Username: ", game.skin);
		Label password = new Label("Password: ", game.skin);
		final Label error = new Label("", game.skin);
		final TextArea usernameInput = new TextArea("",game.skin);
		final TextArea passwordInput = new TextArea("",game.skin);
		
		
		loginFailDialog = new Dialog("Failed to log in", game.skin, "dialog") {
		    public void result(Object obj) {}};
		loginFailDialog.text("Check username/password.");
		loginFailDialog.button("Got it", false); //sends "false" as the result
		
		
		gameFullDialog = new Dialog("Game is full.", game.skin, "dialog") {
		    public void result(Object obj) {}};
		gameFullDialog.text("We already have 2 players.");
		gameFullDialog.button("Got it", false); //sends "false" as the result
		
		
		TextButton loginButton = new TextButton("Login", game.skin, "default");
			loginButton.addListener(new ClickListener(){
				@Override 
				public void clicked(InputEvent event, float x, float y){
					String usernameStr = usernameInput.getText();
					String passwordStr = passwordInput.getText();
					
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
						
						game.client.clientUsername = usernameStr;
						System.out.println("username: ." + usernameStr + ".");
						
						//set up connection to the server
						
						if(!game.client.ConnectState) {
							System.out.println("Trying to connect...");
							game.client.connect();
						}
						
						//send username and password to back-end
						game.client.sendUsername(new Username(usernameStr));
						game.client.sendPassword(new Password(passwordStr));
						game.client.sendLoginRegister(new LoginRegister("login"));
						
						displayDialog = true;
					}
				}
			});
			
		TextButton guestButton = new TextButton("Login as Guest", game.skin, "default");
				guestButton.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						game.client.clientUsername = "Guest";
          
						
						if(!game.client.ConnectState) {
							//set up connection to the server
							System.out.println("Trying to connect...");
							game.client.connect();
						}
						
						game.client.sendUsername(new Username("guest"));
						game.client.sendPassword(new Password("guest"));
						game.client.sendLoginRegister(new LoginRegister("guest"));
						
						displayDialog = true;
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
					System.exit(0);
				}
			});
		
		//use vg and hg to group the actors now. changes should be made to make it look better
		VerticalGroup vg = new VerticalGroup();
		vg.setFillParent(true);
		vg.addActor(gameTitle);
		
		HorizontalGroup inputRow1 = new HorizontalGroup();
		inputRow1.addActor(username);
		inputRow1.addActor(usernameInput);
		vg.addActor(inputRow1);
		
		HorizontalGroup inputRow2 = new HorizontalGroup();
		inputRow2.addActor(password);
		inputRow2.addActor(passwordInput);
		vg.addActor(inputRow2);
		
		HorizontalGroup inputRow3 = new HorizontalGroup();
		inputRow3.addActor(loginButton);
		inputRow3.addActor(guestButton);
		vg.addActor(inputRow3);
		
		HorizontalGroup inputRow4 = new HorizontalGroup();
		inputRow4.addActor(backButton);
		inputRow4.addActor(exitButton);
		vg.addActor(inputRow4);
		
		HorizontalGroup inputRow5 = new HorizontalGroup();
		inputRow5.addActor(error);
		vg.addActor(inputRow5);
		
		//add actors onto the stage
		stage.addActor(vg);
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
			}
		}	
	}
}
