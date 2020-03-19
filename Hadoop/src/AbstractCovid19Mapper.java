import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

public abstract class AbstractCovid19Mapper extends Mapper<LongWritable, Text, Text, Text> {

    private final Text keyText = new Text();
    private final Text valueText = new Text();

    /**
     * Get the type of cases for covid 19 mapper.
     * @return The type of cases for covid 19 mapper.
     */
    protected abstract String getType();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        // CSV Header should be ignored and they start at the first index.
        if (key.get() == 0) return;

        try (CSVReader csvReader = new CSVReader(new StringReader(value.toString()))) {
            List<String[]> csvRows = csvReader.readAll();
            String[] csvColumns = csvRows.get(0);

            // Country/Region
            String country = csvColumns[1];

            if (country.endsWith("*")) {
                country = country.substring(0, country.length() - 1);
            }

            keyText.set(country);

            StringBuilder confirmedCases = new StringBuilder(getType());

            // Cases starting from 1/22/20 (MM/DD/YY) to the latest.
            for (int i = 4; i < csvColumns.length; i++) {
                confirmedCases.append("\t");
                confirmedCases.append(csvColumns[i]);
            }

            valueText.set(confirmedCases.toString());

            context.write(keyText, valueText);
        } catch (CsvException e) {
            e.printStackTrace();
        }
    }
}
