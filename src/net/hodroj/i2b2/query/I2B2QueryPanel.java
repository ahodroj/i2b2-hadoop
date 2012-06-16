package net.hodroj.i2b2.query;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import net.hodroj.i2b2.input.PatientObservationFact;

public class I2B2QueryPanel {

	private int panelNumber;
	
	private boolean excluded;
	
	private int occurrence = 1;
	
	private Date startDate;
	
	private Date endDate;
	
	private ArrayList<I2B2QueryItem> items;
	
	public I2B2QueryPanel(int panelNumber, int occurrence, boolean excluded, Date startDate, Date endDate, ArrayList<I2B2QueryItem> items) {
		this.panelNumber = panelNumber;
		this.occurrence = occurrence;
		this.excluded = excluded;
		this.startDate = startDate;
		this.endDate = endDate;
		this.items = items;
	}

	public int getPanelNumber() {
		return panelNumber;
	}

	public void setPanelNumber(int panelNumber) {
		this.panelNumber = panelNumber;
	}

	public boolean isExcluded() {
		return excluded;
	}

	public void setExcluded(boolean excluded) {
		this.excluded = excluded;
	}

	public int getOccurrence() {
		return occurrence;
	}

	public void setOccurrence(int occurrence) {
		this.occurrence = occurrence;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public ArrayList<I2B2QueryItem> getItems() {
		return items;
	}
	
	
}
