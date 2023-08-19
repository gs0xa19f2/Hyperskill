package battleship;

import java.util.Objects;
import java.lang.Math;

import static battleship.Info.*;

public class ShipChecker {

    public static final int NUMBER_DIFF_FROM_CHAR_NUMBER_TO_INT = 48;

    public static int[] getCoordinates(String coordinate) {
        if (coordinate.length() < 2 || 3 < coordinate.length()) {
            throw new WrongCoordinatesException();
        }
        if (!('A' <= coordinate.charAt(0) && coordinate.charAt(0) <= 'J') ||
                !('1' <= coordinate.charAt(1) && coordinate.charAt(1) <= '9')) {
            throw new WrongCoordinatesException();
        }
        int[] coordinates = new int[2];
        coordinates[0] = coordinate.charAt(0) - FieldBuilder.NUMBER_DIFF_FROM_ONE_TO_A;
        if (coordinate.length() == 3) {
            if (coordinate.charAt(2) != '0' || coordinate.charAt(1) != '1') {
                throw new WrongCoordinatesException();
            } else {
                coordinates[1] = 10;
            }
        } else {
            coordinates[1] = coordinate.charAt(1) - NUMBER_DIFF_FROM_CHAR_NUMBER_TO_INT;
        }
        return coordinates;
    }

    public static int[][] getCoordinatesFromLine(String line) {
        String[] stringCoordinates = line.split(" ");
        int[][] coordinates = new int[2][2];
        coordinates[0] = getCoordinates(stringCoordinates[0]);
        coordinates[1] = getCoordinates(stringCoordinates[1]);
        return coordinates;
    }

    public static void isAnythingAround(int[] coordinates, String[][] field) {
        int i = coordinates[0];
        int j = coordinates[1];
        for (int i1 = -1; i1 <= 1; i1++) {
            for (int j1 = -1; j1 <= 1; j1++) {
                if (!(i1 == 0 && j1 == 0 || i + i1 > 10 || j + j1 > 10) &&
                        Objects.equals(field[i + i1][j + j1], "O")) {
                    throw new TooCloseException();
                }
            }
        }
    }

    public static void isLengthRight(int[] firstCoordinates, int[] secondCoordinates, Ship ship) {
        if (firstCoordinates[0] == secondCoordinates[0]) {
            if (!(ship.getLength() == Math.abs(secondCoordinates[1] - firstCoordinates[1]) + 1)) {
                throw new WrongLengthException();
            }
        } else if (firstCoordinates[1] == secondCoordinates[1]) {
            if (!(ship.getLength() == Math.abs(secondCoordinates[0] - firstCoordinates[0]) + 1)) {
                throw new WrongLengthException();
            }
        } else {
            throw new WrongLocationException();
        }
    }

    public static int[][] getShipCoordinates(int[] firstCoordinate, int[] secondCoordinate, Ship ship) {
        int[][] shipCoordinates = new int[ship.getLength()][2];
        int i1 = firstCoordinate[0];
        int j1 = firstCoordinate[1];
        int i2 = secondCoordinate[0];
        int j2 = secondCoordinate[1];
        isLengthRight(firstCoordinate, secondCoordinate, ship);
        if (i1 == i2) {
            if (j1 < j2) {
                for (int i = 0, j = j1; i < ship.getLength() && j <= j2; i++, j++) {
                    shipCoordinates[i][0] = i1;
                    shipCoordinates[i][1] = j;
                }
            } else {
                for (int i = 0, j = j2; i < ship.getLength() && j <= j1; i++, j++) {
                    shipCoordinates[i][0] = i1;
                    shipCoordinates[i][1] = j;
                }
            }
        } else {
            if (i1 < i2) {
                for (int i = i1, l = 0; i <= i2 && l < ship.getLength(); i++, l++) {
                    shipCoordinates[l][0] = i;
                    shipCoordinates[l][1] = j1;
                }
            } else {
                for (int i = i2, l = 0; i <= i1 && l < ship.getLength(); i++, l++) {
                    shipCoordinates[l][0] = i;
                    shipCoordinates[l][1] = j1;
                }
            }
        }
        ship.setCoordinates(shipCoordinates);
        return shipCoordinates;
    }

    public static void checkShip(Ship ship) {
        if (ship.getNumOfHitCoordinates() == ship.getLength()) {
            ship.setShipDown();
        }
    }

    public static void placeShip(String[][] field, int[] firstCoordinates, int[] secondCoordinates, Ship ship) {
        int[][] shipCoordinates = getShipCoordinates(firstCoordinates, secondCoordinates, ship);
        for (int[] coordinate : shipCoordinates) {
            isAnythingAround(coordinate, field);
        }
        for (int[] coordinate : shipCoordinates) {
            field[coordinate[0]][coordinate[1]] = "O";
        }
    }
}
