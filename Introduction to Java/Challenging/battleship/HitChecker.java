package battleship;

import static battleship.Info.*;
import static battleship.ShipChecker.*;

import java.util.Arrays;
import java.util.Objects;

public class HitChecker {
    public static void recordTheShot(int[] coordinates, Player player) {
        for (Ship ship : player.ships) {
            for (int[] shipCoordinates : ship.getCoordinates()) {
                if (ship.isShipDown()) {
                    break;
                }
                if (Arrays.equals(coordinates, shipCoordinates)) {
                    ship.increaseNumOfHitCoordinatesByOne();
                    checkShip(ship);
                    if (!ship.isShipDown()) {
                        System.out.println(hit);
                    } else {
                        player.increaseNumOfDownShipsByOne();
                        if (player.getNumOfDownShips() != 5) {
                            System.out.println(shipIsDown);
                        }
                    }
                }
            }
        }
    }

    public static void takeAShot(int[] coordinates, String[][] field, String[][] fogField, Player player) {
        if (Objects.equals(field[coordinates[0]][coordinates[1]], "O")) {
            field[coordinates[0]][coordinates[1]] = "X";
            fogField[coordinates[0]][coordinates[1]] = "X";
            recordTheShot(coordinates, player);
        } else if (Objects.equals(field[coordinates[0]][coordinates[1]], "X")) {
            System.out.println(hit);
        } else {
            field[coordinates[0]][coordinates[1]] = "M";
            fogField[coordinates[0]][coordinates[1]] = "M";
            System.out.println(miss);
        }
    }

}
