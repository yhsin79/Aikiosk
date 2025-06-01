package com.example.AIkiosk.service;

import com.example.AIkiosk.dto.CoffeeMenuDTO;
import com.example.AIkiosk.entity.CoffeeMenu;
import com.example.AIkiosk.repository.CoffeeMenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CoffeeMenuService {

    @Autowired
    private CoffeeMenuRepository coffeeMenuRepository;

    // 커피 메뉴 목록을 반환하는 메소드
    public List<CoffeeMenuDTO> getAllCoffeeMenu() {
        List<CoffeeMenu> coffeeMenuList = coffeeMenuRepository.findAll();
        return coffeeMenuList.stream()
                .map(menu -> new CoffeeMenuDTO(menu.getId(),menu.getName(), menu.getPrice(), menu.getDescription(),menu.getImage_url()))
                .collect(Collectors.toList());
    }

    // ✅ 페이징 처리된 커피 메뉴 가져오기
    public List<CoffeeMenuDTO> getPagedCoffeeMenu(int page, int itemsPerPage) {
        Pageable pageable = PageRequest.of(page - 1, itemsPerPage); // 페이지 번호는 0부터 시작
        Page<CoffeeMenu> coffeePage = coffeeMenuRepository.findAll(pageable);

        return coffeePage.getContent().stream()
                .map(coffee -> new CoffeeMenuDTO(coffee.getId(),coffee.getName(),coffee.getPrice(),coffee.getDescription(),coffee.getImage_url()))
                .toList();
    }

    // ✅ 총 데이터 개수 가져오기
    public int getTotalItems() {
        return (int) coffeeMenuRepository.count();
    }


}
