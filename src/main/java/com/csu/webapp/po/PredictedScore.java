package com.csu.webapp.po;

/**
 * miRTMC计算结果
 * 
 * @author kayzhao
 * @author chenx
 * 
 */
public class PredictedScore{
	
	private Integer rank;
	
	private Integer geneId;

	private String geneName;
	
	private Integer miRNAId;
	
	private String miRNAName;
	
	private float score;

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public Integer getGeneId() {
		return geneId;
	}

	public void setGeneId(Integer geneId) {
		this.geneId = geneId;
	}

	public String getGeneName() {
		return geneName;
	}

	public void setGeneName(String geneName) {
		this.geneName = geneName;
	}

	public Integer getMiRNAId() {
		return miRNAId;
	}

	public void setMiRNAId(Integer miRNAId) {
		this.miRNAId = miRNAId;
	}

	public String getMiRNAName() {
		return miRNAName;
	}

	public void setMiRNAName(String miRNAName) {
		this.miRNAName = miRNAName;
	}

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}


}
