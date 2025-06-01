package com.example.AIkiosk.service;

import com.example.AIkiosk.entity.CoffeeOrder;
import com.example.AIkiosk.repository.CoffeeOrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CoffeeOrderService {

    @Autowired
    private CoffeeOrderRepository coffeeOrderRepository;



    public void saveOrderMultipleTimes(Long faceId, Long coffeeId, int quantity) {
        for (int i = 0; i < quantity; i++) {
            CoffeeOrder order = new CoffeeOrder(faceId, coffeeId, LocalDateTime.now());
            coffeeOrderRepository.save(order);
        }
    }

    @Transactional
    public void saveOrder(Long faceId, Long coffeeId) {
        CoffeeOrder order = new CoffeeOrder();
        order.setFaceId(faceId);
        order.setCoffeeId(coffeeId);
        order.setOrderTime(LocalDateTime.now());

        coffeeOrderRepository.save(order);
    }

}
