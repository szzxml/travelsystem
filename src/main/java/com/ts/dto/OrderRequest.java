package com.ts.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class OrderRequest {

    @NotNull
    private Long routeId;

    @NotNull
    @Min(1)
    private Integer persons;

    @NotNull
    private LocalDate travelDate;

    @NotBlank
    private String contactName;

    @NotBlank
    private String contactPhone;

    private String remark;

    public Long getRouteId() { return routeId; }
    public void setRouteId(Long routeId) { this.routeId = routeId; }
    public Integer getPersons() { return persons; }
    public void setPersons(Integer persons) { this.persons = persons; }
    public LocalDate getTravelDate() { return travelDate; }
    public void setTravelDate(LocalDate travelDate) { this.travelDate = travelDate; }
    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
