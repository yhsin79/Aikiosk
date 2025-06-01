package com.example.AIkiosk.repository;
import com.example.AIkiosk.entity.CoffeeMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoffeeMenuRepository extends JpaRepository<CoffeeMenu, Long>{

    // 커피 메뉴 이름으로 찾기 (예시: "Americano")
    CoffeeMenu findByName(String name);

}
