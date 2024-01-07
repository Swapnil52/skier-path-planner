import java.util.ArrayList;
import java.util.List;

public class TestingApplication {

    public static void main(String[] args) {
        List<Integer> integers = new ArrayList<>();
        for (int i = 1; i <= 1051; i++) {
            integers.add(i);
        }
        List<List<Integer>> batches = partition(integers, 50);

        int nBatches = 0;
        int nIntegers = 0;
        for (List<Integer> batch : batches) {
            nIntegers += batch.size();
            nBatches++;
            System.out.println(batch);
        }
        System.out.println(nBatches);
        System.out.println(nIntegers);
    }

    public static <T> List<List<T>> partition(List<T> list, int batchSize) {
        List<List<T>> batches = new ArrayList<>();
        List<T> batch = new ArrayList<>();
        int i = 0;
        while (i < list.size()) {
            batch.add(list.get(i));
            i++;
            if (i % batchSize == 0) {
                batches.add(batch);
                batch = new ArrayList<>();
            }
        }
        if (!homework.Utils.isEmpty(batch)) {
            batches.add(batch);
        }
        return batches;
    }
}
