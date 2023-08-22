package bullscows;

import java.util.Random;

public class SecretCode {
    public static int rnd(int min, int max) {
        max -= min + 1;
        return (new Random().nextInt(max) + min);
    }

    public static void putRandomDigitOrChar(StringBuilder digitsAndChars, StringBuilder secretCode) {
        int index = rnd(0, digitsAndChars.length());
        secretCode.append(digitsAndChars.charAt(index));
        digitsAndChars.deleteCharAt(index);
    }

    public static String generateSecretCode(int lengthOfCode, int numberOfSymbols) {
        StringBuilder digitsAndChars = new StringBuilder("0123456789abcdefghijklmnopqrstuvwxyz");
        digitsAndChars = new StringBuilder(digitsAndChars.substring(0, numberOfSymbols));
        StringBuilder secretCode = new StringBuilder();
        while (secretCode.length() != lengthOfCode) {
            putRandomDigitOrChar(digitsAndChars, secretCode);
        }
        return new String(secretCode);
    }
}
