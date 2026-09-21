import java.util.*;

/**
 * Hibernate N+1 Query Simulation — Live Talk Edition
 * Ashish Vaghela | Software Crafter
 */
public class NPlusOneSimFun {

    static final String GREEN = "\u001B[32m";
    static final String RED = "\u001B[31m";
    static final String YELLOW = "\u001B[33m";
    static final String CYAN = "\u001B[36m";
    static final String BOLD = "\u001B[1m";
    static final String RESET = "\u001B[0m";

    record Order(int id, String customer) {}

    static final int NETWORK_LATENCY_MS = 2; // Simulated DB Roundtrip Latency

    static List<Order> fetchOrders(int count) throws InterruptedException {
        Thread.sleep(NETWORK_LATENCY_MS);
        List<Order> orders = new ArrayList<>();
        for (int i = 1; i <= count; i++) orders.add(new Order(i, "Customer " + i));
        return orders;
    }

    // Strategy A: Lazy Loading N+1 Queries
    static int naiveNPlusOne(List<Order> orders) throws InterruptedException {
        int total = 0;
        for (Order o : orders) {
            Thread.sleep(NETWORK_LATENCY_MS); // +1 Query per Order
            total += 3;
        }
        return total;
    }

    // Strategy B: Batched Join Fetch Query
    static int batchedFetch(List<Order> orders) throws InterruptedException {
        Thread.sleep(NETWORK_LATENCY_MS); // 1 Batched Query
        return orders.size() * 3;
    }

    public static void main(String[] args) throws InterruptedException {
        int[] sizes = {10, 50, 200};
        double worstSlowdown = 0;

        System.out.println(CYAN + BOLD + "========================================================================" + RESET);
        System.out.println(GREEN + BOLD + "  DEMO 2: HIBERNATE N+1 QUERY EXPLOSION SIMULATION" + RESET);
        System.out.println(YELLOW + "  Simulated DB Roundtrip Latency: " + NETWORK_LATENCY_MS + " ms" + RESET);
        System.out.println(CYAN + BOLD + "========================================================================" + RESET);
        System.out.printf("%-10s %-24s %-24s %-10s%n", "Orders", "Naive (1 + N Queries)", "Batched (1 + 1 Query)", "Slowdown");
        System.out.println("------------------------------------------------------------------------");

        for (int n : sizes) {
            List<Order> orders = fetchOrders(n);

            long t0 = System.nanoTime();
            naiveNPlusOne(orders);
            long naiveMs = (System.nanoTime() - t0) / 1_000_000;

            long t1 = System.nanoTime();
            batchedFetch(orders);
            long batchMs = (System.nanoTime() - t1) / 1_000_000;

            double slowdown = (double) naiveMs / Math.max(batchMs, 1);
            worstSlowdown = Math.max(worstSlowdown, slowdown);

            System.out.printf("%-10d %-24s %-24s " + RED + "%.1fx" + RESET + "%n",
                    n, naiveMs + " ms (" + (n + 1) + " queries)", batchMs + " ms (2 queries)", slowdown);
        }

        System.out.println("------------------------------------------------------------------------");
        System.out.printf(RED + BOLD + "VERDICT: Naive strategy is %.0fx SLOWER at 200 orders!%n" + RESET, worstSlowdown);
        System.out.println(YELLOW + "DHABA ROTI ANALOGY: This is like going to a dhaba and asking the waiter");
        System.out.println("to make a separate trip to the kitchen for EVERY SINGLE ROTI,");
        System.out.println("instead of just bringing the whole thali at once." + RESET);
        System.out.println(CYAN + BOLD + "========================================================================" + RESET + "\n");
    }
}
