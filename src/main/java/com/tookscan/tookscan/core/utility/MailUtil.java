package com.tookscan.tookscan.core.utility;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MailUtil {
    private static final String TEST_EMAIL_TEMPLATE = """
        <!doctype html>
        <html lang="ko">
          <head>
            <meta charset="UTF-8" />
            <meta name="viewport" content="width=device-width, initial-scale=1.0" />
            <title>테스트 이메일</title>
          </head>
          <body style="margin: 0; padding: 64px; font-family: Arial, sans-serif; background-color: #ffffff;">
            <div class="container" style="width: 100%; max-width: 600px; margin: 0 auto; background: #f2f7ff; padding: 3.125rem; box-sizing: border-box; border-radius: 1.5rem;">
              <div class="blue-bar" style="width: 100%; height: 3px; background-color: #5592fc;"></div>
              
              <div class="title-section" style="text-align: center; padding: 3.25rem; margin-top: 5rem;">
                <h1 style="font-size: 2rem; color: #5592fc; margin: 0; text-align: center;">테스트 이메일이 도착했어요!</h1>
                <p style="font-size: 1.25rem; color: #777777; text-align: center; margin-top: 8px;">가장 합리적인 비대면 셀프 스캔, 한방에 툭스캔!</p>
              </div>
              
              <div class="img" style="text-align: center;">
                <img src="https://i.ibb.co/HTS5jm9m/testmail.png" alt="testmail" style="max-width: 250px; height: auto;" />
              </div>
              
              <div class="message-section" style="padding: 20px; font-size: 1.25rem; color: #333; text-align: start; margin-top: 3.125rem; margin-bottom: 5rem;">
                <p>
                  안녕하세요, 요청하신 테스트 이메일을 발송드렸습니다.<br /><br />
                  이메일 주소를 확인하셨다면,<br />
                  마지막 단계까지 신청서를 작성하여 서비스 신청을 완료해보세요!<br /><br />
                  툭스캔과 함께 더 편리한 서비스를 경험하실 수 있도록<br />
                  항상 노력하겠습니다 :)
                </p>
              </div>
              
              <div class="blue-bar" style="width: 100%; height: 3px; background-color: #5592fc;"></div>
            </div>
          </body>
        </html>
        """;

    private static final String PDF_EMAIL_TEMPLATE = """
            <!doctype html>
            <html lang="ko">
              <head>
                <meta charset="UTF-8" />
                <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                <title>${OrderName} 스캔본 전송 - TOOKSCAN</title>
              </head>
              <body style="margin: 0; padding: 64px; font-family: Arial, sans-serif; background-color: #ffffff;">
                <div class="container" style="width: 100%; max-width: 600px; margin: 0 auto; background: #f2f7ff; padding: 3.125rem; box-sizing: border-box; border-radius: 1.5rem;">
                  <div class="blue-bar" style="width: 100%; height: 3px; background-color: #5592fc;"></div>
                 \s
                  <div class="title-section" style="text-align: center; padding: 3.25rem; margin-top: 5rem;">
                    <h1 style="font-size: 2rem; color: #5592fc; margin: 0; text-align: center;">요청하신 책의 스캔본이 도착했어요!</h1>
                    <p style="font-size: 1.25rem; color: #777777; text-align: center; margin-top: 8px;">가장 합리적인 비대면 셀프 스캔, 한방에 툭스캔!</p>
                  </div>
                 \s
                  <div class="img" style="text-align: center;">
                    <img src="https://i.ibb.co/HTS5jm9m/testmail.png" alt="testmail" style="max-width: 250px; height: auto;" />
                  </div>
                 \s
                  <div class="message-section" style="padding: 20px; font-size: 1.25rem; color: #333; text-align: start; margin-top: 3.125rem; margin-bottom: 5rem;">
                    <p>
                      안녕하세요, 요청하신 ${orderName} 스캔본을 발송드렸습니다.<br /><br />
                      아래 url을 통해 다운로드 받아주세요!<br />
                      ${PdfUrl}<br /><br />
                      툭스캔과 함께 더 편리한 서비스를 경험하실 수 있도록<br />
                      항상 노력하겠습니다 :)
                    </p>
                  </div>
                 \s
                  <div class="blue-bar" style="width: 100%; height: 3px; background-color: #5592fc;"></div>
                </div>
              </body>
            </html>
            """;

    private static final String FORGET_PASSWORD_TEMPLATE = """
            <div style="width: 540px; border-top: 4px solid #02b875; border-bottom: 4px solid #02b875; margin: 100px auto; padding: 30px 0; box-sizing: border-box;">
            	<h1 style="margin: 0; padding: 0 5px; font-size: 28px; font-weight: 400;">
            		개발용 - <span style="color: #02b875;">임시 비밀번호</span> 안내입니다.
            	</h1>
            	<p style="font-size: 16px; line-height: 26px; margin-top: 50px; padding: 0 5px;">
            		안녕하세요, 국내 유일 스캔 서비스 TOOK-SCAN 입니다.<br />
            		요청하신 임시 비밀번호가 생성되었습니다.<br />
            		아래 임시 비밀번호로 로그인 이후 비밀번호를 변경해주세요.<br />
            		감사합니다.
            	</p>
            	<p style="font-size: 16px; margin: 40px 5px 20px; line-height: 28px;">
            		임시 비밀번호: <br />
            		<span style="font-size: 24px;">${TemporaryPassword}</span>
            	</p>
            </div>
           \s""";

    private final JavaMailSender javaMailSender;

    public void sendTestEmail(
            String receiverAddress
    ) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        mimeMessage.setSubject("TOOK-SCAN 테스트 이메일");

        // 위 HTML을 이용하여 이메일을 작성하고 전송하는 코드
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setFrom("TOOK-SCAN");
        mimeMessageHelper.setTo(receiverAddress);
        // UTF-8로 인코딩
        mimeMessageHelper.setText(TEST_EMAIL_TEMPLATE, true);

        javaMailSender.send(mimeMessage);
    }

    public void sendPdfEmail(
            String receiverAddress,
            String orderName,
            String pdfUrl
    ) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        mimeMessage.setSubject(orderName + " 스캔본 전송 - TOOKSCAN");

        // 위 HTML을 이용하여 이메일을 작성하고 전송
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setFrom("TOOK-SCAN");
        mimeMessageHelper.setTo(receiverAddress);
        // UTF-8로 인코딩
        String content = PDF_EMAIL_TEMPLATE
                .replace("${OrderName}", orderName)
                .replace("${orderName}", orderName)
                .replace("${PdfUrl}", pdfUrl);
        mimeMessageHelper.setText(content, true);

        javaMailSender.send(mimeMessage);
    }

    public void sendTemporaryPassword(
            String receiverAddress,
            String temporaryPassword
    ) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        mimeMessage.setSubject("TOOK-SCAN 임시 비밀번호 안내");

        // 위 HTML을 이용하여 이메일을 작성하고 전송하는 코드
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setFrom("TOOK-SCAN");
        mimeMessageHelper.setTo(receiverAddress);

        // UTF-8로 인코딩
        mimeMessageHelper.setText(FORGET_PASSWORD_TEMPLATE.replace("${TemporaryPassword}", temporaryPassword), true);

        javaMailSender.send(mimeMessage);
    }
}
