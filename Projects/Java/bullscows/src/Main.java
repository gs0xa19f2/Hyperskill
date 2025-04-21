package bullscows;

import java.util.Scanner;

import static bullscows.SecretCode.*;
import static bullscows.Grader.*;
import static bullscows.InputAndExceptions.*;

/**
 * Главный класс программы "Быки и Коровы".
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            // Считываем параметры игры
            int[] codeData = inputCodeData(scanner);
            int length = codeData[0];
            int symbols = codeData[1];

            // Генерируем секретный код
            String secretCode = generateSecretCode(length, symbols);

            // Отображаем маскированный код
            if (symbols > 10) {
                System.out.printf("The secret is prepared: %s (0-9, a-%c).\n", "*".repeat(length), (char) ('a' + symbols - 11));
            } else {
                System.out.printf("The secret is prepared: %s (0-%d).\n", "*".repeat(length), symbols - 1);
            }

            System.out.println("Okay, let's start a game!");
            boolean isCodeGuessed = false;
            int turn = 0;

            // Основной игровой цикл
            while (!isCodeGuessed) {
                turn++;
                System.out.printf("Turn %d:\n", turn);
                String guess = scanner.next();
                int[] bullsAndCows = calculateBullsAndCows(secretCode, guess);

                // Если угадали весь код, завершаем игру
                if (bullsAndCows[0] == length) {
                    isCodeGuessed = true;
                }

                // Отображаем результат текущей попытки
                displayGrade(bullsAndCows);
            }

            System.out.println("Congratulations! You guessed the secret code.");
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }
}
