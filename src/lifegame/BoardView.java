package lifegame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.*;

import javax.swing.JPanel;

public class BoardView extends JPanel implements MouseListener, MouseMotionListener {

	private static final long serialVersionUID = 1L;

	private BoardModel model;

	private int panelWidth = 0, panelHeight = 0;
	private int cellSize = 0;
	private int boardWidth = 0, boardHeight = 0;
	private int boardX = 0, boardY = 0;
	private static final int maximumPixelBoardCellSize = 2;

	private boolean cellChanged = false;
	private int changedCellX = 0, changedCellY = 0;

	public BoardView(BoardModel boardmodel){
		model = boardmodel;
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}

	public void paint(Graphics g){
		super.paint(g);

		Graphics copy = g.create();

		this.updateVariables();

		if(cellSize > maximumPixelBoardCellSize){
			this.drawLines(copy);
			this.fillRects(copy);
		}
		else if(cellSize > 0){
			this.drawPixelBoard(copy);
		}
		else{
			copy.drawString("Window is too small!!", 5, 15);
		}
	}

	private void updateVariables(){
		if(panelWidth != this.getWidth() || panelHeight != this.getHeight()){
			panelWidth = this.getWidth(); panelHeight = this.getHeight();
			cellSize = Math.min((panelWidth - 1) / model.getCols(), (panelHeight - 1) / model.getRows());
			if(cellSize <= maximumPixelBoardCellSize){
				cellSize = Math.min((panelWidth - 2) / model.getCols(), (panelHeight - 2) / model.getRows());
				if(cellSize > 0){
					boardWidth = cellSize * model.getCols() + 2;
					boardHeight = cellSize * model.getRows() + 2;
					boardX = (panelWidth - boardWidth) / 2;
					boardY = (panelHeight - boardHeight) / 2;
				}
			}
			else{
				boardWidth = cellSize * model.getCols() + 1;
				boardHeight = cellSize * model.getRows() + 1;
				boardX = (panelWidth - boardWidth) / 2;
				boardY = (panelHeight - boardHeight) / 2;
			}
		}
	}

	private void drawLines(Graphics g){
		g.setColor(Color.LIGHT_GRAY);
		for(int i = 1; i < model.getCols(); i++){
			g.drawLine(boardX + cellSize * i, boardY, boardX + cellSize * i, boardY + boardHeight - 1);
		}
		for(int i = 1; i < model.getRows(); i++){
			g.drawLine(boardX, boardY + cellSize * i, boardX + boardWidth - 1, boardY + cellSize * i);
		}

		g.setColor(Color.GRAY);
		g.drawRect(boardX, boardY, boardWidth - 1, boardHeight - 1);
	}

	private void fillRects(Graphics g){
		for(int y = 0; y < model.getRows(); y++){
			for(int x = 0; x < model.getCols(); x++){
				if(model.getCellState(x, y)) g.setColor(Color.BLACK);
				else g.setColor(Color.WHITE);
				g.fillRect(boardX + cellSize * x + 1, boardY + cellSize * y + 1, cellSize - 1, cellSize - 1);
			}
		}
	}

	private void drawPixelBoard(Graphics g){
		g.setColor(Color.GRAY);
		g.drawRect(boardX, boardY, boardWidth - 1, boardHeight - 1);

		for(int y = 0; y < model.getRows(); y++){
			for(int x = 0; x < model.getCols(); x++){
				if(model.getCellState(x, y)) g.setColor(Color.BLACK);
				else g.setColor(Color.WHITE);
				g.fillRect(boardX + cellSize * x + 1, boardY + cellSize * y + 1, cellSize, cellSize);
			}
		}
	}

	private void cellChangeFromMouseEvent(MouseEvent e){
		int cellX, cellY;

		if(e.getX() > boardX && e.getY() > boardY && e.getX() < boardX + boardWidth - 1 && e.getY() < boardY + boardHeight - 1){
			cellX = (e.getX() - boardX - 1) / cellSize;
			cellY = (e.getY() - boardY - 1) / cellSize;
			if(cellChanged && changedCellX == cellX && changedCellY == cellY) return;
			else if(cellSize > maximumPixelBoardCellSize && ((e.getX() - boardX) % cellSize == 0 || (e.getY() - boardY) % cellSize == 0)) return;
			else if(cellSize > 0){
				cellChanged = true;
				changedCellX = cellX;
				changedCellY = cellY;
				model.changeCellState(cellX, cellY);
			}
		}
		else if(cellChanged == true) cellChanged = false;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		cellChangeFromMouseEvent(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		//Do nothing
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		//Do nothing
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		//Do nothing
	}

	@Override
	public void mouseExited(MouseEvent e) {
		//Do nothing
	}

	@Override
	public void mousePressed(MouseEvent e) {
		cellChangeFromMouseEvent(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		this.cellChanged = false;
	}

}
