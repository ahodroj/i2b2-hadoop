package net.hodroj.i2b2;

import net.hodroj.i2b2.input.PatientFactInputFormat;
import net.hodroj.i2b2.input.PatientObservationFact;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class I2B2QueryJob  {
	
	public static void main(String[] args) throws Exception {
		
		if(args.length < 3) {
			System.out.println("Usage: i2b2hadoop <query.xml> <input> <output>");
			return;
		}
		run(args);
		
	}
	
	
	public static void run(String[] args) throws Exception {
		
		String queryFile = args[0];
		String input = args[1];
		String output = args[2];
		
		
		// Add the files to distributed cache
		Configuration conf = new Configuration();
		conf.set("query-file", queryFile);
		
		// Setup the distributed cache
		DistributedCache.addFileToClassPath(new Path(queryFile), conf);
		DistributedCache.addFileToClassPath(new Path("concept_dimension.csv"), conf);		
				
		
		Job job = new Job(conf);
		job.setJarByClass(I2B2QueryJob.class);
	
		// Mapper/Reducer
		job.setMapperClass(CrcQueryMapper.class);
		job.setReducerClass(CrcQueryReducer.class);
		
		
		// I/O formats
		job.setInputFormatClass(PatientFactInputFormat.class);
		job.setOutputValueClass(IntWritable.class);
		
		// Key/Value 
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(PatientObservationFact.class);
		
		FileInputFormat.setInputPaths(job, new Path(input));
		Path outputPath  = new Path(output);
		FileOutputFormat.setOutputPath(job, outputPath);
		
		outputPath.getFileSystem(conf).delete(outputPath, true);
		
		// execute
		job.waitForCompletion(true);
	}
	

}
