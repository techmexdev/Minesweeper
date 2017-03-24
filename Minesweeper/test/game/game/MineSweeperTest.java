package game;

import javafx.scene.control.Cell;
import org.junit.*;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import game.MineSweeper.CellStatus;

public class MineSweeperTest
{

    MineSweeper mineSweeper;

    @Before
    public void setup() {
        mineSweeper = new MineSweeper();
    }

    @Test
    public void Canary()
    {
        assertTrue(true);
    }

    @Test
    public void exposeCell()
    {
        mineSweeper.exposeCell(1, 2);
        assertEquals(CellStatus.EXPOSED, mineSweeper.getCellStatus(1, 2));
    }

    @Test
    public void checkExposedCellRemainsExposedOnCallToExposeCell()
    {
        mineSweeper.exposeCell(1,2);
        mineSweeper.exposeCell(1,2);
        assertEquals(MineSweeper.CellStatus.EXPOSED, mineSweeper.getCellStatus(1,2));
    }

    @Test
    public void exposeCellRowOutOfBounds()
    {
        try {
            mineSweeper.exposeCell(11, 1);
            fail("Expected exception for row out of bounds");
        } catch (IndexOutOfBoundsException ex) {
            //
        }
    }

    @Test
    public void exposeCellColumnOutOfBounds()
    {
        try {
            mineSweeper.exposeCell(1, 11);
            fail("Expected exception for column out of bounds");
        } catch (IndexOutOfBoundsException ex)
        {
            //
        }
    }

    class MineSweeperWithExposeNeighborsStubbed extends MineSweeper {
        public boolean exposeNeighborsCalled;

        public void exposeNeighbors(int row, int column) {
            exposeNeighborsCalled = true;
        }
    }

    @Test
    public void exposeCellCallsExposeNeighbor() {
        MineSweeperWithExposeNeighborsStubbed mineSweeper = new MineSweeperWithExposeNeighborsStubbed();
        mineSweeper.exposeCell(1, 4);

        assertTrue(mineSweeper.exposeNeighborsCalled);
    }

    @Test
    public void exposeCellDoesNotCallsExposeNeighborIfCellAlreadyExposed() {
        MineSweeperWithExposeNeighborsStubbed mineSweeper = new MineSweeperWithExposeNeighborsStubbed();
        mineSweeper.exposeCell(1, 4);
        mineSweeper.exposeNeighborsCalled = false;

        mineSweeper.exposeCell(1, 4);

        assertFalse(mineSweeper.exposeNeighborsCalled);
    }

    class MineSweeperWithExposeCellStubbed extends MineSweeper {
        public List<Integer> rows = new ArrayList<>();
        public List<Integer> columns = new ArrayList<>();

        public void exposeCell(int row, int column) {
            rows.add(row);
            columns.add(column);
        }
    }

    @Test
    public void exposeNeighborsCallsExposeCellOnNeighborsAndSelf() {
        MineSweeperWithExposeCellStubbed mineSweeper = new MineSweeperWithExposeCellStubbed();

        mineSweeper.exposeNeighbors(3, 4);

        assertEquals(Arrays.asList(2, 2, 2, 3, 3, 3, 4, 4, 4), mineSweeper.rows);
        assertEquals(Arrays.asList(3, 4, 5, 3, 4, 5, 3, 4, 5), mineSweeper.columns);
    }

    @Test
    public void exposeNeighborsOnTopLeftCellCallsExposeCellOnNeighbors()
    {
        MineSweeperWithExposeCellStubbed mineSweeper = new MineSweeperWithExposeCellStubbed();

        mineSweeper.exposeNeighbors(0, 0);

        assertEquals(Arrays.asList(0, 0, 1, 1), mineSweeper.rows);
        assertEquals(Arrays.asList(0, 1, 0, 1), mineSweeper.columns);
    }

    @Test
    public void exposeNeighborsOnTopLeftCellCallsExposeCellOnNeighborsTopRight()
    {
        MineSweeperWithExposeCellStubbed mineSweeper = new MineSweeperWithExposeCellStubbed();

        mineSweeper.exposeNeighbors(0, 9);

        assertEquals(Arrays.asList(0, 0, 1, 1), mineSweeper.rows);
        assertEquals(Arrays.asList(8, 9, 8, 9), mineSweeper.columns);
    }

    @Test
    public void exposeNeighborsOnTopLeftCellCallsExposeCellOnNeighborsBottomLeft()
    {
        MineSweeperWithExposeCellStubbed mineSweeper = new MineSweeperWithExposeCellStubbed();

        mineSweeper.exposeNeighbors(0, 9);

        assertEquals(Arrays.asList(0, 0, 1, 1), mineSweeper.rows);
        assertEquals(Arrays.asList(8, 9, 8, 9), mineSweeper.columns);
    }

    @Test
    public void exposeNeighborsOnTopLeftCellCallsExposeCellOnNeighborsBottomRight()
    {
        MineSweeperWithExposeCellStubbed mineSweeper = new MineSweeperWithExposeCellStubbed();

        mineSweeper.exposeNeighbors(9, 9);

        assertEquals(Arrays.asList(8, 8, 9, 9), mineSweeper.rows);
        assertEquals(Arrays.asList(8, 9, 8, 9), mineSweeper.columns);
    }

    @Test
    public void sealCell()
    {
        mineSweeper.toggleSeal(4, 5);
        assertTrue(mineSweeper.isSealed(4, 5));
    }

    @Test
    public void toggleSeal()
    {
        mineSweeper.toggleSeal(4, 5);
        mineSweeper.toggleSeal(4, 5);
        assertEquals(CellStatus.EMPTY, mineSweeper.getCellStatus(4, 5));
    }

    @Test
    public void exposingASealedCellRemainsSealed()
    {
        mineSweeper.toggleSeal(3, 6);
        mineSweeper.exposeCell(3, 6);

        assertEquals(CellStatus.SEALED, mineSweeper.getCellStatus(3, 6));
    }

    @Test
    public void sealingAnExposedCellRemainsExposed()
    {
        mineSweeper.exposeCell(3, 6);
        mineSweeper.toggleSeal(3, 6);
        assertEquals(CellStatus.EXPOSED, mineSweeper.getCellStatus(3, 6));
    }

    @Test
    public void unsealingAMinedSealedCell() {
        mineSweeper.setMine(1, 2);
        mineSweeper.toggleSeal(1, 2);
        mineSweeper.toggleSeal(1, 2);
        assertEquals(CellStatus.EMPTY, mineSweeper.getCellStatus(1, 2));
        assertTrue(mineSweeper.isMined(1, 2));
    }

    class MineSweeperWithIsAdjacentStubbed extends MineSweeperWithExposeNeighborsStubbed
    {
        public boolean isAdjacent(int row, int column) {
            return true;
        }
    }

    @Test
    public void exposingAnAdjacentCellDoesNotCallExposeNeighbors()
    {
        MineSweeperWithIsAdjacentStubbed mineSweeper = new MineSweeperWithIsAdjacentStubbed();

        mineSweeper.exposeCell(4, 5);
        assertFalse(mineSweeper.exposeNeighborsCalled);
    }

    class MineSweeperWithIsMinedStubbed extends MineSweeperWithExposeNeighborsStubbed
    {
        public boolean isMined(int row, int column) {
            return true;
        }
    }

    @Test
    public void exposingAMineDoesNotCallExposeNeighbors()
    {
        MineSweeperWithIsMinedStubbed mineSweeper = new MineSweeperWithIsMinedStubbed();

        mineSweeper.exposeCell(4, 5);
        assertFalse(mineSweeper.exposeNeighborsCalled);
    }

    @Test
    public void noMineAtCell()
    {
        assertFalse(mineSweeper.isMined(1, 3));
    }

    @Test
    public void mineAtALocation()
    {
        mineSweeper.setMine(4, 5);
        assertTrue(mineSweeper.isMined(4, 5));
    }

    @Test
    public void notAnAdjacentMine()
    {
        assertFalse(mineSweeper.isAdjacent(3, 5));
    }

    @Test
    public void isAnAdjacentMine()
    {
        mineSweeper.setMine(2, 3);
        assertTrue(mineSweeper.isAdjacent(2, 2));
    }

    @Test
    public void cellIsAdjacentAfterSetMine()
    {
        mineSweeper.setMine(1,1);
        assertTrue(mineSweeper.isAdjacent(0, 0));
    }

    @Test
    public void minedCellIsNotAdjacent()
    {
        mineSweeper.setMine(1, 1);
        assertFalse(mineSweeper.isAdjacent(1, 1));
    }

    @Test
    public void gameIsInProgress()
    {
        assertEquals(MineSweeper.GameStatus.INPROGRESS, mineSweeper.getCurrentGameStatus());
    }

    @Test
    public void sealingAllMinesAndExposingAllEmptyCellWinsGame()
    {
        for(int i = 0; i < mineSweeper.GRIDSIZE; i++)
            mineSweeper.setMine(i, i);

        for(int i = 0; i < mineSweeper.GRIDSIZE; i++)
            mineSweeper.toggleSeal(i, i);

        for(int x = 0; x < mineSweeper.GRIDSIZE; ++x)
        {
            for(int y = 0; x < mineSweeper.GRIDSIZE; ++x)
            {
                if(!mineSweeper.isMined(x,y))
                    mineSweeper.exposeCell(x, y);
            }
        }

        assertEquals(MineSweeper.GameStatus.WON, mineSweeper.getCurrentGameStatus());
    }

    @Test
    public void sealingAllMinesAndSomeEmptyCellsStaysInProgress()
    {
        for(int i = 0; i < mineSweeper.GRIDSIZE; i++)
            mineSweeper.setMine(i, i);

        for(int i = 0; i < mineSweeper.GRIDSIZE; i++)
            mineSweeper.toggleSeal(i, i);

        mineSweeper.toggleSeal(1, 2);
        mineSweeper.toggleSeal(2, 3);


        for(int i = 0; i < mineSweeper.GRIDSIZE; ++i)
        {
            for(int j = 0; j < mineSweeper.GRIDSIZE; ++j)
            {
                if(!mineSweeper.isMined(i,j))
                    mineSweeper.exposeCell(i, j);
            }
        }

        assertEquals(MineSweeper.GameStatus.INPROGRESS, mineSweeper.getCurrentGameStatus());
    }

    @Test
    public void exposeMinedCellLosesGame()
    {
        mineSweeper.setMine(1, 2);
        mineSweeper.exposeCell(1, 2);

        assertEquals(MineSweeper.GameStatus.LOST, mineSweeper.getCurrentGameStatus());
    }

    @Test
    public void placeMinesPlacesTenMines()
    {
        mineSweeper.placeMines();

        int counter = 0;

        for(int i = 0; i < mineSweeper.GRIDSIZE; ++ i)
            for(int j = 0; j < mineSweeper.GRIDSIZE; ++j)
                if(mineSweeper.isMined(i, j))
                    ++counter;

        assertEquals(10, counter);
    }

    @Test
    public void placeMinesHasAtLeastOneDifference()
    {
        MineSweeper mineSweeper2 = new MineSweeper();

        mineSweeper.placeMines();
        mineSweeper2.placeMines();

        boolean difference = false;

        for(int i = 0; i < mineSweeper.GRIDSIZE; ++ i)
        {
            if(!difference)
            {
                for(int j = 0; j < mineSweeper.GRIDSIZE; ++j)
                    if(mineSweeper.isMined(i, j) != mineSweeper2.isMined(i, j))
                    {
                        difference = true;
                        break;
                    }
            }
        }
        assertTrue(difference);
    }

    @Test
    public void testToQuitCoverageForEnumCellStatus() {
        CellStatus.values();
        CellStatus.valueOf("EMPTY");
    }
}
