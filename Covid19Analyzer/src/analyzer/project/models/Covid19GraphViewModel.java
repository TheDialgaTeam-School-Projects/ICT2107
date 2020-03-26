package analyzer.project.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Covid19GraphViewModel {
    private final Covid19Repository covid19Repository;

    public Covid19GraphViewModel() throws IOException {
        covid19Repository = new Covid19Repository(Covid19Database.getInstance());
    }

    public List<String> getCovid19Countries() {
        final List<String> countries = new ArrayList<>();

        for (Covid19Case covid19Case : covid19Repository.getCovid19CasesByCountry()) {
            countries.add(covid19Case.getCountry());
        }

        return countries;
    }

    public Covid19Case getCovid19CaseByCountry(String country) {
        for (Covid19Case covid19Case : covid19Repository.getCovid19CasesByCountry()) {
            if (covid19Case.getCountry().contentEquals(country)) return covid19Case;
        }

        return null;
    }
}
