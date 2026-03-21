package io.github.dordor12.hbase.orm.test;

import io.github.dordor12.hbase.orm.codec.BestSuitCodec;
import io.github.dordor12.hbase.orm.mapper.HBaseMapper;
import io.github.dordor12.hbase.orm.test.entity.Crawl;
import io.github.dordor12.hbase.orm.test.entity.CrawlHBMapper;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the generated CrawlHBMapper (multi-version fields).
 */
class CrawlMapperTest {

    private HBaseMapper<String, Crawl> mapper;

    @BeforeEach
    void setUp() {
        mapper = new CrawlHBMapper(new BestSuitCodec());
    }

    @Test
    void testSimpleStringRowKey() {
        Crawl crawl = new Crawl("http://example.com");
        byte[] rowKey = mapper.composeRowKey(crawl);
        assertEquals("http://example.com", Bytes.toString(rowKey));
    }

    @Test
    void testMultiVersionRoundtrip() {
        Crawl original = new Crawl("page1");
        original.addF1(100L, 1.1);
        original.addF1(200L, 2.2);
        original.addF1(300L, 3.3);

        Put put = mapper.writeAsPut(original);
        Crawl restored = mapper.readFromPut(put);

        assertEquals("page1", restored.getKey());
        assertNotNull(restored.getF1());
        assertEquals(3, restored.getF1().size());
        assertEquals(1.1, restored.getF1().get(100L));
        assertEquals(2.2, restored.getF1().get(200L));
        assertEquals(3.3, restored.getF1().get(300L));
    }

    @Test
    void testEmptyMultiVersion() {
        Crawl original = new Crawl("empty");
        original.setF1(null);

        Put put = mapper.writeAsPut(original);
        Crawl restored = mapper.readFromPut(put);

        assertEquals("empty", restored.getKey());
        assertNull(restored.getF1());
    }

    @Test
    void testTableMetadata() {
        assertEquals("crawls", mapper.getTableName());
        assertEquals(10, mapper.getColumnFamiliesAndVersions().get("a"));
    }
}
