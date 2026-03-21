package io.github.dordor12.hbase.orm.test.entity;

import io.github.dordor12.hbase.orm.annotation.*;

import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Entity with multi-version columns and simple String row key.
 */
@Table(name = "crawls", families = {@ColumnFamily(name = "a", versions = 10)})
public class Crawl {

    @RowKey
    private String key;

    @MultiVersion(family = "a", qualifier = "f1")
    private NavigableMap<Long, Double> f1;

    public Crawl() {}

    public Crawl(String key) {
        this.key = key;
        this.f1 = new TreeMap<>();
    }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public NavigableMap<Long, Double> getF1() { return f1; }
    public void setF1(NavigableMap<Long, Double> f1) { this.f1 = f1; }

    public Crawl addF1(long timestamp, Double value) {
        if (this.f1 == null) this.f1 = new TreeMap<>();
        this.f1.put(timestamp, value);
        return this;
    }
}
