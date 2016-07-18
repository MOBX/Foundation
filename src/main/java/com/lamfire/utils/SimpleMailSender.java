package com.lamfire.utils;

import java.io.IOException;
import java.util.*;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;

public class SimpleMailSender {

    private String               smtpHost        = null;                          // SMTP服务器地址

    private String               smtpAccount     = null;                          // SMTP服务器账户

    private String               smtpPassword    = null;                          // SMTP服务器密码

    private boolean              smtpAuthEnable  = true;                          // SMTP服务器是否验证

    private String               mailContentType = "text/html; charset=UTF-8";    // 邮件类型

    private boolean              debug           = false;                         // 是否采用调试方式

    private String               mailFrom        = null;                          // 发送者地址

    private Set<InternetAddress> mailTo          = new HashSet<InternetAddress>(); // 接收者地址

    private Set<InternetAddress> mailCCTo        = new HashSet<InternetAddress>();

    private Set<InternetAddress> mailBCCTo       = new HashSet<InternetAddress>();

    private String               subject         = null;                          // 邮件标题

    private String               mailContent     = "";                            // 邮件内容

    private final List<String>   attachedFiles   = Lists.newArrayList();          // 附件列表

    public void addMailTo(String mailAddress) throws AddressException {
        mailTo.add(new InternetAddress(mailAddress));
    }

    public void addMailCCTo(String mailAddress) throws AddressException {

        mailCCTo.add(new InternetAddress(mailAddress));

    }

    public void addMailBCCTo(String mailAddress) throws AddressException {
        mailBCCTo.add(new InternetAddress(mailAddress));
    }

    protected MimeMessage makeMessage(Session session) throws IOException, MessagingException {
        if (mailFrom == null) {
            throw new MessagingException("Mail from not found.");
        }

        if (mailTo.isEmpty()) {
            throw new MessagingException("Mail to not found.");
        }

        MimeMessage message = new MimeMessage(session);

        // 设置收件人
        InternetAddress[] address = new InternetAddress[mailTo.size()];
        mailTo.toArray(address);
        message.setRecipients(Message.RecipientType.TO, address);

        // 设置CC
        if (!mailCCTo.isEmpty()) {
            InternetAddress[] ccaddress = new InternetAddress[mailCCTo.size()];
            mailCCTo.toArray(ccaddress);
            message.setRecipients(Message.RecipientType.CC, ccaddress);
        }

        // 设置BCC
        if (!mailBCCTo.isEmpty()) {
            InternetAddress[] bccaddress = new InternetAddress[mailBCCTo.size()];
            mailBCCTo.toArray(bccaddress);
            message.setRecipients(Message.RecipientType.BCC, bccaddress);
        }

        // 设置回复地址
        InternetAddress[] replyAddress = { new InternetAddress(mailFrom) };
        message.setReplyTo(replyAddress);

        // 设置内容正文
        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setContent(mailContent, mailContentType);

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(bodyPart);

        // 添加附件
        for (int i = 0; i < attachedFiles.size(); i++) {
            String file = (String) attachedFiles.get(i);
            MimeBodyPart mBodyPart = new MimeBodyPart();
            FileDataSource fds = new FileDataSource(file);
            mBodyPart.setDataHandler(new DataHandler(fds));
            mBodyPart.setFileName(file);
            multipart.addBodyPart(mBodyPart);
        }

        message.setFrom(new InternetAddress(mailFrom));
        message.setSubject(subject);
        message.setContent(multipart);
        message.setSentDate(new Date());
        return message;
    }

    public void addAttachment(String file) {
        this.attachedFiles.add(file);
    }

    /**
     * 发送邮件
     * 
     * @throws java.io.IOException
     * @throws MessagingException
     * @return void
     */

    public void send() throws IOException, MessagingException {
        Properties props = System.getProperties();
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.auth", String.valueOf(smtpAuthEnable));

        Authenticator auth = new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpAccount, smtpPassword);
            }
        };

        Session session = Session.getInstance(props, auth);
        session.setDebug(debug);
        MimeMessage message = makeMessage(session);
        Transport.send(message);
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void setMailContent(String mailContent) {
        this.mailContent = mailContent;
    }

    public void setMailContentType(String mailContentType) {
        this.mailContentType = mailContentType;
    }

    public void setMailFrom(String mailFrom) {
        this.mailFrom = mailFrom;
    }

    public void setSmtpAccount(String smtpAccount) {
        this.smtpAccount = smtpAccount;
    }

    public void setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
    }

    public void setSmtpPassword(String smtpPassword) {
        this.smtpPassword = smtpPassword;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public boolean isSmtpAuthEnable() {
        return smtpAuthEnable;
    }

    public void setSmtpAuthEnable(boolean smtpAuthEnable) {
        this.smtpAuthEnable = smtpAuthEnable;
    }
}
