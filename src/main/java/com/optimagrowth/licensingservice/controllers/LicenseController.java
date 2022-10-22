package com.optimagrowth.licensingservice.controllers;

import com.optimagrowth.licensingservice.models.License;
import com.optimagrowth.licensingservice.services.LicenseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeoutException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "v1/organization/{organizationId}/license")
@Slf4j
@RolesAllowed("ADMIN")
public class LicenseController {

    @Autowired
    private LicenseService licenseService;

    @GetMapping(value = "/{licenseId}/{clientType}")
    public ResponseEntity<License> getLicense(@PathVariable("organizationId") String organizationId, @PathVariable("licenseId") String licenseId, @PathVariable("clientType") String clientType) {
        License license = licenseService.getLicense(licenseId, organizationId, clientType);
        license.add(linkTo(methodOn(LicenseController.class).getLicense(organizationId, license.getLicenseId(), clientType)).withSelfRel(),
                linkTo(methodOn(LicenseController.class).createLicense(organizationId, license, null)).withRel("createLicense"),
                linkTo(methodOn(LicenseController.class).updateLicense(organizationId, license, null)).withRel("updateLicense"),
                linkTo(methodOn(LicenseController.class).deleteLicense(organizationId, license.getLicenseId(), null)).withRel("deleteLicense"));
        return ResponseEntity.ok(license);
    }

    @GetMapping
    public ResponseEntity<List<License>> getLicenseByOrganizationId(@PathVariable("organizationId") String organizationId) throws TimeoutException {
        //For RateLimitter Testing
        List<License> licenses = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
             licenses = licenseService.getLicenseByOrganizationId(organizationId);

        }
//        log.debug("LicenseServiceController Correlation id: {}",
//                UserContextHolder.getContext().getCorrelationId());
//        List<License> licenses = licenseService.getLicenseByOrganizationId(organizationId);
        return ResponseEntity.ok(licenses);
    }

    @RequestMapping(value = "{licenseId}", method = {RequestMethod.PATCH, RequestMethod.PUT})
    public ResponseEntity<License> updateLicense(@PathVariable("organizationId")
                                                         String organizationId, @RequestBody License request, @RequestHeader(value = "Accept-Language", required = false)
                                                         Locale locale) {
        return ResponseEntity.ok(licenseService.updateLicense(request));
    }

    @PostMapping
    public ResponseEntity<License> createLicense(
            @PathVariable("organizationId") String organizationId,
            @RequestBody License request, @RequestHeader(value = "Accept-Language", required = false)
                    Locale locale) {
        return ResponseEntity.ok(licenseService.createLicense(request));
    }

    @DeleteMapping(value = "/{licenseId}")
    public ResponseEntity<String> deleteLicense(@PathVariable("organizationId") String organizationId,
                                                @PathVariable("licenseId") String licenseId,
                                                @RequestHeader(value = "Accept-Language", required = false)
                                                        Locale locale) {
        return ResponseEntity.ok(licenseService.deleteLicense(licenseId));
    }


}
