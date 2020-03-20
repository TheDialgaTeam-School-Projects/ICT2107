public class Covid19ConfirmedMapper extends AbstractCovid19Mapper {
    @Override
    protected String getDataType() {
        return Covid19Constants.DATA_TYPE_CONFIRMED;
    }
}
