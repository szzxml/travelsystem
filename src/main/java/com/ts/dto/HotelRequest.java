package com.ts.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class HotelRequest {
    @NotBlank
    private String name;
    private String city;
    private String address;
    private String phone;
    @Min(1)
    @Max(5)
    private Integer starLevel;
    private String description;
    private String status;

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
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
