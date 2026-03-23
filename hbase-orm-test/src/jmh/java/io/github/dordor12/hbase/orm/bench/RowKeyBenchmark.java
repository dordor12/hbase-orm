package io.github.dordor12.hbase.orm.bench;

import io.github.dordor12.hbase.orm.codec.BestSuitCodec;
import io.github.dordor12.hbase.orm.mapper.HBaseMapper;
import io.github.dordor12.hbase.orm.test.entity.*;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * JMH benchmarks for row key composition, parsing, and retrieval.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 2)
@Measurement(iterations = 3)
public class RowKeyBenchmark {

    private HBaseMapper<String, Citizen> citizenMapper;
    private HBaseMapper<Long, Employee> employeeMapper;
    private HBaseMapper<String, Crawl> crawlMapper;

    private Citizen citizen;
    private Employee employee;
    private Crawl crawl;

    private byte[] citizenRowKeyBytes;
    private byte[] employeeRowKeyBytes;
    private byte[] crawlRowKeyBytes;

    @Setup
    public void setup() {
        BestSuitCodec codec = new BestSuitCodec();
        citizenMapper = new CitizenHBMapper(codec);
        employeeMapper = new EmployeeHBMapper(codec);
        crawlMapper = new CrawlHBMapper(codec);

        citizen = new Citizen("US", 42, "Alice");
        employee = new Employee(12345L, "Engineer");
        crawl = new Crawl("http://example.com/page");

        citizenRowKeyBytes = citizenMapper.composeRowKey(citizen);
        employeeRowKeyBytes = employeeMapper.composeRowKey(employee);
        crawlRowKeyBytes = crawlMapper.composeRowKey(crawl);
    }

    // ─── composeRowKey ────────────────────────────────────────────────

    @Benchmark
    public byte[] citizenComposeRowKey() {
        return citizenMapper.composeRowKey(citizen);
    }

    @Benchmark
    public byte[] employeeComposeRowKey() {
        return employeeMapper.composeRowKey(employee);
    }

    @Benchmark
    public byte[] crawlComposeRowKey() {
        return crawlMapper.composeRowKey(crawl);
    }

    // ─── parseRowKey ──────────────────────────────────────────────────

    @Benchmark
    public Citizen citizenParseRowKey() {
        Citizen c = new Citizen();
        citizenMapper.parseRowKey(citizenRowKeyBytes, c);
        return c;
    }

    @Benchmark
    public Employee employeeParseRowKey() {
        Employee e = new Employee();
        employeeMapper.parseRowKey(employeeRowKeyBytes, e);
        return e;
    }

    @Benchmark
    public Crawl crawlParseRowKey() {
        Crawl c = new Crawl();
        crawlMapper.parseRowKey(crawlRowKeyBytes, c);
        return c;
    }

    // ─── getRowKey ────────────────────────────────────────────────────

    @Benchmark
    public String citizenGetRowKey() {
        return citizenMapper.getRowKey(citizen);
    }

    @Benchmark
    public Long employeeGetRowKey() {
        return employeeMapper.getRowKey(employee);
    }

    @Benchmark
    public String crawlGetRowKey() {
        return crawlMapper.getRowKey(crawl);
    }
}
