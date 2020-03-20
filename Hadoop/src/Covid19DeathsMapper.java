public class Covid19DeathsMapper extends AbstractCovid19Mapper {
    @Override
    protected String getDataType() {
        return Covid19Constants.DATA_TYPE_DEATHS;
    }
}
