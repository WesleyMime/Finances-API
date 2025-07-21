package br.com.finances.config.security;

import br.com.finances.api.client.Client;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class TokenService {
	
	@Value("${finances.jwt.expiration}")
	private String expiration;
	@Value("${finances.jwt.secret}")
	private String secret;

	public String generateToken(Authentication authentication) {
		Client client = (Client) authentication.getPrincipal();
		Date today = new Date();
		Date dateExpiration = new Date(today.getTime() + Long.parseLong(this.expiration));

		return Jwts.builder()
				.issuer("Finances-API")
				.subject(client.getId().toString())
				.issuedAt(today)
				.expiration(dateExpiration)
				.signWith(getKey())
				.compact();
	}

	public boolean isValid(String token) {
		try {
			Jwts.parser()
					.verifyWith(getKey())
					.build()
					.parseSignedClaims(token);
			return true;
		} catch (Exception _) {
			return false;
		}
	}

	public Long getIdClient(String token) {
		Claims claims = Jwts.parser()
				.verifyWith(getKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
		return Long.parseLong(claims.getSubject());
	}

	public SecretKey getKey() {
		/* To generate a new key
		SecretKey secretKey = Jwts.SIG.HS512.key().build();
        String encodedKey = Encoders.BASE64.encode(secretKey.getEncoded());
		 */
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(this.secret));
	}
}
