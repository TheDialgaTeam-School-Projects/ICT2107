package analyzer.project.models;

import java.io.IOException;
import java.util.List;

public class Covid19AnalyzerViewModel {
    private final Covid19Repository covid19Repository;

    public Covid19AnalyzerViewModel() throws IOException {
        covid19Repository = new Covid19Repository(new Covid19Database());
    }

    public int getTotalAmountOfDays() {
        return covid19Repository.getTotalAmountOfDays();
    }

    public long getTotalConfirmedCases(int numberOfDays) {
        return covid19Repository.getTotalConfirmedCases(numberOfDays);
    }

    public List<Covid19Case> getCovid19CasesByCountry(int numberOfDays, int sortBy) {
        return covid19Repository.getCovid19CasesByCountry(numberOfDays, sortBy);
    }

    public List<Covid19Case> getCovid19CasesByState(int numberOfDays, int sortBy) {
        return covid19Repository.getCovid19CasesByState(numberOfDays, sortBy);
    }
}
