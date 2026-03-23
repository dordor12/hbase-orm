package io.github.dordor12.hbase.orm.bench;

import io.github.dordor12.hbase.orm.codec.BestSuitCodec;
import io.github.dordor12.hbase.orm.mapper.HBaseMapper;
import io.github.dordor12.hbase.orm.test.entity.*;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.openjdk.jmh.annotations.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

/**
 * JMH benchmarks for generated HBaseMapper writeAsPut/readFromResult/readFromPut/writeAsResult.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 2)
@Measurement(iterations = 3)
public class MapperBenchmark {

    private HBaseMapper<String, Citizen> citizenMapper;
    private HBaseMapper<Long, Employee> employeeMapper;
    private HBaseMapper<String, Crawl> crawlMapper;

    private Citizen citizenFull;
    private Citizen citizenMinimal;
    private Employee employee;
    private Crawl crawl;

    private Put citizenFullPut;
    private Put citizenMinimalPut;
    private Put employeePut;
    private Put crawlPut;

    private Result citizenFullResult;
    private Result citizenMinimalResult;
    private Result employeeResult;
    private Result crawlResult;

    @Setup
    public void setup() {
        BestSuitCodec codec = new BestSuitCodec();
        citizenMapper = new CitizenHBMapper(codec);
        employeeMapper = new EmployeeHBMapper(codec);
        crawlMapper = new CrawlHBMapper(codec);

        // Citizen full (all fields + multi-version + composite key)
        citizenFull = new Citizen("US", 42, "Alice");
        citizenFull.setAge((short) 30);
        citizenFull.setSal(100000);
        citizenFull.setIsPassportHolder(true);
        citizenFull.setF1(1.5f);
        citizenFull.setF2(2.5);
        citizenFull.setF3(999L);
        citizenFull.setF4(new BigDecimal("12345.67"));
        citizenFull.setPincode(90210);
        NavigableMap<Long, Integer> phones = new TreeMap<>();
        phones.put(1000L, 111);
        phones.put(2000L, 222);
        phones.put(3000L, 333);
        citizenFull.setPhoneNumber(phones);

        // Citizen minimal (only key + name)
        citizenMinimal = new Citizen("GB", 1, "Bob");

        // Employee (inheritance + Jackson LocalDateTime)
        employee = new Employee(1L, "Engineer");
        employee.setReporteeCount((short) 5);
        employee.setCreatedAt(LocalDateTime.of(2024, 6, 15, 9, 30));

        // Crawl (multi-version Double)
        crawl = new Crawl("http://example.com");
        crawl.addF1(1000L, 1.1);
        crawl.addF1(2000L, 2.2);
        crawl.addF1(3000L, 3.3);

        // Pre-build Puts and Results
        citizenFullPut = citizenMapper.writeAsPut(citizenFull);
        citizenMinimalPut = citizenMapper.writeAsPut(citizenMinimal);
        employeePut = employeeMapper.writeAsPut(employee);
        crawlPut = crawlMapper.writeAsPut(crawl);

        citizenFullResult = citizenMapper.writeAsResult(citizenFull);
        citizenMinimalResult = citizenMapper.writeAsResult(citizenMinimal);
        employeeResult = employeeMapper.writeAsResult(employee);
        crawlResult = crawlMapper.writeAsResult(crawl);
    }

    // ─── writeAsPut ───────────────────────────────────────────────────

    @Benchmark
    public Put citizenFullWriteAsPut() {
        return citizenMapper.writeAsPut(citizenFull);
    }

    @Benchmark
    public Put citizenMinimalWriteAsPut() {
        return citizenMapper.writeAsPut(citizenMinimal);
    }

    @Benchmark
    public Put employeeWriteAsPut() {
        return employeeMapper.writeAsPut(employee);
    }

    @Benchmark
    public Put crawlWriteAsPut() {
        return crawlMapper.writeAsPut(crawl);
    }

    // ─── readFromResult ───────────────────────────────────────────────

    @Benchmark
    public Citizen citizenFullReadFromResult() {
        return citizenMapper.readFromResult(citizenFullResult);
    }

    @Benchmark
    public Citizen citizenMinimalReadFromResult() {
        return citizenMapper.readFromResult(citizenMinimalResult);
    }

    @Benchmark
    public Employee employeeReadFromResult() {
        return employeeMapper.readFromResult(employeeResult);
    }

    @Benchmark
    public Crawl crawlReadFromResult() {
        return crawlMapper.readFromResult(crawlResult);
    }

    // ─── readFromPut ──────────────────────────────────────────────────

    @Benchmark
    public Citizen citizenFullReadFromPut() {
        return citizenMapper.readFromPut(citizenFullPut);
    }

    @Benchmark
    public Employee employeeReadFromPut() {
        return employeeMapper.readFromPut(employeePut);
    }

    @Benchmark
    public Crawl crawlReadFromPut() {
        return crawlMapper.readFromPut(crawlPut);
    }

    // ─── writeAsResult ────────────────────────────────────────────────

    @Benchmark
    public Result citizenFullWriteAsResult() {
        return citizenMapper.writeAsResult(citizenFull);
    }

    @Benchmark
    public Result employeeWriteAsResult() {
        return employeeMapper.writeAsResult(employee);
    }
}
