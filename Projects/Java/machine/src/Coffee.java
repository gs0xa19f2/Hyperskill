package machine;

/**
 * Класс Coffee представляет рецепт определенного типа кофе.
 */
public class Coffee {

    // Объем воды, необходимый для приготовления кофе (мл)
    final int requiredVolumeOfWater;

    // Объем молока, необходимый для приготовления кофе (мл)
    final int requiredVolumeOfMilk;

    // Вес кофейных зерен, необходимых для приготовления кофе (г)
    final int requiredWeightOfBeans;

    // Стоимость кофе ($)
    final int cost;

    /**
     * Конструктор для создания объекта Coffee.
     *
     * @param requiredVolumeOfWater объем воды (мл).
     * @param requiredVolumeOfMilk объем молока (мл).
     * @param requiredWeightOfBeans вес кофейных зерен (г).
     * @param cost стоимость ($).
     */
    public Coffee(int requiredVolumeOfWater, int requiredVolumeOfMilk, int requiredWeightOfBeans, int cost) {
        this.requiredVolumeOfWater = requiredVolumeOfWater;
        this.requiredVolumeOfMilk = requiredVolumeOfMilk;
        this.requiredWeightOfBeans = requiredWeightOfBeans;
        this.cost = cost;
    }
}
