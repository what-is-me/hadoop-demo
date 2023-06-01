package com.example;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public final class App {
    public static class MyMapper extends Mapper<LongWritable, Text, Text, IntWritable>{
		protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException{
			String[] row = value.toString().split(":");
			System.out.println(row[0] + ":" + Integer.parseInt(row[2]));
			context.write(new Text(row[0]), new IntWritable(Integer.parseInt(row[2])));
		}
	}

	public static class MyReduce extends Reducer<Text, IntWritable, Text, DoubleWritable>{
		private static final int[] GPABOUNDS = {95, 90, 85, 80, 75, 70, 65, 60};
		private static final double MAXGPA = 4.5;
		private static final double DECREASE = 0.5;
		private double calcGPA(int score){
			double gpa = MAXGPA;
			for(int gpaCheck : GPABOUNDS){
				if(score >= gpaCheck) return gpa;
				gpa -= DECREASE;
			}
			return 0;
		}
		protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException{
			double sum = 0;
			int num = 0;
			for(IntWritable value: values){
				sum += calcGPA(value.get());
				num += 1;
			}
			context.write(key, new DoubleWritable(sum/num));
		}
	}

	public static void main(String[] args) throws InterruptedException, IOException, ClassNotFoundException{
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "top");
		job.setJobName("top");
		job.setJarByClass(App.class);
		job.setMapperClass(MyMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		job.setReducerClass(MyReduce.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		job.waitForCompletion(true);
	}
}
