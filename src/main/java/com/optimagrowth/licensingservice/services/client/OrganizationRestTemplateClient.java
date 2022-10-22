package com.optimagrowth.licensingservice.services.client;

import com.optimagrowth.licensingservice.models.Organization;
import com.optimagrowth.licensingservice.services.OrganizationRedisRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.adapters.springsecurity.client.KeycloakClientRequestFactory;
import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


@Component
@Slf4j
public class OrganizationRestTemplateClient {

    @Autowired
    public KeycloakRestTemplate restTemplate;

    @Autowired
    OrganizationRedisRepository redisRepository;

    private Organization insertRedis(String organizationId) {
        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setContactEmail("hoanglap@gmail.com");
        organization.setContactPhone("9292929");
        organization.setName("Optima Growth Stock");
        return redisRepository.save(organization);
    }

    private Organization checkRedisCache(String organizationId) {
        try {
            return redisRepository
                    .findById(organizationId)
                    .orElse(null);
        } catch (Exception ex) {
            log.error("Error encountered while trying to retrieve organization { check Redis Cache.Exception { } ", organizationId, ex);
            return null;
        }
    }

    private void cacheOrganizationObject(Organization organization) {
        try {
            redisRepository.save(organization);
        } catch (Exception ex) {
            log.error("Unable to cache organization {} in Redis.Exception { } ", organization.getId(), ex);
        }
    }


    @CircuitBreaker(name = "organizationService")
    @Retry(name = "retryLicenseService")
    public Organization getOrganization(String organizationId) throws RestClientException {
        Organization organization = checkRedisCache(organizationId);
        if (organization != null && organization.isUpdateToDate()) {
            log.debug("I have successfully retrieved an organization { } from the redis cache:{ } ", organizationId, organization);
            return organization;
        } else {
            log.debug("Unable to locate organization or organization data is out of date from the redis cache: {}.", organizationId);
            ResponseEntity<Organization> restExchange =
                    restTemplate.exchange(
                            "http://organization-service/v1/organization/{organizationId}",
                            HttpMethod.GET,
                            null, Organization.class, organizationId);
            Organization restRespose = restExchange.getBody();
            if (restRespose != null) {
                organization = restRespose;
                cacheOrganizationObject(restRespose);
            }
        }
        return organization;
    }
}
