package project.puzzungeon;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class PuzzlePiece extends Image{
	
	private int pieceID;
	
	private int pieceCorrectLoc;
	
	public PuzzlePiece(Texture texture, int pieceID ){
		super(texture);
		this.pieceID = pieceID;
		//this.pieceCorrectLoc = pieceCorrectLoc;
	}
	
	public PuzzlePiece(Drawable drawable, int pieceID) {
		super(drawable);
		this.pieceID = pieceID;
	}
	
	public PuzzlePiece(TextureRegion texture, int pieceID) {
		super(texture);
		this.pieceID = pieceID;
	}
	
	public int getPieceID() {
		return this.pieceID;
	}
	
	public int getPieceCorrectLoc(){
		return this.pieceCorrectLoc;
	}
}
