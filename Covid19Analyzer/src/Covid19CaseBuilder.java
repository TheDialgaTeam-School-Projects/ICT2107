import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Covid19CaseBuilder {
    private final List<Covid19CaseAmount> caseAmounts = new ArrayList<>();
    private String state;
    private String country;
    private double lat;
    private double lon;

    public Covid19CaseBuilder setLocation(String state, String country, double lat, double lon) {
        this.state = state;
        this.country = country;
        this.lat = lat;
        this.lon = lon;

        return this;
    }

    public Covid19CaseBuilder setCases(long[] confirmed, long[] deaths, long[] recovered) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2020, Calendar.JANUARY, 22);

        for (int i = 0; i < confirmed.length; i++) {
            Date date = calendar.getTime();
            caseAmounts.add(new Covid19CaseAmount(date, confirmed[i], deaths[i], recovered[i]));
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        return this;
    }

    public Covid19CaseOrigin build() {
        return new Covid19CaseOrigin(state, country, lat, lon, caseAmounts);
    }
}
