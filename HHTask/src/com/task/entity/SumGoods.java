package com.task.entity;

import java.io.Serializable;

/**
 * SumGoods generated by MyEclipse Persistence Tools
 */

public class SumGoods implements java.io.Serializable {
		private static final long serialVersionUID = 1L;
	// Fields

	private SumGoodsId id;

	// Constructors

	/** default constructor */
	public SumGoods() {
	}

	/** full constructor */
	public SumGoods(SumGoodsId id) {
		this.id = id;
	}

	// Property accessors

	public SumGoodsId getId() {
		return this.id;
	}

	public void setId(SumGoodsId id) {
		this.id = id;
	}

}