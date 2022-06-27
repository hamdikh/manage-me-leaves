package com.groupehillstone;

import com.groupehillstone.security.token.JwtConfiguration;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.ResourceRetriever;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.MalformedURLException;
import java.net.URL;

import static com.nimbusds.jose.JWSAlgorithm.RS256;

@ComponentScan(basePackages = {"com.groupehillstone.*"})
@EntityScan(basePackages = {"com.groupehillstone.*"})
@SpringBootApplication
@EnableScheduling
public class ManageMeLeavesApplication {

    @Autowired
    private JwtConfiguration jwtConfiguration;

    public static void main(String[] args) {
        SpringApplication.run(ManageMeLeavesApplication.class, args);
    }

    @Bean
    public ConfigurableJWTProcessor configurableJWTProcessor() throws MalformedURLException {
        final ResourceRetriever resourceRetriever = new DefaultResourceRetriever();
        final URL jwkSetURL = new URL(jwtConfiguration.getJwkUrl());
        final JWKSource keySource = new RemoteJWKSet(jwkSetURL, resourceRetriever);
        final ConfigurableJWTProcessor jwtProcessor = new DefaultJWTProcessor();
        final JWSKeySelector keySelector = new JWSVerificationKeySelector(RS256, keySource);
        jwtProcessor.setJWSKeySelector(keySelector);
        return jwtProcessor;
    }
}
