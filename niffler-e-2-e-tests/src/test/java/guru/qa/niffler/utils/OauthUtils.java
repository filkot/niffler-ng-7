package guru.qa.niffler.utils;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class OauthUtils {

    // Генерация случайного code_verifier
    public static String generateCodeVerifier() {
        Random random = new Random();
        byte[] codeVerifier = new byte[32];
        random.nextBytes(codeVerifier);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(codeVerifier);
    }

    // Генерация code_challenge на основе code_verifier
    public static String generateCodeChallenge(String codeVerifier) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(codeVerifier.getBytes(StandardCharsets.US_ASCII));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    public static String extractCodeFromUrl(String url) {
        // Создаем объект URI из строки
        URI uri = URI.create(url);

        // Получаем query-часть URL (все, что после "?")
        String query = uri.getQuery();

        // Если query-часть отсутствует, возвращаем null
        if (query == null || query.isEmpty()) {
            throw new IllegalArgumentException("Invalid redirect URL: " + url);
        }

        // Разделяем query-параметры на пары ключ-значение
        Map<String, String> queryParams = new HashMap<>();
        for (String param : query.split("&")) {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2) {
                queryParams.put(keyValue[0], keyValue[1]);
            }
        }

        // Возвращаем значение параметра "code"
        return queryParams.get("code");
    }
}