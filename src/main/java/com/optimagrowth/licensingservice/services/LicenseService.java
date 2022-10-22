package com.optimagrowth.licensingservice.services;

import com.optimagrowth.licensingservice.models.License;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeoutException;

public interface LicenseService {
    License getLicense(String licenseId, String organizationId, String clientType);

    License createLicense(License license);

    License updateLicense(License license);

    String deleteLicense(String licenseId);

    List<License> getLicenseByOrganizationId(String organizationId) throws TimeoutException;
}
