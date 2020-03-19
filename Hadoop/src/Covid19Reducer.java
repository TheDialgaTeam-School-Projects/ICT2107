import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Covid19Reducer extends Reducer<Text, Text, Text, Text> {

    private final Text keyText = new Text();
    private final Text valueText = new Text();

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        List<Long> confirmed = new ArrayList<>();
        List<Long> deaths = new ArrayList<>();
        List<Long> recovered = new ArrayList<>();

        for (Text value : values) {
            String[] columns = value.toString().split("\t");

            if (columns[0].contentEquals("confirmed")) {
                sumAllValues(confirmed, columns);
            } else if (columns[0].contentEquals("deaths")) {
                sumAllValues(deaths, columns);
            } else if (columns[0].contentEquals("recovered")) {
                sumAllValues(recovered, columns);
            }
        }

        keyText.set(key + "\t" + "confirmed");
        valueText.set(getValueText(confirmed));

        context.write(keyText, valueText);

        keyText.set(key + "\t" + "deaths");
        valueText.set(getValueText(deaths));

        context.write(keyText, valueText);

        keyText.set(key + "\t" + "recovered");
        valueText.set(getValueText(recovered));

        context.write(keyText, valueText);
    }

    private void sumAllValues(List<Long> type, String[] data) {
        for (int i = 1; i < data.length; i++) {
            if (type.size() != data.length - 1) {
                type.add(Long.parseLong(data[i]));
            } else {
                type.set(i - 1, type.get(i - 1) + Long.parseLong(data[i]));
            }
        }
    }

    private String getValueText(List<Long> data) {
        StringBuilder value = new StringBuilder();

        for (long longValue : data) {
            value.append(longValue);
            value.append("\t");
        }

        value.deleteCharAt(value.lastIndexOf("\t"));

        return value.toString();
    }
}
