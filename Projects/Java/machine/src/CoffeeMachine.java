package machine;

import java.util.Scanner;

/**
 * Главный класс программы, представляющий кофемашину.
 */
public class CoffeeMachine {

    // Текущие запасы ресурсов кофемашины
    public static int currentWater = 400; // мл
    public static int currentMilk = 540; // мл
    public static int currentBeans = 120; // г
    public static int currentCups = 9; // количество стаканов
    public static int currentMoney = 550; // $

    // Рецепты кофе
    static Coffee espresso = new Coffee(250, 0, 16, 4);
    static Coffee latte = new Coffee(350, 75, 20, 7);
    static Coffee cappuccino = new Coffee(200, 100, 12, 6);

    // Флаг работы меню
    public static boolean MenuWorking = true;

    public static void main(String[] args) {
        var scanner = new Scanner(System.in);

        // Основной цикл работы кофемашины
        while (MenuWorking) {
            Actions.startMenu(scanner);
        }
    }
}
