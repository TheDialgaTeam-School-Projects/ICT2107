// Guo Zhi Yong and Huang YiYi

package analyzer.project.models;

import java.io.IOException;
import java.util.List;

public class Covid19AnalyzerViewModel {
    private final Covid19Repository covid19Repository;

    public Covid19AnalyzerViewModel() throws IOException {
        covid19Repository = new Covid19Repository(Covid19Database.getInstance());
    }

    public int getTotalAmountOfDays() {
        return covid19Repository.getTotalAmountOfDays();
    }

    public long getTotalConfirmedCases(int numberOfDays) {
        return covid19Repository.getTotalConfirmedCases(numberOfDays);
    }

    public long getTotalDeathCases(int numberOfDays) {
        return covid19Repository.getTotalDeathCases(numberOfDays);
    }

    public long getTotalRecoveredCases(int numberOfDays) {
        return covid19Repository.getTotalRecoveredCases(numberOfDays);
    }

    public long getTotalActiveCases(int numberOfDays) {
        return covid19Repository.getTotalActiveCases(numberOfDays);
    }

    public List<Covid19Case> getCovid19CasesByCountry(int numberOfDays, int sortBy) {
        return covid19Repository.getCovid19CasesByCountry(numberOfDays, sortBy);
    }

    public List<Covid19Case> getCovid19CasesByState(int numberOfDays, int sortBy) {
        return covid19Repository.getCovid19CasesByState(numberOfDays, sortBy);
    }
}
