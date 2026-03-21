package io.github.dordor12.hbase.orm.test;

import io.github.dordor12.hbase.orm.codec.BestSuitCodec;
import io.github.dordor12.hbase.orm.mapper.HBaseMapper;
import io.github.dordor12.hbase.orm.test.entity.Employee;
import io.github.dordor12.hbase.orm.test.entity.EmployeeHBMapper;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the generated EmployeeHBMapper (simple row key + inheritance).
 */
class EmployeeMapperTest {

    private HBaseMapper<Long, Employee> mapper;

    @BeforeEach
    void setUp() {
        mapper = new EmployeeHBMapper(new BestSuitCodec());
    }

    @Test
    void testSimpleRowKey() {
        Employee emp = new Employee(42L, "Alice");
        byte[] rowKey = mapper.composeRowKey(emp);
        assertEquals(42L, Bytes.toLong(rowKey));

        assertEquals(Long.valueOf(42), mapper.getRowKey(emp));
    }

    @Test
    void testWriteAndReadPut() {
        Employee original = new Employee(100L, "Bob");
        original.setReporteeCount((short) 5);
        original.setCreatedAt(LocalDateTime.of(2024, 1, 15, 10, 30));

        Put put = mapper.writeAsPut(original);
        assertNotNull(put);

        Employee restored = mapper.readFromPut(put);
        assertEquals(Long.valueOf(100), restored.getEmpid());
        assertEquals("Bob", restored.getEmpName());
        assertEquals(Short.valueOf((short) 5), restored.getReporteeCount());
        // Inherited field from AbstractRecord
        assertNotNull(restored.getCreatedAt());
        assertEquals(LocalDateTime.of(2024, 1, 15, 10, 30), restored.getCreatedAt());
    }

    @Test
    void testWriteAndReadResult() {
        Employee original = new Employee(200L, "Charlie");

        Result result = mapper.writeAsResult(original);
        assertFalse(result.isEmpty());

        Employee restored = mapper.readFromResult(result);
        assertEquals(Long.valueOf(200), restored.getEmpid());
        assertEquals("Charlie", restored.getEmpName());
    }

    @Test
    void testTableMetadata() {
        assertEquals("employees", mapper.getTableName());
        assertEquals(1, mapper.getColumnFamiliesAndVersions().size());
        assertEquals(1, mapper.getColumnFamiliesAndVersions().get("a"));
    }
}
