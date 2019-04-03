package com.jasong.nycschools;

public class SchoolScores {
    // Data members
    private String name;
    private String math;
    private String writing;
    private String reading;

    public SchoolScores(String schoolName, String avgMathScore, String avgWritingScore, String avgReadingScore) {
        // Store the school name and scores provided
        name = schoolName;
        math = avgMathScore;
        writing = avgWritingScore;
        reading = avgReadingScore;
    }

    public String getSchoolName() {
        return name;
    }

    public String getAvgMathScore() {
        return math;
    }

    public String getAvgWritingScore() {
        return writing;
    }

    public String getAvgReadingScore() {
        return reading;
    }
}
