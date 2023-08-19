package battleship;

import java.util.Scanner;

import static battleship.Info.*;

public class Main {

    public static void main(String[] args) {
        var scanner = new Scanner(System.in);
        Player playerOne = new Player("Player 1");
        Player playerTwo = new Player("Player 2");
        playerOne.placeTheShips(scanner);
        playerTwo.placeTheShips(scanner);
        while (playerOne.getNumOfDownShips() != 5 || playerTwo.getNumOfDownShips() != 5) {
            playerOne.takeTurn(playerTwo, scanner);
            if (playerTwo.getNumOfDownShips() == 5) {
                System.out.println(allShipsDown);
            } else {
                playerTwo.takeTurn(playerOne, scanner);
            }
        }
    }
}
