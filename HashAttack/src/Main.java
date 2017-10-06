import java.io.File;
import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;

public class Main {
    private static final int SAMPLES = 50;
    private static final int[] bit_sizes = {8, 12, 16, 20};

    public static void main(String args[]) throws Exception{
        HashAttack h = new HashAttack();

        Map<Integer, Long> pre_image_avgs = new TreeMap<>();
        Map<Integer, Long> collision_avgs = new TreeMap<>();

        // Test collision attack at each bit size, sampled and averaged
        for (int bit_size : bit_sizes) {
            long[] iterations = new long[SAMPLES];
            long total = 0;
            for (long iteration : iterations) {
                iteration = h.collisionAttack(bit_size);
                total = total + iteration;
            }
            long average = total/SAMPLES;
            System.out.printf("Collision Average for %d bits: %d\n",bit_size, average);
            collision_avgs.put(bit_size, average);
        }

        // Test pre image attack at each bit size, sampled and averaged
        for (int bit_size : bit_sizes) {
            long[] iterations = new long[SAMPLES];
            long total = 0;
            for (long iteration : iterations) {
                iteration = h.preImageAttack(bit_size);
                total = total + iteration;
            }
            long average = total/SAMPLES;
            System.out.printf("Pre-image Average for %d bits: %d\n",bit_size, average);
            pre_image_avgs.put(bit_size, average);
        }

        System.out.println(pre_image_avgs.toString());
        System.out.println(collision_avgs.toString());
        writeCSV(pre_image_avgs, collision_avgs);
    }

    public static void writeCSV(Map<Integer, Long> pre_image_avgs, Map<Integer, Long> collision_avgs) throws Exception {
        PrintWriter pw = new PrintWriter(new File("hash_attack_avgs.csv"));
        StringBuilder sb = new StringBuilder();
        sb.append("Bits");
        for (int bit : bit_sizes) {
            sb.append(',');
            sb.append(bit);
        }
        sb.append('\n');

        sb.append("Pre-image Attack Averages");
        for (long avg : pre_image_avgs.values()) {
            sb.append(',');
            sb.append(avg);
        }
        sb.append('\n');

        sb.append("Expected Pre-image Attack Averages");
        for(int bit : bit_sizes) {
            sb.append(',');
            sb.append(Math.pow(2, bit));
        }
        sb.append('\n');

        sb.append("Collision Attack Averages");
        for (long avg : collision_avgs.values()) {
            sb.append(',');
            sb.append(avg);
        }
        sb.append('\n');

        sb.append("Expected Collision Attack Averages");
        for(int bit : bit_sizes) {
            sb.append(',');
            sb.append(Math.pow(2, (bit/2.0)));
        }

        pw.write(sb.toString());
        pw.close();
    }
}
