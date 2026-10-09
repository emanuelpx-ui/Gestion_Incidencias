package com.devdesk.Gestion_Incidencias.security;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.List;
import java.util.Set;

@Component
public class JwtSecretValidator {

    private static final Logger log = LoggerFactory.getLogger(JwtSecretValidator.class);
    private static final int MINIMO_BYTES = 32;
    private static final int MINIMO_CARACTERES_DISTINTOS = 12;

    private static final Set<String> CLAVES_PROHIBIDAS = Set.of(
            "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970",
            "586E3272357538782F413F4428472B4B6250655368566D597133743677397A24",
            "secret", "mysecret", "secretkey", "changeme", "GENERA_LA_TUYA_CON_openssl_rand_base64_32"
    );

    private final String secret;

    public JwtSecretValidator(@Value("${app.jwt.secret}") String secret) {
        this.secret = secret;
    }

    @PostConstruct
    public void validar() {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("La propiedad 'app.jwt.secret' está vacía o no definida.");
        }

        final String limpia = secret.trim();

        if (CLAVES_PROHIBIDAS.stream().anyMatch(c -> c.equalsIgnoreCase(limpia))) {
            throw new IllegalStateException("La clave configurada es una clave de EJEMPLO públicamente conocida.");
        }

        int bytesEfectivos = calcularBytesEfectivos(limpia);
        if (bytesEfectivos < MINIMO_BYTES) {
            throw new IllegalStateException(String.format("La clave aporta %d bytes y HMAC-SHA256 exige al menos %d.", bytesEfectivos, MINIMO_BYTES));
        }

        long distintos = limpia.chars().distinct().count();
        if (distintos < MINIMO_CARACTERES_DISTINTOS) {
            throw new IllegalStateException("La clave parece generada a mano y no de forma aleatoria.");
        }

        log.info("Clave JWT validada correctamente: {} bytes efectivos de entropía.", bytesEfectivos);
    }

    private int calcularBytesEfectivos(String valor) {
        try {
            return Base64.getDecoder().decode(valor).length;
        } catch (IllegalArgumentException noEsBase64) {
            return valor.getBytes(java.nio.charset.StandardCharsets.UTF_8).length;
        }
    }
}