/**
 * This will map all the confirmed covid 19 patients by country.
 */
public class Covid19ConfirmedMapper extends AbstractCovid19Mapper {
    @Override
    protected String getType() {
        return "confirmed";
    }
}
