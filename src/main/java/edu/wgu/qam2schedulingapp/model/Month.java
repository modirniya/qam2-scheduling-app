package edu.wgu.qam2schedulingapp.model;

/**
 * @author Parham Modirniya
 */

public enum Month {
    JANUARY("1", "January"),
    FEBRUARY("2", "February"),
    MARCH("3", "March"),
    APRIL("4", "April"),
    MAY("5", "May"),
    JUNE("6", "June"),
    JULY("7", "July"),
    AUGUST("8", "August"),
    SEPTEMBER("9", "September"),
    OCTOBER("10", "October"),
    NOVEMBER("11", "November"),
    DECEMBER("12", "December");

    private final String displayName;
    private final String number;

    Month(String number, String displayName) {
        this.number = number;
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.number + " - " + this.displayName;
    }

    public String getNumber() {
        return this.number;
    }
}
