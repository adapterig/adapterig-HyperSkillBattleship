package battleship;

import java.io.IOException;
import java.util.*;

class Main {
    public static void main(String[] args) {

        Battlefield battlefield1 = new Battlefield("Player 1");
        Battlefield battlefield2 = new Battlefield("Player 2");

        GamePlay.placementOfShips(battlefield1);
        GamePlay.placementOfShips(battlefield2);

        GamePlay.playingGame(battlefield1, battlefield2);
    }
}

class GamePlay extends GameRules {
    static Scanner scanner = new Scanner(System.in);

    public static void placementOfShips(Battlefield myBattlefield) {
        myBattlefield.createBattlefield();
        System.out.println(myBattlefield.PLAYER_NAME + ", place your ships on the game field\n");

        myBattlefield.printBattlefield();

        while (myBattlefield.getUnplacedShips() != 0) {

            System.out.printf("\nEnter the coordinates of the %s (%d cells):\n\n",
                    myBattlefield.getShip().getNAME(), myBattlefield.getShip().getSIZE());

            while (true) {
                try {
                    myBattlefield.getShip().setDeckCoordinates(scanner.nextLine().toUpperCase(Locale.ROOT).split(" "));

                    CheckShipPlacementRules(myBattlefield);

                    myBattlefield.placeShipOnBattlefield();
                    myBattlefield.printBattlefield();
                    break;
                } catch (Exception e) {
                    System.out.println(e.getMessage().contains("Error") ? "\n" + e.getMessage()
                            : "\n" + new Exception(String.format("Error! %s. Try again:" + "\n",
                            e.getLocalizedMessage())).getMessage());
                }
            }
        }
        System.out.println("Press Enter and pass the move to another player");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void playingGame(Battlefield battlefield1, Battlefield battlefield2) {
        //System.out.println("\nThe game starts!\n");


        while (true) {
            makeMove(battlefield1, battlefield2);
            makeMove(battlefield2, battlefield1);

        }
    }

    private static void makeMove(Battlefield battlefield1, Battlefield battlefield2) {
        battlefield1.printFoggedBattlefield();
        System.out.println("---------------------");
        battlefield1.printBattlefield();
        System.out.println(battlefield1.PLAYER_NAME + "\n, it's your turn:\n");
        shooting(battlefield1, battlefield2);
        System.out.println("Press Enter and pass the move to another player");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void shooting(Battlefield battlefieldFrom, Battlefield battlefieldTo) {
        while (true) {
            String[] coordinates = scanner.nextLine().toUpperCase(Locale.ROOT).split(" ");
            try {
                int row = coordinates[0].charAt(0) - '@';
                int col = Integer.parseInt(coordinates[0].substring(1));
                if (battlefieldTo.getBATTLEFIELD()[row][col] == '~' || battlefieldTo.getBATTLEFIELD()[row][col] == 'M') {
                    battlefieldTo.getBATTLEFIELD()[row][col] = 'M';
                    battlefieldFrom.getFOGGED_BATTLEFIELD()[row][col] = 'M';
                    //battlefield1.printFoggedBattlefield();
                    System.out.println("\nYou missed!\n");
                    return;

                } else if (battlefieldTo.getBATTLEFIELD()[row][col] == 'O' || battlefieldTo.getBATTLEFIELD()[row][col] == 'X') {
                    battlefieldTo.getBATTLEFIELD()[row][col] = 'X';
                    battlefieldFrom.getFOGGED_BATTLEFIELD()[row][col] = 'X';
                    //battlefield1.printFoggedBattlefield();
                    battlefieldTo.checkIsAllShipsSank();
                    if (isShipSank(row, col, battlefieldTo)) {
                        System.out.println("\nYou sank a ship!\n");
                    } else {
                        System.out.println("\nYou hit a ship!\n");
                    }
                    return;
                }
            } catch (ArrayIndexOutOfBoundsException exception) {
                System.out.println("Error! You entered the wrong coordinates! Try again:");
            }
        }

    }

    public static boolean isShipSank(int row, int col, Battlefield battlefield) {
        //looking for a ship
        Ship shipToCheck = null;
        int[][] deckCoordinates;
        Ship[] ships = battlefield.getSHIPS();
        for (Ship ship : ships) {
            deckCoordinates = ship.getDeckCoordinates();
            for (int i = 0; i < deckCoordinates[0].length; i++) {
                System.out.println(battlefield.PLAYER_NAME);
                if (row == deckCoordinates[0][i] && col == deckCoordinates[1][i]) {
                    shipToCheck = ship;
                }
            }
        }
        //checking ship is sank or not

        assert shipToCheck != null;
        deckCoordinates = shipToCheck.getDeckCoordinates();
        for (int i = 0; i < deckCoordinates[0].length; i++) {
            if (battlefield.getBATTLEFIELD()[deckCoordinates[0][i]][deckCoordinates[1][i]] != 'X') {
                return false;
            }
        }

        shipToCheck.sank();
        return true;
    }
}

class Battlefield {
    private final char[][] BATTLEFIELD = new char[12][12];
    private final char[][] FOGGED_BATTLEFIELD = new char[12][12];
    private final Ship[] SHIPS;
    private int unplacedShips;
    public final String PLAYER_NAME;

    public Battlefield(String playerName) {
        Ship[] ships = {new Ship("Destroyer", 2),
                new Ship("Cruiser", 3),
                new Ship("Submarine", 3),
                new Ship("Battleship", 4),
                new Ship("Aircraft Carrier", 5)};
        SHIPS = ships;
        this.unplacedShips = ships.length;
        this.PLAYER_NAME = playerName;
    }

    public char[][] getBATTLEFIELD() {
        return BATTLEFIELD;
    }

    public char[][] getFOGGED_BATTLEFIELD() {
        return FOGGED_BATTLEFIELD;
    }

    public Ship getShip() {
        return this.SHIPS[this.unplacedShips - 1];
    }

    public int getUnplacedShips() {
        return this.unplacedShips;
    }

    public void createBattlefield() {
        for (int i = 1; i < BATTLEFIELD.length - 1; i++) {
            for (int j = 1; j < BATTLEFIELD.length - 1; j++) {
                BATTLEFIELD[i][j] = '~';
                FOGGED_BATTLEFIELD[i][j] = '~';
            }
        }
    }

    public void checkIsAllShipsSank() {
        for (int i = 1; i < BATTLEFIELD.length - 1; i++) {
            for (int j = 1; j < BATTLEFIELD.length - 1; j++) {
                if (BATTLEFIELD[i][j] == 'O') {
                    return;
                }
            }
        }
        System.out.println("\nYou sank the last ship. You won. Congratulations!...");
        System.exit(0);
    }

    public void printBattlefield() {
        System.out.println("\n  1 2 3 4 5 6 7 8 9 10");
        for (int i = 1; i < BATTLEFIELD.length - 1; i++) {
            System.out.print((char) ('@' + i) + " ");
            for (int j = 1; j < BATTLEFIELD.length - 1; j++) {
                System.out.print(j == BATTLEFIELD.length - 2 ? BATTLEFIELD[i][j] + "\n" : BATTLEFIELD[i][j] + " ");
            }
        }
    }

    public void printFoggedBattlefield() {
        System.out.println("\n  1 2 3 4 5 6 7 8 9 10");
        for (int i = 1; i < FOGGED_BATTLEFIELD.length - 1; i++) {
            System.out.print((char) ('@' + i) + " ");
            for (int j = 1; j < FOGGED_BATTLEFIELD.length - 1; j++) {
                System.out.print(j == FOGGED_BATTLEFIELD.length - 2 ? FOGGED_BATTLEFIELD[i][j] + "\n" : FOGGED_BATTLEFIELD[i][j] + " ");
            }
        }
    }

    public void placeShipOnBattlefield() {
        for (int i = 0; i < getShip().getDeckCoordinates()[0].length; i++) {
            BATTLEFIELD[getShip().getDeckCoordinates()[0][i]][getShip().getDeckCoordinates()[1][i]] = 'O';
        }
        this.unplacedShips--;
    }

    public Ship[] getSHIPS() {
        return SHIPS;
    }
}

class Ship {
    private final String NAME;
    private final int SIZE;
    private int[][] deckCoordinates;
    private boolean isSank;

    public Ship(String name, int size) {
        NAME = name;
        SIZE = size;
        isSank = false;
    }

    public String getNAME() {
        return NAME;
    }

    public int getSIZE() {
        return SIZE;
    }

    public void setDeckCoordinates(String[] coordinates) {

        int[] row = {coordinates[0].charAt(0) - '@', coordinates[1].charAt(0) - '@'};
        int[] col = {Integer.parseInt(coordinates[0].substring(1)), Integer.parseInt(coordinates[1].substring(1))};

        this.deckCoordinates = new int[2][Math.abs((row[0] + col[0]) - (row[1] + col[1])) + 1];

        for (int i = 0; i < this.deckCoordinates[0].length; i++) {
            this.deckCoordinates[0][i] = row[0] == row[1] ? Math.min(row[0], row[1]) : Math.min(row[0], row[1]) + i;
            this.deckCoordinates[1][i] = row[0] == row[1] ? Math.min(col[0], col[1]) + i : Math.min(col[0], col[1]);
        }
    }

    public int[][] getDeckCoordinates() {
        return this.deckCoordinates;
    }

    public void sank() {
        isSank = true;

    }
}

class GameRules {
    public static void CheckShipPlacementRules(Battlefield myBattlefield) throws Exception {
        int[][] d = myBattlefield.getShip().getDeckCoordinates();
        if (d[0][0] != d[0][d[0].length - 1] && d[1][0] != d[1][d[0].length - 1]
                || d[0][d[0].length - 1] > 10 || d[1][d[0].length - 1] > 10) {
            throw new Exception("Error! Wrong ship location! Try again:\n");
        } else if (myBattlefield.getShip().getSIZE() != d[0].length) {
            throw new Exception(String.format("Error! Wrong length of the %s! Try again:\n", myBattlefield.getShip().getNAME()));
        } else {
            for (int i = 0; i < d[0].length; i++) {
                for (int j = d[0][i] - 1; j <= d[0][i] + 1; j++) {
                    for (int l = d[1][i] - 1; l <= d[1][i] + 1; l++) {
                        if (myBattlefield.getBATTLEFIELD()[j][l] == 'O') {
                            throw new Exception("Error! You placed it too close to another one. Try again:\n");
                        }
                    }
                }
            }
        }
    }
}