package io.github.dordor12.hbase.orm.test.entity;

import io.github.dordor12.hbase.orm.annotation.*;

/**
 * Entity with simple row key and @MappedSuperclass inheritance.
 */
@Table(name = "employees", families = {@ColumnFamily(name = "a")})
public class Employee extends AbstractRecord {

    @RowKey
    private Long empid;

    @Column(family = "a", qualifier = "name")
    private String empName;

    @Column(family = "a", qualifier = "reportee_count")
    private Short reporteeCount;

    public Employee() {}

    public Employee(Long empid, String empName) {
        this.empid = empid;
        this.empName = empName;
    }

    public Long getEmpid() { return empid; }
    public void setEmpid(Long empid) { this.empid = empid; }

    public String getEmpName() { return empName; }
    public void setEmpName(String empName) { this.empName = empName; }

    public Short getReporteeCount() { return reporteeCount; }
    public void setReporteeCount(Short reporteeCount) { this.reporteeCount = reporteeCount; }
}
