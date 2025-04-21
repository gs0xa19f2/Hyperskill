package machine;

import java.util.Scanner;

import static machine.CoffeeMachine.*;

/**
 * Класс Actions содержит методы для выполнения действий кофемашины.
 */
public class Actions {

    /**
     * Меню действий, запрашивающее у пользователя команду и выполняющее соответствующее действие.
     *
     * @param scanner объект Scanner для считывания ввода пользователя.
     */
    public static void startMenu(Scanner scanner) {
        System.out.println("Write action (buy, fill, take, remaining, exit): ");
        String action = scanner.next();
        System.out.println();

        switch (action) {
            case "buy" -> buy(scanner);
            case "fill" -> fill(scanner);
            case "take" -> take();
            case "remaining" -> showResources();
            case "exit" -> MenuWorking = false;
            default -> System.out.println("Wrong input");
        }
    }

    /**
     * Метод для покупки кофе. Пользователь выбирает тип кофе, который он хочет приобрести.
     *
     * @param scanner объект Scanner для считывания ввода пользователя.
     */
    public static void buy(Scanner scanner) {
        System.out.println("What do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino, back - to main menu: ");
        String option = scanner.next();

        switch (option) {
            case "1" -> makeCoffee(espresso);
            case "2" -> makeCoffee(latte);
            case "3" -> makeCoffee(cappuccino);
            case "back" -> {
                // Возврат в главное меню
            }
            default -> System.out.println("Wrong input");
        }
    }

    /**
     * Метод для пополнения запасов кофемашины.
     *
     * @param scanner объект Scanner для считывания ввода пользователя.
     */
    public static void fill(Scanner scanner) {
        System.out.println("Write how many ml of water you want to add: ");
        currentWater += scanner.nextInt();

        System.out.println("Write how many ml of milk you want to add: ");
        currentMilk += scanner.nextInt();

        System.out.println("Write how many grams of coffee beans you want to add: ");
        currentBeans += scanner.nextInt();

        System.out.println("Write how many disposable cups you want to add: ");
        currentCups += scanner.nextInt();
    }

    /**
     * Метод для получения всех денег из кофемашины.
     */
    public static void take() {
        System.out.printf("I gave you $%d\n", currentMoney);
        currentMoney = 0;
    }

    /**
     * Метод для приготовления выбранного типа кофе.
     *
     * @param coffee объект Coffee, описывающий выбранный тип кофе.
     */
    public static void makeCoffee(Coffee coffee) {
        if (checkResources(coffee)) {
            currentWater -= coffee.requiredVolumeOfWater;
            currentMilk -= coffee.requiredVolumeOfMilk;
            currentBeans -= coffee.requiredWeightOfBeans;
            currentCups--;
            currentMoney += coffee.cost;
            System.out.println("I have enough resources, making you a coffee!\n");
        }
    }

    /**
     * Проверяет, хватает ли ресурсов для приготовления выбранного типа кофе.
     *
     * @param coffee объект Coffee, описывающий выбранный тип кофе.
     * @return true, если ресурсов достаточно, иначе false.
     */
    public static boolean checkResources(Coffee coffee) {
        if (currentWater < coffee.requiredVolumeOfWater) {
            System.out.println("Sorry, not enough water!\n");
            return false;
        } else if (currentMilk < coffee.requiredVolumeOfMilk) {
            System.out.println("Sorry, not enough milk!\n");
            return false;
        } else if (currentBeans < coffee.requiredWeightOfBeans) {
            System.out.println("Sorry, not enough coffee beans!\n");
            return false;
        } else if (currentCups == 0) {
            System.out.println("Sorry, not enough disposable cups!\n");
            return false;
        }
        return true;
    }

    /**
     * Отображает текущие запасы ресурсов в кофемашине.
     */
    public static void showResources() {
        System.out.println("The coffee machine has:");
        System.out.printf("%d ml of water\n", currentWater);
        System.out.printf("%d ml of milk\n", currentMilk);
        System.out.printf("%d g of coffee beans\n", currentBeans);
        System.out.printf("%d disposable cups\n", currentCups);
        System.out.printf("$%d of money\n\n", currentMoney);
    }
}
