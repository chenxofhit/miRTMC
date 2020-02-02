package com.csu.webapp.po;


/**
 * 
 * @author chenx
 * * @since 2020-01-15 11:49:58
 *
 */
public class Gene {

	private Integer gene_id;
	
	private String utraname1;
	
	private String gene_symbol;
	
	private String gs_id;

	public Integer getGene_id() {
		return gene_id;
	}

	public void setGene_id(Integer gene_id) {
		this.gene_id = gene_id;
	}

	public String getUtraname1() {
		return utraname1;
	}

	public void setUtraname1(String utraname1) {
		this.utraname1 = utraname1;
	}

	public String getGene_symbol() {
		return gene_symbol;
	}

	public void setGene_symbol(String gene_symbol) {
		this.gene_symbol = gene_symbol;
	}

	public String getGs_id() {
		return gs_id;
	}

	public void setGs_id(String gs_id) {
		this.gs_id = gs_id;
	}

	@Override
	public String toString() {
		return "Gene [gene_id=" + gene_id + ", utraname1=" + utraname1 + ", gene_symbol=" + gene_symbol + ", gs_id="
				+ gs_id + "]";
	}
}
