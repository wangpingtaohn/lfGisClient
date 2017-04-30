package com.gis.client.model;

import java.io.Serializable;

public class CommonObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// 编号
	private int number;

	//板号
	private int board;

	// 主线编号
	private int superId;

	// 设备名称
	private String name;

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getSuperId() {
		return superId;
	}

	public void setSuperId(int superId) {
		this.superId = superId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setBoard(int board) {
		this.board = board;
	}

	public int getBoard() {
		return board;
	}

}
