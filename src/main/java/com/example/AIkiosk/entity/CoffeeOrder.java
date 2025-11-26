package com.example.AIkiosk.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "coffee_order")
public class CoffeeOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "face_id", nullable = false)
    private Long faceId;

    @Column(name = "coffee_id", nullable = false)
    private Long coffeeId;

    @Column(name = "order_time", nullable = false)
    private LocalDateTime orderTime;

    // 새로 추가: 온도 타입
    @Column(name = "temp_type")
    private String tempType;

    // 기본 생성자 (JPA용)
    public CoffeeOrder() {
    }

    // 기존 생성자 유지
    public CoffeeOrder(Long faceId, Long coffeeId, LocalDateTime orderTime) {
        this.faceId = faceId;
        this.coffeeId = coffeeId;
        this.orderTime = orderTime;
    }

    // 생성자
    public CoffeeOrder(Long faceId, Long coffeeId, LocalDateTime orderTime, String tempType) {
        this.faceId = faceId;
        this.coffeeId = coffeeId;
        this.orderTime = orderTime;
        this.tempType = tempType;
    }

    // Getter/Setter
    public Long getId() {
        return id;
    }

    public Long getFaceId() {
        return faceId;
    }

    public void setFaceId(Long faceId) {
        this.faceId = faceId;
    }

    public Long getCoffeeId() {
        return coffeeId;
    }

    public void setCoffeeId(Long coffeeId) {
        this.coffeeId = coffeeId;
    }

    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(LocalDateTime orderTime) {
        this.orderTime = orderTime;
    }

    public String getTempType() {
        return tempType;
    }

    public void setTempType(String tempType) {
        this.tempType = tempType;
    }
}
