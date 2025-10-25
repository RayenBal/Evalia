package com.example.evaliabackoffice.service;

import org.springframework.beans.factory.annotation.Value;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;
    // Optionnel: adresse d'expéditeur, si configurée
    @Value("${spring.mail.username:}")
    private String from;

    /** Méthode générique utilisée par PasswordResetService */
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage msg = new SimpleMailMessage();
        if (from != null && !from.isBlank()) {
            msg.setFrom(from);
        }
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(body);
        mailSender.send(msg);
    }

    public void sendEmailR(String toEmail, String subject, String body) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(body, true); // true = send as HTML
        helper.setFrom("shil.yosra01@gmail.com"); // change to your sender email
        mailSender.send(mimeMessage);
    }
    public void sendVerificationEmail(String to, String link) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Vérification de votre adresse e-mail");
        // Contenu du mail (simple texte)
        message.setText(
                "Bonjour,\n\n" +
                        "Merci de vous être inscrit(e). Pour finaliser la création de votre compte, merci de cliquer sur le lien suivant :\n\n" +
                        link + "\n\n" +
                        "Ce code/lien expire dans 2 heures.\n\n" +
                        "Si vous n’avez pas fait cette demande, ignorez ce message.\n\n" +
                        "Cordialement,\n" +
                        "L’équipe Evalia"
        );
        mailSender.send(message);

    }

    public void sendRejectionEmail(String to, String firstname) throws MessagingException {
        String subject = "Evalia - Rejet de votre demande";
        String body = "<h3>Bonjour " + firstname + ",</h3>" +
                "<p>Votre demande a été rejetée par notre équipe.</p>" +
                "<p>Pour toute question, veuillez nous contacter.</p>" +
                "<br><p>Cordialement,<br>L’équipe Evalia</p>";
        sendEmailR(to, subject, body);
    }


}
