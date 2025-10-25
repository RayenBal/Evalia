package com.example.evaliaproject.service;

import com.example.evaliaproject.entity.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class ServiceEmail {
    @Autowired
    private JavaMailSender mailSender;
    // Optionnel: adresse d'expéditeur, si configurée
    @Value("${spring.mail.username:}")
    private String from;



    @Value("${app.support.email:support@evalia.local}")
    private String supportEmail;

    @Value("${app.front.login-url:http://localhost:4200/login}")
    private String loginUrl;

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
    public void sendVerificationEmail(String to, String link) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Vérification de votre adresse e-mail");
        // Contenu du mail (simple texte)
        message.setText(
                "Bonjour,\n\n" +
                        "Merci de vous être inscrit(e). Pour finaliser la création de votre compte, merci de cliquer sur le lien suivant :\n\n" +
                        link + "\n\n" +
                        "Ce code/lien expire dans 8 heures.\n\n" +
                        "Si vous n’avez pas fait cette demande, ignorez ce message.\n\n" +
                        "Cordialement,\n" +
                        "L’équipe Evalia"
        );
        mailSender.send(message);

    }



    /** ▼▼ NOUVEAU : mail après validation par l’admin */
    public void sendAccountApproved(User user) {
        String body =
                "Bonjour " + safe(user.getFirstname()) + ",\n\n" +
                        "Votre compte a été validé par l’administrateur.\n" +
                        "Vous pouvez maintenant vous connecter : " + loginUrl + "\n\n" +
                        "Cordialement,\nL’équipe Evalia";
        sendEmail(user.getEmail(), "Votre compte a été validé", body);
    }

    /** ▼▼ NOUVEAU : mail après rejet par l’admin */
    public void sendAccountRejected(User user) {
        String body =
                "Bonjour " + safe(user.getFirstname()) + ",\n\n" +
                        "Votre compte a été rejeté. Nous sommes désolés.\n" +
                        "Pour toute information complémentaire, contactez notre support : " + supportEmail + "\n\n" +
                        "Cordialement,\nL’équipe Evalia";
        sendEmail(user.getEmail(), "Votre compte a été rejeté", body);
    }

    private String safe(String s) { return (s == null || s.isBlank()) ? "utilisateur" : s; }
}

