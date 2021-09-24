package lifegame;

public class BoardSetting {

	private int cols;
	private int rows;

	public enum Border{
		BORDER_FALSE, BORDER_TRUE, BORDER_CONNECTED
	}
	private Border border;

	public BoardSetting(){
		this(20, 20, Border.BORDER_FALSE);
	}

	public BoardSetting(int boardCols, int boardRows, Border boardBorder) throws IllegalArgumentException{
		if(boardCols <= 0 || boardRows <= 0) throw new IllegalArgumentException();
		cols = boardCols;
		rows = boardRows;
		border = boardBorder;
	}

	public int getBoardCols(){ return cols; }
	public int getBoardRows(){ return rows; }
	public Border getBoardBorder(){ return border; }

	public String toString(){
		return "BoardSetting; cols: " + this.cols + ", rows: " + this.rows + ", border: " + this.border;
	}

}
