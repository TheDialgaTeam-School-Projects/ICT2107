import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.io.StringReader;

/**
 * This class maps the csv file content into two separate keys and values.
 * KEYIN (LongWritable): File offset from where the KEYVALUE is.
 * VALUEIN (Text): Content of the line.
 *
 * KEYOUT (Text): [DATA_LEVEL] [COUNTRY] [STATE]
 * VALUEOUT (Text): [LAT] [LONG] [DATA_TYPE] [DATA]
 */
public abstract class AbstractCovid19Mapper extends Mapper<LongWritable, Text, Text, Text> {
    private final Text keyText = new Text();
    private final Text valueText = new Text();
    private final StringBuilder valueStringBuilder = new StringBuilder();

    /**
     * Get the data type of the cases for covid-19 mapper.
     * @return The data type of the cases for covid-19 mapper.
     */
    protected abstract String getDataType();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        // CSV Header should be ignored and they start at the first index.
        if (key.get() == 0) return;

        try (CSVReader csvReader = new CSVReader(new StringReader(value.toString()))) {
            final String[] csvColumns = csvReader.readNext();

            // Province/State
            String state = csvColumns[0];
            if (state == null || state.isEmpty()) state = "";

            // Country/Region
            String country = csvColumns[1];

            // Remove extra * character in the country name.
            if (country.endsWith("*")) {
                country = country.substring(0, country.length() - 1);
            }

            // Lat
            final String latitude = csvColumns[2];

            // Long
            final String longitude = csvColumns[3];

            valueStringBuilder.setLength(0);
            valueStringBuilder.append(latitude);
            valueStringBuilder.append('\t');
            valueStringBuilder.append(longitude);
            valueStringBuilder.append('\t');
            valueStringBuilder.append(getDataType());

            // Cases starting from 1/22/20 (MM/DD/YY) to the latest.
            for (int i = 4; i < csvColumns.length; i++) {
                valueStringBuilder.append("\t");
                valueStringBuilder.append(csvColumns[i].isEmpty() ? "0" : csvColumns[i]);
            }

            valueText.set(valueStringBuilder.toString());

            // Serialize data for state level
            keyText.set(Covid19Constants.DATA_LEVEL_STATE + "\t" + country + "\t" + state);
            context.write(keyText, valueText);

            // Serialize data for country level
            keyText.set(Covid19Constants.DATA_LEVEL_COUNTRY + "\t" + country);
            context.write(keyText, valueText);
        } catch (CsvException e) {
            e.printStackTrace();
        }
    }
}
