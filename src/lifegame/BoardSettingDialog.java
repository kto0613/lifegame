package lifegame;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;

public class BoardSettingDialog extends JDialog{

	private static final long serialVersionUID = 1L;

	private JDialog self = this;
	private JPanel base;

	private JPanel center;
	private JLabel colsLabel, rowsLabel, borderLabel;
	private JSpinner colsSpinner, rowsSpinner;
	private JComboBox<String> borderComboBox;
	private String border_dead;
	private String border_alive;
	private String border_connected;

	private JPanel button;
	private JButton confirmButton;
	private JButton cancelButton;

	public BoardSettingDialog(JFrame parent){
		super(parent, true);

		ResourceBundle string = ResourceBundle.getBundle("lifegame.resources.string.BoardSettingDialog");

		this.setTitle(string.getString("title"));
		this.setResizable(false);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		base = new JPanel();
		base.setLayout(new BorderLayout());
		{
			center = new JPanel();
			center.setBorder(new EmptyBorder(5, 5, 5, 5));
			center.setLayout(new GridLayout(0, 2, 5, 5));
			{
				colsLabel = new JLabel(string.getString("cols") + "(1~1000):");
				colsSpinner = new JSpinner(new SpinnerNumberModel(20, 1, 1000, 1));
				rowsLabel = new JLabel(string.getString("rows") + "(1~1000):");
				rowsSpinner = new JSpinner(new SpinnerNumberModel(20, 1, 1000, 1));
				borderLabel = new JLabel(string.getString("border") + ":");
				border_dead = string.getString("border_dead");
				border_alive = string.getString("border_alive");
				border_connected = string.getString("border_connected");
				String[] borderString = {border_dead, border_alive, border_connected};
				borderComboBox = new JComboBox<String>(borderString);
			}
			center.add(colsLabel);
			center.add(colsSpinner);
			center.add(rowsLabel);
			center.add(rowsSpinner);
			center.add(borderLabel);
			center.add(borderComboBox);

			button = new JPanel();
			button.setLayout(new FlowLayout(FlowLayout.RIGHT));
			{
				confirmButton = new JButton(string.getString("newGame"));
				confirmButton.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e1) {
						BoardSetting setting;
						try{
							int cols = (Integer)colsSpinner.getValue();
							int rows = (Integer)rowsSpinner.getValue();
							BoardSetting.Border border;
							String selectedBorder = (String)borderComboBox.getSelectedItem();
							if(selectedBorder == border_alive){
								border = BoardSetting.Border.BORDER_TRUE;
							}
							else if(selectedBorder == border_connected){
								border = BoardSetting.Border.BORDER_CONNECTED;
							}
							else{
								border = BoardSetting.Border.BORDER_FALSE;
							}
							setting = new BoardSetting(cols, rows, border);
						}
						catch(Exception e2){
							JOptionPane.showMessageDialog(self, string.getString("invalidInputError"));
							return;
						}
						self.setVisible(false);
						self.dispose();
						SwingUtilities.invokeLater(new Main(setting));
					}
				});
				cancelButton = new JButton(string.getString("cancel"));
				cancelButton.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
						self.setVisible(false);
						self.dispose();
					}
				});
			}
			button.add(confirmButton);
			button.add(cancelButton);
		}
		base.add(center, BorderLayout.CENTER);
		base.add(button, BorderLayout.SOUTH);

		this.setContentPane(base);
		this.pack();
	}

}
