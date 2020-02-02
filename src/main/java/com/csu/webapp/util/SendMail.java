package com.csu.webapp.util;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import com.csu.webapp.config.Conf;

/**
 * 执行邮件发送过程
 * 
 * @author kayzhao
 * @author chenx
 * 
 *
 */
public class SendMail {

	private static Properties prop = new Properties();
	
	private String mailSubject;

	public SendMail(String mailSubject) {
		prop.setProperty("mail.host", Conf.HOST);
		prop.setProperty("mail.transport.protocol", "smtp");
		prop.setProperty("mail.smtp.auth", "true");
		this.mailSubject = mailSubject;
	}

	/**
	 * 执行邮件发送 创建一封只包含文本的邮件
	 * 
	 * @param sendto
	 * @throws NoSuchProviderException
	 * @throws MessagingException
	 * @throws Exception
	 */
	public void sendEmail(String sendto, String mailContent)
			throws NoSuchProviderException, MessagingException, Exception {

		// 1、创建session
		Session session = Session.getInstance(prop);
		// 开启Session的debug模式，这样就可以查看到程序发送Email的运行状态
		session.setDebug(true);

		// 2、通过session得到transport对象
		Transport ts = session.getTransport();

		// 3、使用邮箱的用户名和密码连上邮件服务器，发送邮件时，发件人需要提交邮箱的用户名和密码给smtp服务器，用户名和密码都通过验证之后才能够正常发送邮件给收件人。
		ts.connect(Conf.HOST, Conf.USER,
				Conf.PASSWORD);
		// ts.connect("smtp.csu.edu.cn", "weilan@csu.edu.cn", "3346250");

		// 4、创建邮件
		MimeMessage message = new MimeMessage(session);
		// 指明邮件的发件人
		message.setFrom(new InternetAddress(Conf.USER));
		// 指明邮件的收件人，现在发件人和收件人是一样的，那就是自己给自己发
		message.setRecipient(Message.RecipientType.TO, new InternetAddress(
				sendto));
		// 邮件的标题
		message.setSubject(this.mailSubject);
		// 邮件的文本内容
		message.setContent(mailContent, "text/html;charset=UTF-8");
		message.setSentDate(new Date());

		// 5、发送邮件
		ts.sendMessage(message, message.getAllRecipients());
		ts.close();
	}

	/**
	 * 发送邮件带附件
	 * 
	 * @param email
	 * @param mailContent
	 * @param attachment
	 * @throws IOException
	 * @throws MessagingException
	 */
	public void sendEmailWithAttachment(String email, String mailContent,
			File attachment) throws IOException, MessagingException {
		Session session = Session.getInstance(prop);
		session.setDebug(true);

		Message message = new MimeMessage(session);
		// 发件人
		InternetAddress from = new InternetAddress(Conf.USER);
		message.setFrom(from);

		// 收件人
		InternetAddress to = new InternetAddress(email);
		message.setRecipient(Message.RecipientType.TO, to);

		// 邮件主题
		message.setSubject(this.mailSubject);

		// 向multipart对象中添加邮件的各个部分内容，包括文本内容和附件
		Multipart multipart = new MimeMultipart();

		// 添加邮件正文
		BodyPart contentPart = new MimeBodyPart();
		contentPart.setContent(mailContent, "text/html;charset=UTF-8");
		multipart.addBodyPart(contentPart);

		// 添加附件的内容
		if (attachment != null) {
			BodyPart attachmentBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(attachment);
			attachmentBodyPart.setDataHandler(new DataHandler(source));

			// MimeUtility.encodeWord可以避免文件名乱码
			attachmentBodyPart.setFileName(MimeUtility.encodeWord(attachment
					.getName()));
			multipart.addBodyPart(attachmentBodyPart);
		}

		// 将multipart对象放到message中
		message.setContent(multipart);
		// 保存邮件
		message.saveChanges();

		Transport transport = session.getTransport();
		transport.connect(Conf.HOST, Conf.USER,
				Conf.PASSWORD);

		// 发送
		transport.sendMessage(message, message.getAllRecipients());

		System.out.println("send success!");
	}

	// public static void main(String[] args) throws NoSuchProviderException,
	// MessagingException, Exception {
	// Resource resource = new ClassPathResource("log4j.properties");
	// new SendMail("hhhh").sendEmailWithAttachment("test",
	// "1427569333@qq.com", resource.getFile());
	// }
}