package com.ts.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "ts_order",
        indexes = {
                @Index(name = "idx_ts_order_status", columnList = "status"),
                @Index(name = "idx_ts_order_created_at", columnList = "created_at"),
                @Index(name = "idx_ts_order_user_id", columnList = "user_id"),
                @Index(name = "idx_ts_order_route_id", columnList = "route_id")
        }
)
public class Order extends BaseEntity {

    @Column(name = "order_no", nullable = false, unique = true, length = 30)
    private String orderNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private TourRoute route;

    @Column(nullable = false)
    private Integer persons;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    private LocalDate travelDate;

    @Column(length = 200)
    private String contactName;

    @Column(length = 20)
    private String contactPhone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    @Column(name = "reject_reason", length = 500)
    private String rejectReason;

    @Column(columnDefinition = "TEXT")
    private String remark;

    public enum Status { PENDING, CONFIRMED, REJECTED, PAID, CANCELLED, REFUNDING, REFUNDED, COMPLETED }

    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public TourRoute getRoute() { return route; }
    public void setRoute(TourRoute route) { this.route = route; }
    public Integer getPersons() { return persons; }
    public void setPersons(Integer persons) { this.persons = persons; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public LocalDate getTravelDate() { return travelDate; }
    public void setTravelDate(LocalDate travelDate) { this.travelDate = travelDate; }
    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public String getRejectReason() { return rejectReason; }
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
