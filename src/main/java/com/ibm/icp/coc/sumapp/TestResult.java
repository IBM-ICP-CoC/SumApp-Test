package com.ibm.icp.coc.sumapp;

public class TestResult {
	
	private int op1;
	private int op2;
	private String resultString;
	private boolean titleFound;
	private boolean passed;
	
	public int getOp1() {
		return op1;
	}
	public void setOp1(int op1) {
		this.op1 = op1;
	}
	public int getOp2() {
		return op2;
	}
	public void setOp2(int op2) {
		this.op2 = op2;
	}
	public String getResultString() {
		return resultString;
	}
	public void setResultString(String resultString) {
		this.resultString = resultString;
	}
	public boolean isPassed() {
		return passed;
	}
	public void setPassed(boolean passed) {
		this.passed = passed;
	}
	public boolean isTitleFound() {
		return titleFound;
	}
	public void setTitleFound(boolean titleFound) {
		this.titleFound = titleFound;
	}
	
	@Override
	public String toString() {
		String overallResult = "passed";
		if( !passed ) {
			overallResult = "*FAILED*";
		}
		String titleFoundStr = "  OK ";
		if( !titleFound ) {
			titleFoundStr = " FAIL";
		}
		String output = String.format("%3d %3d  [%-10s] %5s --> %7s", op1, op2, resultString, titleFoundStr, overallResult );
		return output;
		
	}

}
