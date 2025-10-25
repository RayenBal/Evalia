package com.example.evaliaproject.dto;

public class FeedbackStatsDto {
    private long total;
    private double average;
    private long stars1;
    private long stars2;
    private long stars3;
    private long stars4;
    private long stars5;

    public FeedbackStatsDto() {}
    public FeedbackStatsDto(long total, double average,
                            long stars1, long stars2, long stars3, long stars4, long stars5) {
        this.total = total; this.average = average;
        this.stars1 = stars1; this.stars2 = stars2; this.stars3 = stars3; this.stars4 = stars4; this.stars5 = stars5;
    }

    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
    public double getAverage() { return average; }
    public void setAverage(double average) { this.average = average; }
    public long getStars1() { return stars1; }
    public void setStars1(long stars1) { this.stars1 = stars1; }
    public long getStars2() { return stars2; }
    public void setStars2(long stars2) { this.stars2 = stars2; }
    public long getStars3() { return stars3; }
    public void setStars3(long stars3) { this.stars3 = stars3; }
    public long getStars4() { return stars4; }
    public void setStars4(long stars4) { this.stars4 = stars4; }
    public long getStars5() { return stars5; }
    public void setStars5(long stars5) { this.stars5 = stars5; }}
