package com.example.calorietrackerassignment;

public class Food {
    private String foodId;
    private String name;
    private String category;
    private String calorie;
    private String servingUnit;
    private String fat;
    private String servingAmount;

    public Food(){

    }

    public Food(String foodId, String name, String category, String calorie, String servingUnit, String fat, String servingAmount) {
        this.foodId = foodId;
        this.name = name;
        this.category = category;
        this.calorie = calorie;
        this.servingUnit = servingUnit;
        this.fat = fat;
        this.servingAmount = servingAmount;
    }

    public String getServingAmount() {
        return servingAmount;
    }

    public void setServingAmount(String servingAmount) {
        this.servingAmount = servingAmount;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCalorie() {
        return calorie;
    }

    public void setCalorie(String calorie) {
        this.calorie = calorie;
    }

    public String getServingUnit() {
        return servingUnit;
    }

    public void setServingUnit(String servingUnit) {
        this.servingUnit = servingUnit;
    }

    public String getFat() {
        return fat;
    }

    public void setFat(String fat) {
        this.fat = fat;
    }
}
