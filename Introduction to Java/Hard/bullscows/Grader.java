package bullscows;

public class Grader {

    // 0123456789abcdefghijklmnopqrstuvwxyz
    public static int[] digitsAndCharsInCode(String code) {
        int[] digitsAndChars = new int[36];
        for (int i = 0; i < code.length(); i++) {
            if ('0' <= code.charAt(i) && code.charAt(i) <= '9') {
                digitsAndChars[code.charAt(i) - '0']++;
            }
            if ('a' <= code.charAt(i) && code.charAt(i) <= 'z') {
                digitsAndChars[code.charAt(i) - 'a' + 10]++;
            }
        }
        return digitsAndChars;
    }

    public static int[] getBullsAndCows(String first, String second) {
        int cows = 0;
        int bulls = 0;
        int[] digitsAndCharsInCodeInFirst = digitsAndCharsInCode(first);
        int[] digitsAndCharsInCodeInSecond = digitsAndCharsInCode(second);
        for (int i = 0; i < first.length(); i++) {
            if (first.charAt(i) == second.charAt(i)) {
                bulls++;
            }
        }
        for (int i = 0; i < 36; i++) {
            if (digitsAndCharsInCodeInFirst[i] > 0 && digitsAndCharsInCodeInSecond[i] > 0) {
                cows += digitsAndCharsInCodeInSecond[i];
            }
        }
        cows -= bulls;
        return new int[]{bulls, cows};
    }

    public static void showGrade(int[] bullsAndCows) {
        StringBuilder[] stringBullsAndCows = new StringBuilder[]{new StringBuilder("bull"), new StringBuilder("cow")};
        for (int i = 0; i < 2; i++) {
            if (bullsAndCows[i] > 1) {
                stringBullsAndCows[i].append('s');
            }
        }
        if (bullsAndCows[0] == 0 && bullsAndCows[1] == 0) {
            System.out.println("Grade: None\n");
        } else if (bullsAndCows[0] == 0) {
            System.out.printf("Grade: %d %s\n", bullsAndCows[1], stringBullsAndCows[1]);
        } else if (bullsAndCows[1] == 0) {
            System.out.printf("Grade: %d %s\n", bullsAndCows[0], stringBullsAndCows[0]);
        } else {
            System.out.printf("Grade: %d %s and %d %s\n",
                    bullsAndCows[0], stringBullsAndCows[0], bullsAndCows[1], stringBullsAndCows[1]);
        }
    }
}
