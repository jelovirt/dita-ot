package org.dita.dost.util;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.dita.dost.exception.DITAOTException;

/**
 * Parallel processing utilities.
 */
public final class ThreadUtils {

    /** Maximum number of allocated threads */
    private final static int count = Configuration.configuration.containsKey("parallel.thread_count")
                                     ? Integer.parseInt(Configuration.configuration.get("parallel.thread_count"))
                                     : Runtime.getRuntime().availableProcessors();
    /** Threshold for switching to parallel processing */
    private final static int threshold = Configuration.configuration.containsKey("parallel.threshold")
                                         ? Integer.parseInt(Configuration.configuration.get("parallel.threshold"))
                                         : 10;

    /**
     * Run process items.
     * 
     * @param rs process item list
     * @throws DITAOTException if processing timed out
     */
    public static void run(final List<Runnable> rs) throws DITAOTException {
        if (count == 1 || rs.size() < threshold) {
            for (final Runnable r: rs) {
                r.run();
            }
        } else {
            final ExecutorService exec = Executors.newFixedThreadPool(count);
            for (final Runnable r: rs) {
                exec.submit(r);
            }
            exec.shutdown();
            try {
                if (!exec.awaitTermination(60, TimeUnit.MINUTES)) {
                    exec.shutdownNow();
                    throw new DITAOTException("Timeout elepsed while waiting for processing to finish");
                }
            } catch (final InterruptedException e) {
                exec.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
    
}
