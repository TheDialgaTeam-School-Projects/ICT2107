import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Covid19Cases {
    public static final int COMPARE_MODE_CONFIRMED = 0;
    public static final int COMPARE_MODE_DEATHS = 1;
    public static final int COMPARE_MODE_RECOVERED = 2;

    private static final List<Covid19CaseOrigin> covid19CaseOriginByCountry = new ArrayList<>();
    private static final List<Covid19CaseOrigin> covid19CaseOriginByState = new ArrayList<>();

    public static int getAmountOfDays() throws IOException, CsvValidationException {
        return getCovid19CaseOriginByState().get(0).getCaseAmounts().size() - 1;
    }

    public static long getTotalConfirmedCase(int numberOfDays) throws IOException {
        long amount = 0;
        List<Covid19CaseOrigin> covid19CaseOrigins = getCovid19CaseOriginByCountry();

        for (Covid19CaseOrigin caseOrigin : covid19CaseOrigins) {
            amount += caseOrigin.getCaseAmounts().get(numberOfDays).getConfirmed();
        }

        return amount;
    }

    public static List<Covid19CaseOrigin> getTotalConfirmedCaseByCountry(int numberOfDays, int compareMode) throws IOException {
        List<Covid19CaseOrigin> covid19CaseOrigins = new ArrayList<>(getCovid19CaseOriginByCountry());
        covid19CaseOrigins.sort((a, b) -> {
            switch (compareMode) {
                case COMPARE_MODE_CONFIRMED:
                    long confirmedA = a.getCaseAmounts().get(numberOfDays).getConfirmed();
                    long confirmedB = b.getCaseAmounts().get(numberOfDays).getConfirmed();

                    if (confirmedA == confirmedB) return 0;
                    return confirmedA > confirmedB ? -1 : 1;

                case COMPARE_MODE_DEATHS:
                    long deathsA = a.getCaseAmounts().get(numberOfDays).getDeaths();
                    long deathsB = b.getCaseAmounts().get(numberOfDays).getDeaths();

                    if (deathsA == deathsB) return 0;
                    return deathsA > deathsB ? -1 : 1;

                case COMPARE_MODE_RECOVERED:
                    long recoveredA = a.getCaseAmounts().get(numberOfDays).getRecovered();
                    long recoveredB = b.getCaseAmounts().get(numberOfDays).getRecovered();

                    if (recoveredA == recoveredB) return 0;
                    return recoveredA > recoveredB ? -1 : 1;

                default:
                    return 0;
            }
        });

        return covid19CaseOrigins;
    }

    public static List<Covid19CaseOrigin> getCovid19CaseOriginByCountry() throws IOException {
        if (covid19CaseOriginByCountry.size() == 0) preloadCovid19CaseOriginByCountry();
        return covid19CaseOriginByCountry;
    }

    public static List<Covid19CaseOrigin> getCovid19CaseOriginByState() throws IOException, CsvValidationException {
        if (covid19CaseOriginByState.size() == 0) preloadCovid19CaseOriginByState();
        return covid19CaseOriginByState;
    }

    private static void preloadCovid19CaseOriginByCountry() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader("resource/time_series_19-covid-Combined.tsv"))) {
            while (true) {
                String confirmed = reader.readLine();
                if (confirmed == null) break;

                String deaths = reader.readLine();
                if (deaths == null) break;

                String recovered = reader.readLine();
                if (recovered == null) break;

                String[] confirmedData = confirmed.split("\t");
                String[] deathsData = deaths.split("\t");
                String[] recoveredData = recovered.split("\t");

                long[] confirmedLongData = new long[confirmedData.length - 2];
                long[] deathsLongData = new long[deathsData.length - 2];
                long[] recoveredLongData = new long[recoveredData.length - 2];

                for (int i = 0; i < confirmedLongData.length; i++) {
                    confirmedLongData[i] = Long.parseLong(confirmedData[i + 2]);
                    deathsLongData[i] = Long.parseLong(deathsData[i + 2]);
                    recoveredLongData[i] = Long.parseLong(recoveredData[i + 2]);
                }

                Covid19CaseBuilder builder = new Covid19CaseBuilder();
                builder.setLocation(null, confirmedData[0], 0, 0);
                builder.setCases(confirmedLongData, deathsLongData, recoveredLongData);

                covid19CaseOriginByCountry.add(builder.build());
            }
        }
    }

    private static void preloadCovid19CaseOriginByState() throws IOException, CsvValidationException {
        try (CSVReader confirmedReader = new CSVReader(new FileReader("resource/time_series_19-covid-Confirmed.csv"))) {
            try (CSVReader deathsReader = new CSVReader(new FileReader("resource/time_series_19-covid-Deaths.csv"))) {
                try (CSVReader recoveredReader = new CSVReader(new FileReader("resource/time_series_19-covid-Recovered.csv"))) {
                    confirmedReader.readNextSilently();
                    deathsReader.readNextSilently();
                    recoveredReader.readNextSilently();

                    while (true) {
                        String[] confirmedData = confirmedReader.readNext();
                        if (confirmedData == null) break;

                        String[] deathsData = deathsReader.readNext();
                        if (deathsData == null) break;

                        String[] recoveredData = recoveredReader.readNext();
                        if (recoveredData == null) break;

                        long[] confirmedLongData = new long[confirmedData.length - 4];
                        long[] deathsLongData = new long[deathsData.length - 4];
                        long[] recoveredLongData = new long[recoveredData.length - 4];

                        for (int i = 0; i < confirmedLongData.length; i++) {
                            confirmedLongData[i] = Long.parseLong(confirmedData[i + 4]);
                            deathsLongData[i] = Long.parseLong(deathsData[i + 4]);
                            recoveredLongData[i] = Long.parseLong(recoveredData[i + 4]);
                        }

                        Covid19CaseBuilder builder = new Covid19CaseBuilder();
                        builder.setLocation(confirmedData[0], confirmedData[1], Double.parseDouble(confirmedData[2]), Double.parseDouble(confirmedData[3]));
                        builder.setCases(confirmedLongData, deathsLongData, recoveredLongData);

                        covid19CaseOriginByState.add(builder.build());
                    }
                }
            }
        }
    }
}
