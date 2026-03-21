package io.github.dordor12.hbase.orm.test;

import io.github.dordor12.hbase.orm.codec.BestSuitCodec;
import io.github.dordor12.hbase.orm.mapper.HBaseMapper;
import io.github.dordor12.hbase.orm.test.entity.Citizen;
import io.github.dordor12.hbase.orm.test.entity.CitizenHBMapper;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the generated CitizenHBMapper.
 */
class CitizenMapperTest {

    private HBaseMapper<String, Citizen> mapper;

    @BeforeEach
    void setUp() {
        mapper = new CitizenHBMapper(new BestSuitCodec());
    }

    @Test
    void testCompositeRowKey() {
        Citizen citizen = new Citizen("US", 123, "John");
        byte[] rowKey = mapper.composeRowKey(citizen);
        assertEquals("US#123", Bytes.toString(rowKey));

        String rk = mapper.getRowKey(citizen);
        assertEquals("US#123", rk);
    }

    @Test
    void testParseRowKey() {
        Citizen citizen = new Citizen();
        mapper.parseRowKey(Bytes.toBytes("IN#456"), citizen);
        assertEquals("IN", citizen.getCountryCode());
        assertEquals(Integer.valueOf(456), citizen.getUid());
    }

    @Test
    void testWriteAndReadPut() {
        Citizen original = new Citizen("US", 42, "Alice");
        original.setAge((short) 30);
        original.setSal(100000);
        original.setIsPassportHolder(true);
        original.setF1(1.5f);
        original.setF2(2.5);
        original.setF3(1000L);
        original.setF4(new BigDecimal("99.99"));
        original.setPincode(12345);

        Put put = mapper.writeAsPut(original);
        assertNotNull(put);
        assertEquals("US#42", Bytes.toString(put.getRow()));

        // Read back from Put
        Citizen restored = mapper.readFromPut(put);
        assertEquals("US", restored.getCountryCode());
        assertEquals(Integer.valueOf(42), restored.getUid());
        assertEquals("Alice", restored.getName());
        assertEquals(Short.valueOf((short) 30), restored.getAge());
        assertEquals(Integer.valueOf(100000), restored.getSal());
        assertTrue(restored.getIsPassportHolder());
        assertEquals(1.5f, restored.getF1());
        assertEquals(2.5, restored.getF2());
        assertEquals(1000L, restored.getF3());
        assertEquals(new BigDecimal("99.99"), restored.getF4());
        // Pincode serialized as string
        assertEquals(Integer.valueOf(12345), restored.getPincode());
    }

    @Test
    void testWriteAndReadResult() {
        Citizen original = new Citizen("GB", 7, "Bob");
        original.setAge((short) 25);

        Result result = mapper.writeAsResult(original);
        assertNotNull(result);
        assertFalse(result.isEmpty());

        Citizen restored = mapper.readFromResult(result);
        assertEquals("GB", restored.getCountryCode());
        assertEquals(Integer.valueOf(7), restored.getUid());
        assertEquals("Bob", restored.getName());
        assertEquals(Short.valueOf((short) 25), restored.getAge());
    }

    @Test
    void testMultiVersionField() {
        Citizen original = new Citizen("JP", 1, "Yuki");
        NavigableMap<Long, Integer> phoneNumbers = new TreeMap<>();
        phoneNumbers.put(1000L, 111);
        phoneNumbers.put(2000L, 222);
        phoneNumbers.put(3000L, 333);
        original.setPhoneNumber(phoneNumbers);

        Put put = mapper.writeAsPut(original);
        Citizen restored = mapper.readFromPut(put);

        assertNotNull(restored.getPhoneNumber());
        assertEquals(3, restored.getPhoneNumber().size());
        assertEquals(Integer.valueOf(111), restored.getPhoneNumber().get(1000L));
        assertEquals(Integer.valueOf(222), restored.getPhoneNumber().get(2000L));
        assertEquals(Integer.valueOf(333), restored.getPhoneNumber().get(3000L));
    }

    @Test
    void testNullFieldsHandled() {
        Citizen original = new Citizen("CA", 99, "EmptyFields");
        // All optional fields are null

        Put put = mapper.writeAsPut(original);
        Citizen restored = mapper.readFromPut(put);

        assertEquals("CA", restored.getCountryCode());
        assertEquals(Integer.valueOf(99), restored.getUid());
        assertEquals("EmptyFields", restored.getName());
        assertNull(restored.getAge());
        assertNull(restored.getSal());
        assertNull(restored.getF1());
    }

    @Test
    void testTableMetadata() {
        assertEquals("govt:citizens", mapper.getTableName());

        Map<String, Integer> families = mapper.getColumnFamiliesAndVersions();
        assertEquals(2, families.size());
        assertEquals(1, families.get("main"));
        assertEquals(10, families.get("optional"));
    }

    @Test
    void testGetColumn() {
        byte[][] nameCol = mapper.getColumn("name");
        assertEquals("main", Bytes.toString(nameCol[0]));
        assertEquals("name", Bytes.toString(nameCol[1]));

        byte[][] ageCol = mapper.getColumn("age");
        assertEquals("optional", Bytes.toString(ageCol[0]));
        assertEquals("age", Bytes.toString(ageCol[1]));

        assertThrows(IllegalArgumentException.class, () -> mapper.getColumn("nonexistent"));
    }
}
