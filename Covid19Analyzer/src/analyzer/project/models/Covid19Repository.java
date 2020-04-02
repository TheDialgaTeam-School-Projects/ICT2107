// Yong Jian Ming

package analyzer.project.models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class Covid19Repository {
    public static final int SORT_BY_CONFIRMED = 0;
    public static final int SORT_BY_DEATHS = 1;
    public static final int SORT_BY_RECOVERED = 2;
    public static final int SORT_BY_ACTIVE = 3;

    private final ICovid19Database covid19Database;

    public Covid19Repository(ICovid19Database covid19Database) {
        this.covid19Database = covid19Database;
    }

    public List<Covid19Case> getCovid19CasesByCountry() {
        return new ArrayList<>(covid19Database.getCovid19CasesByCountry());
    }

    public int getTotalAmountOfDays() {
        return covid19Database.getCovid19CasesByCountry().get(0).getConfirmed().length;
    }

    public long getTotalConfirmedCases(int numberOfDays) {
        long total = 0;

        for (Covid19Case covid19Case : covid19Database.getCovid19CasesByCountry()) {
            total += covid19Case.getConfirmed(numberOfDays);
        }

        return total;
    }

    public long getTotalDeathCases(int numberOfDays) {
        long total = 0;

        for (Covid19Case covid19Case : covid19Database.getCovid19CasesByCountry()) {
            total += covid19Case.getDeaths(numberOfDays);
        }

        return total;
    }

    public long getTotalRecoveredCases(int numberOfDays) {
        long total = 0;

        for (Covid19Case covid19Case : covid19Database.getCovid19CasesByCountry()) {
            total += covid19Case.getRecovered(numberOfDays);
        }

        return total;
    }

    public long getTotalActiveCases(int numberOfDays) {
        long total = 0;

        for (Covid19Case covid19Case : covid19Database.getCovid19CasesByCountry()) {
            total += covid19Case.getActive(numberOfDays);
        }

        return total;
    }

    public List<Covid19Case> getCovid19CasesByCountry(int numberOfDays, int sortBy) {
        final List<Covid19Case> covid19Cases = new ArrayList<>(covid19Database.getCovid19CasesByCountry());
        covid19Cases.sort(getCovid19CaseComparator(numberOfDays, sortBy));
        return covid19Cases;
    }

    public List<Covid19Case> getCovid19CasesByState(int numberOfDays, int sortBy) {
        final List<Covid19Case> covid19Cases = new ArrayList<>(covid19Database.getCovid19CasesByState());
        covid19Cases.sort(getCovid19CaseComparator(numberOfDays, sortBy));
        return covid19Cases;
    }

    private Comparator<Covid19Case> getCovid19CaseComparator(int numberOfDays, int sortBy) {
        return (a, b) -> {
            final long aValue;
            final long bValue;

            switch (sortBy) {
                case SORT_BY_CONFIRMED:
                    aValue = a.getConfirmed(numberOfDays);
                    bValue = b.getConfirmed(numberOfDays);
                    break;

                case SORT_BY_DEATHS:
                    aValue = a.getDeaths(numberOfDays);
                    bValue = b.getDeaths(numberOfDays);
                    break;

                case SORT_BY_RECOVERED:
                    aValue = a.getRecovered(numberOfDays);
                    bValue = b.getRecovered(numberOfDays);
                    break;

                case SORT_BY_ACTIVE:
                    aValue = a.getActive(numberOfDays);
                    bValue = b.getActive(numberOfDays);
                    break;

                default:
                    return 0;
            }

            if (aValue == bValue) return 0;
            return aValue > bValue ? -1 : 1;
        };
    }
}
