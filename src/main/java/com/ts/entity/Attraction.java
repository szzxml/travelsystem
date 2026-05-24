package com.ts.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "ts_attraction",
        indexes = {
                @Index(name = "idx_ts_attraction_status", columnList = "status"),
                @Index(name = "idx_ts_attraction_location", columnList = "location")
        }
)
public class Attraction extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "location", length = 100)
    private String location;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 200)
    private String coverImage;

    private Double ticketPrice;

    @Column(length = 100)
    private String openTime;

    private Integer capacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.OPEN;

    public enum Status { OPEN, CLOSED, MAINTENANCE }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
    public Double getTicketPrice() { return ticketPrice; }
    public void setTicketPrice(Double ticketPrice) { this.ticketPrice = ticketPrice; }
    public String getOpenTime() { return openTime; }
    public void setOpenTime(String openTime) { this.openTime = openTime; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}
