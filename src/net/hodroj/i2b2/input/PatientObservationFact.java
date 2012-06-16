package net.hodroj.i2b2.input;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableUtils;

public class PatientObservationFact implements WritableComparable<PatientObservationFact>, Cloneable {
	
	private int encounterNum;
	private int patientNum;
	private String conceptCd;
	private int providerId;
	private Date startDate;
	private String valTypeCd;
	private float nvalNum;
	private String valueFlagCd;
	private Date endDate;
	
	
	public PatientObservationFact() {
		
	}
	
	public PatientObservationFact(int encounterNum,
								  int patientNum,
								  String conceptCd,
								  int providerId,
								  Date startDate,
								  String valTypeCd,
								  float nvalNum,
								  String valueFlagCd,
								  Date endDate) {
		this.encounterNum = encounterNum;
		this.patientNum = patientNum;
		this.conceptCd = conceptCd;
		this.providerId = providerId;
		this.startDate = startDate;
		this.valTypeCd = valTypeCd;
		this.nvalNum = nvalNum;
		this.valueFlagCd = valueFlagCd;
		this.endDate = endDate;
	}
	
	@Override
	public void readFields(DataInput in) throws IOException {
		encounterNum = WritableUtils.readVInt(in);
		patientNum = WritableUtils.readVInt(in);
		conceptCd = WritableUtils.readString(in);
		providerId = WritableUtils.readVInt(in);
		startDate = createDateFromString(WritableUtils.readString(in));
		valTypeCd = WritableUtils.readString(in);
		String nvalStr = WritableUtils.readString(in);
		nvalNum = Float.parseFloat(nvalStr);
		valueFlagCd = WritableUtils.readString(in);
		endDate = createDateFromString(WritableUtils.readString(in));
	}

	@Override
	public void write(DataOutput out)  {
		try {
		WritableUtils.writeVInt(out, encounterNum);
		WritableUtils.writeVInt(out, patientNum);
		WritableUtils.writeString(out, conceptCd);
		WritableUtils.writeVInt(out, providerId);
		WritableUtils.writeString(out, (startDate == null) ? null : new SimpleDateFormat("dd-MMM-yy").format(startDate));
		WritableUtils.writeString(out, valTypeCd);
		WritableUtils.writeString(out, String.valueOf(nvalNum));
		WritableUtils.writeString(out, valueFlagCd);
		WritableUtils.writeString(out, (endDate == null) ? null : new SimpleDateFormat("dd-MMM-yy").format(endDate));
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	private Date createDateFromString(String dateString) {
		DateFormat formatter = new SimpleDateFormat("dd-MMM-yy");
		try {
			return (Date)formatter.parse(dateString);
		} catch (Exception e) {
			return null;
		}
	}

	
	@Override
	public int compareTo(PatientObservationFact fact) {
		return CompareToBuilder.reflectionCompare(this, fact);
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public int getEncounterNum() {
		return encounterNum;
	}

	public void setEncounterNum(int encounterNum) {
		this.encounterNum = encounterNum;
	}

	public int getPatientNum() {
		return patientNum;
	}

	public void setPatientNum(int patientNum) {
		this.patientNum = patientNum;
	}

	public String getConceptCd() {
		return conceptCd;
	}

	public void setConceptCd(String conceptCd) {
		this.conceptCd = conceptCd;
	}

	public int getProviderId() {
		return providerId;
	}

	public void setProviderId(int providerId) {
		this.providerId = providerId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getValTypeCd() {
		return valTypeCd;
	}

	public void setValTypeCd(String valTypeCd) {
		this.valTypeCd = valTypeCd;
	}

	public float getNvalNum() {
		return nvalNum;
	}

	public void setNvalNum(int nvalNum) {
		this.nvalNum = nvalNum;
	}

	public String getValueFlagCd() {
		return valueFlagCd;
	}

	public void setValueFlagCd(String valueFlagCd) {
		this.valueFlagCd = valueFlagCd;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
}
