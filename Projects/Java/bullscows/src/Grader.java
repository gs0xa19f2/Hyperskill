package bullscows;

/**
 * Класс Grader отвечает за подсчет количества "быков" и "коров" в игре.
 */
public class Grader {

    /**
     * Подсчитывает количество каждого символа в коде.
     * @param code строка, представляющая секретный код или догадку.
     * @return массив, где индекс соответствует символу (0-9, a-z), а значение — количество его вхождений.
     */
    private static int[] countDigitsAndChars(String code) {
        int[] counts = new int[36];
        for (char ch : code.toCharArray()) {
            if (Character.isDigit(ch)) {
                counts[ch - '0']++;
            } else if (Character.isLowerCase(ch)) {
                counts[ch - 'a' + 10]++;
            }
        }
        return counts;
    }

    /**
     * Рассчитывает количество "быков" и "коров" в соответствии с правилами игры.
     * @param secret секретный код.
     * @param guess догадка игрока.
     * @return массив, где первый элемент — "быки", а второй — "коровы".
     */
    public static int[] calculateBullsAndCows(String secret, String guess) {
        int bulls = 0, cows = 0;
        int[] secretCounts = countDigitsAndChars(secret);
        int[] guessCounts = countDigitsAndChars(guess);

        // Подсчет "быков"
        for (int i = 0; i < secret.length(); i++) {
            if (secret.charAt(i) == guess.charAt(i)) {
                bulls++;
            }
        }

        // Подсчет "коров" (включая "быков", чтобы потом вычесть их)
        for (int i = 0; i < 36; i++) {
            cows += Math.min(secretCounts[i], guessCounts[i]);
        }

        // Исключаем "быков" из общего количества "коров"
        cows -= bulls;

        return new int[]{bulls, cows};
    }

    /**
     * Выводит результат проверки (количество "быков" и "коров").
     * @param bullsAndCows массив из двух элементов: "быков" и "коров".
     */
    public static void displayGrade(int[] bullsAndCows) {
        int bulls = bullsAndCows[0];
        int cows = bullsAndCows[1];

        // Формируем строку для корректного склонения слов "bull" и "cow"
        String bullStr = bulls == 1 ? "bull" : "bulls";
        String cowStr = cows == 1 ? "cow" : "cows";

        if (bulls == 0 && cows == 0) {
            System.out.println("Grade: None\n");
        } else if (bulls == 0) {
            System.out.printf("Grade: %d %s\n", cows, cowStr);
        } else if (cows == 0) {
            System.out.printf("Grade: %d %s\n", bulls, bullStr);
        } else {
            System.out.printf("Grade: %d %s and %d %s\n", bulls, bullStr, cows, cowStr);
        }
    }
}
