package battleship;

import java.io.IOException;

public class Info {

    // Сообщения для различных игровых событий
    public static final String hit = "You hit a ship!";
    public static final String miss = "You missed!";
    public static final String shipIsDown = "You sank a ship!";
    public static final String allShipsDown = "You sank the last ship. You won. Congratulations!";

    /**
     * Класс для представления корабля.
     */
    public static class Ship {
        private final String message;
        private final int length;
        private int[][] coordinates;
        private boolean isShipDown = false;
        private int numOfHitCoordinates = 0;

        public Ship(String message, int length) {
            this.message = message;
            this.length = length;
        }

        public String getMessage() {
            return message;
        }

        public int getLength() {
            return length;
        }

        public int[][] getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(int[][] coordinates) {
            this.coordinates = coordinates;
        }

        public boolean isShipDown() {
            return isShipDown;
        }

        public void setShipDown() {
            isShipDown = true;
        }

        public int getNumOfHitCoordinates() {
            return numOfHitCoordinates;
        }

        public void increaseNumOfHitCoordinatesByOne() {
            numOfHitCoordinates++;
        }
    }

    // Исключения для обработки ошибок
    public static class WrongLengthException extends RuntimeException {
        public WrongLengthException() {
            super("Error! Wrong length of the Submarine! Try again:");
        }
    }

    public static class WrongLocationException extends RuntimeException {
        public WrongLocationException() {
            super("Error! Wrong ship location! Try again:");
        }
    }

    public static class TooCloseException extends RuntimeException {
        public TooCloseException() {
            super("Error! You placed it too close to another one. Try again:");
        }
    }

    public static class WrongCoordinatesException extends RuntimeException {
        public WrongCoordinatesException() {
            super("Error! You entered the wrong coordinates! Try again:");
        }
    }

    /**
     * Запрашивает нажатие Enter для передачи хода другому игроку.
     */
    public static void promptEnterKey() {
        System.out.println("Press Enter and pass the move to another player");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.print("\033[H\033[2J");
            System.out.flush();
        }
    }
}
