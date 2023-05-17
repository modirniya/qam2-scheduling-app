package edu.wgu.qam2schedulingapp.repository;

import edu.wgu.qam2schedulingapp.helper.Logs;
import edu.wgu.qam2schedulingapp.helper.SqlHelper;
import edu.wgu.qam2schedulingapp.model.Country;
import edu.wgu.qam2schedulingapp.model.SPR;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.Objects;

/**
 * This is a singleton class that fetches and stores all location-related data from the database.
 * The class provides methods to retrieve countries and subdivisions of countries (SPRs).
 * <p>
 * The class utilizes the {@link SqlHelper} class to execute SQL queries and
 * the {@link Logs} class to log events during its operations.
 * <p>
 * Fields:
 * <ul>
 *  <li>allCountries: An ObservableList that stores all fetched Country objects.</li>
 *  <li>allSPR: An ObservableList that stores all fetched SPR objects.</li>
 *  <li>TAG: A string identifier for logging purposes.</li>
 *  <li>instance: Static instance of LocationRepository for singleton pattern.</li>
 * </ul>
 *
 * @author Parham Modirniya
 */
public class LocationRepository {
    private static final String TAG = "LocationRepository";
    private static LocationRepository instance;

    public final ObservableList<Country> allCountries = getAllCountries();

    public final ObservableList<SPR> allSPR = getAllSPR();

    /**
     * Private constructor for the LocationRepository class.
     * Initiates log with the class tag.
     */
    private LocationRepository() {
        Logs.initLog(TAG);
    }

    /**
     * Retrieves the singleton instance of LocationRepository.
     * If the instance does not exist, it is created.
     *
     * @return The singleton instance of LocationRepository.
     */
    public static LocationRepository getInstance() {
        if (instance == null) instance = new LocationRepository();
        return instance;
    }

    /**
     * Returns the Country object with the country ID associated with the provided division ID.
     * If no Country is associated with the provided division ID, it logs an error and throws a RuntimeException.
     *
     * @param divisionId The division ID of the country to be retrieved.
     * @return The Country object associated with the provided division ID.
     */
    public Country getCountryByDivisionId(int divisionId) {
        int countryId = Objects.requireNonNull(getSPRByDivisionId(divisionId)).getCountryId();
        for (Country country : allCountries)
            if (country.getId() == countryId)
                return country;
        Logs.error(TAG,
                "There is inconsistency with data: No country associated with such Division_ID: " + divisionId);
        throw new RuntimeException();
    }

    /**
     * Returns the SPR object with the provided division ID.
     * If no SPR is associated with the provided division ID, it logs an error and throws a RuntimeException.
     *
     * @param divisionId The division ID of the SPR to be retrieved.
     * @return The SPR object associated with the provided division ID.
     */
    public SPR getSPRByDivisionId(int divisionId) {
        for (SPR spr : allSPR)
            if (spr.getDivisionId() == divisionId)
                return spr;
        Logs.error(TAG,
                "There is inconsistency with data: No SPR associated with such Division_ID: " + divisionId);
        throw new RuntimeException();
    }

    /**
     * Fetches all countries from the database and stores them in an ObservableList.
     * If an SQL error occurs during this process, it logs an error.
     *
     * @return An ObservableList of all Country objects.
     */
    private ObservableList<Country> getAllCountries() {
        Logs.info(TAG, "Getting all countries");
        ObservableList<Country> olResult = FXCollections.observableArrayList();
        try {
            String strStatement = "SELECT Country_ID, Country FROM client_schedule.countries";
            var rsResult = SqlHelper.executeForResult(strStatement);
            while (rsResult.next()) {
                olResult.add(new Country(
                        rsResult.getInt("Country_ID"),
                        rsResult.getString("Country")));
            }
            rsResult.close();
        } catch (SQLException e) {
            Logs.error(TAG, "Exception occurred while executing getAllCountries()");
        }
        return olResult;
    }

    /**
     * Fetches all SPRs associated with the provided country ID from the database and stores them in an ObservableList.
     * If an SQL error occurs during this process, it logs an error.
     *
     * @param countryId The ID of the country whose SPRs are to be retrieved.
     * @return An ObservableList of all SPR objects associated with the provided country ID.
     */
    public ObservableList<SPR> getAllSPRByCountryId(int countryId) {
        Logs.info(TAG, "Getting all SPR by CountryId");
        String strStatement =
                "SELECT Division_ID,Country_ID, Division " +
                "FROM client_schedule.first_level_divisions " +
                "WHERE Country_ID = " + countryId;
        return getSPR(strStatement);
    }

    /**
     * Fetches all SPRs from the database and stores them in an ObservableList.
     * If an SQL error occurs during this process, it logs an error.
     *
     * @return An ObservableList of all SPR objects.
     */
    private ObservableList<SPR> getAllSPR() {
        Logs.info(TAG, "Getting all SPR");
        String strStatement = "SELECT Division_ID,Country_ID, Division " +
                              "FROM client_schedule.first_level_divisions";
        return getSPR(strStatement);
    }

    /**
     * Fetches all SPRs according to the provided SQL statement and stores them in an ObservableList.
     * If an SQL error occurs during this process, it logs an error.
     *
     * @param strStatement The SQL statement to execute.
     * @return An ObservableList of all SPR objects.
     */
    private ObservableList<SPR> getSPR(String strStatement) {

        ObservableList<SPR> olResult = FXCollections.observableArrayList();
        try {
            var rsResult = SqlHelper.executeForResult(strStatement);
            while (rsResult.next()) {
                olResult.add(new SPR(
                        rsResult.getInt("Division_ID"),
                        rsResult.getInt("Country_ID"),
                        rsResult.getString("Division")));
            }
            rsResult.close();
        } catch (SQLException e) {
            Logs.error(TAG, "Exception occurred while executing getAllSPR()");
        }
        return olResult;
    }
}
