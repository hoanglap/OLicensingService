package com.optimagrowth.licensingservice.services;

import com.optimagrowth.licensingservice.models.License;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LicenseRepository extends CrudRepository<License, String> {

    List<License> findByOrganizationId
            (String organizationId);

    License findByOrganizationIdAndLicenseId
            (String organizationId,
             String licenseId);
}
