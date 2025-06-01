package com.example.AIkiosk.dto;

public class CoffeeMenuDTO {

    private Long id;
    private String name;
    private int price;

    private String description;
    private String image_url;

    // 기본 생성자
    public CoffeeMenuDTO() {
    }

    // 모든 필드를 포함한 생성자
    public CoffeeMenuDTO(Long id, String name, int price, String description, String image_url) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.image_url = image_url;
    }

    // Getter and Setter methods

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }

    public void setImage_url(String image_url){
        this.image_url = image_url;
    }

    public String getImage_url(){
        return image_url;
    }


}
