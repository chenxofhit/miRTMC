package com.csu.webapp.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.csu.webapp.po.MiRNA;

/**
 * 
 * MiRNA 的查询层
 * 
 * @author chenx
 *
 * @since 2020-01-15 14:38:03
 * 
 */

public class MiRNADao {

	private final static Logger logger = Logger.getLogger(MiRNADao.class);

	private static final String MIRNA_DB_FILE = "/miRTMC/mtis7_miRNA_list_final_withid.txt";

	private static MiRNADao instance = null;

	private static HashMap<String, MiRNA> miRNAMapByName;

	private static HashMap<Integer, MiRNA> miRNAMapById;

	/**
	 * Singleton pattern
	 * 
	 * @return
	 * @throws IOException
	 */
	public static synchronized MiRNADao getInstance() {

		if (instance == null) {
			
			logger.info("initialize the MiRNADao instance...");

			
			instance = new MiRNADao();
			miRNAMapByName = new HashMap<String, MiRNA>();
			miRNAMapById = new HashMap<Integer, MiRNA>();

			Resource miRNASheet = new ClassPathResource(MIRNA_DB_FILE);
			boolean isSkipHeader = true;

			BufferedReader br;
			try {
				br = new BufferedReader(new InputStreamReader(miRNASheet.getInputStream()));

				int i = 0;
				String line;
				while ((line = br.readLine()) != null) {
					if (i == 0 && isSkipHeader) {
						i++;
						continue;
					}
					MiRNA miRNA = new MiRNA();
					String[] words = line.split("\t");
					miRNA.setMiRNA_id(Integer.valueOf(words[0]));
					miRNA.setMiRNA_name(words[1]);

					miRNAMapByName.put(miRNA.getMiRNA_name(), miRNA);
					miRNAMapById.put(miRNA.getMiRNA_id(), miRNA);

					i++;
				}
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		return instance;
	}

	public static HashMap<String, MiRNA> getMiRNAMapByName() {
		return miRNAMapByName;
	}

	public static void setMiRNAMapByName(HashMap<String, MiRNA> miRNAMapByName) {
		MiRNADao.miRNAMapByName = miRNAMapByName;
	}

	public static HashMap<Integer, MiRNA> getMiRNAMapById() {
		return miRNAMapById;
	}

	public static void setMiRNAMapById(HashMap<Integer, MiRNA> miRNAMapById) {
		MiRNADao.miRNAMapById = miRNAMapById;
	}

	/**
	 * 
	 * JUnit case
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		MiRNADao miRNADao = MiRNADao.getInstance();
		MiRNA miRNA = miRNADao.getMiRNAMapByName().get("hsa-miR-548d-5p");
		System.out.println(miRNA);
	}
}
