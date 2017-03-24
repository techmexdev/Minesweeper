package game;

import javax.swing.*;

import java.util.Random;

public class MineSweeper
{
  public final int GRIDSIZE = 10;
  public enum CellStatus { EMPTY, EXPOSED, SEALED }
  public enum GameStatus { LOST, WON, INPROGRESS };
  public CellStatus[][] cellStatus = new CellStatus[GRIDSIZE][GRIDSIZE];
  public boolean[][] mines = new boolean[GRIDSIZE][GRIDSIZE];

  public MineSweeper()
  {
    for (int i = 0; i < GRIDSIZE; i++)
      for (int j = 0; j < GRIDSIZE; j++)
        cellStatus[i][j] = CellStatus.EMPTY;
  }

  public void exposeCell(int row, int column)
  {
    if(row >= GRIDSIZE || column >= GRIDSIZE) throw new IndexOutOfBoundsException();

    if(cellStatus[row][column] == CellStatus.EMPTY)
    {
      cellStatus[row][column] = CellStatus.EXPOSED;

      if(!isAdjacent(row, column) && !isMined(row, column))
        exposeNeighbors(row, column);
    }
  }

  public CellStatus getCellStatus(int row, int column)
  {
    return cellStatus[row][column];
  }


  public void exposeNeighbors(int row, int column)
  {
    for(int i = row - 1; i <= row + 1; i++)
      for(int j = column - 1; j <= column + 1; j++)
      {
        if(i >= 0 && i < GRIDSIZE && j >= 0 && i < GRIDSIZE && j < GRIDSIZE)
          exposeCell(i, j);
      }
  }

  public void toggleSeal(int row, int column)
  {
    switch(cellStatus[row][column])
    {
      case EMPTY:
        cellStatus[row][column] = CellStatus.SEALED;
        break;

      case SEALED:
        cellStatus[row][column] = CellStatus.EMPTY;
        break;
    }
  }

  public boolean isAdjacent(int row, int column)
  {
    if(!isMined(row, column))
    {
      for(int i = row - 1; i <= row + 1; i++)
        for(int j = column - 1; j <= column + 1; j++)
          if (i >= 0 && j >= 0 && i < GRIDSIZE && j < GRIDSIZE && isMined(i, j)) return true;
    }

    return false;
  }

  public boolean isMined(int row, int column)
  {
    return mines[row][column];
  }

  public void setMine(int row, int column)
  {
    mines[row][column] = true;
  }

  public GameStatus getCurrentGameStatus()
  {
    int sealedMines = 0;
    int sealedCells = 0;

    for(int i = 0; i < GRIDSIZE; ++i)
    {
      for(int j = 0; j < GRIDSIZE; ++j)
      {
        if(isMined(i, j) && getCellStatus(i, j) == CellStatus.EXPOSED)
          return GameStatus.LOST;
        else if(isMined(i, j) && isSealed(i, j))
          ++sealedMines;
        else if (isSealed(i, j))
          ++sealedCells;
      }
    }

    if(sealedMines == 10 && sealedCells == 0)
      return GameStatus.WON;

    return GameStatus.INPROGRESS;
  }

  public boolean isSealed(int row, int column)
  {
    return (cellStatus[row][column] == CellStatus.SEALED);
  }

  public void placeMines()
  {
    Random generator = new Random();

    int counter = 0;

    while(counter < 10)
    {
      int x = generator.nextInt(GRIDSIZE);
      int y = generator.nextInt(GRIDSIZE);
      if(!isMined(x, y))
      {
        setMine(x, y);
        ++counter;
      }
    }
  }

  public int calculateAdjacentMines(int row, int column)
  {
    int counter = 0;
    for(int i = row - 1; i <= row + 1; i++)
      for(int j = column - 1; j <= column + 1; j++)
      {
        if(i >= 0 && j >= 0 && i < GRIDSIZE && j < GRIDSIZE && isMined(i, j)) ++counter;
      }

    return counter;
  }
}
