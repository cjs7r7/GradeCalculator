package edu.umkc.connor.gradecalculator;

//An assignment
public class AssignmentItem {

    private String name;
    private double totalpoints;
    private double score;

    public AssignmentItem(String assnName, double yourScore, double pointsPossible) {
        name = assnName;
        totalpoints = pointsPossible;
        score = yourScore;
    }

    public String getName() { return name; }

    public double getTotalPoints() { return totalpoints; }

    public double getScore() { return score; }

}
