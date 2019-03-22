package com.hengtiansoft.bluemorpho.workbench.quartz.job.datastructure;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：Jun 6, 2018 2:27:10 PM
 */
public class Pair<T> {

	private T left;
	private T right;

	public Pair() {
	}

	public Pair(T left, T right) {
		super();
		this.left = left;
		this.right = right;
	}

	public T getLeft() {
		return left;
	}

	public void setLeft(T left) {
		this.left = left;
	}

	public T getRight() {
		return right;
	}

	public void setRight(T right) {
		this.right = right;
	}

}
