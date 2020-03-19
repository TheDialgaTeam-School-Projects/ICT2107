import java.util.Date;

public class Covid19CaseAmount {
    private Date date;

    private long confirmed;

    private long deaths;

    private long recovered;

    public Covid19CaseAmount(Date date, long confirmed, long deaths, long recovered) {
        this.date = date;
        this.confirmed = confirmed;
        this.deaths = deaths;
        this.recovered = recovered;
    }

    public Date getDate() {
        return date;
    }

    public long getConfirmed() {
        return confirmed;
    }

    public long getDeaths() {
        return deaths;
    }

    public long getRecovered() {
        return recovered;
    }
}
