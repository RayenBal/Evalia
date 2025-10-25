package com.example.evaliabackoffice.service;


import com.example.evaliabackoffice.entity.User;
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


//    @Value("${app.support.email:support@evalia.local}")

    @Value("${app.support.email:suppevalia@gmail.com}")

    private String supportEmail;

    @Value("${app.front.login-url:http://localhost:4200/login}")
    private String loginUrl;

    /**
     * Méthode générique utilisée par PasswordResetService
     */
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
        sendEmail(
                to,
                "Vérification de votre adresse e-mail",
                "Bonjour,\n\n" +
                        "Merci pour votre inscription. Veuillez cliquer sur le lien suivant pour vérifier votre e-mail :\n" +
                        link + "\n\n" +
                        "Le lien expire dans 8 heures.\n\n" +
                        "Cordialement,\nL’équipe Evalia"
        );
    }
    // ✅ appelé après approbation
    public void sendAccountApproved(User user) {
        String name = (user.getFirstname() == null || user.getFirstname().isBlank()) ? "utilisateur" : user.getFirstname();
        String body =
                "Bonjour " + name + ",\n\n" +
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


    // ▼ Email envoyé quand l’admin tente d’approuver un compte non vérifié
    public void sendPleaseVerify(User user, String link) {
        String name = (user.getFirstname() == null || user.getFirstname().isBlank()) ? "utilisateur" : user.getFirstname();
        String body =
                "Bonjour " + name + ",\n\n" +
                        "Avant la validation de votre compte par l’administrateur, vous devez vérifier votre adresse e-mail.\n" +
                        "Merci de cliquer sur ce lien :\n" + link + "\n\n" +
                        "Cordialement,\nL’équipe Evalia";
        sendEmail(user.getEmail(), "Vérifiez votre e-mail pour finaliser l'inscription", body);
    }
}

