package bullscows;

import java.util.Scanner;

public class InputAndExceptions {

    public static int inputNumber(Scanner scanner, String message) {
        System.out.println(message);
        String number = scanner.nextLine();
        int integerNumber;
        try {
            integerNumber = Integer.parseInt(number);
            if (integerNumber < 1) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            throw new NotValidNumberException(number);
        }
        return integerNumber;
    }

    public static int[] inputCodeData(Scanner scanner) {
        int integerLength = inputNumber(scanner, "Input the length of the secret code:");
        int integerSymbols = inputNumber(scanner, "Input the number of possible symbols in the code:");
        if (integerSymbols < integerLength) {
            throw new SymbolsLessThanLengthException(integerLength, integerSymbols);
        }
        if (integerSymbols > 36) {
            throw new OutOfMaxNumberException();
        }
        return new int[]{integerLength, integerSymbols};
    }

    public static class SymbolsLessThanLengthException extends RuntimeException {
        SymbolsLessThanLengthException(int length, int symbols) {
            super(String.format("Error: it's not possible to generate a code " +
                    "with a length of %d with %d unique symbols.", length, symbols));
        }
    }

    public static class NotValidNumberException extends RuntimeException {
        NotValidNumberException(String number) {
            super(String.format("Error: \"%s\" isn't a valid number.", number));
        }
    }

    public static class OutOfMaxNumberException extends RuntimeException {
        OutOfMaxNumberException() {
            super("Error: maximum number of possible symbols in the code is 36 (0-9, a-z).");
        }
    }
}
