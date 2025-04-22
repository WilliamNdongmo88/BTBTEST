package will.dev.BTBTEST.services;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import will.dev.BTBTEST.entity.Validation;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final JavaMailSender javaMailSender;

    public void envoyer(Validation validation){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("no-rely@will.dev");
        message.setTo(validation.getUser().getEmail());
        message.setSubject("BTB activate code");
        message.setText("Hello " + validation.getUser().getName() + ",\n\nWelcome to BTB platform.\n\nYour activate code is " + validation.getCode());

        javaMailSender.send(message);
    }
}
