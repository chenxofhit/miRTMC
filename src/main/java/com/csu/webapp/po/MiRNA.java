package com.csu.webapp.po;


/**
 * 
 * @author chenx
 * 
 * @since 2020-01-15 11:49:58
 *
 */
public class MiRNA {

	private Integer  miRNA_id;
	
	private String miRNA_name;

	public Integer getMiRNA_id() {
		return miRNA_id;
	}

	public void setMiRNA_id(Integer miRNA_id) {
		this.miRNA_id = miRNA_id;
	}

	public String getMiRNA_name() {
		return miRNA_name;
	}

	public void setMiRNA_name(String miRNA_name) {
		this.miRNA_name = miRNA_name;
	}

	@Override
	public String toString() {
		return "MiRNA [miRNA_id=" + miRNA_id + ", miRNA_name=" + miRNA_name + "]";
	}
	
	
	
}
