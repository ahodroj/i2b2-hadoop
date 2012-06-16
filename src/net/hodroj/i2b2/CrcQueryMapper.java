package net.hodroj.i2b2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;

import net.hodroj.i2b2.input.PatientObservationFact;

import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;


public class CrcQueryMapper extends Mapper<LongWritable, PatientObservationFact, Text, PatientObservationFact> {
	
	private Text rk = new Text();
	
	private HashMap<String,String> ontologyHash = new HashMap<String,String>(); 
	
	
	@Override 
	public void setup(Context context) {
		
		SortedMap<String,String> ontMap = new TreeMap<String,String>();
	
		
		try {
			Path[] ontologyCacheFiles = DistributedCache.getLocalCacheFiles(context.getConfiguration());
			if(null != ontologyCacheFiles && ontologyCacheFiles.length > 0) {
				for(Path cachePath : ontologyCacheFiles) {
					if(cachePath.toString().contains("concept_dimension.csv")) {
						BufferedReader conceptReader = new BufferedReader(new FileReader(cachePath.toString()));
						try {
							String line;
							while((line = conceptReader.readLine()) != null) {
								String [] concept = line.split(",");
								try {
									ontologyHash.put(concept[0], concept[1]);
								} catch(Exception e) { }
							}
						} finally {
							conceptReader.close();
						}
					}
				}
			}
		} catch(IOException ioe) {
			System.err.println("Error reading ontology from distributed cache");
			System.err.println(ioe.toString());
		}
	}
	
	@Override 
	protected void map(LongWritable key, PatientObservationFact value, Context context) throws IOException, InterruptedException {
		
		rk.set(String.valueOf(value.getPatientNum()));
		
		// Annotate the patients with the ontology path
		value.setValTypeCd(ontologyHash.get(value.getConceptCd()));
		
		context.write(rk, value);
	}
	
}