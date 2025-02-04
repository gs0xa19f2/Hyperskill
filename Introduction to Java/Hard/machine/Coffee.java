package machine;

public class Coffee {
    Coffee(int requiredVolumeOfWater, int requiredVolumeOfMilk, int requiredWeightOfBeans, int cost) {
        this.requiredVolumeOfWater = requiredVolumeOfWater;
        this.requiredVolumeOfMilk = requiredVolumeOfMilk;
        this.requiredWeightOfBeans = requiredWeightOfBeans;
        this.cost = cost;
    }

    int requiredVolumeOfWater; // ml
    int requiredVolumeOfMilk; // ml
    int requiredWeightOfBeans; // g
    int cost; // $

}