package project.puzzungeon;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class PuzzlePiece extends Image{
	
	private int pieceID;
	
	public PuzzlePiece(Texture texture, int pieceID){
		super(texture);
		this.pieceID = pieceID;
	}

	public int getPieceID() {
		return this.pieceID;
	}
}
