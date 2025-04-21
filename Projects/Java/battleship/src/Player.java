package battleship;

import battleship.Info.Ship;

import java.util.Scanner;

import static battleship.FieldBuilder.*;
import static battleship.HitChecker.takeAShot;
import static battleship.Info.promptEnterKey;
import static battleship.ShipChecker.*;

public class Player {

    // Корабли игрока
    private final Ship[] ships = {
            new Ship("Enter the coordinates of the Aircraft Carrier (5 cells):", 5),
            new Ship("Enter the coordinates of the Battleship (4 cells):", 4),
            new Ship("Enter the coordinates of the Submarine (3 cells):", 3),
            new Ship("Enter the coordinates of the Cruiser (3 cells):", 3),
            new Ship("Enter the coordinates of the Destroyer (2 cells):", 2)
    };

    private final String[][] field = buildEmptyField();
    private final String[][] fogField = buildEmptyField();
    private final String playerName;
    private int numOfDownShips = 0;

    public Player(String playerName) {
        this.playerName = playerName;
    }

    public int getNumOfDownShips() {
        return numOfDownShips;
    }

    public void increaseNumOfDownShipsByOne() {
        numOfDownShips++;
    }

    /**
     * Размещение кораблей на поле.
     */
    public void placeTheShips(Scanner scanner) {
        System.out.printf("%s, place your ships on the game field\n", playerName);
        for (Ship ship : ships) {
            showField(field);
            System.out.println(ship.getMessage());
            boolean hasErrorOccurred;
            do {
                try {
                    int[][] coordinates = getCoordinatesFromLine(scanner.nextLine());
                    placeShip(field, coordinates[0], coordinates[1], ship);
                    hasErrorOccurred = false;
                } catch (RuntimeException e) {
                    System.out.println(e.getMessage());
                    hasErrorOccurred = true;
                }
            } while (hasErrorOccurred);
        }
        showField(field);
        promptEnterKey();
    }

    /**
     * Выполнение хода.
     */
    public void takeTurn(Player opponent, Scanner scanner) {
        showField(opponent.fogField);
        System.out.println("---------------------");
        showField(field);
        System.out.printf("%s, it's your turn:\n", playerName);

        try {
            takeAShot(getCoordinates(scanner.nextLine()), opponent.field, opponent.fogField, opponent);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }

        if (opponent.getNumOfDownShips() < 5) {
            promptEnterKey();
        }
    }
}
