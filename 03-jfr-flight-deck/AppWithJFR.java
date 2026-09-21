package benchmark;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Sample App for JFR Live Telemetry Recording
 * Ashish Vaghela | Software Crafter
 */
public class AppWithJFR {

    static final String GREEN = "\u001B[32m";
    static final String YELLOW = "\u001B[33m";
    static final String CYAN = "\u001B[36m";
    static final String BOLD = "\u001B[1m";
    static final String RESET = "\u001B[0m";

    public static void main(String[] args) throws Exception {
        System.out.println(CYAN + BOLD + "========================================================================" + RESET);
        System.out.println(GREEN + BOLD + "  DEMO 3: JAVA FLIGHT RECORDER (JFR) TELEMETRY RUNNER" + RESET);
        System.out.println(YELLOW + "  Generating JVM events: allocations, reflection, and simulated latency..." + RESET);
        System.out.println(CYAN + BOLD + "========================================================================" + RESET);

        long endTime = System.currentTimeMillis() + 10_000; // Run workload for 10 seconds

        List<Object> memoryAllocationSink = new ArrayList<>();

        while (System.currentTimeMillis() < endTime) {
            // 1. Simulate Reflection Introspection
            Method m = String.class.getMethod("toUpperCase");
            m.invoke("jugaad vs jfr");

            // 2. Simulate Allocation Hotspot (Short-lived objects)
            for (int i = 0; i < 1_000; i++) {
                memoryAllocationSink.add(new byte[1024]);
            }
            memoryAllocationSink.clear();

            // 3. Thread Sleep
            Thread.sleep(10);
        }

        System.out.println(GREEN + BOLD + "✔ Workload complete! JFR recording dumped successfully." + RESET);
        System.out.println(CYAN + "Open the .jfr file in JDK Mission Control or convert to Flamegraph." + RESET);
        System.out.println(CYAN + BOLD + "========================================================================" + RESET + "\n");
    }
}
