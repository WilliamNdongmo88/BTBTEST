package will.dev.BTBTEST.classUtiles;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
public class GoogleTokenValidator {

    private static final String TOKEN_INFO_URL = "https://oauth2.googleapis.com/tokeninfo";

    public Map<String, Object> validateToken(String idToken) {
        RestTemplate restTemplate = new RestTemplate();

        String url = UriComponentsBuilder
                .fromHttpUrl(TOKEN_INFO_URL)
                .queryParam("id_token", idToken)
                .toUriString();

        try {
            // La réponse contient un map JSON avec des infos comme email, name, etc.
            return restTemplate.getForObject(url, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("ID Token invalide ou expiré");
        }
    }
}

