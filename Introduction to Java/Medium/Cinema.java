import java.util.Scanner;

public class Cinema {
    public static final int INT_CHAR_DIFFERENCE = 48;
    public static final int LIMINAL_NUMBER = 60;
    public static int rows = 0;
    public static int seats = 0;
    public static int totalSeats = 0;
    public static int purchasedTickets = 0;
    public static int currentIncome = 0;

    public static char[][] startStandardSpace(Scanner scanner) {
        System.out.println("Enter the number of rows:\n");
        rows = scanner.nextInt();
        System.out.println("Enter the number of seats in each row:\n");
        seats = scanner.nextInt();
        totalSeats = rows * seats;
        char[][] cinema = new char[rows + 1][seats + 1];
        for (int i = 0; i < cinema.length; i++) {
            for (int j = 0; j < cinema[i].length; j++) {
                if (i == 0 && j == 0) {
                    cinema[i][j] = ' ';
                } else if (i == 0) {
                    cinema[i][j] = (char) (j + INT_CHAR_DIFFERENCE);
                } else if (j == 0) {
                    cinema[i][j] = (char) (i + INT_CHAR_DIFFERENCE);
                } else {
                    cinema[i][j] = 'S';
                }
            }
        }
        return cinema;
    }

    public static void showCinema(char[][] cinema) {
        System.out.println("Cinema: ");
        for (char[] chars : cinema) {
            for (char element : chars) {
                System.out.print(element + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void buyTicket(char[][] cinema, Scanner scanner) {
        boolean availableSeatSelected = false;
        while (!availableSeatSelected) {
            System.out.println("Enter a row number:\n");
            int row = scanner.nextInt();
            System.out.println("Enter a seat number in that row:\n");
            int seat = scanner.nextInt();
            if (!(0 < row && row <= rows) || !(0 < seat && seat <= seats)) {
                System.out.println("Wrong input!");
                continue;
            }
            if (cinema[row][seat] == 'B') {
                System.out.println("That ticket has already been purchased!");
                continue;
            }
            int price = totalSeats <= LIMINAL_NUMBER || row <= rows / 2 ? 10 : 8;
            System.out.printf("Ticket price: $%d\n", price);
            cinema[row][seat] = 'B';
            availableSeatSelected = true;
            purchasedTickets++;
            currentIncome += price;
        }
    }

    public static void showCinemaMenu(char[][] cinema, Scanner scanner) {
        String menu = """
                1. Show the seats
                2. Buy a ticket
                3. Statistics
                0. Exit
                """;
        menuLoop:
        while (true) {
            System.out.println(menu);
            int item = scanner.nextInt();
            switch (item) {
                case 1 -> showCinema(cinema);
                case 2 -> buyTicket(cinema, scanner);
                case 3 -> {
                    System.out.printf("Number of purchased tickets: %d\n", purchasedTickets);
                    System.out.printf("Percentage: %.2f%%\n", purchasedTickets * 100 / (float) totalSeats);
                    System.out.printf("Current income: $%d\n", currentIncome);
                    System.out.printf("Total income: $%d\n", rows / 2 * seats * 10 +
                            (rows / 2 + rows % 2) * seats * 8);
                }
                case 0 -> {
                    break menuLoop;
                }
                default -> System.out.println("Error when selecting an item." +
                        " Please enter an integer from 0 to 2 inclusive");
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        char[][] cinema = startStandardSpace(scanner);
        showCinemaMenu(cinema, scanner);
    }
}