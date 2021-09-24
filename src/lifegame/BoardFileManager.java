package lifegame;

import java.io.*;

public final class BoardFileManager {

	private BoardFileManager() {
		//static class constructor
		//Do nothing
	}

	public static final boolean saveBoardModel(BoardModel model, File file){
		BufferedWriter writeFile = null;
		boolean result = true;

		try{
			writeFile = new BufferedWriter(new FileWriter(file));
			writeFile.write("" + model.getCols() + "\n");
			writeFile.write("" + model.getRows() + "\n");
			writeFile.write("" + model.getBorder() + "\n");

			for(int i = 0; i < model.getRows(); i++){
				for(int j = 0; j < model.getCols(); j++){
					writeFile.write(model.getCellState(j, i) ? '*' : '.');
				}
				writeFile.write('\n');
			}
		}
		catch(Exception e){
			System.out.println(e.getMessage());
			result = false;
		}
		finally{
			try{
				writeFile.close();
			}
			catch(Exception e){
				System.out.println(e.getMessage());
				result = false;
			}
		}

		return result;
	}

	public static final BoardModel loadBoardModel(File file){
		BufferedReader readFile = null;
		boolean result = true;

		int cols = 0, rows = 0;
		BoardSetting.Border border = null;
		boolean[][] cells = null;

		try{
			readFile = new BufferedReader(new FileReader(file));
			cols = Integer.parseInt(readFile.readLine());
			rows = Integer.parseInt(readFile.readLine());
			border = BoardSetting.Border.valueOf(readFile.readLine());

			cells = new boolean[rows][cols];
			for(int i = 0; i < rows; i++){
				for(int j = 0; j < cols; j++){
					cells[i][j] = (readFile.read() == '*');
				}
				readFile.read();
			}

		}
		catch(Exception e){
			System.out.println(e.getMessage());
			result = false;
		}
		finally{
			try{
				readFile.close();
			}
			catch(Exception e){
				System.out.println(e.getMessage());
				result = false;
			}
		}

		if(result){
			BoardSetting setting;
			BoardModel model;
			try{
				setting = new BoardSetting(cols, rows, border);
				model = new BoardModel(setting, cells);
			}
			catch(IllegalArgumentException e){
				return null;
			}
			return model;
		}
		else return null;
	}
}
