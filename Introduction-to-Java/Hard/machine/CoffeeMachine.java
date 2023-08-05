package machine;

import java.util.Scanner;

public class CoffeeMachine {
    // Current resources
    public static int currentWater = 400; // ml
    public static int currentMilk = 540; // ml
    public static int currentBeans = 120; // g
    public static int currentCups = 9; // amount
    public static int currentMoney = 550; // $
    static Coffee espresso = new Coffee(250, 0, 16, 4);
    static Coffee latte = new Coffee(350, 75, 20, 7);
    static Coffee cappuccino = new Coffee(200, 100, 12, 6);
    public static boolean MenuWorking = true;

    public static void main(String[] args) {
        var scanner = new Scanner(System.in);
        while (MenuWorking) {
            Actions.startMenu(scanner);
        }
    }
}
