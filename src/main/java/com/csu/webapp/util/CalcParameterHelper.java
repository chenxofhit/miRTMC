package com.csu.webapp.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.csu.webapp.dto.CalcParameter;

/**
 * 
 * CalcParameter序列化辅助类 
 * 
 * @author chenx
 * @since 2020-01-17 10:16:09
 * 
 *
 */
public class CalcParameterHelper {
	
	private String fileName;
	
	private String fileFastaName;
	
	public CalcParameterHelper(){
	}
	
	public CalcParameterHelper(String fileName, String fileFastaName){
		this.fileName=fileName;
		this.fileFastaName = fileFastaName;

	}
	
	/**
	 * 保存 fasta 文件到 对应目录
	 * 
	 * @param p
	 */
	public void saveFastaToFile(CalcParameter p) {
		if (null != p.getSequence() && !p.getSequence().isEmpty()) {
			try {
				File tempFile = new File(fileFastaName);
				if (!tempFile.exists()) {
					tempFile.mkdirs();
				}
				System.out.println("fasta file path: " + tempFile.getAbsolutePath());

				BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile));
				while (null != p.getSequence()) {
					bw.write(p.getSequence());
				}
				bw.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void saveObjToFile(CalcParameter p){
		try {
			ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream(fileName));
			
			oos.writeObject(p); 
			oos.close();
			
			saveFastaToFile(p);

		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public CalcParameter getObjFromFile(){
		try {
			ObjectInputStream ois=new ObjectInputStream(new FileInputStream(fileName));
			CalcParameter sysParameter=(CalcParameter)ois.readObject();
			return sysParameter;  
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}