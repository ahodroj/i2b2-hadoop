package net.hodroj.i2b2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

import net.hodroj.i2b2.input.PatientObservationFact;
import net.hodroj.i2b2.query.I2B2QueryDefinition;
import net.hodroj.i2b2.query.I2B2QueryItem;
import net.hodroj.i2b2.query.I2B2QueryItemValueConstraint;
import net.hodroj.i2b2.query.I2B2QueryPanel;

import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class CrcQueryReducer extends Reducer<Text, PatientObservationFact, IntWritable, NullWritable> {
	
	private I2B2QueryDefinition query = null;
	private int patientNum = 0;

	@Override
	public void setup(Context context) {
		String queryFile = context.getConfiguration().get("query-file");
		try {
			Path[] measureCacheFiles = DistributedCache.getLocalCacheFiles(context.getConfiguration());
			if(null != measureCacheFiles && measureCacheFiles.length > 0) {
				for(Path cachePath : measureCacheFiles) {
					if(cachePath.toString().contains("query.xml")) {
						BufferedReader fileReader = new BufferedReader(new FileReader(cachePath.toString()));
						StringBuffer result = new StringBuffer();
						try {
							String line;
							while((line = fileReader.readLine()) != null) {
								result.append(line);
							}
							
							// Serialize the query logic
							query = new I2B2QueryDefinition(result.toString());
							
						} finally {
							fileReader.close();
						}
					}
				}
			}
		} catch(Exception e) {
			System.out.println("Error while reading cache files for reducer");
			System.out.println("Excetion: " + e);
		}
	}
	

	public void reduce(Text key, Iterable<PatientObservationFact> values, Context context) throws IOException, InterruptedException {
		
		// TODO: Parallelize this in a fork-join approach
		boolean matches = true;
		for(I2B2QueryPanel panel : query.getPanels()) {
			matches = panelMatches(panel, values) && !panel.isExcluded();
			if(!matches) {
				break;
			}
		}
		
		if(matches) {
			context.write(new IntWritable(patientNum),  NullWritable.get());
			
		}
		
	}
	
	private boolean panelMatches(I2B2QueryPanel panel, Iterable<PatientObservationFact> values) {
		
		int occurs = 0;
	
		
		for(PatientObservationFact fact : values) {
			patientNum = fact.getPatientNum();
			
			if(occurs >=  panel.getOccurrence()) {
				break;
			}
			
			for(I2B2QueryItem item : panel.getItems()) {
				if(fact.getValTypeCd().startsWith(item.getKey()) && factMeetsConstraints(fact, panel.getStartDate(), panel.getEndDate(), item.getValueConstraint())) {
					occurs += 1;
					break;
				}
			}
		}
		
		return occurs >= panel.getOccurrence();
	}

	/*
	 * Checks  whether the patient meets all the item criteria
	 */
	private boolean factMeetsConstraints(PatientObservationFact fact, Date startDate, Date endDate,
												I2B2QueryItemValueConstraint valueConstraint) {
		// Check start date
		if(startDate != null)  {
			if(fact.getStartDate().before(startDate))
				return false;
		}
		
		// Check end date
		if(endDate != null) {
			if(fact.getStartDate().after(endDate))
				return false;
		}
		
	
		if(valueConstraint != null) {
			float nval = fact.getNvalNum();
			float left = valueConstraint.getOpLeft();
			float right = valueConstraint.getOpRight();
			
			switch(valueConstraint.getOperator()) {
				case GT:
					if(!(nval > left)) return false;
					break;
				case LT:
					if(!(nval < left)) return false;
					break;
				case GTEQ:
					if(!(nval >= left)) return false;
					break;
				case LTEQ:
					if(!(nval <= left)) return false;
					break;
				case BETWEEN:
					if(!(nval >= left && nval <= right)) return false;
					break;
			}
		}
		
		return true;
	}
	
	
	
	
}
