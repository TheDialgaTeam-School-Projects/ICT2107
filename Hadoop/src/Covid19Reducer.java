import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class reduce the mapper into appropriate keys and values.
 * KEYIN (Text): [DATA_LEVEL] [COUNTRY] [STATE]
 * VALUEIN (Text): [LAT] [LONG] [DATA_TYPE] [DATA]
 *
 * KEYOUT (Text): [DATA_LEVEL] [COUNTRY] [STATE]
 *
 * DATA_LEVEL == State
 * VALUEOUT (Text): [LAT] [LONG] [CONFIRMED] [DEATHS] [RECOVERED]
 *
 * DATA_LEVEL == Country
 * VALUEOUT (Text): [CONFIRMED] [DEATHS] [RECOVERED]
 */
public class Covid19Reducer extends Reducer<Text, Text, Text, Text> {
    private final StringBuilder valueStringBuilder = new StringBuilder();
    private final Text valueText = new Text();

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        final String[] keyArray = key.toString().split("\t");

        String latitude = null;
        String longitude = null;

        final ArrayList<Long> confirmed = new ArrayList<>();
        final ArrayList<Long> deaths = new ArrayList<>();
        final ArrayList<Long> recovered = new ArrayList<>();

        for (Text value : values) {
            String[] valueArray = value.toString().split("\t");

            if (latitude == null) latitude = valueArray[0];
            if (longitude == null) longitude = valueArray[1];

            if (valueArray[2].contentEquals(Covid19Constants.DATA_TYPE_CONFIRMED)) {
                sumAllValues(confirmed, valueArray);
            } else if (valueArray[2].contentEquals(Covid19Constants.DATA_TYPE_DEATHS)) {
                sumAllValues(deaths, valueArray);
            } else if (valueArray[2].contentEquals(Covid19Constants.DATA_TYPE_RECOVERED)) {
                sumAllValues(recovered, valueArray);
            }
        }

        valueStringBuilder.setLength(0);

        if (keyArray[0].contentEquals(Covid19Constants.DATA_LEVEL_STATE)) {
            valueStringBuilder.append(latitude);
            valueStringBuilder.append('\t');
            valueStringBuilder.append(longitude);
        }

        for (long data : confirmed) {
            valueStringBuilder.append('\t');
            valueStringBuilder.append(data);
        }

        for (long data : deaths) {
            valueStringBuilder.append('\t');
            valueStringBuilder.append(data);
        }

        for (long data : recovered) {
            valueStringBuilder.append('\t');
            valueStringBuilder.append(data);
        }

        if (valueStringBuilder.indexOf("\t") == 0) {
            valueStringBuilder.deleteCharAt(0);
        }

        valueText.set(valueStringBuilder.toString());
        context.write(key, valueText);
    }

    private void sumAllValues(List<Long> type, String[] data) {
        if (type.size() == 0) {
            type.addAll(Collections.nCopies(data.length - 3, 0L));
        }

        for (int i = 3; i < data.length; i++) {
            type.set(i - 3, type.get(i - 3) + Long.parseLong(data[i]));
        }
    }
}
