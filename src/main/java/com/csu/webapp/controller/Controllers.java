package com.csu.webapp.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.csu.webapp.dao.GeneDao;
import com.csu.webapp.dao.MiRNADao;
import com.csu.webapp.dto.CalcParameter;
import com.csu.webapp.dto.ResultView;
import com.csu.webapp.po.Gene;
import com.csu.webapp.po.MiRNA;
import com.csu.webapp.po.MyThread;
import com.csu.webapp.po.PredictedScore;
import com.csu.webapp.type.SearchBy;
import com.csu.webapp.util.CalcParameterHelper;
import com.csu.webapp.util.DataPreprocess;
import com.csu.webapp.util.DoJobOfflineThread;
import com.csu.webapp.util.DoJobThread;
import com.csu.webapp.util.SequenceExtractor;
import com.csu.webapp.util.ThreadPoolUtils;

/**
 * 控制器Controller
 * 
 * @author kayzhao
 * @author chenx
 * 
 * 
 * @since  2020-01-15 20:53:09
 *
 */
@Controller
public class Controllers {

	private final Logger logger = Logger.getLogger(Controllers.class);

	public static final String BASE_PATH = ContextLoader.getCurrentWebApplicationContext().getServletContext()
			.getRealPath("/");

	/**
	 * 进入首页
	 * 
	 * @return
	 */
	@RequestMapping("/index")
	public String indexInit(ModelMap model) {
		return "index";
	}

	/**
	 *  查询输入的key 是否已经存在于结果文件中
	 *  
	 * @param option 选项
	 * @param content 内容
	 * @return
	 */
	@ResponseBody
	@RequestMapping(method = RequestMethod.POST, value = "/checkQueryKeyExsit")
	public boolean checkQueryKeyExsit(@RequestParam(required = true) Integer option,
			@RequestParam(required = true) String content) {

		MiRNA miRNA = null;

		if (option.intValue() == SearchBy.miRNA_name.getCode()) { // Search by MiRNA name
			miRNA = MiRNADao.getInstance().getMiRNAMapByName().get(content);
			if (null != miRNA) {
				return true;
			}
		}

		Gene gene = null;
		if (option.intValue() == SearchBy.gene_utraname.getCode()) { // Search by gene utraname
			gene = GeneDao.getInstance().getGeneMapByUltraName1().get(content);
			if (null != gene) {
				return true;
			}
		}
			if (option.intValue() == SearchBy.gene_symbol.getCode()) { // Search by gene symbol
				gene = GeneDao.getInstance().getGeneMapByGeneSymbol().get(content);
				if (null != gene) {
					return true;
				}
			}

			if (option.intValue() == SearchBy.gene_gs_id.getCode()) { // Search by gene gs
				// gene = GeneDao.getInstance().getGeneMapByGsId().get(content);
				try {
				gene = GeneDao.getInstance().getGeneMapById().get(Integer.valueOf(content));
				if (null != gene) {
					return true;
				}
			}catch(Exception e) {
				return false;
			}
			}
		return false;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/index/submit")
	public String submit(ModelMap model, @RequestParam(required = true) Integer option,
			@RequestParam(required = false) String content, @RequestParam(required = false) String sequence,@RequestParam(required = false) String email) {
		
		String jobid = Long.toString(System.currentTimeMillis());
		CalcParameter parameter = new CalcParameter();
		
		MiRNA miRNA = null;
		
		if (option.intValue() == SearchBy.miRNA_name.getCode()) { // Search by MiRNA name
			miRNA = MiRNADao.getInstance().getMiRNAMapByName().get(content);

			if (null != miRNA) { // The miRNA is in our provided file

				parameter.setType(option.intValue());
				parameter.setContent(content);
				parameter.setJobid(jobid);
				parameter.setMiRNAId(miRNA.getMiRNA_id());
				String resultPath = BASE_PATH + "userdata/" + parameter.getJobid();
				
				// 如果不存在 创建目录
				File userFileDir = new File(resultPath);
				if (!userFileDir.exists()) {
					userFileDir.mkdirs();
				}
				parameter.setResultPath(resultPath);

				MyThread dojob = new DoJobThread(parameter, "Thread-" + jobid);
				
				ExecutorService threadPool = ThreadPoolUtils.getThreadPool();
				threadPool.submit(dojob);

				String msg = "<p>Thank you for using miRTMC webserver. Please wait for about 1 minute while your job is in process.<br><br>"
						+ "Note: <br>" + "<p>JobID：" + jobid
						+ "<p>Do not close this page until the job finished, otherwise you cannot check the result. ";
				model.addAttribute("message", msg);
//			model.addAttribute("email", email);
				model.addAttribute("jobid", jobid);
				return "loading";

			} else {
				// The miRNA is not in our provided file, offline calculating
				parameter.setType(option.intValue());
				parameter.setJobid(jobid);
				String resultPath = BASE_PATH + "userdata/" + parameter.getJobid();
				
				File userFileDir = new File(resultPath);
				if (!userFileDir.exists()) {
					userFileDir.mkdirs();
				}
				parameter.setResultPath(resultPath);

				parameter.setContent(SequenceExtractor.getWrappedSequenceContent(sequence));
				parameter.setSequence(SequenceExtractor.getWrappedSequence(sequence));
				
				parameter.setEmail(email);
				
			    MyThread dojob= new DoJobOfflineThread(parameter, "Thread-"+jobid);

			    ExecutorService threadPool = ThreadPoolUtils.getThreadPool();
				threadPool.submit(dojob);

				String msg = "<p>Thank you for using miRTMC webserver. Please wait minutes while your job will be processed in a queue.<br>" + "Note: <br>" + "<p>JobID：" + jobid
						+ "<p>You will be notified with an email when the job is finished.";
				
				model.addAttribute("message", msg);
//				model.addAttribute("email", email);
				model.addAttribute("jobid", jobid);
				return "loading";
				
			}
		}
		
		Gene gene = null;
		if (option.intValue() == SearchBy.gene_utraname.getCode()) { //Search by gene utraname
			 gene = GeneDao.getInstance().getGeneMapByUltraName1().get(content);
		}
		
		if (option.intValue() == SearchBy.gene_symbol.getCode()) { //Search by gene symbol
			 gene = GeneDao.getInstance().getGeneMapByGeneSymbol().get(content);
		}
		
		if (option.intValue() == SearchBy.gene_gs_id.getCode()) { //Search by gene gs
			 //gene = GeneDao.getInstance().getGeneMapByGsId().get(content);
			gene = GeneDao.getInstance().getGeneMapById().get(Integer.valueOf(content));
		}
		
		if(null!= gene) { //The gene is in our provided file
			
			parameter.setType(option.intValue());
			parameter.setContent(content);
			parameter.setJobid(jobid);
			parameter.setGeneId(Integer.valueOf(gene.getGs_id()));
			String resultPath = BASE_PATH + "userdata/"  + parameter.getJobid();

			File userFileDir = new File(resultPath);
			if (!userFileDir.exists()) {
				userFileDir.mkdirs();
			}
			parameter.setResultPath(resultPath);
			
		    MyThread dojob= new DoJobThread(parameter, "Thread-"+jobid);

		    ExecutorService threadPool = ThreadPoolUtils.getThreadPool();
			threadPool.submit(dojob);

			String msg = "<p>Thank you for using miRTMC webserver. Please wait for about 1 minute while your job is in process.<br><br>" + "Note: <br>" + "<p>JobID：" + jobid
					+ "<p>Do not close this page until the job finished, otherwise you cannot check the result. ";
			
			model.addAttribute("message", msg);
//			model.addAttribute("email", email);
			model.addAttribute("jobid", jobid);
			return "loading";
			
		}else {
			if (option.intValue() == SearchBy.gene_utraname.getCode() || option.intValue() == SearchBy.gene_symbol.getCode()||option.intValue() == SearchBy.gene_gs_id.getCode()) {
				//The gene is not in our provided file, offline calculating
				
				parameter.setType(option.intValue());
				
				parameter.setJobid(jobid);
				String resultPath = BASE_PATH + "userdata/" + parameter.getJobid();
				
				// 如果不存在 创建目录
				File userFileDir = new File(resultPath);
				if (!userFileDir.exists()) {
					userFileDir.mkdirs();
				}
				parameter.setResultPath(resultPath);	
				
				parameter.setContent(SequenceExtractor.getWrappedSequenceContent(sequence));
				parameter.setSequence(SequenceExtractor.getWrappedSequence(sequence));
				
				parameter.setEmail(email);
				
			    MyThread dojob= new DoJobOfflineThread(parameter, "Thread-"+jobid);

			    ExecutorService threadPool = ThreadPoolUtils.getThreadPool();
				threadPool.submit(dojob);

				String msg = "<p>Thank you for using miRTMC webserver. Please wait minutes while your job will be processed in a queue.<br><br>" + "Note: <br>" + "<p>JobID：" + jobid
						+ "<p>You will be notified with an email when the job is finished.";
				
				model.addAttribute("message", msg);
//				model.addAttribute("email", email);
				model.addAttribute("jobid", jobid);
				return "loading";
				
			}
		}
		
		
		return "error";
	}
//	/**
//	 * 提交sequences数据<br>
//	 * 或文件file、Email
//	 * 
//	 * @param model
//	 * @param sequence
//	 * @param file
//	 * @param email
//	 * @return
//	 */
//	@RequestMapping(method = RequestMethod.POST, value = "/index/submitBack")
//	public String submitJob(ModelMap model, 
//			@RequestParam(required = false) String sequence, @RequestParam(required = false) CommonsMultipartFile file,
//			@RequestParam(required = false) String email) {
//
//		Status view = new Status();
//		boolean sequence_bool = (sequence == null || sequence.length() <= 0);
//		boolean file_bool = (file == null || file.isEmpty() || file.getSize() == 0
//				|| !file.getOriginalFilename().endsWith("txt"));
//		boolean email_bool = (email == null) || (email.length() <= 0);
//		if (sequence_bool && file_bool) {
//			view.setFlag(0);
//			view.setMsg("please input the sequence or upload a fasta format file");
//			model.addAttribute("view", view);
//			return "error";
//		}
//
//		String jobid = Long.toString(System.currentTimeMillis());
//
//		// 如果不存在 创建目录
//		File userFileDir = new File(BASE_PATH + "userdata/" + email + "/" + jobid);
//		if (!userFileDir.exists()) {
//			// 此处mkdir()方法会因为创建多级目录不起作用
//			userFileDir.mkdirs();
//		}
//
//		File localFile = null;
//		// 判断文件是否存在
//		String path = userFileDir.getPath() + "/sequence.txt";
//		localFile = new File(path);
//
//		FileOutputStream fos = null;
//		BufferedWriter bw = null;
//		BufferedReader br = null;
//		List<String> seqsHeaderList = null;
//		List<String> seqs = null;
//		// 文件或者序列不为空
//		try {
//			if (!file_bool && !file.isEmpty()) {
//				// 预处理临时文件
//				File tempFile = new File(BASE_PATH + "userdata/temp/" + System.currentTimeMillis() + "/"
//						+ file.getOriginalFilename().trim());
//				if (!tempFile.exists()) {
//					// 此处mkdir()方法会因为创建多级目录不起作用
//					tempFile.mkdirs();
//				}
//				file.transferTo(tempFile);
//				System.out.println("temp file path ===" + tempFile.getAbsolutePath());
//				System.out.println("sequence file path ===" + localFile.getAbsolutePath());
//				// file存在
//				br = new BufferedReader(new FileReader(tempFile));
//				bw = new BufferedWriter(new FileWriter(localFile));
//				String line = null;
//				while ((line = br.readLine()) != null) {
//					if (line == null || line.length() == 0)
//						continue;
//					bw.write(line + System.lineSeparator());
//				}
//				tempFile.delete();
//				br.close();
//				bw.close();
//				// file.transferTo(localFile);
//			} else if (!sequence_bool) {
//				// sequence存在
//				fos = new FileOutputStream(localFile);
//				fos.write(sequence.trim().getBytes());
//				fos.flush();
//				fos.close();
//			}
//
//			// 校验序列是否合法
//			boolean isValid = DataPretreatment.readFastaOrRawSequence(localFile);
//			if (!isValid) {
//				view.setFlag(0);
//				view.setMsg("your sequence is invalid");
//				model.addAttribute("view", view);
//				return "error";
//			}
//
//			// 读序列的名字信息,隔行读取
//			seqsHeaderList = new ArrayList<String>();
//			seqs = new ArrayList<String>();
//			Scanner scanner = new Scanner(localFile);
//			while (scanner.hasNext()) {
//				String line = scanner.nextLine();
//				// 遇见空行
//				if (line == null || line.length() == 0)
//					continue;
//				if (line.startsWith(">")) {
//					seqsHeaderList.add(line);
//				} else {
//					seqs.add(line);
//				}
//			}
//		} catch (IOException e) {
//			logger.error("This is error message.");
//			e.printStackTrace();
//			logger.error(e.getMessage());
//			return null;
//		}
//
//		// 合并提交数据
//		SysParameter ldap = new SysParameter();
//		ldap.setEmail(email.trim());
//		ldap.setSeqs(seqsHeaderList);
//		ldap.setSequenceFile(localFile);
//		ldap.setJobid(jobid.trim());
//		ldap.setResultPath(userFileDir.getPath().trim());
//
//		// 统计要计算的数据量大小
//		int all_length = 0;
//		for (String string : seqs) {
//			all_length += string.length();
//		}
//		// 估算时间，长度为1000的序列计算时间约为1分钟
//		int minutes = all_length / 1000 > 0 ? (all_length / 1000) : 1;
//		// System.out.println(all_length);
//
//		// if (all_length < StaticVariables.SEQ_LENGTH_CUTOFF) {
//
//		DoCalThread docal = new DoCalThread(ldap);
//		// 提交计算,Thread.start()
//		docal.start();
//
//		if (file_bool && email_bool) {
//			/**
//			 * 在线计算
//			 */
//			String msg = "<p>Thank you for using miRTMC webserver. Please wait for " + minutes
//					+ " minute while your job is in process.<br>" + "Note: <br>" + "<p>JobID：" + jobid
//					+ "<p>Do not close this page until the job finished, otherwise you cannot check the result. ";
//			model.addAttribute("message", msg);
//			model.addAttribute("email", email);
//			model.addAttribute("jobid", jobid);
//			return "loading";
//		} else {
//			/**
//			 * 离线计算
//			 */
//
//			// 返回界面、调用过程单独写
//			view.setFlag(0);
//			String msg = "Thank you for using miRTMC webserver. The job is in process.<br>" + "Note: <br>" + "<p>JobID："
//					+ jobid
//					+ "<p>You can turn off this page. The server will sent you email when the job finished. You can check the result in result page by jobid and email address.";
//			view.setMsg(msg);
//			model.addAttribute("message", msg);
//			model.addAttribute("view", view);
//			// model.addAttribute("jobid", jobid);
//			return "success";
//		}
//	}

	/**
	 * 通过前台轮训，查看是否执行完毕
	 * 
	 * @param model
	 * @param check_email
	 * @param check_jobid
	 * @return
	 */
	@RequestMapping(value = "/calculating", method = RequestMethod.POST)
	@ResponseBody
	public int onlineCal(ModelMap model, @RequestParam(required = false) String email, String jobid) {
		// int flag = 0;
		// result path
		String path = "";
		if (email == null || email.length() == 0)
			path = ResultView.conevertToPathWithoutEmail(BASE_PATH + "userdata", jobid.trim());
		else if (jobid != null) {
			path = ResultView.conevertToPath(BASE_PATH + "userdata", email.trim(), jobid.trim());
		} else {
			return 0;
		}

		FileSystemResource local_file = new FileSystemResource(new File(path + "/success.txt"));// 临时文件

		// 如果存在则表示执行计算过程完毕
		if (local_file.exists())
			return 1;
		return 0;
	}

	/**
	 * 校验fastafile格式<br>
	 * 同时保证数据大小不超50KB
	 * 
	 * @param model
	 * @param file
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/index/validateFasta")
	@ResponseBody
	public int validateFastaFile(ModelMap model, @RequestParam(required = false) String sequence,
			@RequestParam(required = false) CommonsMultipartFile file, @RequestParam(required = false) String email) {

		// 去空
		if (file == null || file.isEmpty()) {
			return 0;
		}

		// 大小在50kb
		if (file.getSize() >= 500000 || file.getSize() <= 0) {
			return 0;
		}

		// 执行Fasta校验
		File localFile = null;
		// 判断文件是否存在
		String path = BASE_PATH + "userdata/temp/" + System.currentTimeMillis() + "/"
				+ file.getOriginalFilename().trim();
		localFile = new File(path);
		FileOutputStream fos = null;
		// 如果不存在 创建目录
		// File userFileDir = new File(BASE_PATH + "userdata/temp/");
		if (!localFile.exists()) {
			// 此处mkdir()方法会因为创建多级目录不起作用
			localFile.mkdirs();
		}
		if (!file.isEmpty()) {
			try {
				file.transferTo(localFile);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}
		} else {
			try {
				fos = new FileOutputStream(localFile);
				fos.write(sequence.getBytes());
				fos.flush();
				fos.close();
			} catch (IOException e) {
				try {
					fos.close();
				} catch (IOException e1) {
					logger.error("This is error message.");
					logger.error(e1.getMessage());
					e1.printStackTrace();
				}
				e.printStackTrace();
				logger.error(e.getMessage());
			}
		}
		//
		boolean isFasta = DataPreprocess.readFastaOrRawSequence(localFile);
		if (isFasta) {
			localFile.delete();
			return 1;
		} else {
			localFile.delete();
			return 0;
		}
	}

	
	@RequestMapping(method = RequestMethod.POST, value = "/checkresult")
	public String checkResult(ModelMap model, String check_email, String check_jobid) {
		
		// result path which store the temporary results
		String path = null;
		if(null == check_email || check_email.isEmpty()) {
			path = ResultView.conevertToPathWithoutEmail(BASE_PATH + "userdata", check_jobid);
		}else {
			 path = ResultView.conevertToPath(BASE_PATH + "userdata", check_email.trim(), check_jobid.trim());
		}
		
		logger.info("check the result of output path" + path);
		if(!new File(path).exists()) {
			model.addAttribute("message", "Sorry, the job id does not exist. Please check the job id and input again. ");
			return "running";
		}
		
		try {
			List<PredictedScore> knownList = new ArrayList<PredictedScore>();
			
			// get result
			try {
				knownList = ResultView.readKnownOutputText(path);
			}catch(Exception e) {
				if (e instanceof FileNotFoundException) {
					logger.warn("known.txt not found in this case, maybe in the fasta input model.");
				}else {
					logger.error("read known txt  error" + e.getMessage(), e);
				}
			}
			
			List<PredictedScore> predictdScoreList = ResultView.readPredictedOutputText(path);

			// get parameter
			CalcParameter parameter = null;
			parameter = new CalcParameterHelper(path + "/parameter.txt","").getObjFromFile();


			model.addAttribute("parameter", parameter);
			model.addAttribute("knownList", knownList);
			model.addAttribute("predictdScoreList", predictdScoreList);
			
			model.addAttribute("email", parameter.getEmail());
			model.addAttribute("jobid", parameter.getJobid());
			return "online-result2";

		} catch (IOException e) {
			e.printStackTrace();
			model.addAttribute("message", "Sorry, the job is still running. You will be notified with an email once the job is finished! ");
			return "running";
		}
	}

	
	
	
//	/**
//	 * 计算结果的在线展示<br>
//	 * post方式查看
//	 * 
//	 * @param model
//	 * @param email
//	 * @param jobid
//	 * @return
//	 */
//	@RequestMapping(method = RequestMethod.POST, value = "/checkresultBak")
//	public String checkResult(ModelMap model, String check_email, String check_jobid) {
//		// result path
//		String path = ResultView.conevertToPath(BASE_PATH + "userdata", check_email.trim(), check_jobid.trim());
//		System.out.println("output path" + path);
//		try {
//			// get result
//			List<List<PredictedScore>> dsLists = null;
//			dsLists = ResultView.readOutputText(path);
//			if (dsLists == null) {
//				model.addAttribute("message", "Sorry , result is not exist. You must have the wrong email or jobid ");
//				return "running";
//			}
//
//			// get parameter
//			SysParameter ldapPara = null;
//			ldapPara = ResultView.readParameter(path);
//			if (ldapPara == null) {
//				model.addAttribute("message", "Sorry , result is not exist. You must have the wrong email or jobid");
//				return "running";
//			}
//
//			model.addAttribute("parameter", ldapPara);
//			model.addAttribute("resultview", dsLists);
//			model.addAttribute("email", ldapPara.getEmail());
//			model.addAttribute("jobid", ldapPara.getJobid());
//			return "email-result";
//
//		} catch (IOException e) {
//			e.printStackTrace();
//			model.addAttribute("message", "Sorry , result is not exist. You must have the wrong jobid to check ");
//			return "running";
//		}
//	}

//	/**
//	 * 计算结果的在线展示，带email
//	 * 
//	 * @param model
//	 * @param email
//	 * @param jobid
//	 * @return
//	 */
//	@RequestMapping("/result/{email}/{jobid}")
//	public String result(ModelMap model, @PathVariable("email") String email, @PathVariable("jobid") String jobid) {
//		// result path
//		String path = ResultView.conevertToPath(BASE_PATH + "userdata", email.trim(), jobid.trim());
//		System.out.println("output path" + path);
//		try {
//			// get result
//			// List<List<DiseaseScore>> dsLists = null;
//			// dsLists = ResultView.readOutputText(path);
//			// if (dsLists == null) {
//			// model.addAttribute("message",
//			// "Sorry , result is not exist. You must have the wrong email or jobid ");
//			// return "running";
//			// }
//
//			// get parameter
//			SysParameter ldapPara = null;
//			ldapPara = ResultView.readParameter(path);
//			if (ldapPara == null) {
//				model.addAttribute("message", "Sorry , result is not exist. You must have the wrong email or jobid");
//				return "running";
//			}
//
//			model.addAttribute("parameter", ldapPara);
//			// model.addAttribute("resultview", dsLists);
//			return "email-result";
//
//		} catch (IOException e) {
//			e.printStackTrace();
//			model.addAttribute("message", "Sorry , result is not exist. You must have the wrong email or jobid");
//			return "running";
//		}
//	}
//
//	/**
//	 * Ajax获取
//	 * 
//	 * @param model
//	 * @param email
//	 * @param jobid
//	 * @param selectedIndex
//	 * @return
//	 */
//	@ResponseBody
//	@RequestMapping(value = "/ajaxData/{email}/{jobid}/{selectedIndex}", method = RequestMethod.POST)
//	public List<PredictedScore> resultWithIndex(ModelMap model, @PathVariable("email") String email,
//			@PathVariable("jobid") String jobid, @PathVariable("selectedIndex") Integer selectedIndex) {
//
//		// result path
//		String path = ResultView.conevertToPath(BASE_PATH + "userdata", email.trim(), jobid.trim());
//		System.out.println("output path" + path);
//
//		List<PredictedScore> dataScores = null;
//		try {
//			// get result
//			List<List<PredictedScore>> dsLists = null;
//			dsLists = ResultView.readOutputText(path);
//			if (dsLists == null) {
//				model.addAttribute("message", "Sorry , there's no result for it. ");
//				return null;
//			} else {
//				dataScores = dsLists.get(selectedIndex);
//				return dataScores;
//			}
//
//		} catch (IOException e) {
//			e.printStackTrace();
//			model.addAttribute("message", "Sorry , there's no result for it. ");
//			return dataScores;
//		}
//	}
//
	/**
	 * 计算结果的在线展示，无邮件
	 * 
	 * @param model
	 * @param email
	 * @param jobid
	 * @return
	 */
	@RequestMapping("/result/{jobid}")
	public String resultWithoutEmail(ModelMap model, @PathVariable("jobid") String jobid) {
		// result path
		String path = ResultView.conevertToPathWithoutEmail(BASE_PATH + "userdata", jobid.trim());

		logger.info("check the result of output path" + path);

		try {
			// get result
			List<PredictedScore> knownList = new ArrayList<PredictedScore>();
			
			try {
				knownList = ResultView.readKnownOutputText(path);
			}catch(Exception e) {
				if (e instanceof FileNotFoundException) {
					logger.warn("known.txt not found in this case, maybe in the fasta input model.");
				}else {
					logger.error("read known txt  error" + e.getMessage(), e);
				}
			}
			
			List<PredictedScore> predictdScoreList = ResultView.readPredictedOutputText(path);

			// get parameter
			CalcParameter parameter = null;
			parameter = new CalcParameterHelper(path + "/parameter.txt","").getObjFromFile();


			model.addAttribute("parameter", parameter);
			// model.addAttribute("resultview", dsLists);
			model.addAttribute("knownList", knownList);
			model.addAttribute("predictdScoreList", predictdScoreList);

			model.addAttribute("email", parameter.getEmail());
			model.addAttribute("jobid", parameter.getJobid());
			return "online-result2";

		} catch (IOException e) {
			e.printStackTrace();
			model.addAttribute("message", "Sorry , result is not exist. You must have the wrong jobid to check ");
			return "running";
		}

	}
//
//	/**
//	 * ajax发送邮件<br>
//	 * 执行发送同时要把文件拷贝对应邮箱目录下面<br>
//	 * 同时要更新相关参数
//	 * 
//	 * @param model
//	 * @param email
//	 * @param resultPath
//	 * @return
//	 */
//	@RequestMapping(value = "/result/toemail", method = RequestMethod.POST)
//	public String resultToEmail(RedirectAttributesModelMap model, String email, String jobid) {
//		// result path
//		String path = ResultView.conevertToPathWithoutEmail(BASE_PATH + "userdata", jobid.trim());
//		String transferPath = ResultView.conevertToPath(BASE_PATH + "userdata", email.trim(), jobid.trim());
//
//		SendMail sm = new SendMail("miRTMC results for jobID " + jobid);
//
//		// 只有当外部程序执行完之后在进行邮件发送
//		try {
//			String filename = ResultView.resultConvert(path);
//			if (filename == null) {
//				model.addAttribute("errorInfo", "Send Email failed");
//				return "redirect:/result/" + jobid;
//			}
//
//			File file = new File(path + "/output.txt");
//			File attachmentFile = new File(filename);
//			logger.info("file.exists()：------------" + file.exists());
//			if (file.exists()) {
//				System.out.println("---------------[发邮件]------------------");
//				// // 不带附件
//				// sm.sendEmail(email, mailContent);
//				// 带附件
//				sm.sendEmailWithAttachment(email, MailVariables.MailContent(email, jobid), attachmentFile);
//				System.out.println("---------------[移动文件夹]------------------");
//				// 移动所有
//				FileMove.fileCopy(path, transferPath);
//
//				// 注意还要更新一下拷贝目标文件夹下面的parameter.txt的email参数
//				SysParameter ldap = ResultView.readParameter(transferPath);
//				ldap.setResultPath(transferPath);
//				ldap.setEmail(email);
//				MainFlow.writeParameter(ldap);
//			} else {
//				model.addAttribute("errorInfo", "Send Email failed");
//				return "redirect:/result/" + jobid;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			model.addAttribute("errorInfo", "Send Email failed");
//			return "redirect:/result/" + jobid;
//		}
//		model.addAttribute("successInfo", "Send Email Success");
//		return "redirect:/result/" + jobid;
//	}
//
//	/**
//	 * 下载计算结果，带email
//	 * 
//	 * @param model
//	 * @param email
//	 * @param jobid
//	 * @param request
//	 * @param response
//	 * @return
//	 * @throws Exception
//	 */
//	@RequestMapping("/download/{email}/{jobid}")
//	public ModelAndView download(ModelMap model, @PathVariable("email") String email,
//			@PathVariable("jobid") String jobid, HttpServletRequest request, HttpServletResponse response)
//			throws Exception {
//
//		// result path
//		String path = ResultView.conevertToPath(BASE_PATH + "userdata", email, jobid);
//
//		response.setContentType("text/html;charset=utf-8");
//		request.setCharacterEncoding("UTF-8");
//		BufferedInputStream bis = null;
//		BufferedOutputStream bos = null;
//		// 结果转换后的下载的文件名
//		String down_file_name = ResultView.resultConvert(path);
//		if (down_file_name != null) {
//			try {
//				// 下载的文件
//				System.out.println(down_file_name);
//				File result = new File(down_file_name);
//				if (result.exists()) {
//					long fileLength = result.length();
//					response.setContentType("application/x-msdownload;");
//					response.setHeader("Content-disposition", "attachment; filename=ldap-result-" + jobid + ".txt");
//					response.setHeader("Content-Length", String.valueOf(fileLength));
//					bis = new BufferedInputStream(new FileInputStream(down_file_name));
//					bos = new BufferedOutputStream(response.getOutputStream());
//					byte[] buff = new byte[2048];
//					int bytesRead;
//					while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
//						bos.write(buff, 0, bytesRead);
//					}
//				} else {
//					return null;
//				}
//
//			} catch (Exception e) {
//				e.printStackTrace();
//			} finally {
//				if (bis != null)
//					bis.close();
//				if (bos != null)
//					bos.close();
//			}
//		}
//		return null;
//	}
//
//	/**
//	 * 无Email时，下载计算结果
//	 * 
//	 * @param model
//	 * @param jobid
//	 * @param request
//	 * @param response
//	 * @return
//	 * @throws Exception
//	 */
//	@RequestMapping("/download/{jobid}")
//	public ModelAndView downloadWithoutEmail(ModelMap model, @PathVariable("jobid") String jobid,
//			HttpServletRequest request, HttpServletResponse response) throws Exception {
//
//		// result path
//		String path = ResultView.conevertToPathWithoutEmail(BASE_PATH + "userdata", jobid);
//
//		response.setContentType("text/html;charset=utf-8");
//		request.setCharacterEncoding("UTF-8");
//		BufferedInputStream bis = null;
//		BufferedOutputStream bos = null;
//		// 结果转换后的下载的文件名
//		String down_file_name = ResultView.resultConvert(path);
//		if (down_file_name != null) {
//			try {
//				// 下载的文件
//				System.out.println(down_file_name);
//				File result = new File(down_file_name);
//				if (result.exists()) {
//					long fileLength = result.length();
//					response.setContentType("application/x-msdownload;");
//					response.setHeader("Content-disposition", "attachment; filename=ldap-result-" + jobid + ".txt");
//					response.setHeader("Content-Length", String.valueOf(fileLength));
//					bis = new BufferedInputStream(new FileInputStream(down_file_name));
//					bos = new BufferedOutputStream(response.getOutputStream());
//					byte[] buff = new byte[2048];
//					int bytesRead;
//					while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
//						bos.write(buff, 0, bytesRead);
//					}
//				} else {
//					return null;
//				}
//
//			} catch (Exception e) {
//				e.printStackTrace();
//			} finally {
//				if (bis != null)
//					bis.close();
//				if (bos != null)
//					bos.close();
//			}
//		}
//		return null;
//	}

	/**
	 * 下载样例数据
	 * 
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/download/examples")
	public ModelAndView downloadExamples(ModelMap model, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("UTF-8");
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;

		String downLoadPath = BASE_PATH + "userdata/example.zip";
		System.out.println(downLoadPath);
		try {
			File result = new File(downLoadPath);
			if (result.exists()) {
				long fileLength = result.length();
				response.setContentType("application/x-msdownload;");
				response.setHeader("Content-disposition", "attachment; filename=example.zip");
				response.setHeader("Content-Length", String.valueOf(fileLength));
				bos = new BufferedOutputStream(response.getOutputStream());
				byte[] buff = new byte[2048];
				int bytesRead;
				while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
					bos.write(buff, 0, bytesRead);
				}
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bis != null)
				bis.close();
			if (bos != null)
				bos.close();
		}
		return null;
	}
}
