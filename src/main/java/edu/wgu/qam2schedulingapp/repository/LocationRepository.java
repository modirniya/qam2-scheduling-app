package edu.wgu.qam2schedulingapp.repository;

import edu.wgu.qam2schedulingapp.model.Country;
import edu.wgu.qam2schedulingapp.model.SPR;
import edu.wgu.qam2schedulingapp.utility.Logs;
import edu.wgu.qam2schedulingapp.utility.SqlDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.Objects;

public class LocationRepository {
    private static final String TAG = "LocationRepository";
    public static final ObservableList<Country> allCountries = getAllCountries();
    public static final ObservableList<SPR> allSPR = getAllSPR();

    public static Country getCountryByDivisionId(int divisionId) {
        int countryId = Objects.requireNonNull(getSPRByDivisionId(divisionId)).getCountryId();
        for (Country country : allCountries)
            if (country.getId() == countryId)
                return country;
        Logs.error(TAG,
                "There is inconsistency with data: No country associated with such Division_ID: " + divisionId);
        throw new RuntimeException();
    }

    public static SPR getSPRByDivisionId(int divisionId) {
        for (SPR spr : allSPR)
            if (spr.getDivisionId() == divisionId)
                return spr;
        Logs.error(TAG,
                "There is inconsistency with data: No SPR associated with such Division_ID: " + divisionId);
        throw new RuntimeException();
    }

    private static ObservableList<Country> getAllCountries() {
        Logs.info(TAG, "Getting all countries");
        ObservableList<Country> olResult = FXCollections.observableArrayList();
        try {
            String strStatement = "SELECT Country_ID, Country FROM client_schedule.countries";
            var rsResult = SqlDatabase.executeForResult(strStatement);
            while (rsResult.next()) {
                olResult.add(new Country(
                        rsResult.getInt("Country_ID"),
                        rsResult.getString("Country")));
            }
        } catch (SQLException e) {
            Logs.error(TAG, "Exception occurred while executing getAllCountries()");
        }
        return olResult;
    }

    public static ObservableList<SPR> getAllSPRByCountryId(int countryId) {
        String strStatement =
                "SELECT Division_ID,Country_ID, Division " +
                        "FROM client_schedule.first_level_divisions " +
                        "WHERE Country_ID = " + countryId;
        return getSPR(strStatement);
    }

    private static ObservableList<SPR> getAllSPR() {
        String strStatement = "SELECT Division_ID,Country_ID, Division " +
                "FROM client_schedule.first_level_divisions";
        return getSPR(strStatement);
    }

    private static ObservableList<SPR> getSPR(String strStatement) {
        Logs.info(TAG, "Getting All SPR");
        ObservableList<SPR> olResult = FXCollections.observableArrayList();
        try {
            var rsResult = SqlDatabase.executeForResult(strStatement);
            while (rsResult.next()) {
                olResult.add(new SPR(
                        rsResult.getInt("Division_ID"),
                        rsResult.getInt("Country_ID"),
                        rsResult.getString("Division")));
            }
        } catch (SQLException e) {
            Logs.error(TAG, "Exception occurred while executing getAllSPR()");
        }
        return olResult;
    }
}
