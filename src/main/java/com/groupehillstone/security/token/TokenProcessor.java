package com.groupehillstone.security.token;

import com.groupehillstone.leavemgt.entities.Collaborator;
import com.groupehillstone.leavemgt.services.CollaboratorService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static java.util.List.of;

@Component
public class TokenProcessor {

    @Autowired
    private JwtConfiguration jwtConfiguration;

    @Autowired
    private ConfigurableJWTProcessor configurableJWTProcessor;

    @Autowired
    private CollaboratorService collaboratorService;

    public Authentication authenticate(HttpServletRequest request) throws Exception {
        String idToken = request.getHeader(this.jwtConfiguration.getHttpHeader());
        if (idToken != null) {
            JWTClaimsSet claims = this.configurableJWTProcessor.process(this.getBearerToken(idToken), null);
            validateIssuer(claims);
            verifyIfIdToken(claims);
            final String email = getUserEmailFrom(request);
            if (email != null) {
                final Collaborator collaborator = collaboratorService.findByEmail(email);
                if (collaborator == null) {
                    return null;
                }
                final String role = "ROLE_".concat(collaborator.getIdentityRole().name());
                final List<GrantedAuthority> grantedAuthorities = of(new SimpleGrantedAuthority(role));
                final User user = new User(email, "", of());
                return new JwtAuthentication(user, claims, grantedAuthorities);
            }
        }
        return null;
    }

    private String getUserEmailFrom(HttpServletRequest request) {
        return request.getHeader("email");
    }

    private void verifyIfIdToken(JWTClaimsSet claims) throws Exception {
        if (!claims.getIssuer().equals(this.jwtConfiguration.getCognitoIdentityPoolUrl())) {
            throw new Exception("JWT Token is not an ID Token");
        }
    }

    private void validateIssuer(JWTClaimsSet claims) throws Exception {
        if (!claims.getIssuer().equals(this.jwtConfiguration.getCognitoIdentityPoolUrl())) {
            throw new Exception(String.format("Issuer %s does not match cognito idp %s", claims.getIssuer(), this.jwtConfiguration.getCognitoIdentityPoolUrl()));
        }
    }

    private String getBearerToken(String token) {
        return token.startsWith("Bearer ") ? token.substring("Bearer ".length()) : token;
    }
}
