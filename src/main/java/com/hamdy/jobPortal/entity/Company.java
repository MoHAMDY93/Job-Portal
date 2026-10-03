package com.hamdy.jobPortal.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
/*
    Note if the class name is the same as the table name,
    then there is need to mention this name attribute

    The same for the @Column annotation
*/
@Table(name = "COMPANIES")
@Getter @Setter

@NamedQueries({
        @NamedQuery(
                name = "Company.fetchCompaniesWithJobsByStatus",
                query = "SELECT DISTINCT c FROM Company c JOIN FETCH c.jobs j WHERE j.status = :status"
        )
})

@NamedNativeQueries({
        @NamedNativeQuery(
                name = "Company.fetchCompaniesWithJobsByStatusNative",
                query = "SELECT DISTINCT c.* FROM companies c JOIN jobs j ON c.id = j.company_id WHERE j.status = ?",
                resultClass = Company.class
        )
})

public class Company extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME" , nullable = false , unique = true)
    private String name;

    @Column(name = "LOGO", length = 500)
    private String logo;

    @Column(name = "INDUSTRY", nullable = false, length = 100)
    private String industry;

    @Column(name = "SIZE", nullable = false, length = 50)
    private String size;

    @Column(name = "RATING", nullable = false, precision = 3, scale = 2)
    private BigDecimal rating;

    @Column(name = "LOCATIONS", length = 1000)
    private String locations;

    @Column(name = "FOUNDED", nullable = false)
    private Integer founded;

    @Lob
    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "EMPLOYEES")
    private Integer employees;

    @Column(name = "WEBSITE", length = 500)
    private String website;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    @SQLRestriction("status = 'ACTIVE'")
    private List<Job> jobs = new ArrayList<>();
}
