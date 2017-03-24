package game.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import game.MineSweeper;
import sun.font.FontFamily;

class MineSweeperGUI
{
    private final int FRAMESIZE = 600;
    private int gridSize;
    private JFrame frame;
    private JPanel panel;
    private JButton[][] buttonGrid;
    private JButton b;
    private MineSweeper mineSweeper;
    private GridBagConstraints constraints;

    public MineSweeperGUI()
    {
        initMineSweeper();
        constraints = new GridBagConstraints();
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.FIRST_LINE_START;
        gridSize = mineSweeper.GRIDSIZE;
        buttonGrid = new JButton[gridSize][gridSize];
        frame = new JFrame("Minesweeper");
        panel = new JPanel(new GridBagLayout());
        initButtonGrid();
        panel.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(FRAMESIZE, FRAMESIZE);
        frame.setLocation(300,300);
        frame.setResizable(false);
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }

    public void initMineSweeper()
    {
        mineSweeper = new MineSweeper();
        mineSweeper.placeMines();
    }

    public void exposeAllMinesOnLoss() {
        for(int i = 0; i < gridSize; i++)
            for(int j = 0; j < gridSize; j++) {
                if(mineSweeper.isMined(i, j)) {
                    buttonGrid[i][j].setBackground(Color.RED);
                    buttonGrid[i][j].setText("*");
                }
            }
        JOptionPane.showMessageDialog(null, "Loser :(");
        System.exit(0);
    }

    public void initButtonGrid()
    {
        for(int i = 0; i < gridSize; ++i)
        {
            for(int j = 0; j < gridSize; ++j)
            {
                b = new JButton(" ");
                b.setOpaque(true);
                b.setBackground(Color.GRAY);
                int buttonSize = (int) Math.floor(FRAMESIZE/gridSize);
                b.setPreferredSize(new Dimension(buttonSize, buttonSize));
                b.setVisible(true);
                buttonGrid[i][j] = b;
                final int row = i;
                final int column = j;

                b.addMouseListener(new MouseAdapter()
                {
                    @Override
                    public void mouseClicked(MouseEvent e)
                    {

                        if(SwingUtilities.isLeftMouseButton(e) && !mineSweeper.isSealed(row, column))
                        {
                            mineSweeper.exposeCell(row, column);
                            if (mineSweeper.isMined(row, column))
                            {
                                buttonGrid[row][column].setBackground(Color.RED);
                                buttonGrid[row][column].setText("*");
                            }
                            else
                            {
                                buttonGrid[row][column].setBackground(Color.BLACK);
                                for (int x = 0; x < gridSize; x++) {
                                    for (int y = 0; y < gridSize; y++) {
                                        if (mineSweeper.getCellStatus(x, y) == MineSweeper.CellStatus.EXPOSED) {
                                            buttonGrid[x][y].setBackground(Color.BLACK);
                                            if (mineSweeper.calculateAdjacentMines(x, y) == 0)
                                                buttonGrid[x][y].setText(" ");
                                            else {
                                                if(mineSweeper.calculateAdjacentMines(x, y) == 1)
                                                    buttonGrid[x][y].setForeground(Color.BLUE);
                                                if(mineSweeper.calculateAdjacentMines(x, y) == 2)
                                                    buttonGrid[x][y].setForeground(Color.GREEN.darker());
                                                if(mineSweeper.calculateAdjacentMines(x, y) >= 3)
                                                    buttonGrid[x][y].setForeground(Color.RED);
                                                buttonGrid[x][y].setText("" + mineSweeper.calculateAdjacentMines(x, y));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if(SwingUtilities.isRightMouseButton(e) && (mineSweeper.getCellStatus(row, column) != MineSweeper.CellStatus.EXPOSED))
                        {
                            mineSweeper.toggleSeal(row, column);
                            if(mineSweeper.isSealed(row, column)) {
                                buttonGrid[row][column].setBackground(Color.BLUE);
                                buttonGrid[row][column].setText("|>");
                            }
                            else {
                                buttonGrid[row][column].setBackground(Color.GRAY);
                                buttonGrid[row][column].setText("");
                            }
                        }
                        if(mineSweeper.getCurrentGameStatus() == MineSweeper.GameStatus.WON)
                        {
                            JOptionPane.showMessageDialog(null, "Winner! :D");
                            System.exit(0);
                        }
                        else if(mineSweeper.getCurrentGameStatus() == MineSweeper.GameStatus.LOST)
                        {
                            exposeAllMinesOnLoss();
                        }
                    }
                });
                constraints.gridx = i;
                constraints.gridy = j;
                panel.add(b, constraints);
            }
        }
    }

    public static void main(String[]args)
    {
        MineSweeperGUI gui = new MineSweeperGUI();
    }
}
