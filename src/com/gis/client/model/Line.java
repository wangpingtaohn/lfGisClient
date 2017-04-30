package com.gis.client.model;

import java.util.List;

public class Line extends CommonObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int lineNumber;
	
	private String isPower;
	
	private List<Double> longitudeList;
	
	private List<Double> latitudeList;

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public String getIsPower() {
		return isPower;
	}

	public void setIsPower(String isPower) {
		this.isPower = isPower;
	}

	public List<Double> getLongitudeList() {
		return longitudeList;
	}

	public void setLongitudeList(List<Double> longitudeList) {
		this.longitudeList = longitudeList;
	}

	public List<Double> getLatitudeList() {
		return latitudeList;
	}

	public void setLatitudeList(List<Double> latitudeList) {
		this.latitudeList = latitudeList;
	}
	@Override
	public int getNumber() {
		return super.getNumber();
	}

	@Override
	public void setNumber(int number) {
		super.setNumber(lineNumber);
	}

	@Override
	public int getSuperId() {
		return super.getSuperId();
	}

	@Override
	public void setSuperId(int superId) {
		super.setSuperId(superId);
	}

	@Override
	public String getName() {
		return super.getName();
	}

	@Override
	public void setName(String name) {
		super.setName(name);
	}
}
