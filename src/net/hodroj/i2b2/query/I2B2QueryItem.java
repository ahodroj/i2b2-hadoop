package net.hodroj.i2b2.query;

public class I2B2QueryItem {

	private int level;
	private String name;
	private String key;
	private I2B2QueryItemValueConstraint valueConstraint;
	
	public I2B2QueryItem(int level, String name, String key, I2B2QueryItemValueConstraint valueConstraint) {
		this.level = level;
		this.name = name;
		this.key = key;
		this.valueConstraint = valueConstraint;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public I2B2QueryItemValueConstraint getValueConstraint() {
		return valueConstraint;
	}

	public void setValueConstraint(I2B2QueryItemValueConstraint valueConstraint) {
		this.valueConstraint = valueConstraint;
	}
	
	
}
