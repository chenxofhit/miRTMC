package com.csu.webapp.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.csu.webapp.po.Gene;

/**
 * 
 * Gene 的查询层
 * 
 * @author chenx
 * 
 * @since 2020-01-15 14:58:42
 *
 */

public class GeneDao {

	private final static Logger logger = Logger.getLogger(GeneDao.class);

	private static final String GENE_DB_FILE = "/miRTMC/mtis7_utr_list_final2.txt";

	private static GeneDao instance = null;

	private static HashMap<Integer, Gene> geneMapById;

	private static HashMap<String, Gene> geneMapByUltraName1;

	private static HashMap<String, Gene> geneMapByGeneSymbol;

	private static HashMap<Integer, Gene> geneMapByGsId;

	/**
	 * Singleton pattern
	 * 
	 * @return
	 * @throws IOException
	 */
	public static synchronized GeneDao getInstance() {

		if (instance == null) {

			logger.info("initialize the GeneDao instance...");
			
			instance = new GeneDao();
			geneMapById = new HashMap<Integer, Gene>();
			geneMapByUltraName1 = new HashMap<String, Gene>();
			geneMapByGeneSymbol = new HashMap<String, Gene>();
			geneMapByGsId = new HashMap<Integer, Gene>();

			Resource geneSheet = new ClassPathResource(GENE_DB_FILE);
			boolean isSkipHeader = true;
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(geneSheet.getInputStream()));

				int i = 0;
				String line;
				while ((line = br.readLine()) != null) {
					if (i == 0 && isSkipHeader) {
						i++;
						continue;
					}
					Gene gene = new Gene();
					String[] words = line.split("\t");
					gene.setGene_id(Integer.valueOf(words[0]));
					gene.setUtraname1(words[1]);
					gene.setGene_symbol(words[2]);
					gene.setGs_id(words[3]);

					geneMapById.put(Integer.valueOf(words[0]), gene);
					geneMapByUltraName1.put(words[1], gene);
					geneMapByGeneSymbol.put(words[2], gene);
					geneMapByGsId.put(Integer.valueOf(words[3]), gene);

					i++;
				}
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		return instance;
	}

	public static HashMap<Integer, Gene> getGeneMapById() {
		return geneMapById;
	}

	public static void setGeneMapById(HashMap<Integer, Gene> geneMapById) {
		GeneDao.geneMapById = geneMapById;
	}

	public static HashMap<String, Gene> getGeneMapByUltraName1() {
		return geneMapByUltraName1;
	}

	public static void setGeneMapByUltraName1(HashMap<String, Gene> geneMapByUltraName1) {
		GeneDao.geneMapByUltraName1 = geneMapByUltraName1;
	}

	public static HashMap<String, Gene> getGeneMapByGeneSymbol() {
		return geneMapByGeneSymbol;
	}

	public static void setGeneMapByGeneSymbol(HashMap<String, Gene> geneMapByGeneSymbol) {
		GeneDao.geneMapByGeneSymbol = geneMapByGeneSymbol;
	}

	public static HashMap<Integer, Gene> getGeneMapByGsId() {
		return geneMapByGsId;
	}

	public static void setGeneMapByGsId(HashMap<Integer, Gene> geneMapByGsId) {
		GeneDao.geneMapByGsId = geneMapByGsId;
	}

	/**
	 * 
	 * JUnit case
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		GeneDao geneDao = GeneDao.getInstance();
		Gene gene = geneDao.getGeneMapById().get(2);
		System.out.println(gene);
	}
}
