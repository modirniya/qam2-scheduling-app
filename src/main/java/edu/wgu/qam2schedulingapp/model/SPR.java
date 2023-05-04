package edu.wgu.qam2schedulingapp.model;

public class SPR {
    private final int divisionId;
    private final int countryId;
    private final String division;

    public SPR(int divisionId, int countryId, String division) {
        this.divisionId = divisionId;
        this.countryId = countryId;
        this.division = division;
    }

    public int getDivisionId() {
        return divisionId;
    }

    public int getCountryId() {
        return countryId;
    }

    public String getDivision() {
        return division;
    }

    @Override
    public String toString() {
        return division;
    }
}
