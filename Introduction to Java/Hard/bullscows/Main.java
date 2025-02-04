package bullscows;

import java.util.Scanner;

import static bullscows.SecretCode.*;
import static bullscows.Grader.*;
import static bullscows.InputAndExceptions.*;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int turn = 0;
        boolean isCodeGuessed = false;
        try {
            int[] temp = inputCodeData(scanner);
            int length = temp[0];
            int symbols = temp[1];
            String secretCode = generateSecretCode(length, symbols);

            if (symbols > 10) {
                System.out.printf("The secret is prepared: %s (0-9, a-%c).\n", "*".repeat(length),
                        (char) (symbols - 11 + 'a'));
            } else {
                System.out.printf("The secret is prepared: %s (0-%d).\n", "*".repeat(length), symbols - 1);
            }

            System.out.println("Okay, let's start a game!");
            while (!isCodeGuessed) {
                turn++;
                System.out.printf("Turn %d:\n", turn);
                int[] bullsAndCows = getBullsAndCows(secretCode, scanner.next());
                if (bullsAndCows[0] == length) {
                    isCodeGuessed = true;
                }
                showGrade(bullsAndCows);
            }
            System.out.println("Congratulations! You guessed the secret code.");
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }
}
