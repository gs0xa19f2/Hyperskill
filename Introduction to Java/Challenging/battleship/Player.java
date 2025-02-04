package battleship;

import battleship.Info.Ship;

import java.util.Scanner;

import static battleship.FieldBuilder.*;
import static battleship.HitChecker.takeAShot;
import static battleship.Info.allShipsDown;
import static battleship.ShipChecker.*;
import static battleship.Info.promptEnterKey;

public class Player {
    Ship airCraftCarrier = new Ship("Enter the coordinates of the Aircraft Carrier (5 cells):", 5);
    Ship battleship = new Ship("Enter the coordinates of the Battleship (4 cells):", 4);
    Ship submarine = new Ship("Enter the coordinates of the Submarine (3 cells):", 3);
    Ship cruiser = new Ship("Enter the coordinates of the Cruiser (3 cells):", 3);
    Ship destroyer = new Ship("Enter the coordinates of the Destroyer (2 cells):", 2);
    Ship[] ships = new Ship[]{airCraftCarrier, battleship, submarine, cruiser, destroyer};
    String[][] field = buildEmptyField();
    String[][] fogField = buildEmptyField();
    String playerName;
    private int NumOfDownShips = 0;

    public int getNumOfDownShips() {
        return NumOfDownShips;
    }

    public void increaseNumOfDownShipsByOne() {
        NumOfDownShips++;
    }

    Player(String playerName) {
        this.playerName = playerName;
    }

    void placeTheShips(Scanner scanner) {
        System.out.printf("%s, place your ships on the game field\n", playerName);
        boolean hasErrorOccurred;
        for (Ship ship : ships) {
            showField(field);
            System.out.println(ship.getMessage());
            do {
                try {
                    int[][] coordinates = getCoordinatesFromLine(scanner.nextLine());
                    int[] firstCoordinates = coordinates[0];
                    int[] secondCoordinates = coordinates[1];
                    placeShip(field, firstCoordinates, secondCoordinates, ship);
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

    void takeTurn(Player opponent, Scanner scanner) {
        showField(opponent.fogField);
        System.out.print("---------------------\n");
        showField(this.field);
        System.out.printf("%s, it's your turn:\n", this.playerName);
        try {
            takeAShot(getCoordinates(scanner.nextLine()), opponent.field, opponent.fogField, opponent);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        } finally {
            if (opponent.getNumOfDownShips() != 5) {
                promptEnterKey();
            } else {
                System.out.println(allShipsDown);
            }
        }
    }
}
