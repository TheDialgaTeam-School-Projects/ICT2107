package analyzer.project.models;

public final class Covid19Case {
    private final String country;
    private final String state;

    private final double latitude;
    private final double longitude;

    private final long[] confirmed;
    private final long[] deaths;
    private final long[] recovered;

    public Covid19Case(String[] data) {
        if (data == null) throw new NullPointerException("data is null");

        if (data[0].contentEquals(Covid19Constants.DATA_LEVEL_COUNTRY)) {
            country = data[1];
            state = "";

            latitude = 0;
            longitude = 0;

            final int dataLength = (data.length - 2) / 3;

            confirmed = new long[dataLength];
            deaths = new long[dataLength];
            recovered = new long[dataLength];

            for (int i = 0; i < dataLength; i++) {
                confirmed[i] = Long.parseLong(data[i + 2]);
                deaths[i] = Long.parseLong(data[i + 2 + dataLength]);
                recovered[i] = Long.parseLong(data[i + 2 + dataLength * 2]);
            }
        } else if (data[0].contentEquals(Covid19Constants.DATA_LEVEL_STATE)) {
            country = data[1];
            state = data[2];

            latitude = Double.parseDouble(data[3]);
            longitude = Double.parseDouble(data[4]);

            final int dataLength = (data.length - 5) / 3;

            confirmed = new long[dataLength];
            deaths = new long[dataLength];
            recovered = new long[dataLength];

            for (int i = 0; i < dataLength; i++) {
                confirmed[i] = Long.parseLong(data[i + 5]);
                deaths[i] = Long.parseLong(data[i + 5 + dataLength]);
                recovered[i] = Long.parseLong(data[i + 5 + dataLength * 2]);
            }
        } else {
            throw new IllegalArgumentException("data is invalid");
        }
    }

    public String getCountry() {
        return country;
    }

    public String getState() {
        return state;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public long[] getConfirmed() {
        return confirmed;
    }

    public long getConfirmed(int numberOfDays) {
        return confirmed[numberOfDays];
    }

    public long getDeaths(int numberOfDays) {
        return deaths[numberOfDays];
    }

    public long getRecovered(int numberOfDays) {
        return recovered[numberOfDays];
    }

    public long getActive(int numberOfDays) {
        return confirmed[numberOfDays] - deaths[numberOfDays] - recovered[numberOfDays];
    }

    public double[] getRecoveredPercentage() {
        double[] percentage = new double[recovered.length];

        for (int i = 0; i < recovered.length; i++) {
            if (confirmed[i] == 0) {
                percentage[i] = 0;
            } else {
                percentage[i] = (recovered[i] / (double) confirmed[confirmed.length - 1]) * 100;
            }
        }

        return percentage;
    }
}
