package io.github.dordor12.hbase.orm.perf;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Utility that runs warmup + measured iterations, computes latency percentiles
 * and throughput, and writes a JSON report to build/reports/perfTest/results.json.
 */
public final class PerfResultReporter {

    private static final List<Entry> entries = Collections.synchronizedList(new ArrayList<>());

    private PerfResultReporter() {}

    /**
     * Run a benchmark: warmup, then measured iterations. Records result for the final report.
     */
    public static BenchmarkResult run(String name, int warmupCount, int measuredCount, Runnable operation) {
        // Warmup
        for (int i = 0; i < warmupCount; i++) {
            operation.run();
        }

        // Measured
        long[] latencies = new long[measuredCount];
        long start = System.nanoTime();
        for (int i = 0; i < measuredCount; i++) {
            long opStart = System.nanoTime();
            operation.run();
            latencies[i] = System.nanoTime() - opStart;
        }
        long totalNanos = System.nanoTime() - start;

        Arrays.sort(latencies);
        BenchmarkResult result = new BenchmarkResult(
                name,
                measuredCount,
                percentile(latencies, 50),
                percentile(latencies, 95),
                percentile(latencies, 99),
                mean(latencies),
                (measuredCount * 1_000_000_000.0) / totalNanos
        );

        entries.add(new Entry(name, result));
        return result;
    }

    /**
     * Write all collected results to the JSON report file.
     */
    public static void writeReport(Path projectDir) throws IOException {
        Path reportDir = projectDir.resolve("build/reports/perfTest");
        Files.createDirectories(reportDir);
        Path reportFile = reportDir.resolve("results.json");

        StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < entries.size(); i++) {
            Entry e = entries.get(i);
            BenchmarkResult r = e.result;
            sb.append("  {\n");
            sb.append("    \"name\": \"").append(escapeJson(r.name)).append("\",\n");
            sb.append("    \"ops\": ").append(r.ops).append(",\n");
            sb.append("    \"p50_us\": ").append(String.format("%.2f", r.p50Nanos / 1000.0)).append(",\n");
            sb.append("    \"p95_us\": ").append(String.format("%.2f", r.p95Nanos / 1000.0)).append(",\n");
            sb.append("    \"p99_us\": ").append(String.format("%.2f", r.p99Nanos / 1000.0)).append(",\n");
            sb.append("    \"mean_us\": ").append(String.format("%.2f", r.meanNanos / 1000.0)).append(",\n");
            sb.append("    \"throughput_ops_sec\": ").append(String.format("%.2f", r.throughputOpsPerSec)).append("\n");
            sb.append("  }");
            if (i < entries.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]\n");

        Files.writeString(reportFile, sb.toString());
        System.out.println("Performance report written to: " + reportFile);
    }

    /**
     * Clear collected entries (call in @BeforeAll if needed).
     */
    public static void clear() {
        entries.clear();
    }

    private static long percentile(long[] sorted, int pct) {
        int idx = (int) Math.ceil(pct / 100.0 * sorted.length) - 1;
        return sorted[Math.max(0, idx)];
    }

    private static double mean(long[] values) {
        long sum = 0;
        for (long v : values) sum += v;
        return (double) sum / values.length;
    }

    private static String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    public record BenchmarkResult(
            String name,
            int ops,
            long p50Nanos,
            long p95Nanos,
            long p99Nanos,
            double meanNanos,
            double throughputOpsPerSec
    ) {
        @Override
        public String toString() {
            return String.format("%s: ops=%d, p50=%.1fus, p95=%.1fus, p99=%.1fus, mean=%.1fus, throughput=%.1f ops/s",
                    name, ops, p50Nanos / 1000.0, p95Nanos / 1000.0, p99Nanos / 1000.0,
                    meanNanos / 1000.0, throughputOpsPerSec);
        }
    }

    private record Entry(String name, BenchmarkResult result) {}
}
