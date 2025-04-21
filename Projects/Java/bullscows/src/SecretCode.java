package bullscows;

import java.util.Random;

/**
 * Класс SecretCode отвечает за генерацию секретного кода.
 */
public class SecretCode {

    /**
     * Генерирует случайное число в заданном диапазоне.
     * @param min минимальное значение (включительно).
     * @param max максимальное значение (исключительно).
     * @return случайное число.
     */
    private static int randomInRange(int min, int max) {
        return new Random().nextInt(max - min) + min;
    }

    /**
     * Добавляет случайный символ из набора в секретный код.
     * @param availableSymbols набор доступных символов.
     * @param secretCode объект StringBuilder для секретного кода.
     */
    private static void appendRandomSymbol(StringBuilder availableSymbols, StringBuilder secretCode) {
        int index = randomInRange(0, availableSymbols.length());
        secretCode.append(availableSymbols.charAt(index));
        availableSymbols.deleteCharAt(index);
    }

    /**
     * Генерирует секретный код заданной длины из указанного количества уникальных символов.
     * @param lengthOfCode длина секретного кода.
     * @param numberOfSymbols количество уникальных символов.
     * @return строка, представляющая секретный код.
     */
    public static String generateSecretCode(int lengthOfCode, int numberOfSymbols) {
        StringBuilder availableSymbols = new StringBuilder("0123456789abcdefghijklmnopqrstuvwxyz".substring(0, numberOfSymbols));
        StringBuilder secretCode = new StringBuilder();

        while (secretCode.length() < lengthOfCode) {
            appendRandomSymbol(availableSymbols, secretCode);
        }

        return secretCode.toString();
    }
}
