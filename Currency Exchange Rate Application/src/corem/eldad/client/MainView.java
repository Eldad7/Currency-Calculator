package corem.eldad.client;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;

public class MainView extends CurrencyHolder implements ListenersInterface {
	private String coins[][];
	private	JScrollPane scrollPane;
	private JScrollBar scrollBar;
	private JFrame mainFrame;
	private JFrame coinInfo;
	private JPanel infoTopPanel;
	private JPanel infoBottomPanel;
	private JPanel topPanel;
	private JPanel middlePanel;
	private JPanel rightPanel;
	private JPanel bottomPanel;
	private CurrencyHistoryGraph gr;
	private JLabel addLabel;
	private JLabel refreshLabel;
	private JLabel update;
	private JLabel coinName;
	private JLabel coinNameData;
	private JLabel countryName;
	private JLabel countryNameData;
	private JLabel unitName;
	private JLabel unitNameData;
	private JLabel rateName;
	private JLabel rateNameData;
	private JLabel codeName;
	private JLabel codeNameData;
	private JTable table;
	private JButton quit;
	private JButton openCalc;
	private JButton addCoin;
	private JButton graph;
	private JButton refresh;
	private JButton search;
	private JButton online;
	private JButton updateCoin;
	private JTextField searchField;
	private JLabel created;
	private JComboBox<String> searchItems;
	private GroupLayout groupLayoutTop;
	private GroupLayout groupLayoutRight;
	final String cat[] = { "Coin Name", "Coin Code", "Units", "Rate" };

	public void initialize() {
		new CurrencyHolder();
		String categories[] = { "Currency Code", "Country" };
		coinsInit();
		searchItems = new JComboBox<String>(categories);
		mainFrame = new JFrame("Currency exchange app");
		if (isLocal())
			mainFrame.setTitle("Currency exchange app - Offline mode");
		mainFrame.setBackground(Color.gray);
		mainFrame.setSize(750, 500);
		mainFrame.setLocation(300, 100);
		topPanel = new JPanel();
		topPanel.setSize(750, 30);
		if (isLocal())
			online = new JButton("Reconnect");
		else {
			online = new JButton("Connected");
			online.setEnabled(false);
		}
		online.addActionListener(this);
		search = new JButton("Search");
		search.addActionListener(this);
		openCalc = new JButton("Open Calculator");
		openCalc.addActionListener(this);
		searchField = new JTextField("");
		searchField.setEditable(true);
		topPanel = new JPanel();
		groupLayoutTop = new GroupLayout(topPanel);
		topPanel.setLayout(groupLayoutTop);
		groupLayoutTop.setAutoCreateContainerGaps(true);
		groupLayoutTop.setAutoCreateGaps(true);
		groupLayoutTop
				.setHorizontalGroup(
						groupLayoutTop.createSequentialGroup().addComponent(searchField).addComponent(searchItems)
								.addComponent(search).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
										GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(openCalc).addComponent(online));
		groupLayoutTop
				.setVerticalGroup(groupLayoutTop.createSequentialGroup()
						.addGroup(groupLayoutTop.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(searchField).addComponent(searchItems).addComponent(search)
								.addComponent(openCalc).addComponent(online)));
		addLabel = new JLabel("Add a new coin");
		addCoin = new JButton("Add");
		addCoin.addActionListener(this);
		updateCoin = new JButton("Update local");
		updateCoin.addActionListener(this);
		updateCoin.setEnabled(false);
		refresh = new JButton("Refresh");
		refresh.addActionListener(this);
		if (isLocal())
			refresh.setEnabled(false);
		refreshLabel = new JLabel("Refresh database");
		quit = new JButton("Exit");
		quit.addActionListener(this);
		rightPanel = new JPanel();
		groupLayoutRight = new GroupLayout(rightPanel);
		rightPanel.setLayout(groupLayoutRight);
		groupLayoutRight.setAutoCreateContainerGaps(true);
		groupLayoutRight.setAutoCreateGaps(true);
		groupLayoutRight
				.setVerticalGroup(groupLayoutRight.createSequentialGroup().addComponent(addLabel).addComponent(addCoin)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)
						.addComponent(updateCoin).addComponent(refreshLabel).addComponent(refresh)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)
						.addComponent(quit));
		groupLayoutRight.setHorizontalGroup(groupLayoutRight.createSequentialGroup()
				.addGroup(groupLayoutRight.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(addLabel)
						.addComponent(addCoin).addComponent(updateCoin).addComponent(refreshLabel).addComponent(refresh)
						.addComponent(quit)));
		table = new JTable(coins, cat);
		table.setGridColor(Color.BLACK);
		table.setEnabled(false);
		table.setBackground(Color.lightGray);
		table.setShowVerticalLines(false);
		scrollPane = new JScrollPane(table);
		update = new JLabel("Last update on " + getDate());
		middlePanel = new JPanel();
		middlePanel.setLayout(new BorderLayout());
		middlePanel.setBackground(Color.lightGray);
		middlePanel.add(table, BorderLayout.NORTH);
		middlePanel.add(update, BorderLayout.SOUTH);
		scrollBar = new JScrollBar();
		scrollBar.setEnabled(true);
		scrollBar.setVisible(true);
		scrollPane.setVerticalScrollBar(scrollBar);	
		bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());
		bottomPanel.setFont(Font.getFont("Arial"));
		bottomPanel.setBackground(Color.cyan);
		created = new JLabel("Created by Eldad Corem & Naama Lapidot - All Rights Reserved");
		created.setSize(750, 50);
		bottomPanel.add(created, BorderLayout.CENTER);
		mainFrame.add(topPanel, BorderLayout.NORTH);
		mainFrame.add(middlePanel, BorderLayout.CENTER);
		mainFrame.add(rightPanel, BorderLayout.EAST);
		mainFrame.add(bottomPanel, BorderLayout.SOUTH);
		mainFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				System.out.println("Closing");
				mainFrame.setVisible(false);
				mainFrame.dispose();
				if (!(isLocal())) {
					try {
						getClientSocket().close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				System.exit(1);
			}
		});
		mainFrame.setVisible(true);
	}

	private void coinsInit() {
		int i, j;
		coins = new String[getVector().size() + 1][4];
		coins[0][0] = "Coin Name";
		coins[0][1] = "Coin Code";
		coins[0][2] = "Unit";
		coins[0][3] = "Rate";
		for (i = 0, j = 0; i <getVector().size(); i++) {
			coins[i + 1][j] = getVector().get(i).getName();
			coins[i + 1][j + 1] = getVector().get(i).getCurrencyCode();
			int t = getVector().get(i).getUnit();
			float f = getVector().get(i).getRate();
			coins[i + 1][j + 2] = String.valueOf(t);
			coins[i + 1][j + 3] = String.valueOf(f);
		}
	}

	private void popupInit(corem.eldad.client.Currency curr) {
		coinInfo = new JFrame();
		coinName = new JLabel("Coin name:");
		coinNameData = new JLabel();
		countryName = new JLabel("Country name:");
		countryNameData = new JLabel();
		unitName = new JLabel("Units number:");
		unitNameData = new JLabel();
		rateName = new JLabel("Current rate:");
		rateNameData = new JLabel();
		codeName = new JLabel("Currency Code");
		codeNameData = new JLabel();
		infoTopPanel = new JPanel();
		infoBottomPanel = new JPanel();
		graph = new JButton("Show Rate History");
		graph.addActionListener(this);
		if (curr.isLocal())
			graph.setEnabled(false);
		else
			gr = new CurrencyHistoryGraph(curr.getHistory());
		coinInfo.setSize(300, 170);
		infoTopPanel.setSize(300, 150);
		infoTopPanel.setLayout(new GridLayout(0, 2));
		infoTopPanel.add(coinName);
		infoTopPanel.add(coinNameData);
		infoTopPanel.add(codeName);
		infoTopPanel.add(codeNameData);
		infoTopPanel.add(countryName);
		infoTopPanel.add(countryNameData);
		infoTopPanel.add(unitName);
		infoTopPanel.add(unitNameData);
		infoTopPanel.add(rateName);
		infoTopPanel.add(rateNameData);
		infoBottomPanel.setSize(300, 150);
		infoBottomPanel.setLayout(new GridLayout(0, 1));
		infoBottomPanel.add(graph);
		coinInfo.add(infoTopPanel, BorderLayout.NORTH);
		coinInfo.add(infoBottomPanel, BorderLayout.SOUTH);
		coinInfo.setLocation(300, 200);
		coinInfo.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				coinInfo.setVisible(false);
				coinInfo.dispose();
			}
		});
	}

	public MainView() {
		super();
	}

	@SuppressWarnings("static-access")
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == refresh) {
			refresh();
			coinsInit();
			table.setVisible(false);
			table = new JTable(coins, cat);
			table.setGridColor(Color.BLACK);
			table.setEnabled(false);
			table.setBackground(Color.lightGray);
			table.setShowVerticalLines(false);
			middlePanel.add(table, BorderLayout.NORTH);
			table.setVisible(true);
			update.setText("Last update on " + getDate());
			JInternalFrame frame = new JInternalFrame("Notice");
			frame.setVisible(true);
			JOptionPane.showMessageDialog(frame, "Updated successfuly\nPlease note - local added data wasn't updated");
		}
		if (e.getSource() == quit) {
			System.out.println("Closing\n");
			try {
				if (!(isLocal()))
					getClientSocket().close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			mainFrame.setVisible(false);
			mainFrame.dispose();
			System.exit(0);
		}

		if (e.getSource() == search) {
			if (searchField.getText().equals("")) {
				JInternalFrame frame = new JInternalFrame("Error");
				frame.setVisible(true);
				JOptionPane.showMessageDialog(frame, "Search field is empty!");
			} else {
				Currency curr = search(String.valueOf(searchItems.getSelectedItem()),searchField.getText());
				if (curr != null) {
					popupInit(curr);
					coinNameData.setText(curr.getName());
					codeNameData.setText(curr.getCurrencyCode());
					unitNameData.setText(String.valueOf(curr.getUnit()));
					rateNameData.setText(String.valueOf(curr.getRate()));
					countryNameData.setText(curr.getCountry());
					coinInfo.setVisible(true);
				} else {
					JInternalFrame frame = new JInternalFrame("Error");
					frame.setVisible(true);
					JOptionPane.showMessageDialog(frame, "Coin not found");
				}
			}
		}
		if (e.getSource() == openCalc) {
			CalculatorGUI calc = new CalculatorGUI();
			calc.init(getVector());
		}
		if (e.getSource() == graph)
			gr.start();
		if (e.getSource() == online) {
			try {
				reconnect();
				online.setText("connected");
				online.setEnabled(false);
				refresh.setEnabled(true);
			} catch (IOException e1) {
				JInternalFrame frame = new JInternalFrame("Error");
				frame.setVisible(true);
				JOptionPane.showMessageDialog(frame, "Can't connect to server");
			}
		}
		if (e.getSource() == addCoin) {
			add();
			updateCoin.setEnabled(true);
		}
		if (e.getSource() == updateCoin) {
			coinsInit();
			table.setVisible(false);
			table = new JTable(coins, cat);
			table.setGridColor(Color.BLACK);
			table.setEnabled(false);
			table.setBackground(Color.lightGray);
			table.setShowVerticalLines(false);
			middlePanel.add(table, BorderLayout.NORTH);
			table.setVisible(true);
			updateCoin.setEnabled(false);
		}

	}
	public void add(){													//Add currency
		String newCoin[] = new String[5];
		JButton ok;
		JFrame frame;
		JPanel panel;
		ok = new JButton("OK");
		frame = new JFrame();
		panel = new JPanel();
		panel.setLayout(new GridLayout(0,2));
		JLabel coinName = new JLabel("Coin name:");
		JTextField nameData = new JTextField();
		JLabel countryName = new JLabel("Country name:");
		JTextField countryNameData = new JTextField();
		JLabel unitName = new JLabel("Units number:");
		JTextField unitNameData = new JTextField();
		JLabel rateName = new JLabel("Current rate:");
		JTextField rateNameData = new JTextField();
		JLabel codeName = new JLabel("Currency Code");
		JTextField codeNameData = new JTextField();
		panel.setSize(400, 150);
		panel.setLayout(new GridLayout(3,0));
		panel.add(coinName);
		panel.add(nameData);
		panel.add(codeName);
		panel.add(codeNameData);
		panel.add(countryName);
		panel.add(countryNameData);
		panel.add(unitName);
		panel.add(unitNameData);
		panel.add(rateName);
		panel.add(rateNameData);
		frame.setSize(400, 200);
		frame.setLocation(300, 200);
		frame.add(panel, BorderLayout.NORTH);
		frame.add(ok, BorderLayout.SOUTH);
		ok.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if ((nameData.getText().equals("")) || (codeNameData.getText().equals("")) || (countryNameData.getText().equals("")) || (unitNameData.getText().equals("")) || (rateNameData.getText().equals(""))){
					JInternalFrame errorFrame = new JInternalFrame("Error");
		    		errorFrame.setVisible(true);
		    		frame.setVisible(false);
					frame.dispose();
					JOptionPane.showMessageDialog(errorFrame, "One or more fields are empty");
					frame.setVisible(true);
				}
				else{
					newCoin[0] = nameData.getText();
					newCoin[1] = unitNameData.getText();
					newCoin[2] = codeNameData.getText();
					newCoin[3] = countryNameData.getText();
					newCoin[4] = rateNameData.getText();
					try {
						create(newCoin);
						getVector().add(new Currency(newCoin[0], newCoin[1],newCoin[2],newCoin[3],newCoin[4],String.valueOf(0)));
					} catch (ParserConfigurationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					frame.dispose();
					JInternalFrame errorFrame = new JInternalFrame("Success");
		    		errorFrame.setVisible(true);
		    		JOptionPane.showMessageDialog(errorFrame, "Coin added successfully");
				}
			}		
		});
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent event){
				frame.setVisible(false);
				frame.dispose();
			}});
	}
}
