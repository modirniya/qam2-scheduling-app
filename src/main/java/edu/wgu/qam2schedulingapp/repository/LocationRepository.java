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
    private static LocationRepository instance;

    public final ObservableList<Country> allCountries = getAllCountries();

    public final ObservableList<SPR> allSPR = getAllSPR();

    private LocationRepository() {
        Logs.initLog(TAG);
    }

    public static LocationRepository getInstance() {
        if (instance == null) instance = new LocationRepository();
        return instance;
    }

    public Country getCountryByDivisionId(int divisionId) {
        int countryId = Objects.requireNonNull(getSPRByDivisionId(divisionId)).getCountryId();
        for (Country country : allCountries)
            if (country.getId() == countryId)
                return country;
        Logs.error(TAG,
                "There is inconsistency with data: No country associated with such Division_ID: " + divisionId);
        throw new RuntimeException();
    }

    public SPR getSPRByDivisionId(int divisionId) {
        for (SPR spr : allSPR)
            if (spr.getDivisionId() == divisionId)
                return spr;
        Logs.error(TAG,
                "There is inconsistency with data: No SPR associated with such Division_ID: " + divisionId);
        throw new RuntimeException();
    }

    private ObservableList<Country> getAllCountries() {
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

    public ObservableList<SPR> getAllSPRByCountryId(int countryId) {
        Logs.info(TAG, "Getting all SPR by CountryId");
        String strStatement =
                "SELECT Division_ID,Country_ID, Division " +
                "FROM client_schedule.first_level_divisions " +
                "WHERE Country_ID = " + countryId;
        return getSPR(strStatement);
    }

    private ObservableList<SPR> getAllSPR() {
        Logs.info(TAG, "Getting all SPR");
        String strStatement = "SELECT Division_ID,Country_ID, Division " +
                              "FROM client_schedule.first_level_divisions";
        return getSPR(strStatement);
    }

    private ObservableList<SPR> getSPR(String strStatement) {

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
