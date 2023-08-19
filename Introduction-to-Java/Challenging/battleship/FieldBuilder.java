package battleship;

public class FieldBuilder {

    public static final int NUMBER_DIFF_FROM_ONE_TO_A = 64;

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

    public static void showField(String[][] field) {
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                System.out.print(field[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}
