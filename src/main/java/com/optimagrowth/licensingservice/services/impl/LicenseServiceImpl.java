package com.optimagrowth.licensingservice.services.impl;

import com.optimagrowth.licensingservice.configuration.ServiceConfig;
import com.optimagrowth.licensingservice.models.License;
import com.optimagrowth.licensingservice.models.Organization;
import com.optimagrowth.licensingservice.services.LicenseRepository;
import com.optimagrowth.licensingservice.services.LicenseService;
import com.optimagrowth.licensingservice.services.client.OrganizationDiscoveryClient;
import com.optimagrowth.licensingservice.services.client.OrganizationFeignClient;
import com.optimagrowth.licensingservice.services.client.OrganizationRestTemplateClient;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

@Service
@Slf4j
public class LicenseServiceImpl implements LicenseService {

    @Autowired
    MessageSource messageSource;
    @Autowired
    private LicenseRepository licenseRepository;
    @Autowired
    ServiceConfig config;
    @Autowired
    OrganizationFeignClient organizationFeignClient;

    @Autowired
    OrganizationRestTemplateClient organizationRestClient;

    @Autowired
    OrganizationDiscoveryClient organizationDiscoveryClient;


    @Override
//
    public License getLicense(String licenseId, String organizationId, String clientType) {
        License license = licenseRepository
                .findByOrganizationIdAndLicenseId(organizationId, licenseId);
        if (null == license) {
            throw new IllegalArgumentException(
                    String.format(messageSource.getMessage(
                            "license.search.error.message", null, null),
                            licenseId, organizationId));
        }
        Organization organization = retrieveOrganizationInfo(organizationId,
                clientType);
        if (null != organization) {
            Organization organization1 = new Organization();
            organization1.setContactName(organization.getContactName());
            organization1.setContactEmail(organization.getContactEmail());
            organization1.setContactPhone(organization.getContactPhone());
            license.setOrganization(organization);
        }
        return license;
    }

    @Bulkhead(name = "bulkheadLicenseService", type = Bulkhead.Type.THREADPOOL, fallbackMethod = "buildFallbackLicenseList")
    public CompletableFuture<License> getLicense(String licenseId) {
        return CompletableFuture.completedFuture(licenseRepository.findById(licenseId).get());
    }


    private License buildFallbackLicenseList(String licenseId, String organizationId, String clientType, Throwable t) {
        License license = new License();
        license.setLicenseId("0000000-00-00000");
        license.setOrganizationId(organizationId);
        license.setProductName(
                "Sorry no licensing information currently available");
        return license;
    }


    private Organization retrieveOrganizationInfo(String organizationId, String clientType) {
        Organization organization = null;

        switch (clientType) {
            case "feign":
                System.out.println("I am using the feign client");
                organization = organizationFeignClient.getOrganization(organizationId);
                break;
            case "rest":
                System.out.println("I am using the rest client");
                organization = organizationRestClient.getOrganization(organizationId);
                break;
            case "discovery":
                System.out.println("I am using the discovery client");
                organization = organizationDiscoveryClient.getOrganization(organizationId);
                break;
            default:
                organization = organizationRestClient.getOrganization(organizationId);
                break;
        }
        return organization;
    }

    @Override
    public License createLicense(License license) {
        license.setLicenseId(UUID.randomUUID().toString());
        licenseRepository.save(license);
        return license;
    }

    @Override
    public License updateLicense(License license) {
        licenseRepository.save(license);
        return license;
    }

    @Override
    public String deleteLicense(String licenseId) {
        String responseMessage = null;
        License license = new License();
        license.setLicenseId(licenseId);
        licenseRepository.delete(license);
        responseMessage = String.format(messageSource.getMessage(
                "license.delete.message", null, null), licenseId);
        return responseMessage;
    }

    @Override
    @RateLimiter(name = "licenseService")
//    @CircuitBreaker(name = "licenseService", fallbackMethod = "buildFallbackLicenseList")
//    @Retry(name = "retryLicenseService", fallbackMethod = "retryFallback")
    public List<License> getLicenseByOrganizationId(String organizationId) throws TimeoutException {
        log.debug("getLicensesByOrganization Correlation id:");
//            randomlyRunLong();
        return licenseRepository.findByOrganizationId(organizationId);

    }


    public List<License> retryFallback(String organizationId, Throwable t) {
        log.debug("fallback retry");
        return licenseRepository.findByOrganizationId(organizationId);
    }

    private void randomlyRunLong() throws TimeoutException {
        Random rand = new Random();
        int randomNum = rand.nextInt(3) + 1;
        if (randomNum < 4)
            sleep();
    }

    private void sleep() throws TimeoutException {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        throw new java.util.concurrent.TimeoutException();

    }
}