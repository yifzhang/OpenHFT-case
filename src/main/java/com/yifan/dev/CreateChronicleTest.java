package com.yifan.dev;

import net.openhft.chronicle.map.ChronicleMap;
import net.openhft.chronicle.map.ChronicleMapBuilder;
import net.openhft.lang.values.IntValue;

import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 * Created by yifanzhang.
 */
public class CreateChronicleTest {
    private static final int ITERATIONS = 10000000;

    public static void demoChronicleMap() throws IOException, InterruptedException {
        System.out.println("----- CHRONICLE MAP ------------------------");
        File file = new File("/tmp/chronicle-map.map");
        file.deleteOnExit();

        ChronicleMapBuilder<IntValue, BondVOInterface> builder =
            ChronicleMapBuilder.of(IntValue.class, BondVOInterface.class)
                .entries(ITERATIONS);

        try {
            ChronicleMap<IntValue, BondVOInterface> map =
                builder.createPersistedTo(file);

            final BondVOInterface value = map.newValueInstance();
            final IntValue key = map.newKeyInstance();
            long actualQuantity = 0;
            long expectedQuantity = 0;

            long time = System.currentTimeMillis();

            System.out.println("*** Entering critical section ***");

            for (int i = 0; i < ITERATIONS; i++) {
                value.setQuantity(i);
                key.setValue(i);
                map.put(key, value);
                expectedQuantity += i;
            }
            System.out.println("*** After putting objs");
            printMemUsage();

            long putTime = (System.currentTimeMillis()-time);
            time = System.currentTimeMillis();

            for (int i = 0; i < ITERATIONS; i++) {
                key.setValue(i);
                actualQuantity += map.getUsing(key, value).getQuantity();
            }

            System.out.println("*** After getting objs");
            printMemUsage();

            System.out.println("*** Exiting critical section ***");

            System.out.println("Time for putting " + putTime);
            System.out.println("Time for getting " + (System.currentTimeMillis()-time));

            printMemUsage();
            while (true) {
                int index = new Random(ITERATIONS).nextInt();
                key.setValue(index);
                map.getUsing(key,value);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            file.delete();
        }
    }

    public static void printMemUsage(){
        System.gc();
        System.gc();
        System.out.println("Memory(heap) used " + humanReadableByteCount(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory(), true));
    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }


    public static void main(String[] args) {
        System.out.println("The value is:" + ITERATIONS);

        try {
            CreateChronicleTest.demoChronicleMap();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
