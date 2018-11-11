package project.puzzungeon.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import project.puzzungeon.Puzzungeon;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = Puzzungeon.DEFAULT_WIDTH;
		config.height = Puzzungeon.DEFAULT_HEIGHT;
		new LwjglApplication(new Puzzungeon(), config);
	}
}
