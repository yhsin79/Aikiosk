package com.example.AIkiosk.dto;

public class FaceWithCoffeeDTO {

    private String imagePath;
    private String coffeeName;

    public FaceWithCoffeeDTO(String imagePath, String coffeeName) {
        this.imagePath = imagePath;
        this.coffeeName = coffeeName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getCoffeeName() {
        return coffeeName;
    }
}
