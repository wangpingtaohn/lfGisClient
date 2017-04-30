package com.gis.client.model;

/**
 * @author wpt
 *  开关信息
 */
public class Switch extends CommonObject {

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
	
	//生产日期
	private String madeDate;
	
	//机构型号
	private String organizationNumber;
	
	//FTU型号
	private String ftuNumber;
	
	//出厂编号
	private int madeNumber;
	
	//额定电压
	private int ratedVatige;
	
	//过流值
	private int electricityValue;
	
	//速流值
	private int speedValue;
	
	//整定时间
	private int intTime;
	
	//角度
	private int angle;

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

	public String getMadeDate() {
		return madeDate;
	}

	public void setMadeDate(String madeDate) {
		this.madeDate = madeDate;
	}

	public String getOrganizationNumber() {
		return organizationNumber;
	}

	public void setOrganizationNumber(String organizationNumber) {
		this.organizationNumber = organizationNumber;
	}

	public String getFtuNumber() {
		return ftuNumber;
	}

	public void setFtuNumber(String ftuNumber) {
		this.ftuNumber = ftuNumber;
	}

	public int getMadeNumber() {
		return madeNumber;
	}

	public void setMadeNumber(int madeNumber) {
		this.madeNumber = madeNumber;
	}

	public int getRatedVatige() {
		return ratedVatige;
	}

	public void setRatedVatige(int ratedVatige) {
		this.ratedVatige = ratedVatige;
	}

	public int getElectricityValue() {
		return electricityValue;
	}

	public void setElectricityValue(int electricityValue) {
		this.electricityValue = electricityValue;
	}

	public int getSpeedValue() {
		return speedValue;
	}

	public void setSpeedValue(int speedValue) {
		this.speedValue = speedValue;
	}

	public int getIntTime() {
		return intTime;
	}

	public void setIntTime(int intTime) {
		this.intTime = intTime;
	}

	public int getAngle() {
		return angle;
	}

	public void setAngle(int angle) {
		this.angle = angle;
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
	public void setBoard(int board) {
		super.setBoard(board);
	}
	@Override
	public int getBoard() {
		return super.getBoard();
	}
	
}
