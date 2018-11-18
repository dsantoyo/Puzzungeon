package project.puzzungeon;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class PuzzlePiece extends Image{
	
	private float pieceX;
	private float pieceY;
	private int pieceID;
	
	public boolean playerID;
	private boolean inRightLocation;
	private float pieceCorrectLocX;
	private float pieceCorrectLocY;
	private boolean visible;
	
	public PuzzlePiece(Texture texture, int pieceID, float pieceX, float pieceY, boolean playerID){

		super(texture);
		this.pieceID = pieceID;
		this.pieceCorrectLocX = pieceX;
		this.pieceCorrectLocY = pieceY;
		inRightLocation=false;
		//this.pieceCorrectLoc = pieceCorrectLoc;
		visible=true;
		this.playerID=playerID;
	}

	public float getPieceX() {
		return this.pieceX+50;
	}
	
	public float getPieceY() {
		return this.pieceY+50;
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
	
	public boolean checkrightLocation(){
		return inRightLocation;
	}
	public void setrightLocation()
	{
		inRightLocation=true;
	
	}
	public boolean getVisible()
	{
		return visible;
	}
	public void setVisible()
	{
		visible=false;
		
	}
}

		
