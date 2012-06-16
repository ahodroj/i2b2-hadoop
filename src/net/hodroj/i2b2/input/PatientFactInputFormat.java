package net.hodroj.i2b2.input;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.LineRecordReader;


public class PatientFactInputFormat extends FileInputFormat<LongWritable, PatientObservationFact> {

	@Override
	public RecordReader<LongWritable, PatientObservationFact> createRecordReader(InputSplit split, TaskAttemptContext context) throws IOException,
			InterruptedException {
		
		return new PatientFactRecordReader();
	}

	
	public static class PatientFactRecordReader extends RecordReader<LongWritable, PatientObservationFact> {

		public static final String PATIENT_FACT_SEPARATOR = ",";
		private LineRecordReader reader = new LineRecordReader();
		private PatientObservationFact value;
		
		@Override
		public void initialize(InputSplit split, TaskAttemptContext context)
				throws IOException, InterruptedException {
			reader.initialize(split, context);
			
			
		}
		
		@Override
		public boolean nextKeyValue() throws IOException, InterruptedException {
			if(reader.nextKeyValue()) {
				parseFactLine();
				return true;
			} else {
				value = null;
				return false;
			}
		}
		
		private void parseFactLine() {
			String line = reader.getCurrentValue().toString();
			String[] tokens = StringUtils.splitPreserveAllTokens(line, PATIENT_FACT_SEPARATOR);
			try {
				int encounterNum = StringUtils.trimToNull(tokens[0]) == null ? null : Integer.valueOf(tokens[0]);
				int patientNum = StringUtils.trimToNull(tokens[1]) == null ? null : Integer.valueOf(tokens[1]);
				String conceptCd = StringUtils.trimToNull(tokens[2]);
				int providerId = StringUtils.trimToNull(tokens[3]) == null ? null : Integer.valueOf(tokens[3]);
				Date startDate = StringUtils.trimToNull(tokens[4]) == null ? null : createDateFromString(tokens[3]); 
				String valTypeCd = StringUtils.trimToNull(tokens[6]);
				float nvalNum = StringUtils.trimToNull(tokens[8]) == null ? 0 : Float.parseFloat(tokens[8]);
				String valueFlagCd = StringUtils.trimToNull(tokens[9]);
				Date endDate = StringUtils.trimToNull(tokens[12]) == null ? null : createDateFromString(tokens[12]);
			
				// create the patient fact object
				value = new PatientObservationFact(encounterNum, patientNum, conceptCd, providerId, startDate, valTypeCd, nvalNum, valueFlagCd, endDate);
		
			} catch(Exception e) {
				System.out.println("Error while parsing line: " + line);
				System.out.println("Exception: " + e);
			}
		}
		
		private Date createDateFromString(String dateString) {
			DateFormat formatter = new SimpleDateFormat("dd-MMM-yy");
			try {
				return (Date)formatter.parse(dateString);
			} catch (ParseException e) {
				return null;
			}
		}
		
		
		@Override
		public LongWritable getCurrentKey() throws IOException,
				InterruptedException {
			return reader.getCurrentKey();
		}
		
		@Override
		public PatientObservationFact getCurrentValue() throws IOException,
				InterruptedException {
			return value;
		}
		
		@Override
		public void close() throws IOException {
			reader.close();
		}


		@Override
		public float getProgress() throws IOException, InterruptedException {
			return reader.getProgress();
		}

	}
	
	

}
