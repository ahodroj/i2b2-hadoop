package net.hodroj.i2b2.input;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.ReflectionUtils;

public class PatientFactOutputFormat<K, V> extends FileOutputFormat {

	protected static class PatientFactRecordWriter<K, V> extends
			RecordWriter<K, V> {

		private static final String utf8 = "UTF-8";
		private static final byte[] newline;
		private static final byte[] separator;

		static {
			try {
				newline = "\n".getBytes(utf8);
				separator = PatientFactInputFormat.PatientFactRecordReader.PATIENT_FACT_SEPARATOR
						.getBytes(utf8);

			} catch (UnsupportedEncodingException exc) {
				throw new IllegalArgumentException("Cannot find encoding");
			}
		}

		protected DataOutputStream out;

		public PatientFactRecordWriter(DataOutputStream out) {
			this.out = out;
		}

		@Override
		public synchronized void close(TaskAttemptContext context)
				throws IOException, InterruptedException {
			out.close();
		}

		@Override
		public synchronized void write(K key, V value) throws IOException,
				InterruptedException {

			boolean nullKey = key == null || key instanceof NullWritable;
			boolean nullValue = value == null || value instanceof NullWritable;

			if (nullKey && nullValue) {
				return;
			}

			if (!nullKey && key instanceof PatientObservationFact) {
				writePatientFact((PatientObservationFact) key);
				out.write(newline);
			} else if (!nullValue && value instanceof PatientObservationFact) {
				writePatientFact((PatientObservationFact) value);
				out.write(newline);
			}
		}

		private void writePatientFact(PatientObservationFact fact)
				throws IOException {
			try {
			writeString(fact.getEncounterNum());
			out.write(separator);
			writeString(fact.getPatientNum());
			out.write(separator);
			writeString(fact.getConceptCd());
			out.write(separator);
			writeString(fact.getProviderId());
			out.write(separator);
			writeDate(fact.getStartDate());
			out.write(separator);
			writeString(fact.getValTypeCd());
			out.write(separator);
			writeString(String.valueOf(fact.getNvalNum()));
			out.write(separator);
			writeString(fact.getValueFlagCd());
			out.write(separator);
			writeDate(fact.getEndDate());
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		private void writeString(int i) throws IOException {
			out.write(String.valueOf(i).getBytes(utf8));
		}
		
		private void writeString(String str) throws IOException {
			if(str != null) {
				out.write(str.getBytes(utf8));
			}
		}

		private void writeDate(Date date) throws IOException {
			if (date != null) {
				out.write(new SimpleDateFormat("dd-MMM-yy").format(date).getBytes(utf8));
			}
		}

	}
	
	@Override
	public RecordWriter<K, V> getRecordWriter(TaskAttemptContext context) throws IOException, InterruptedException {
		Configuration conf = context.getConfiguration();
		boolean isCompressed = getCompressOutput(context);
		CompressionCodec codec = null;
		
		String extension = "";
		
		if(isCompressed) {
			Class<? extends CompressionCodec> codecClass = getOutputCompressorClass(context, GzipCodec.class);
			
			codec = ReflectionUtils.newInstance(codecClass, conf);
			extension = codec.getDefaultExtension();
		}
		
		Path file = getDefaultWorkFile(context, extension);
		FileSystem fs = file.getFileSystem(conf);
		
		if(!isCompressed) {
			FSDataOutputStream fileOut = fs.create(file, false);
			return new PatientFactRecordWriter<K, V>(fileOut);
		} else {
			FSDataOutputStream fileOut = fs.create(file, false);
			return new PatientFactRecordWriter<K, V>(new DataOutputStream(codec.createOutputStream(fileOut)));
		}
		
	}
}
