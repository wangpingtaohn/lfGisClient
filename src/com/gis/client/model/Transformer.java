package com.gis.client.model;

public class Transformer extends CommonObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int id;
	
	private String desc;
	
	//型号
	private int modelNumber;
	
	//厂家
	private String manufacturers;
	
	//额定容率
	private int ratedCapacitance;
	
	//中继板号
	private int  relayNumber;
	
	private double longitude;
	
	private double latitude;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getModelNumber() {
		return modelNumber;
	}

	public void setModelNumber(int modelNumber) {
		this.modelNumber = modelNumber;
	}

	public String getManufacturers() {
		return manufacturers;
	}

	public void setManufacturers(String manufacturers) {
		this.manufacturers = manufacturers;
	}

	public int getRatedCapacitance() {
		return ratedCapacitance;
	}

	public void setRatedCapacitance(int ratedCapacitance) {
		this.ratedCapacitance = ratedCapacitance;
	}

	public int getRelayNumber() {
		return relayNumber;
	}

	public void setRelayNumber(int relayNumber) {
		this.relayNumber = relayNumber;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	@Override
	public int getNumber() {
		return super.getNumber();
	}

	@Override
	public void setNumber(int number) {
		super.setNumber(number);
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
	
	@Override
	public int getBoard() {
		return super.getBoard();
	}
	
	@Override
	public void setBoard(int board) {
		super.setBoard(board);
	}
}
