package corem.eldad.client;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class CurrencyHistoryGraph extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static ArrayList<Float> history = new ArrayList<Float>(8);
    private int padding = 25;
    private int labelPadding = 25;
    private Color lineColor = new Color(44, 102, 230, 180);
    private Color pointColor = new Color(100, 100, 100, 180);
    private Color gridColor = new Color(200, 200, 200, 200);
    private static final Stroke GRAPH_STROKE = new BasicStroke(2f);
    private int pointWidth = 4;
    private int numberYDivisions = 8;
	
	public CurrencyHistoryGraph(ArrayList<Float> _history){
		for (int i=0; i<8; i++)
			history = _history;
	}
	
	protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        double xScale = ((double) getWidth() - (2 * padding) - labelPadding) / (history.size() - 1);
        double yScale = ((double) getHeight() - 2 * padding - labelPadding) / (getMax() - getMin());
        List<Point> graphPoints = new ArrayList<>();
        for (int i = 0; i < history.size(); i++) {
            int x1 = (int) (i * xScale + padding + labelPadding);
            int y1 = (int) ((getMax() - history.get(i)) * yScale + padding);
            graphPoints.add(new Point(x1, y1));
        }
        g2.setColor(Color.WHITE);
        g2.fillRect(padding + labelPadding, padding, getWidth() - (2 * padding) - labelPadding, getHeight() - 2 * padding - labelPadding);
        g2.setColor(Color.BLACK);
        for (int i = 0; i < numberYDivisions + 1; i++) {
            int x0 = padding + labelPadding;
            int x1 = pointWidth + padding + labelPadding;
            int y0 = getHeight() - ((i * (getHeight() - padding * 2 - labelPadding)) / numberYDivisions + padding + labelPadding);
            int y1 = y0;
            if (history.size() > 0) {
                g2.setColor(gridColor);
                g2.drawLine(padding + labelPadding + 1 + pointWidth, y0, getWidth() - padding, y1);
                g2.setColor(Color.BLACK);
                String yLabel = ((int) ((getMin() + (getMax() - getMin()) * ((i * 1.0) / numberYDivisions)) * 100)) / 100.0 + "";
                FontMetrics metrics = g2.getFontMetrics();
                int labelWidth = metrics.stringWidth(yLabel);
                g2.drawString(yLabel, x0 - labelWidth - 5, y0 + (metrics.getHeight() / 2) - 3);
            }
            g2.drawLine(x0, y0, x1, y1);
            for (i = 0; i < history.size(); i++) {
                if (history.size() > 1) {
                    x0 = i * (getWidth() - padding * 2 - labelPadding) / (history.size() - 1) + padding + labelPadding;
                    x1 = x0;
                    y0 = getHeight() - padding - labelPadding;
                    y1 = y0 - pointWidth;
                    if ((i % ((int) ((history.size() / 20.0)) + 1)) == 0) {
                        g2.setColor(gridColor);
                        g2.drawLine(x0, getHeight() - padding - labelPadding - 1 - pointWidth, x1, padding);
                        g2.setColor(Color.BLACK);
                        String xLabel = i + "";
                        FontMetrics metrics = g2.getFontMetrics();
                        int labelWidth = metrics.stringWidth(xLabel);
                        g2.drawString(xLabel, x0 - labelWidth / 2, y0 + metrics.getHeight() + 3);
                    }
                    g2.drawLine(x0, y0, x1, y1);
                }
            }

            // create x and y axes 
            g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, padding + labelPadding, padding);
            g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, getWidth() - padding, getHeight() - padding - labelPadding);

            Stroke oldStroke = g2.getStroke();
            g2.setColor(lineColor);
            g2.setStroke(GRAPH_STROKE);
            for (i = 0; i < graphPoints.size() - 1; i++) {
                x1 = graphPoints.get(i).x;
                y1 = graphPoints.get(i).y;
                int x2 = graphPoints.get(i + 1).x;
                int y2 = graphPoints.get(i + 1).y;
                g2.drawLine(x1, y1, x2, y2);
            }

            g2.setStroke(oldStroke);
            g2.setColor(pointColor);
            for (i = 0; i < graphPoints.size(); i++) {
                int x = graphPoints.get(i).x - pointWidth / 2;
                int y = graphPoints.get(i).y - pointWidth / 2;
                int ovalW = pointWidth;
                int ovalH = pointWidth;
                g2.fillOval(x, y, ovalW, ovalH);
            }
        }
	}
	
	private float getMin() {
        float min = Float.MAX_VALUE;
        for (Float _history : history) {
            min = Math.min(min, _history);
        }
        return min;
    }

    private double getMax() {
        double max = Double.MIN_VALUE;
        for (Float _history : history) {
            max = Math.max(max, _history);
        }
        return max;
    }
    
    private static void createAndShowGui() {
    	CurrencyHistoryGraph mainPanel = new CurrencyHistoryGraph(history);
        mainPanel.setPreferredSize(new Dimension(400, 150));
        JFrame frame = new JFrame("Graph");
        frame.getContentPane().add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    public static void start() {
        SwingUtilities.invokeLater(new Runnable() {
           public void run() {
              createAndShowGui();
           }
        });
     }
}
