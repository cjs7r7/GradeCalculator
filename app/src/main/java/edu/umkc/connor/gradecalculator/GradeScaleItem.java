package edu.umkc.connor.gradecalculator;

//An item in the grade scale
public class GradeScaleItem {

    private String name;
    private double percentage;

    public GradeScaleItem (String itemName, double weight) {
        name = itemName;
        percentage = weight;
    }

    public String getName() { return name; }

    public Double getPercentage() { return percentage; }
}
