package io.github.dordor12.hbase.orm.test.entity;

import io.github.dordor12.hbase.orm.annotation.*;
import io.github.dordor12.hbase.orm.codec.BestSuitCodec;

import java.math.BigDecimal;
import java.util.NavigableMap;

/**
 * Example entity with composite row key and multiple column families.
 */
@Table(namespace = "govt", name = "citizens", families = {
        @ColumnFamily(name = "main"),
        @ColumnFamily(name = "optional", versions = 10)
})
public class Citizen {

    @RowKeyComponent(order = 0)
    private String countryCode;

    @RowKeyComponent(order = 1, delimiter = "#")
    private Integer uid;

    @Column(family = "main", qualifier = "name")
    private String name;

    @Column(family = "optional", qualifier = "age")
    private Short age;

    @Column(family = "optional", qualifier = "salary")
    private Integer sal;

    @Column(family = "optional", qualifier = "iph")
    private Boolean isPassportHolder;

    @Column(family = "optional", qualifier = "f1")
    private Float f1;

    @Column(family = "optional", qualifier = "f2")
    private Double f2;

    @Column(family = "optional", qualifier = "f3")
    private Long f3;

    @Column(family = "optional", qualifier = "f4")
    private BigDecimal f4;

    @Column(family = "optional", qualifier = "pincode",
            codecFlags = {@CodecFlag(name = BestSuitCodec.SERIALIZE_AS_STRING, value = "true")})
    private Integer pincode;

    @MultiVersion(family = "optional", qualifier = "phone_number")
    private NavigableMap<Long, Integer> phoneNumber;

    public Citizen() {}

    public Citizen(String countryCode, Integer uid, String name) {
        this.countryCode = countryCode;
        this.uid = uid;
        this.name = name;
    }

    // ─── Getters / Setters ───────────────────────────────────────────

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public Integer getUid() { return uid; }
    public void setUid(Integer uid) { this.uid = uid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Short getAge() { return age; }
    public void setAge(Short age) { this.age = age; }

    public Integer getSal() { return sal; }
    public void setSal(Integer sal) { this.sal = sal; }

    public Boolean getIsPassportHolder() { return isPassportHolder; }
    public void setIsPassportHolder(Boolean isPassportHolder) { this.isPassportHolder = isPassportHolder; }

    public Float getF1() { return f1; }
    public void setF1(Float f1) { this.f1 = f1; }

    public Double getF2() { return f2; }
    public void setF2(Double f2) { this.f2 = f2; }

    public Long getF3() { return f3; }
    public void setF3(Long f3) { this.f3 = f3; }

    public BigDecimal getF4() { return f4; }
    public void setF4(BigDecimal f4) { this.f4 = f4; }

    public Integer getPincode() { return pincode; }
    public void setPincode(Integer pincode) { this.pincode = pincode; }

    public NavigableMap<Long, Integer> getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(NavigableMap<Long, Integer> phoneNumber) { this.phoneNumber = phoneNumber; }
}
