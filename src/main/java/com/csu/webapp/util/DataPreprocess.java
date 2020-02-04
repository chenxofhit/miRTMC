package com.csu.webapp.util;

//--------------------------------------------------------------------------------------------------
// 
// Description: Implementation of Needleman-Wunsch global alignment.
// This code is written by Ren-Xiang Yan in China Agricultural University and is originally based on 
// the fortran implementation from Dr. Yang Zhang (http://zhanglab.ccmb.med.umich.edu/NW-align/).
// Last update is in 2010/08/14. 
//
//  Usage:
//      java -jar NWAlign.jar F1.fasta F2.fasta  (align two sequences in fasta file)
//		java -jar NWAlign.jar F1.pdb F2.pdb    1 (align two sequences in PDB file)
//		java -jar NWAlign.jar F.fasta F.pdb  2 (align sequences 1 in fasta and 1 in pdb)
//		java -jar NWAlign.jar GKDGL EVADELVSE    3 (align two sequences in plain text)
//		java -jar NWAlign.jar GKDGL F.fasta  4 (align sequences 1 in text and 1 in fasta)
//		java -jar NWAlign.jar GKDGL F.pdb    5 (align sequences 1 in text and 1 in pdb)
//  
//   Note: You also could complied the code by yourself.
//         Decompress the NWAlign.jar file and you can get the source code in the NWAlign folder.
//   The program can be compiled by 
//              javac NWAlign.java
//   Then you could use the program by the following commands:
//		java NWAlign F1.fasta F2.fasta  (align two sequences in fasta file)
//		java NWAlign F1.pdb F2.pdb    1 (align two sequences in PDB file)
//		java NWAlign file1.fasta file2.pdb  2 (align sequences 1 in fasta and 1 in pdb)
//		java NWAlign GKDGL EVADELVSE    3 (align two sequences in plain text)
//		java NWAlign GKDGL F.fasta  4 (align sequences 1 in text and 1 in fasta)
//		java NWAlign GKDGL F.pdb    5 (align sequences 1 in text and 1 in pdb)            
//-----------------x-------------------x-------------------x--------------------------------------------

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.csu.webapp.controller.Controllers;

/**
 * Fasta预处理
 * 
 * @author kayzhao
 * @author chenx
 * 
 *
 */
public class DataPreprocess {

	private static final Logger logger = Logger.getLogger(DataPreprocess.class);

	
	/**
	 * 检验上传文件是否为Fasta格式
	 * 
	 * @param file
	 * @return
	 */
	@SuppressWarnings("resource")
	public static boolean readFastaOrRawSequence(File file) {
		try {
			// 先判断file格式
			if (!file.getName().endsWith(".txt")) {
				return false;
			}
			Scanner scanner = new Scanner(file);
			while (scanner.hasNext()) {
				String line = scanner.nextLine().trim();
				// 跳过空行 和 >开头的行
				if (line.length() <= 0 || line.startsWith(">")) {
					continue;
				} else {
					// 如果长度小于200或者包含非法字符
					if (line.length() < 200 || !line.matches("[agctAGCT]*"))
						return false;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 读取PDB格式数据
	 * 
	 * @param file
	 * @return
	 */
	public static String readPDB(String file) // read a sequence from a PDB file
	{
		String seq = "";
		String line = "";
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null) {
				if (line.startsWith("TER"))
					break;
				if (line.startsWith("ATO")) {
					// System.out.println(line);
					if (line.substring(13, 16).replaceAll("\\s+", "")
							.endsWith("CA")) {
						seq = seq
								+ NameMap(line.substring(17, 20).toUpperCase());
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return seq;
	}

	/**
	 * Name Map
	 * 
	 * @param residule
	 * @return
	 */
	public static String NameMap(String residule) // Map a three-letter
													// abbreviation to a
													// single-letter code.
	{
		String[] aa = new String[] { "ALA", "ARG", "ASN", "ASP", "CYS", "GLN",
				"GLU", "GLY", "HIS", "ILE", "LEU", "LYS", "MET", "PHE", "PRO",
				"SER", "THR", "TRP", "TYR", "VAL", "ASX", "GLX", "UNK" };
		String[] aaName = new String[] { "A", "R", "N", "D", "C", "Q", "E",
				"G", "H", "I", "L", "K", "M", "F", "P", "S", "T", "W", "Y",
				"V", "B", "Z", "X" };
		int i = 0;
		for (; i < aa.length; i++) {
			if (aa[i].equals(residule))
				break;
		}
		return aaName[i];
	}
}
