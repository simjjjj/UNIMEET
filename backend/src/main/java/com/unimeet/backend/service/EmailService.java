package com.unimeet.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * 이메일 인증 코드 발송
     */
    public void sendVerificationEmail(String toEmail, String verificationCode) {
        // 개발 환경에서는 실제 이메일 대신 콘솔에 출력
        if (fromEmail == null || fromEmail.isEmpty() || fromEmail.equals("your-gmail@gmail.com")) {
            log.info("=== 개발용 이메일 인증 코드 ===");
            log.info("수신자: {}", toEmail);
            log.info("인증 코드: {}", verificationCode);
            log.info("유효시간: 10분");
            log.info("============================");
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("[UniMeet] 이메일 인증 코드");

            // HTML 템플릿 사용
            String htmlContent = createVerificationEmailContent(verificationCode);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("이메일 인증 코드 발송 완료: {}", toEmail);

        } catch (MessagingException | MailException e) {
            log.error("이메일 발송 실패: {} - {}", toEmail, e.getMessage());
            // 개발 환경에서는 에러 대신 콘솔 출력으로 대체
            log.info("=== 이메일 발송 실패 - 개발용 코드 출력 ===");
            log.info("수신자: {}", toEmail);
            log.info("인증 코드: {}", verificationCode);
            log.info("유효시간: 10분");
            log.info("=====================================");
        }
    }

    /**
     * 이메일 인증 HTML 템플릿 생성
     */
    private String createVerificationEmailContent(String verificationCode) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>UniMeet 이메일 인증</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f5f5f5; }
                    .container { max-width: 600px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 10px; }
                    .header { text-align: center; color: #333; margin-bottom: 30px; }
                    .code-box { background-color: #f0f8ff; border: 2px solid #4a90e2; border-radius: 8px; padding: 20px; text-align: center; margin: 20px 0; }
                    .code { font-size: 24px; font-weight: bold; color: #4a90e2; letter-spacing: 4px; }
                    .info { color: #666; line-height: 1.6; }
                    .footer { text-align: center; color: #999; font-size: 12px; margin-top: 30px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🎓 UniMeet 이메일 인증</h1>
                    </div>
                    
                    <div class="info">
                        <p>안녕하세요! UniMeet에 가입해주셔서 감사합니다.</p>
                        <p>아래 인증 코드를 앱에 입력하여 이메일 인증을 완료해주세요.</p>
                    </div>
                    
                    <div class="code-box">
                        <p><strong>인증 코드</strong></p>
                        <div class="code">%s</div>
                        <p style="color: #999; font-size: 14px;">유효시간: 10분</p>
                    </div>
                    
                    <div class="info">
                        <p><strong>주의사항:</strong></p>
                        <ul>
                            <li>이 코드는 10분간 유효합니다</li>
                            <li>본인이 요청하지 않았다면 이 이메일을 무시해주세요</li>
                            <li>코드를 타인과 공유하지 마세요</li>
                        </ul>
                    </div>
                    
                    <div class="footer">
                        <p>© 2024 UniMeet. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """, verificationCode);
    }

    /**
     * 학교 이메일 도메인 검증
     */
    public boolean isValidUniversityEmail(String email) {
        if (email == null || !email.contains("@")) {
            return false;
        }
        
        String domain = email.substring(email.lastIndexOf("@") + 1).toLowerCase();
        
        // 한국 주요 대학교 도메인 목록
        String[] universityDomains = {
            "kku.ac.kr",           // 건국대학교
            "snu.ac.kr",           // 서울대학교
            "yonsei.ac.kr",        // 연세대학교
            "korea.ac.kr",         // 고려대학교
            "sogang.ac.kr",        // 서강대학교
            "hanyang.ac.kr",       // 한양대학교
            "cau.ac.kr",           // 중앙대학교
            "dankook.ac.kr",       // 단국대학교
            "kyunghee.ac.kr",      // 경희대학교
            "dongguk.edu",         // 동국대학교
            "hongik.ac.kr",        // 홍익대학교
            "sejong.ac.kr",        // 세종대학교
            "ssu.ac.kr",           // 숭실대학교
            "kookmin.ac.kr",       // 국민대학교
            "smu.ac.kr",           // 상명대학교
            "dgu.edu",             // 동국대학교
            "kaist.ac.kr",         // KAIST
            "postech.ac.kr",       // POSTECH
            "unist.ac.kr",         // UNIST
            "gist.ac.kr",          // GIST
            "dgist.ac.kr"          // DGIST
        };
        
        for (String universityDomain : universityDomains) {
            if (domain.equals(universityDomain)) {
                return true;
            }
        }
        
        log.warn("지원하지 않는 대학교 도메인: {}", domain);
        return false;
    }

    /**
     * 대학교 이름 추출
     */
    public String getUniversityName(String email) {
        if (!isValidUniversityEmail(email)) {
            return "알 수 없는 대학교";
        }
        
        String domain = email.substring(email.lastIndexOf("@") + 1).toLowerCase();
        
        Map<String, String> universityNames = new HashMap<>();
        universityNames.put("kku.ac.kr", "건국대학교");
        universityNames.put("snu.ac.kr", "서울대학교");
        universityNames.put("yonsei.ac.kr", "연세대학교");
        universityNames.put("korea.ac.kr", "고려대학교");
        universityNames.put("sogang.ac.kr", "서강대학교");
        universityNames.put("hanyang.ac.kr", "한양대학교");
        universityNames.put("cau.ac.kr", "중앙대학교");
        universityNames.put("dankook.ac.kr", "단국대학교");
        universityNames.put("kyunghee.ac.kr", "경희대학교");
        universityNames.put("dongguk.edu", "동국대학교");
        universityNames.put("hongik.ac.kr", "홍익대학교");
        universityNames.put("sejong.ac.kr", "세종대학교");
        universityNames.put("ssu.ac.kr", "숭실대학교");
        universityNames.put("kookmin.ac.kr", "국민대학교");
        universityNames.put("smu.ac.kr", "상명대학교");
        universityNames.put("kaist.ac.kr", "KAIST");
        universityNames.put("postech.ac.kr", "POSTECH");
        universityNames.put("unist.ac.kr", "UNIST");
        universityNames.put("gist.ac.kr", "GIST");
        universityNames.put("dgist.ac.kr", "DGIST");
        
        return universityNames.getOrDefault(domain, "기타 대학교");
    }
}