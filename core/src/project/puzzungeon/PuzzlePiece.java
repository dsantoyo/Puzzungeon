package project.puzzungeon;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class PuzzlePiece extends Image{
	
	private float pieceX;
	private float pieceY;
	private int pieceID;
	
	private float pieceCorrectLocX;
	private float pieceCorrectLocY;
	
	private boolean inRightLocation;
	
	
	public PuzzlePiece(Texture texture, int pieceID, float pieceX, float pieceY){
		super(texture);
		this.pieceID = pieceID;
		this.pieceCorrectLocX = pieceX;
		this.pieceCorrectLocY = pieceY;
		inRightLocation=false;
		//this.pieceCorrectLoc = pieceCorrectLoc;
	}

	public float getPieceX() {
		return this.pieceX;
	}
	
	public float getPieceY() {
		return this.pieceY;
	}
	
	public int getPieceID(){
		
		return this.pieceID;
	}
	
	public int getPieceCorrectLocX(){
		return (int) this.pieceCorrectLocX;
	}
	public int getPieceCorrectLocY(){
		return (int)this.pieceCorrectLocY;
	}
	public boolean checkRightLocation()
	{
		return inRightLocation;
	}
	public void setRightLocation()
	{
		inRightLocation=true;
	}
	
}
