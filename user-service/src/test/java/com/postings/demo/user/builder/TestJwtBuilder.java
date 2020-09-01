package com.postings.demo.user.builder;

import java.security.Key;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import com.postings.demo.user.model.User;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class TestJwtBuilder {
	private String secret ;
	private boolean emailVerified ;
	
	public TestJwtBuilder(String secret) {
		this.secret = secret ;
		this.emailVerified = true ;
	}
	
	public TestJwtBuilder emailVerified(boolean emailVerified) {
		this.emailVerified = emailVerified ;
		return this ;
	}
	
	public String jwt(User user) {
		return getJwt(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), "https://accounts.google.com", 365) ;
	}
	
	public String getJwt(String subject, String givenName, String familyName, String email, String issuer, int expireDays) {
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);

		//We will sign our JWT with our ApiKey secret
		byte[] apiKeySecretBytes = secret.getBytes();
		Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
		
		JwtBuilder builder = Jwts.builder()//.setId(id)
	            .setIssuedAt(now)
	            .setSubject(subject)
	            .setIssuer(issuer)
	            .claim("given_name", givenName)
	            .claim("family_name", familyName)
	            .claim("email", email)
	            .claim("email_verified", emailVerified)
	            .claim("picture", "https://lh3.googleusercontent.com/a-/AOh14GhZSN3SH932iV1wXTX97nAsqXypE3hd3474r3CtUQ=s96-c")
	            .signWith(signatureAlgorithm, signingKey);
		
		if (expireDays > 0) {
	        long expMillis = nowMillis + expireDays * 24 * 60 * 60 * 1000;
	        Date exp = new Date(expMillis);
	        builder.setExpiration(exp);
	    }
		
		return builder.compact();
	}
}
