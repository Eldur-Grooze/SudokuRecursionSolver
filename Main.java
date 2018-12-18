import java.io.*;
import java.util.Scanner;
import java.util.Arrays;

public class Main {

    /**
     * BaseIterator to work with 2D array
     */
    public class BaseIterator{

        int[][] sudokuBoard;
        int index;
        protected BaseIterator(int[][] sudokuBoard){
            this.sudokuBoard = sudokuBoard;
            this.index = 0;
        }

        protected boolean hasNext() {
            return index < sudokuBoard.length;
        }

        public int next() {
            return 1;
        }
    }

    /**
     * Iterator for rows in 2D array
     */
    public class RowIterator extends BaseIterator {

        int row;

        public RowIterator(int[][] sudokuBoard){
            super(sudokuBoard);
        }

        public void setRow(int row)
        {
            this.row = row;
            this.index = 0;
        }

        @Override
        public int next() {
            return sudokuBoard[row][index++];
        }
    }

    /**
     * Iterator for Columns in 2D array
     */
    public class ColumnIterator extends BaseIterator{

        int column;

        public ColumnIterator(int[][] sudokuBoard){
            super(sudokuBoard);
        }

        public void setRow(int column)
        {
            this.column = column;
            this.index = 0;
        }

        @Override
        public int next() {
            return sudokuBoard[index++][column];
        }
    }

    /**
     * Iterator for small boxes in 2D array
     */
    public class BoxIterator extends BaseIterator{
        int row, column;

        public BoxIterator(int[][] sudokuBoard){
            super(sudokuBoard);
        }

        public void setBox(int row, int column)
        {
            this.row = row;
            this.column = column;
            index = 0;
        }

        @Override
        public int next() {
            int number = sudokuBoard[row + (index / 3)][column + (index % 3)];
            index++;
            return number;
        }

    }


    public static final int BIG_GRID_SIZE = 9;
    public static final int SMALL_GRID_SIZE = 3;
    /**
     * Print out our current board for Sudoku
     * @param sudokuBoard
     */
    public void printSudokuBoard(int sudokuBoard[][]){

        for(int x = 0; x < BIG_GRID_SIZE; x++){
            for(int y = 0; y < BIG_GRID_SIZE; y++){
                System.out.print(" " + sudokuBoard[x][y]);
            }
            System.out.print("\n");
        }
    }

    /**
     * DeBug Message
     * @param error
     */
    public void fatalError(String error){
        System.err.println("\nFailed. " + error + "\n");
        System.exit(1);
    }

    /**
     * Check if there is any duplicates in a given sequence
     * @param
     * @return true or falce
     */
    public boolean isSequenceValid(BaseIterator iterator){
        int x = 0;
        while (iterator.hasNext()){
            x |= (1 << (iterator.next()-1));
        }
        return x == (1<<BIG_GRID_SIZE) - 1;
    }

    /**
     * Check if there is Sudoku board is valid
     * @param sudokuBoard Sudoku board
     * @return  true of false
     */
    public boolean isValid(int[][] sudokuBoard){
        RowIterator rowIt = new RowIterator(sudokuBoard);
        for (int i = 0; i < BIG_GRID_SIZE; i++) {
            rowIt.setRow(i);
            if (!isSequenceValid(rowIt))
                return false;
        }

        ColumnIterator columnIt = new ColumnIterator(sudokuBoard);
        for (int i = 0; i < BIG_GRID_SIZE; i++) {
            rowIt.setRow(i);
            if (!isSequenceValid(columnIt))
                return false;
        }

        BoxIterator boxIt = new BoxIterator(sudokuBoard);
        for (int x = 0; x < BIG_GRID_SIZE; x+=SMALL_GRID_SIZE) {
            for (int y = 0; y < BIG_GRID_SIZE; y += SMALL_GRID_SIZE) {
                boxIt.setBox(x, y);
                if (!isSequenceValid(boxIt))
                    return false;
            }
        }
        return true;
    }

    /**
     * Check if our Sudoku board is complete
     * @param sudokuBoard our current board/state of it
     * @return
     */
    public boolean boardFull(int sudokuBoard[][]){
        for(int x = 0; x < BIG_GRID_SIZE; x++){
            for(int y = 0; y < BIG_GRID_SIZE; y++){
                if(sudokuBoard[x][y] == 0){
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Allig number to the grid's starting point
     * @param grid value to allign
     * @return starting point of the grid
     */
    public int allignToGrid(int grid){
        return (grid / SMALL_GRID_SIZE) * SMALL_GRID_SIZE;
    }

    /**
     * Check available values in an empty cell against values in row/column/box
     * @param sudokuBoard current Sudoku board/state of it
     * @param i horizontal coordinates of the cell
     * @param j vertical coordinates of the cell
     * @return array of possible values for the cell
     */
    public int[] possibleValues(int sudokuBoard[][],int i, int j) {
        int valuesArray[] = new int[10];
        for (int x = 0; x < BIG_GRID_SIZE; x++) {
            valuesArray[x] = 0;
        }

        //check for vertical values
        for (int x = 0; x < BIG_GRID_SIZE; x++) {
            if (sudokuBoard[x][j] != 0) {
                valuesArray[sudokuBoard[x][j]] = 1;
            }
        }

        // check for horizontal values
        for (int y = 0; y < BIG_GRID_SIZE; y++) {
            if (sudokuBoard[i][y] != 0) {
                valuesArray[sudokuBoard[i][y]] = 1;
            }
        }

        //check for the coordinates to find the current box we are in
        int k = allignToGrid(i);
        int l = allignToGrid(j);

        //check for values in the box
        for (int x = k; x < (k + SMALL_GRID_SIZE); x++) {
            for (int y = l; y < (l + SMALL_GRID_SIZE); y++) {
                if (sudokuBoard[i][y] != 0) {
                    valuesArray[sudokuBoard[x][y]] = 1;
                }
            }
        }

        //generates array of possible values for the cell
        for (int x = 1; x < BIG_GRID_SIZE + 1; x++) {
            if (valuesArray[x] == 0) {
                valuesArray[x] = x;
            } else {
                valuesArray[x] = 0;
            }
        }
        return valuesArray;
    }

    /**
     * Recursevly going through the whole board and trying to solve it.
     * @param sudokuBoard current Sudoku board/state of it
     * @throws Exception on success to exit
     */
    public boolean bruteForce(int sudokuBoard[][], int numberOfTries){
        int i,j;
        i = j = 0;
        if(numberOfTries == BIG_GRID_SIZE * SMALL_GRID_SIZE + SMALL_GRID_SIZE){
            fatalError("Invalid input data. Out of range. bruteForce - line 222");
        }
        if(boardFull(sudokuBoard) ){
            if(isValid(sudokuBoard)){
                return true;
            } else {
                fatalError("Sudoku board is not valid. bruteForce - line 250");
            }

        } else {
            for(int x = 0; x < BIG_GRID_SIZE; x++){
                for(int y = 0; y < BIG_GRID_SIZE; y++){
                    if(sudokuBoard[x][y] == 0){
                        i = x;
                        j = y;
                        break;
                    }
                }
            }
            int numbers[] = possibleValues(sudokuBoard, i, j);
            for(int x = 1; x < numbers.length; x++){
                if(numbers[x] != 0){
                    sudokuBoard[i][j] = numbers[x];
                    if(bruteForce(sudokuBoard, numberOfTries)){
                        return true;
                    };
                    numberOfTries++;
                }
            }
            //empty current cell for backtracking
            sudokuBoard[i][j] = 0;
        }
        return false;
    }

    /**
     * Check if a number is between 0 and 9
     * @param number passed number
     * @return
     */
    public boolean inRange(int number){
        boolean range = (number > 0 && number <= BIG_GRID_SIZE);
        return range;
    }

    /**
     * Handles user's input (file name to read, coordinates)
     * @return Sudoku board to solve
     * @throws FileNotFoundException
     */
    public int[][] readAll(String fileName) throws FileNotFoundException{
        int sudokuBoard[][] = new int[9][9];
        for (int[] row : sudokuBoard){
            Arrays.fill(row, 0);
        }

        //check if we have anything as file name, if line is empty - read from the console
        if(fileName.length() == 0){
            Scanner scannerConsole = new Scanner(System.in);
            while(scannerConsole.hasNextLine()){
                String[] line = scannerConsole.nextLine().trim().split(",");
                if(line[0].isEmpty()){
                    return sudokuBoard;
                }
                try {
                    if(inRange(Integer.parseInt(line[2]))) {
                        sudokuBoard[(Integer.parseInt(line[0])) - 1][(Integer.parseInt(line[1])) - 1] = Integer.parseInt(line[2]);
                    } else {
                        fatalError("Array Index out of Bounds or Number Format is incorrect. readAll - line 278");
                    }

                } catch (ArrayIndexOutOfBoundsException | NumberFormatException e){
                    fatalError("Array Index out of Bounds or Number Format is incorrect. readAll - line 278");
                }

            }
        } else {
            Scanner scannerFile = new Scanner(new BufferedReader(new FileReader(fileName)));
            while(scannerFile.hasNextLine()){
                String[] line = scannerFile.nextLine().trim().split(",");
                if(inRange(Integer.parseInt(line[2]))){
                    sudokuBoard[(Integer.parseInt(line[0])) - 1][(Integer.parseInt(line[1])) - 1] = Integer.parseInt(line[2]);
                } else {
                    fatalError("Array Index out of Bounds or Number Format is incorrect. readAll - line 297");
                }

            }
            scannerFile.close();
        }
        return sudokuBoard;
    }

    /**
     * Main
     * @param args
     */
    public static void main(String[] args) {
        Main sudoku = new Main();

        //our default Sudoku board
        int sudokuBoard[][] = new int[9][9];
        int numberOfTries = 0;
        try {
            if (args.length != 0) {
                sudokuBoard = sudoku.readAll(args[0]);
            } else {
                sudokuBoard = sudoku.readAll("");
            }

        } catch (FileNotFoundException | ArrayIndexOutOfBoundsException | NumberFormatException e){
            sudoku.fatalError("File is missing or corrupted. main - line 311");
        }

        //attempt to solve Sudoku
        try {
            if(sudoku.bruteForce(sudokuBoard,numberOfTries)){
                sudoku.printSudokuBoard(sudokuBoard);
            };
        } catch (Exception e) {
            sudoku.fatalError("Unable to find a solution. main - line 333");
        }


    }
}
