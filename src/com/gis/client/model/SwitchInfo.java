package com.gis.client.model;

import java.io.Serializable;


public class SwitchInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int id;

	// 板号
	private int boardNumber;

	private int switchStatus;

	private int isSave;

	private int isFlash;

	private int aL;

	private int bL;

	private int cL;

	private int oL;

	private String time;
	
	private int voltage;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getBoardNumber() {
		return boardNumber;
	}

	public void setBoardNumber(int boardNumber) {
		this.boardNumber = boardNumber;
	}

	public int getSwitchStatus() {
		return switchStatus;
	}

	public void setSwitchStatus(int switchStatus) {
		this.switchStatus = switchStatus;
	}

	public int getIsSave() {
		return isSave;
	}

	public void setIsSave(int isSave) {
		this.isSave = isSave;
	}

	public int getIsFlash() {
		return isFlash;
	}

	public void setIsFlash(int isFlash) {
		this.isFlash = isFlash;
	}

	public int getaL() {
		return aL;
	}

	public void setaL(int aL) {
		this.aL = aL;
	}

	public int getbL() {
		return bL;
	}

	public void setbL(int bL) {
		this.bL = bL;
	}

	public int getcL() {
		return cL;
	}

	public void setcL(int cL) {
		this.cL = cL;
	}

	public int getoL() {
		return oL;
	}

	public void setoL(int oL) {
		this.oL = oL;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
	
	public void setVoltage(int voltage) {
		this.voltage = voltage;
	}
	
	public int getVoltage() {
		return voltage;
	}
	

}
