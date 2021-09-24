package lifegame;

import java.util.*;

public class BoardModel {

	private int cols;
	private int rows;
	private boolean[][] cells;

	private List<BoardListener> listeners;

	private static final int maxUndoListSize = 32;
	private List<boolean[][]> undoList;

	BoardSetting.Border border;

	public BoardModel(BoardSetting boardSetting){
		this(boardSetting, null);
	}

	protected BoardModel(BoardSetting boardSetting, boolean[][] cellData){
		cols = boardSetting.getBoardCols();
		rows = boardSetting.getBoardRows();
		listeners = new ArrayList<BoardListener>();
		undoList = new ArrayList<boolean[][]>();
		border = boardSetting.getBoardBorder();

		if(cellData == null) cells = new boolean[rows][cols];
		else if(cellData.length != rows || cellData[0].length != cols) throw new IllegalArgumentException();
		else cells = this.copyCellState(cellData);
	}

	public int getCols(){ return this.cols; }
	public int getRows(){ return this.rows; }
	public BoardSetting.Border getBorder(){ return this.border; }

	public void printForDebug(){
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < cols; j++){
				System.out.print(cells[i][j] ? '*' : '.');
			}
			System.out.println();
		}

		System.out.println();
	}

	public void changeCellState(int x, int y){
		if(x < cols && y < rows && x >= 0 && y >= 0){
			updateUndoList(copyCellState(cells));
			cells[y][x] = !cells[y][x];
			fireUpdate();
		}
	}

	public void addListener(BoardListener listener){
		listeners.add(listener);
	}

	public void next(){
		boolean[][] nextCells = new boolean[rows][cols];

		updateUndoList(cells);

		for(int x = 0; x< cols; x++){
			for(int y = 0; y < rows; y++){
				int count = 0;

				for(int i = -1; i <= 1; i++){
					for(int j = -1; j <= 1; j++){
						if(i == 0 && j == 0) continue;
						count += getCellState(x + i, y + j) ? 1 : 0;
					}
				}

				if(count <= 1 || count >= 4) nextCells[y][x] = false;
				else if(count == 2) nextCells[y][x] = cells[y][x];
				else nextCells[y][x] = true;
			}
		}

		cells = nextCells;

		fireUpdate();
	}

	public boolean getCellState(int x, int y){
		if(x < cols && y < rows && x >= 0 && y >= 0){
			return cells[y][x];
		}
		else{
			switch(border){
			case BORDER_FALSE:
				return false;
			case BORDER_TRUE:
				return true;
			case BORDER_CONNECTED:
				return cells[(y%rows+rows)%rows][(x%cols+cols)%cols];
			default:
				return false;
			}
		}
	}

	public void undo(){
		if(!undoList.isEmpty()){
			cells = undoList.remove(undoList.size() - 1);
			fireUpdate();
		}
	}

	public boolean isUndoable(){
		if(undoList.isEmpty()) return false;
		else return true;
	}

	private void updateUndoList(boolean[][] cellState){
		if(undoList.size() == maxUndoListSize){
			undoList.remove(0);
		}
		undoList.add(cellState);
	}

	private boolean[][] copyCellState(boolean[][] cellState){
		boolean[][] copy = new boolean[rows][cols];

		for(int i = 0; i < rows; i++){
			for(int j = 0; j < cols; j++){
				copy[i][j] = cellState[i][j];
			}
		}

		return copy;
	}

	private void fireUpdate(){
		for(BoardListener listener: listeners){
			listener.updated(this);
		}
	}

	public String toString(){
		return "BoardModel; cols: " + this.cols + ", rows: " + this.rows + ", border: " + this.border;
	}

}
