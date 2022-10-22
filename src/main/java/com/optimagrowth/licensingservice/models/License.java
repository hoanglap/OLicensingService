package com.optimagrowth.licensingservice.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;

@Setter
@Getter
@ToString
@Entity
@Table(name = "licenses")
public class License extends RepresentationModel<License> {
    @Id
    @Column(name = "license_id", nullable = false)
    private String licenseId;
    @Column(name = "description")
    private String description;
    @Column(name = "organizationId")
    private String organizationId;
    @Column(name = "productName", nullable = false)
    private String productName;
    @Column(name = "license_type", nullable = false)
    private String licenseType;
    @Column(name = "comment")
    private String comment;

    @Transient
    Organization organization;

    public License withComment(String comment) {
        this.setComment(comment);
        return this;
    }
}
