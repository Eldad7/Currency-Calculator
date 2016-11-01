package corem.eldad.client;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.*;
import java.util.Vector;

import javax.swing.*;
public class CalculatorGUI extends CurrencyHolder{
	private JComboBox<String> from;
	private JComboBox<String> to;
	private JTextField fromCoin;
	private JTextField toCoin;
	private JButton go;
	private JPanel calcPanel;
	private JPanel southPanel;
	private JLabel created;
	private JFrame calculatorFrame;
	
	public void init(Vector<Currency> vector){
		MathOperation divide = (a, aUnit, num, b, bUnit) -> num*(a*bUnit / b*aUnit);
		from = new JComboBox<String>();
		to = new JComboBox<String>();
		for (int i=0; i<vector.size(); i++){
			from.addItem(vector.get(i).getCurrencyCode());
			to.addItem(vector.get(i).getCurrencyCode());
		}
		go = new JButton("Go!");
		fromCoin = new JTextField();
		toCoin = new JTextField();
		southPanel = new JPanel();
		southPanel.setSize(400, 50);
		created = new JLabel("Created by Eldad Corem & Naama Lapidot - All Rights Reserved");
		southPanel.add(created, BorderLayout.CENTER);
		calcPanel = new JPanel();
		calcPanel.setLayout(new BoxLayout(calcPanel, BoxLayout.X_AXIS));
		calcPanel.setSize(400, 150);
		calcPanel.add(from);
		calcPanel.add(fromCoin);
		calcPanel.add(go);
		calcPanel.add(to);
		calcPanel.add(toCoin);
		calculatorFrame = new JFrame("Currency Calculator");
		calculatorFrame.setBackground(Color.gray);
		calculatorFrame.setSize(400, 200);
		calculatorFrame.setLocation(300, 100);
		calculatorFrame.setLayout(new BorderLayout());
		calculatorFrame.add(calcPanel, BorderLayout.NORTH);
		calculatorFrame.add(southPanel, BorderLayout.SOUTH);
		go.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent e) {
					toCoin.setText(String.valueOf((calculate(String.valueOf(from.getSelectedItem()), String.valueOf(to.getSelectedItem()),divide,vector, fromCoin.getText()))));
			}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {}

			@Override
			public void mouseReleased(MouseEvent e) {}
		});
		calculatorFrame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent event){
				calculatorFrame.setVisible(false);
				calculatorFrame.dispose();
			}
		});
		toCoin.setEditable(false);
		calculatorFrame.setVisible(true);
	}

}
