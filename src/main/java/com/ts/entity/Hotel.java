package com.ts.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(
        name = "ts_hotel",
        indexes = {
                @Index(name = "idx_ts_hotel_city", columnList = "city"),
                @Index(name = "idx_ts_hotel_status", columnList = "status"),
                @Index(name = "idx_ts_hotel_name", columnList = "name")
        }
)
public class Hotel extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 100)
    private String city;

    @Column(length = 200)
    private String address;

    @Column(length = 20)
    private String phone;

    @Column(name = "star_level")
    private Integer starLevel;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;

    public enum Status { ACTIVE, INACTIVE }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Integer getStarLevel() { return starLevel; }
    public void setStarLevel(Integer starLevel) { this.starLevel = starLevel; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}
