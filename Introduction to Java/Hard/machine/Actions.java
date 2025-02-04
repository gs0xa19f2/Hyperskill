package machine;

import java.util.Scanner;

import static machine.CoffeeMachine.*;

public class Actions {
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

    public static void buy(Scanner scanner) {
        System.out.println("What do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino, back - to main menu: ");
        String option = scanner.next();
        switch (option) {
            case "1" -> makeCoffee(espresso);
            case "2" -> makeCoffee(latte);
            case "3" -> makeCoffee(cappuccino);
            case "back" -> {
            }
            default -> System.out.println("Wrong input");
        }
    }

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

    public static void take() {
        System.out.printf("I gave you $%d\n", currentMoney);
        currentMoney = 0;
    }

    public static void makeCoffee(Coffee coffee) {
        if (checkResources(coffee)) {
            currentWater -= coffee.requiredVolumeOfWater;
            currentMilk -= coffee.requiredVolumeOfMilk;
            currentBeans -= coffee.requiredWeightOfBeans;
            currentCups--;
            currentMoney += coffee.cost;
        }
    }

    public static boolean checkResources(Coffee coffee) {
        if (currentWater - coffee.requiredVolumeOfWater < 0) {
            System.out.println("Sorry, not enough water!\n");
            return false;
        } else if (currentMilk - coffee.requiredVolumeOfMilk < 0) {
            System.out.println("Sorry, not enough milk!\n");
            return false;
        } else if (currentBeans - coffee.requiredWeightOfBeans < 0) {
            System.out.println("Sorry, not enough coffee beans!\n");
            return false;
        } else if (currentCups == 0) {
            System.out.println("Sorry, not enough disposable cups!\n");
            return false;
        } else {
            System.out.println("I have enough resources, making you a coffee!\n");
            return true;
        }
    }

    public static void showResources() {
        System.out.println("The coffee machine has: ");
        System.out.printf("%d ml of water\n", currentWater);
        System.out.printf("%d ml of milk\n", currentMilk);
        System.out.printf("%d g of coffee beans\n", currentBeans);
        System.out.printf("%d disposable cups\n", currentCups);
        System.out.printf("$%d of money\n", currentMoney);
        System.out.println();
    }
}
