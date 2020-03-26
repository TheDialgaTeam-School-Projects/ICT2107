package analyzer.project.models;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class Covid19Database implements ICovid19Database {
    private static final String DATA_FILE = "data/time_series_19-covid.tsv";
    private static Covid19Database instance;

    private final List<Covid19Case> covid19CasesByCountry = new ArrayList<>();
    private final List<Covid19Case> covid19CasesByState = new ArrayList<>();

    public Covid19Database() throws IOException {
        try (final BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            String data;

            while ((data = reader.readLine()) != null) {
                final String[] splitData = data.split("\t");

                if (splitData[0].contentEquals(Covid19Constants.DATA_LEVEL_COUNTRY)) {
                    covid19CasesByCountry.add(new Covid19Case(splitData));
                } else if (splitData[0].contentEquals(Covid19Constants.DATA_LEVEL_STATE)) {
                    covid19CasesByState.add(new Covid19Case(splitData));
                }
            }
        }
    }

    public static Covid19Database getInstance() throws IOException {
        if (instance == null) {
            synchronized (Covid19Database.class) {
                if (instance == null) {
                    instance = new Covid19Database();
                }
            }
        }

        return instance;
    }

    public List<Covid19Case> getCovid19CasesByCountry() {
        return covid19CasesByCountry;
    }

    public List<Covid19Case> getCovid19CasesByState() {
        return covid19CasesByState;
    }
}
