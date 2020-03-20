package analyzer.project.models;

import java.util.List;

public interface ICovid19Database {
    List<Covid19Case> getCovid19CasesByCountry();
    List<Covid19Case> getCovid19CasesByState();
}
