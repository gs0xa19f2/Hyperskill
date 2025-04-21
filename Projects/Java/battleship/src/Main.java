package battleship;

import java.util.Scanner;

import static battleship.Info.*;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Создаем двух игроков
        Player playerOne = new Player("Player 1");
        Player playerTwo = new Player("Player 2");

        // Фаза размещения кораблей
        playerOne.placeTheShips(scanner);
        playerTwo.placeTheShips(scanner);

        // Основной игровой цикл
        while (playerOne.getNumOfDownShips() != 5 && playerTwo.getNumOfDownShips() != 5) {
            playerOne.takeTurn(playerTwo, scanner);
            if (playerTwo.getNumOfDownShips() == 5) {
                System.out.println(allShipsDown);
                break;
            }
            playerTwo.takeTurn(playerOne, scanner);
        }
    }
}
