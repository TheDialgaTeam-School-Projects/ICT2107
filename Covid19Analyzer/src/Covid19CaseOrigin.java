import java.util.List;

public class Covid19CaseOrigin {
    private String state;

    private String country;

    private double lat;

    private double lon;

    private List<Covid19CaseAmount> caseAmounts;

    public Covid19CaseOrigin(String state, String country, double lat, double lon, List<Covid19CaseAmount> caseAmounts) {
        this.state = state;
        this.country = country;
        this.lat = lat;
        this.lon = lon;
        this.caseAmounts = caseAmounts;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public List<Covid19CaseAmount> getCaseAmounts() {
        return caseAmounts;
    }
}
