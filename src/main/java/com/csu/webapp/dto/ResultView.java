package com.csu.webapp.dto;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.csu.webapp.config.Conf;
import com.csu.webapp.dao.GeneDao;
import com.csu.webapp.dao.MiRNADao;
import com.csu.webapp.po.Gene;
import com.csu.webapp.po.MiRNA;
import com.csu.webapp.po.PredictedScore;
import com.csu.webapp.type.SearchBy;
import com.csu.webapp.util.CalcParameterHelper;

/**
 * 视图查看
 * 
 * @author kayzhao
 * @author chenx 
 * 
 *
 */
public class ResultView {
	
	private final static Logger logger = Logger.getLogger(ResultView.class);

	private CalcParameter parameter;

	public ResultView() {
		this.parameter = new CalcParameter();
	}

	public CalcParameter getLdapResult() {
		return parameter;
	}

	public static String conevertToPath(String basepath, String email,
			String jobid) {
		return new String(basepath + Conf.FILE_SEPARATOR + email
				+ Conf.FILE_SEPARATOR + jobid);
	}

	public static String conevertToPathWithoutEmail(String basepath,
			String jobid) {
		return new String(basepath + Conf.FILE_SEPARATOR + jobid);
	}
	
	public static int getType(String basepath) {
		String filename = basepath + Conf.FILE_SEPARATOR + "parameter.txt";
		int type = SearchBy.miRNA_name.getCode();
		CalcParameter sysParameter = new CalcParameterHelper(filename, null).getObjFromFile();
		type = sysParameter.getType();
		return type;
	}

	public static String getContent(String basepath) {
		String filename = basepath + Conf.FILE_SEPARATOR + "parameter.txt";
		CalcParameter sysParameter = new CalcParameterHelper(filename, null).getObjFromFile();
		String content  = sysParameter.getContent();
		return content;
	}
	
	public static List<PredictedScore> readPredictedOutputText(String path)
			throws IOException {
		
		int type = ResultView.getType(path);
		String content = ResultView.getContent(path);
		
		List<PredictedScore> predictedScores = new ArrayList<PredictedScore>();
		BufferedReader br;

		String scorefile = path + "/score.txt";
		String indexfile = path + "/ind.txt";
		
		List<Float> scores = new ArrayList<Float>();
		File file = new File(scorefile);
		br = new BufferedReader(new FileReader(file));
		String line = null;
		while ((line = br.readLine()) != null) {
			scores.add(Float.valueOf(line));
		}
		br.close();

		
		file = new File(indexfile);
		br = new BufferedReader(new FileReader(file));
		line = null;
		int rank = 1;
		
		while ((line = br.readLine()) != null) {
				PredictedScore ds = new PredictedScore();
				
				if(type == SearchBy.miRNA_name.getCode()) {
					Gene gene = GeneDao.getInstance().getGeneMapByGsId().get(Integer.valueOf(line));
					
					if(null == gene) {
						logger.warn("gene id " + line  +  " does not exsit in the reference list");
						continue;
					}
					
					ds.setGeneId(gene.getGene_id());
					ds.setGeneName(gene.getGene_symbol());
					ds.setMiRNAName(content);
				}
				
				if(type == SearchBy.gene_gs_id.getCode() ||
					type == SearchBy.gene_symbol.getCode()	||
					type == SearchBy.gene_utraname.getCode()) {
					
					MiRNA miRNA = MiRNADao.getInstance().getMiRNAMapById().get(Integer.valueOf(line));
					if(null == miRNA) {
						logger.warn("miRNA id " + line  +  " does not exsit in the reference list");
						continue;
					}
					if(type == SearchBy.gene_utraname.getCode()) {
						String key = getContent(path);
						Gene gene = GeneDao.getInstance().getGeneMapByUltraName1().get(key);
						if(null == gene) {
							logger.warn("gene null, maybe in the fasta mode");
							ds.setGeneId(-1);
							ds.setGeneName(key);
						}
						else {
							ds.setGeneId(gene.getGene_id());
							ds.setGeneName(key);
						}
						
						ds.setMiRNAId(miRNA.getMiRNA_id());
						ds.setMiRNAName(miRNA.getMiRNA_name());
					}
					if(type == SearchBy.gene_symbol.getCode()) {
						String key = getContent(path);
						Gene gene = GeneDao.getInstance().getGeneMapByGeneSymbol().get(key);

						if(null == gene) {
							logger.warn("gene null, maybe in the fasta mode");
							ds.setGeneId(-1);
							ds.setGeneName(key);
						}
						else {
							ds.setGeneId(gene.getGene_id());
							ds.setGeneName(key);
						}
						
						ds.setMiRNAId(miRNA.getMiRNA_id());
						ds.setMiRNAName(miRNA.getMiRNA_name());
					}
					if(type ==  SearchBy.gene_gs_id.getCode()) {
						String key = getContent(path);
						Gene gene = null;
						
						try {
						  gene = GeneDao.getInstance().getGeneMapById().get(Integer.valueOf(key));
						  ds.setGeneId(gene.getGene_id());
						  ds.setGeneName(gene.getGene_symbol());
						  
						}catch(Exception e) {
							if(e instanceof NumberFormatException) {
								logger.warn("gene null, maybe in the fasta mode");
							    ds.setGeneId(-1);
							    ds.setGeneName(key);
							}
						}
//			
						

						
						ds.setMiRNAId(miRNA.getMiRNA_id());
						ds.setMiRNAName(miRNA.getMiRNA_name());
					}
				}
				
				ds.setRank(rank);
				ds.setScore(scores.get(rank-1));
				
				predictedScores.add(ds);
				rank++;
			}
		
		br.close();

		return predictedScores;
	}
	
	public static List<PredictedScore> readKnownOutputText(String path)
			throws IOException {
		int type = ResultView.getType(path);

		List<PredictedScore> predictedScores = new ArrayList<PredictedScore>();

		String knownfile = path + Conf.FILE_SEPARATOR +  "known.txt";
		
		File file = new File(knownfile);
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = null;
		int rank = 1;

		while ((line = br.readLine()) != null) {
				PredictedScore predictedScore = new PredictedScore();
				
				if(type == SearchBy.miRNA_name.getCode()) {
					Gene gene = GeneDao.getInstance().getGeneMapByGsId().get(Integer.valueOf(line));
					if(null == gene) {
						logger.warn("gene id " + line  +  " does not exsit in the reference list");
						continue;
					}
					predictedScore.setMiRNAName(getContent(path));
					predictedScore.setGeneId(gene.getGene_id());
					predictedScore.setGeneName(gene.getGene_symbol());
				}
				
				if(type == SearchBy.gene_gs_id.getCode() ||
					type == SearchBy.gene_symbol.getCode()	||
					type == SearchBy.gene_utraname.getCode()) {
					
					MiRNA miRNA = MiRNADao.getInstance().getMiRNAMapById().get(Integer.valueOf(line));
					if(null == miRNA) {
						logger.warn("miRNA id " + line  +  " does not exsit in the reference list");
						continue;
					}
					if(type == SearchBy.gene_utraname.getCode()) {
					Gene gene_id = GeneDao.getInstance().getGeneMapByUltraName1().get(getContent(path));
					predictedScore.setGeneId(gene_id.getGene_id());
					predictedScore.setGeneName(getContent(path));
					predictedScore.setMiRNAId(miRNA.getMiRNA_id());
					predictedScore.setMiRNAName(miRNA.getMiRNA_name());
				}
					if(type == SearchBy.gene_symbol.getCode()) {
						Gene gene_id = GeneDao.getInstance().getGeneMapByGeneSymbol().get(getContent(path));
						predictedScore.setGeneId(gene_id.getGene_id());
						predictedScore.setGeneName(getContent(path));
						predictedScore.setMiRNAId(miRNA.getMiRNA_id());
						predictedScore.setMiRNAName(miRNA.getMiRNA_name());
					}
					if(type ==  SearchBy.gene_gs_id.getCode()) {
						Gene gene_id = GeneDao.getInstance().getGeneMapById().get(Integer.valueOf(getContent(path)));
						predictedScore.setGeneId(Integer.valueOf(getContent(path)));
						predictedScore.setGeneName(gene_id.getGene_symbol());
						predictedScore.setMiRNAId(miRNA.getMiRNA_id());
						predictedScore.setMiRNAName(miRNA.getMiRNA_name());
					}
					//predictedScore.setGeneName(getContent(path));
					//predictedScore.setMiRNAId(miRNA.getMiRNA_id());
					//predictedScore.setMiRNAName(miRNA.getMiRNA_name());
				}
				predictedScore.setRank(rank);
				predictedScores.add(predictedScore);
				rank++;
			}
		return predictedScores;
	}

	/**
	 * 首字母大写
	 * 
	 * @param str
	 * @return
	 */
	public static String firstLetterToUpper(String str) {
		str = str.trim();
		char[] array = str.toCharArray();
		if (array[0] >= 'a' && array[0] <= 'z')
			array[0] -= 32;
		for (int i = 1; i < array.length; i++) {
			if (array[i - 1] == ' ') {
				if (array[i] >= 'a' && array[i] <= 'z')
					array[i] -= 32;
			}
		}
		return String.valueOf(array);
	}
//

	/**
	 * 根据top筛选结果的个数
	 * 
	 * @param dsLists
	 * @param top_num
	 * @return
	 */
	public static List<PredictedScore> topFliter(List<PredictedScore> dsList,
			int top_num) {
		top_num = top_num < dsList.size() ? top_num : dsList.size();
		dsList = dsList.subList(0, top_num);
		return dsList;
	}


}
