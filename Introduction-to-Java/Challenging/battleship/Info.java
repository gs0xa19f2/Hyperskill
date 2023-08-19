package battleship;

import java.io.IOException;

public class Info {
    public static String hit = "You hit a ship!";
    public static String miss = "You missed!";
    public static String shipIsDown = "You sank a ship!";
    public static String allShipsDown = "You sank the last ship. You won. Congratulations!";

    public static class Ship {
        private final String message;
        private final int length;
        private int[][] coordinates;
        private boolean isShipDown = false;
        private int NumOfHitCoordinates = 0;


        Ship(String message, int length) {
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
            return NumOfHitCoordinates;
        }

        public void increaseNumOfHitCoordinatesByOne() {
            NumOfHitCoordinates++;
        }
    }

    static class WrongLengthException extends RuntimeException {
        public WrongLengthException() {
            super("Error! Wrong length of the Submarine! Try again:");
        }
    }

    static class WrongLocationException extends RuntimeException {
        public WrongLocationException() {
            super("Error! Wrong ship location! Try again:");
        }
    }

    static class TooCloseException extends RuntimeException {
        public TooCloseException() {
            super("Error! You placed it too close to another one. Try again:");
        }
    }

    static class WrongCoordinatesException extends RuntimeException {
        public WrongCoordinatesException() {
            super("Error! You entered the wrong coordinates! Try again:");
        }
    }

    public static void promptEnterKey() {
        System.out.println("Press Enter and pass the move to another player");
        try {
            int read = System.in.read();
        } catch (IOException e) {
            e.getMessage();
        } finally {
            System.out.print("\033[H\033[2J");
            System.out.flush();
        }
    }
}
