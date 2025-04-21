package battleship;

public class FieldBuilder {

    // Константа для вычисления буквенных обозначений строк (например, A, B, C)
    private static final int NUMBER_DIFF_FROM_ONE_TO_A = 64;

    /**
     * Создает пустое игровое поле размером 11x11.
     * Верхняя строка и первый столбец содержат метки (числовые и буквенные).
     * Остальные клетки заполняются символом "~".
     *
     * @return Двумерный массив строк, представляющий игровое поле.
     */
    public static String[][] buildEmptyField() {
        String[][] field = new String[11][11];
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                if (i == 0 && j == 0) {
                    field[i][j] = " ";
                } else if (i == 0) {
                    field[i][j] = String.valueOf(j);
                } else if (j == 0) {
                    field[i][j] = (char) (i + NUMBER_DIFF_FROM_ONE_TO_A) + "";
                } else {
                    field[i][j] = "~";
                }
            }
        }
        return field;
    }

    /**
     * Выводит игровое поле в консоль.
     *
     * @param field Двумерный массив строк, представляющий игровое поле.
     */
    public static void showField(String[][] field) {
        for (String[] row : field) {
            for (String cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}
