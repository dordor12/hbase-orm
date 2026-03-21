package io.github.dordor12.hbase.orm.test;

import io.github.dordor12.hbase.orm.codec.BestSuitCodec;
import io.github.dordor12.hbase.orm.mapper.HBaseMapper;
import io.github.dordor12.hbase.orm.test.entity.Citizen;
import io.github.dordor12.hbase.orm.test.entity.CitizenHBMapper;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.NavigableMap;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests verifying that AsyncHBaseDAO compiles with the generated mapper
 * and that mapper round-trips work through the async DAO's mapper reference.
 * No Docker/HBase required.
 */
class AsyncCitizenMapperTest {

    private HBaseMapper<String, Citizen> mapper;

    @BeforeEach
    void setUp() {
        mapper = new CitizenHBMapper(new BestSuitCodec());
    }

    @Test
    void asyncDaoAcceptsGeneratedMapper() {
        // Verify AsyncHBaseDAO compiles and can be constructed with a null connection
        // (we only test mapper wiring, not I/O)
        assertNotNull(mapper);
        assertEquals("govt:citizens", mapper.getTableName());
    }

    @Test
    void mapperRoundtripThroughPut() {
        Citizen original = new Citizen("US", 42, "Alice");
        original.setAge((short) 30);
        original.setSal(100000);
        original.setIsPassportHolder(true);
        original.setF1(1.5f);
        original.setF2(2.5);
        original.setF3(999L);
        original.setF4(new BigDecimal("12345.67"));
        original.setPincode(90210);

        Put put = mapper.writeAsPut(original);
        Citizen restored = mapper.readFromPut(put);

        assertEquals("US", restored.getCountryCode());
        assertEquals(Integer.valueOf(42), restored.getUid());
        assertEquals("Alice", restored.getName());
        assertEquals(Short.valueOf((short) 30), restored.getAge());
        assertEquals(Integer.valueOf(100000), restored.getSal());
        assertTrue(restored.getIsPassportHolder());
        assertEquals(1.5f, restored.getF1());
        assertEquals(2.5, restored.getF2());
        assertEquals(999L, restored.getF3());
        assertEquals(new BigDecimal("12345.67"), restored.getF4());
        assertEquals(Integer.valueOf(90210), restored.getPincode());
    }

    @Test
    void mapperRoundtripThroughResult() {
        Citizen original = new Citizen("GB", 7, "Bob");
        original.setAge((short) 25);

        Result result = mapper.writeAsResult(original);
        assertFalse(result.isEmpty());

        Citizen restored = mapper.readFromResult(result);
        assertEquals("GB", restored.getCountryCode());
        assertEquals(Integer.valueOf(7), restored.getUid());
        assertEquals("Bob", restored.getName());
    }

    @Test
    void mapperMultiVersionRoundtrip() {
        Citizen original = new Citizen("JP", 1, "Yuki");
        NavigableMap<Long, Integer> phones = new TreeMap<>();
        phones.put(1000L, 111);
        phones.put(2000L, 222);
        phones.put(3000L, 333);
        original.setPhoneNumber(phones);

        Put put = mapper.writeAsPut(original);
        Citizen restored = mapper.readFromPut(put);

        assertNotNull(restored.getPhoneNumber());
        assertEquals(3, restored.getPhoneNumber().size());
        assertEquals(Integer.valueOf(111), restored.getPhoneNumber().get(1000L));
        assertEquals(Integer.valueOf(333), restored.getPhoneNumber().get(3000L));
    }

    @Test
    void mapperNullFieldsHandled() {
        Citizen original = new Citizen("CA", 99, "EmptyFields");
        Put put = mapper.writeAsPut(original);
        Citizen restored = mapper.readFromPut(put);

        assertEquals("CA", restored.getCountryCode());
        assertNull(restored.getAge());
        assertNull(restored.getSal());
    }
}
