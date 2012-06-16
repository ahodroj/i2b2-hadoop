package net.hodroj.i2b2.query;

public class I2B2QueryItemValueConstraint {
	private float opLeft;
	private float opRight;
	private ItemValueOperatorType operator;
	
	
	public I2B2QueryItemValueConstraint(float opLeft, float opRight, ItemValueOperatorType operator) {
		this.opLeft = opLeft;
		this.opRight = opRight;
		this.operator = operator;
	}


	public float getOpLeft() {
		return opLeft;
	}


	public void setOpLeft(float opLeft) {
		this.opLeft = opLeft;
	}


	public float getOpRight() {
		return opRight;
	}


	public void setOpRight(float opRight) {
		this.opRight = opRight;
	}


	public ItemValueOperatorType getOperator() {
		return operator;
	}


	public void setOperator(ItemValueOperatorType operator) {
		this.operator = operator;
	}
	
	
}
