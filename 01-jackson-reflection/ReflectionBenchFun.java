import java.lang.reflect.Field;
import java.util.*;

/**
 * Jackson Reflection Benchmark — Live Talk Edition
 * Ashish Vaghela | Software Crafter
 */
public class ReflectionBenchFun {

    // ANSI Colors for Stage Presentation
    static final String GREEN = "\u001B[32m";
    static final String RED = "\u001B[31m";
    static final String YELLOW = "\u001B[33m";
    static final String CYAN = "\u001B[36m";
    static final String BOLD = "\u001B[1m";
    static final String RESET = "\u001B[0m";

    static class Person {
        private String name = "Ashish Vaghela";
        private int age = 42;
        private String email = "ashish@nelkinda.com";
    }

    // A) Hand-written manual serializer (fastest, verbose)
    static String manualJson(Person p) {
        return "{\"name\":\"" + p.name + "\",\"age\":" + p.age + ",\"email\":\"" + p.email + "\"}";
    }

    // B) Naive reflection: field lookup on every call
    static String naiveReflectionJson(Object o) throws Exception {
        StringBuilder sb = new StringBuilder("{");
        Field[] fields = o.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            f.setAccessible(true);
            Object v = f.get(o);
            sb.append("\"").append(f.getName()).append("\":");
            if (v instanceof String) sb.append("\"").append(v).append("\""); else sb.append(v);
            if (i < fields.length - 1) sb.append(",");
        }
        return sb.append("}").toString();
    }

    // C) Cached reflection (Jackson / Gson style)
    static final Map<Class<?>, Field[]> CACHE = new HashMap<>();
    static String cachedReflectionJson(Object o) throws Exception {
        Field[] fields = CACHE.computeIfAbsent(o.getClass(), c -> {
            Field[] fs = c.getDeclaredFields();
            for (Field f : fs) f.setAccessible(true);
            return fs;
        });
        StringBuilder sb = new StringBuilder("{");
        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            Object v = f.get(o);
            sb.append("\"").append(f.getName()).append("\":");
            if (v instanceof String) sb.append("\"").append(v).append("\""); else sb.append(v);
            if (i < fields.length - 1) sb.append(",");
        }
        return sb.append("}").toString();
    }

    static double timeIt(String label, int iterations, Callable c) throws Exception {
        for (int i = 0; i < Math.min(50_000, iterations); i++) c.call(); // Warmup
        long start = System.nanoTime();
        Object last = null;
        for (int i = 0; i < iterations; i++) last = c.call();
        long end = System.nanoTime();
        double perCallNs = (end - start) / (double) iterations;
        System.out.printf("%-32s total=%8.2f ms   per-call=%8.1f ns%n",
                label, (end - start) / 1_000_000.0, perCallNs);
        return perCallNs;
    }

    interface Callable { Object call() throws Exception; }

    public static void main(String[] args) throws Exception {
        Person p = new Person();
        int iterations = 2_000_000;

        System.out.println(CYAN + BOLD + "========================================================================" + RESET);
        System.out.println(GREEN + BOLD + "  DEMO 1: JACKSON REFLECTION BENCHMARK (2,000,000 Iterations)" + RESET);
        System.out.println(CYAN + BOLD + "========================================================================" + RESET);

        double manual = timeIt("A) Manual Accessor", iterations, () -> manualJson(p));
        double naive  = timeIt("B) Naive Reflection (No Cache)", iterations, () -> naiveReflectionJson(p));
        double cached = timeIt("C) Cached Reflection (Jackson)", iterations, () -> cachedReflectionJson(p));

        System.out.println("------------------------------------------------------------------------");
        double naiveRatio = naive / manual;
        double cachedRatio = cached / manual;

        System.out.printf(RED + "Naive Reflection is %.1fx SLOWER than manual%n" + RESET, naiveRatio);
        System.out.printf(YELLOW + "Cached Reflection is %.1fx slower than manual (but %.1fx faster than naive)%n" + RESET,
                cachedRatio, naive / cached);

        // Cold Introspection Measurement
        CACHE.clear();
        long t0 = System.nanoTime();
        cachedReflectionJson(p);
        long t1 = System.nanoTime();
        double coldUs = (t1 - t0) / 1000.0;
        System.out.printf(CYAN + "First-ever call (Cold Class Introspection Tax) took %.1f µs%n" + RESET, coldUs);

        // Stage Verdict & Physics Joke
        System.out.println("------------------------------------------------------------------------");
        System.out.println(BOLD + "VERDICT:" + RESET + " Convenience has a price! Reflection introspection is why blank");
        System.out.println("services take seconds to boot.");
        System.out.printf(YELLOW + "Physics Fact: %.0f ns is roughly how long light takes to travel %.1f meters across this room.%n" + RESET,
                naive, naive * 0.3);
        System.out.println(CYAN + BOLD + "========================================================================" + RESET + "\n");
    }
}
