package battleship;

import java.util.Objects;

import static battleship.Info.*;

public class ShipChecker {

    private static final int NUMBER_DIFF_FROM_CHAR_NUMBER_TO_INT = 48;

    /**
     * Преобразует строку координат в числовые значения.
     *
     * @param coordinate Координата в формате строки.
     * @return Массив с числовыми координатами.
     */
    public static int[] getCoordinates(String coordinate) {
        if (coordinate.length() < 2 || coordinate.length() > 3) {
            throw new WrongCoordinatesException();
        }

        char row = coordinate.charAt(0);
        int column = Integer.parseInt(coordinate.substring(1));

        if (row < 'A' || row > 'J' || column < 1 || column > 10) {
            throw new WrongCoordinatesException();
        }

        return new int[]{row - FieldBuilder.NUMBER_DIFF_FROM_ONE_TO_A, column};
    }

    /**
     * Проверяет, можно ли разместить корабль по заданным координатам.
     */
    public static void isAnythingAround(int[] coordinates, String[][] field) {
        int i = coordinates[0], j = coordinates[1];

        for (int di = -1; di <= 1; di++) {
            for (int dj = -1; dj <= 1; dj++) {
                int ni = i + di, nj = j + dj;
                if (ni >= 0 && ni < field.length && nj >= 0 && nj < field[0].length &&
                        Objects.equals(field[ni][nj], "O")) {
                    throw new TooCloseException();
                }
            }
        }
    }

    /**
     * Проверяет, правильно ли задана длина корабля.
     */
    public static void isLengthRight(int[] firstCoordinates, int[] secondCoordinates, Ship ship) {
        int length = ship.getLength();
        int actualLength;

        if (firstCoordinates[0] == secondCoordinates[0]) {
            actualLength = Math.abs(secondCoordinates[1] - firstCoordinates[1]) + 1;
        } else if (firstCoordinates[1] == secondCoordinates[1]) {
            actualLength = Math.abs(secondCoordinates[0] - firstCoordinates[0]) + 1;
        } else {
            throw new WrongLocationException();
        }

        if (actualLength != length) {
            throw new WrongLengthException();
        }
    }

    /**
     * Размещает корабль на поле.
     */
    public static void placeShip(String[][] field, int[] firstCoordinates, int[] secondCoordinates, Ship ship) {
        isLengthRight(firstCoordinates, secondCoordinates, ship);

        int[][] shipCoordinates = getShipCoordinates(firstCoordinates, secondCoordinates, ship);
        for (int[] coordinate : shipCoordinates) {
            isAnythingAround(coordinate, field);
        }

        for (int[] coordinate : shipCoordinates) {
            field[coordinate[0]][coordinate[1]] = "O";
        }
        ship.setCoordinates(shipCoordinates);
    }

    /**
     * Генерирует координаты клеток, занимаемых кораблем.
     */
    public static int[][] getShipCoordinates(int[] start, int[] end, Ship ship) {
        int length = ship.getLength();
        int[][] coordinates = new int[length][2];

        if (start[0] == end[0]) {
            for (int i = 0; i < length; i++) {
                coordinates[i][0] = start[0];
                coordinates[i][1] = start[1] + i * Integer.signum(end[1] - start[1]);
            }
        } else {
            for (int i = 0; i < length; i++) {
                coordinates[i][0] = start[0] + i * Integer.signum(end[0] - start[0]);
                coordinates[i][1] = start[1];
            }
        }

        return coordinates;
    }
}
