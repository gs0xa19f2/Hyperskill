package bullscows;

import java.util.Scanner;

/**
 * Класс InputAndExceptions отвечает за обработку ввода пользователя и исключений.
 */
public class InputAndExceptions {

    /**
     * Считывает число от пользователя и проверяет его корректность.
     * @param scanner объект Scanner для чтения ввода.
     * @param message сообщение, отображаемое перед вводом.
     * @return целое число, введенное пользователем.
     * @throws NotValidNumberException если введенное число некорректно.
     */
    public static int inputNumber(Scanner scanner, String message) {
        System.out.println(message);
        String input = scanner.nextLine();
        try {
            int number = Integer.parseInt(input);
            if (number < 1) {
                throw new NumberFormatException();
            }
            return number;
        } catch (NumberFormatException e) {
            throw new NotValidNumberException(input);
        }
    }

    /**
     * Считывает длину секретного кода и количество символов, проверяя их корректность.
     * @param scanner объект Scanner для чтения ввода.
     * @return массив из двух чисел: длина секретного кода и количество символов.
     */
    public static int[] inputCodeData(Scanner scanner) {
        int length = inputNumber(scanner, "Input the length of the secret code:");
        int symbols = inputNumber(scanner, "Input the number of possible symbols in the code:");

        if (symbols < length) {
            throw new SymbolsLessThanLengthException(length, symbols);
        }
        if (symbols > 36) {
            throw new OutOfMaxNumberException();
        }
        return new int[]{length, symbols};
    }

    // Исключения для обработки ошибок ввода
    public static class SymbolsLessThanLengthException extends RuntimeException {
        SymbolsLessThanLengthException(int length, int symbols) {
            super(String.format("Error: it's not possible to generate a code with a length of %d with %d unique symbols.", length, symbols));
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
