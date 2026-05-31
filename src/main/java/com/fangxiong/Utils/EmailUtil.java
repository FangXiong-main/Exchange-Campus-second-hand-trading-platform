package com.fangxiong.Utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class EmailUtil {

    private final JavaMailSender javaMailSender;

    public EmailUtil(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    private static final String FROM = "1255404327@qq.com";

    public void sendVerifyCodeHtml(String to, String code) throws MessagingException {
        String subject = "Exchange！校园二手交易平台 - 登录验证码";

        String html = """
            <div style="max-width:600px; margin:30px auto; background:#ffffff; border-radius:12px; box-shadow:0 4px 12px rgba(0,0,0,0.1); padding:30px; font-family: 'Microsoft YaHei', sans-serif;">
                <div style="text-align:center; font-size:18px; font-weight:bold; color:#2F4056; padding-bottom:16px; border-bottom:1px solid #eee;">
                    Exchange！校园二手交易平台
                </div>
                <div style="margin-top:24px; font-size:15px; color:#333; line-height:1.8;">
                    <p>您好！</p>
                    <p>您本次登录的验证码是：</p>
                    <div style="font-size:24px; font-weight:bold; color:#0066CC; margin:10px 0; letter-spacing: 2px;">
                        %s
                    </div>
                    <p>该验证码 5 分钟内有效，请勿泄露给他人。</p>
                </div>
                <div style="margin-top:30px; text-align:center; font-size:14px; color:#999; padding-top:16px; border-top:1px solid #eee;">
                    Exchange！运营团队
                </div>
            </div>
            """.formatted(code);

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(FROM);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);

        javaMailSender.send(message);
    }
}