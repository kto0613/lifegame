package lifegame;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.concurrent.*;

import javax.swing.*;
import javax.swing.filechooser.*;

public class Main implements Runnable {

	private BoardModel model;

	private JFrame frame;
	private JPanel base;
	private BoardView view;
	private JPanel buttonPanel;
	private JButton nextButton;
	private JButton undoButton;
	private JButton newGameButton;
	private JToggleButton autoRunButton;

	private JMenuBar menu;
	private JMenu fileMenu;
	private JMenu runMenu;
	private JMenuItem file_newGame;
	private JMenuItem file_saveAs;
	private JMenuItem file_open;
	private JMenuItem file_quit;
	private JMenuItem run_next;
	private JMenuItem run_undo;
	private JCheckBoxMenuItem run_autoRun;
	private JMenu run_autoRunSpeed;

	private BoardSettingDialog boardSettingDialog;
	private JFileChooser fileDialog;

	private ScheduledExecutorService autoRunExecute = null;
	private boolean isAutoRunExecuting = false;

	private long autoRunSpeed = 2;
	private static final long[] autoRunSpeeds = {1, 2, 5, 10, 20, 50};

	public Main(){
		this(new BoardSetting());
	}

	public Main(BoardSetting boardSetting){
		this.model = new BoardModel(boardSetting);
	}

	public Main(BoardModel boardModel){
		this.model = boardModel;
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Main());
	}

	private void initializeGUIComponents(){
		ResourceBundle string = ResourceBundle.getBundle("lifegame.resources.string.MainWindow");

		frame = new JFrame(string.getString("title"));
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setMinimumSize(new Dimension(400, 400));
		{
			base = new JPanel();
			base.setPreferredSize(new Dimension(300, 300));
			base.setLayout(new BorderLayout());
			{
				view = new BoardView(model);
				buttonPanel = new JPanel();
				buttonPanel.setLayout(new FlowLayout());
				{
					nextButton = new JButton(string.getString("next"));
					undoButton = new JButton(string.getString("undo"));
					undoButton.setEnabled(model.isUndoable());
					newGameButton = new JButton(string.getString("newGame"));
					autoRunButton = new JToggleButton(string.getString("autoRun"));
				}
				buttonPanel.add(nextButton);
				buttonPanel.add(undoButton);
				buttonPanel.add(newGameButton);
				buttonPanel.add(autoRunButton);
			}
			base.add(view, BorderLayout.CENTER);
			base.add(buttonPanel, BorderLayout.SOUTH);
		}
		frame.setContentPane(base);

		menu = new JMenuBar();
		{
			fileMenu = new JMenu(string.getString("file"));
			fileMenu.setMnemonic(KeyEvent.VK_F);
			{
				file_newGame = new JMenuItem(string.getString("newGame") + "...");
				file_newGame.setMnemonic(KeyEvent.VK_N);
				file_saveAs = new JMenuItem(string.getString("saveAs"));
				file_saveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
				file_saveAs.setMnemonic(KeyEvent.VK_A);
				file_open = new JMenuItem(string.getString("open"));
				file_open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
				file_open.setMnemonic(KeyEvent.VK_O);
				file_quit = new JMenuItem(string.getString("quit"));
				file_quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK));
				file_quit.setMnemonic(KeyEvent.VK_Q);
			}
			fileMenu.add(file_newGame);
			fileMenu.addSeparator();
			fileMenu.add(file_saveAs);
			fileMenu.add(file_open);
			fileMenu.addSeparator();
			fileMenu.add(file_quit);
			runMenu = new JMenu(string.getString("run"));
			runMenu.setMnemonic(KeyEvent.VK_R);
			{
				run_next = new JMenuItem(string.getString("next"));
				run_next.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
				run_next.setMnemonic(KeyEvent.VK_N);
				run_undo = new JMenuItem(string.getString("undo"));
				run_undo.setEnabled(model.isUndoable());
				run_undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK));
				run_undo.setMnemonic(KeyEvent.VK_U);
				run_autoRun = new JCheckBoxMenuItem(string.getString("autoRun"));
				run_autoRun.setMnemonic(KeyEvent.VK_R);
				run_autoRunSpeed = new JMenu(string.getString("autoRunSpeed"));
				initializeAutoRunSpeedMenu();
			}
			runMenu.add(run_next);
			runMenu.add(run_undo);
			runMenu.addSeparator();
			runMenu.add(run_autoRun);
			runMenu.add(run_autoRunSpeed);
		}
		menu.add(fileMenu);
		menu.add(runMenu);

		frame.setJMenuBar(menu);

		boardSettingDialog = new BoardSettingDialog(frame);

		fileDialog = new JFileChooser();
		fileDialog.setFileFilter(new FileNameExtensionFilter(string.getString("txtFile"), "txt"));
	}

	private void initializeAutoRunSpeedMenu(){
		ButtonGroup radioGroup = new ButtonGroup();
		ActionListener speedMenuActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				autoRunSpeed = Long.parseLong(e.getActionCommand());
			}
		};

		for(long speed : autoRunSpeeds){
			JRadioButtonMenuItem speedMenuItem = new JRadioButtonMenuItem("x" + speed);
			speedMenuItem.setActionCommand("" + speed);
			speedMenuItem.addActionListener(speedMenuActionListener);
			radioGroup.add(speedMenuItem);
			run_autoRunSpeed.add(speedMenuItem);
			if(speed == autoRunSpeed){
				radioGroup.setSelected(speedMenuItem.getModel(), true);
			}
		}
	}

	private void initializeListeners(){
		ResourceBundle string = ResourceBundle.getBundle("lifegame.resources.string.MainMessage");

		ActionListener nextActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				model.next();
			}
		};
		nextButton.addActionListener(nextActionListener);
		run_next.addActionListener(nextActionListener);

		ActionListener undoActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				model.undo();
			}
		};
		undoButton.addActionListener(undoActionListener);
		run_undo.addActionListener(undoActionListener);

		ActionListener newGameActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				boardSettingDialog.setLocationRelativeTo(frame);
				boardSettingDialog.setVisible(true);
			}
		};
		newGameButton.addActionListener(newGameActionListener);
		file_newGame.addActionListener(newGameActionListener);

		ActionListener autoRunActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				AbstractButton o = (AbstractButton)e.getSource();
				if(o.isSelected()){
					isAutoRunExecuting = true;
					nextButton.setEnabled(false);
					undoButton.setEnabled(false);
					autoRunButton.setSelected(true);
					run_next.setEnabled(false);
					run_undo.setEnabled(false);
					run_autoRun.setSelected(true);
					run_autoRunSpeed.setEnabled(false);
					file_saveAs.setEnabled(false);
					file_open.setEnabled(false);

					autoRunExecute = Executors.newSingleThreadScheduledExecutor();
					autoRunExecute.scheduleAtFixedRate(new Runnable(){
						public void run(){
							model.next();
						}
					}, 0, 1000 / autoRunSpeed, TimeUnit.MILLISECONDS);
				}
				else{
					autoRunExecute.shutdown();

					nextButton.setEnabled(true);
					undoButton.setEnabled(model.isUndoable());
					autoRunButton.setSelected(false);
					run_next.setEnabled(true);
					run_undo.setEnabled(model.isUndoable());
					run_autoRun.setSelected(false);
					run_autoRunSpeed.setEnabled(true);
					file_saveAs.setEnabled(true);
					file_open.setEnabled(true);
					isAutoRunExecuting = false;
				}
			}
		};
		autoRunButton.addActionListener(autoRunActionListener);
		run_autoRun.addActionListener(autoRunActionListener);

		model.addListener(new BoardListener(){
			public void updated(BoardModel m){
				if(!isAutoRunExecuting){
					undoButton.setEnabled(m.isUndoable());
					run_undo.setEnabled(m.isUndoable());
				}
				view.repaint();
			}
		});

		frame.addWindowListener(new WindowAdapter(){
			public void windowClosed(WindowEvent e){
				if(isAutoRunExecuting) autoRunExecute.shutdown();
			}
		});

		file_quit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				frame.setVisible(false);
				frame.dispose();
			}
		});

		file_saveAs.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int ret = fileDialog.showSaveDialog(frame);
				if(ret == JFileChooser.APPROVE_OPTION){
					if(!BoardFileManager.saveBoardModel(model, fileDialog.getSelectedFile())){
						JOptionPane.showMessageDialog(frame, string.getString("saveFileError"));
					}
				}
			}
		});

		file_open.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int ret = fileDialog.showOpenDialog(frame);
				if(ret == JFileChooser.APPROVE_OPTION){
					BoardModel loadModel = BoardFileManager.loadBoardModel(fileDialog.getSelectedFile());
					if(loadModel == null){
						JOptionPane.showMessageDialog(frame, string.getString("openFileError"));
					}
					else{
						SwingUtilities.invokeLater(new Main(loadModel));
					}
				}
			}
		});
	}

	public void run(){
		initializeGUIComponents();
		initializeListeners();

		frame.pack();
		frame.setVisible(true);
	}

}
