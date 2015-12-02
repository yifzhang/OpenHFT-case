package com.yifan.dev;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by yifanzhang.
 */
public class CreateOnHeapTest {
    private static final int ITERATIONS = 10000000;

    public void testOnHeapMap() {
        System.out.println("----- HASHMAP ------------------------");
        Map<Integer, BondVOImpl> map = new ConcurrentHashMap<Integer,BondVOImpl>(ITERATIONS);
        long actualQuantity = 0;
        long expectedQuantity = 0;
        long time = System.currentTimeMillis();

        System.out.println("*** Entering critical section ***");

        for (int i = 0; i < ITERATIONS; i++) {
            BondVOImpl bondVo = new BondVOImpl();
            bondVo.setQuantity(i);
            map.put(Integer.valueOf(i), bondVo);
            expectedQuantity += i;
        }

        System.out.println("*** After putting objs");
        printMemUsage();

        long putTime = System.currentTimeMillis() - time;
        time = System.currentTimeMillis();
        System.out.println("************* STARTING GET *********************");
        for (int i = 0; i < map.size(); i++) {
            actualQuantity += map.get(i).getQuantity();
        }

        System.out.println("*** After getting objs");
        printMemUsage();

        System.out.println("*** Exiting critical section ***");

        System.out.println("Time for putting " + putTime);
        System.out.println("Time for getting " + (System.currentTimeMillis() - time));

        System.out.println(
            "Put quantity:" + actualQuantity + ", assume quantity:" + expectedQuantity);
        //        Assert.assertEquals(expectedQuantity, actualQuantity);

        printMemUsage();
        while (true) {
            int index = new Random(ITERATIONS).nextInt();
            map.get(index);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void printMemUsage() {
        System.gc();
        System.gc();
        System.out.println("Memory(heap) used " + humanReadableByteCount(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory(), true));
    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static void main(String[] args) {
        CreateOnHeapTest t = new CreateOnHeapTest();
        t.testOnHeapMap();
    }
}
