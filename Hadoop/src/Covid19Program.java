import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class Covid19Program {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration configuration = new Configuration();

        Job job = Job.getInstance(configuration, "Covid19Program");
        job.setJarByClass(Covid19Program.class);

        job.setReducerClass(Covid19Reducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        Path confirmedInputPath = new Path("hdfs://localhost:9000/covid/input/time_series_19-covid-Confirmed.csv");
        Path deathsInputPath = new Path("hdfs://localhost:9000/covid/input/time_series_19-covid-Deaths.csv");
        Path recoveredInputPath = new Path("hdfs://localhost:9000/covid/input/time_series_19-covid-Recovered.csv");

        MultipleInputs.addInputPath(job, confirmedInputPath, TextInputFormat.class, Covid19ConfirmedMapper.class);
        MultipleInputs.addInputPath(job, deathsInputPath, TextInputFormat.class, Covid19DeathsMapper.class);
        MultipleInputs.addInputPath(job, recoveredInputPath, TextInputFormat.class, Covid19RecoveredMapper.class);

        Path outputPath = new Path("hdfs://localhost:9000/covid/output/time_series_19");
        outputPath.getFileSystem(configuration).delete(outputPath, true);

        FileOutputFormat.setOutputPath(job, outputPath);

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
