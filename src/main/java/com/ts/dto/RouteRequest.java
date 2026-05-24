package com.ts.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class RouteRequest {
    @NotBlank
    private String title;
    private String description;
    private String coverImage;
    @NotNull
    @Min(1)
    private Integer days;
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal price;
    private Integer maxGroupSize;
    private String departure;
    private String destination;
    private Long hotelId;
    private String status;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
    public Integer getDays() { return days; }
    public void setDays(Integer days) { this.days = days; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getMaxGroupSize() { return maxGroupSize; }
    public void setMaxGroupSize(Integer maxGroupSize) { this.maxGroupSize = maxGroupSize; }
    public String getDeparture() { return departure; }
    public void setDeparture(String departure) { this.departure = departure; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public Long getHotelId() { return hotelId; }
    public void setHotelId(Long hotelId) { this.hotelId = hotelId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
