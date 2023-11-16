package com.file.controller;

import jakarta.persistence.Column;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@NoArgsConstructor
@ToString
public class SuperEntity {

    @Column(name = "create_by", length = 100)
    protected String createdBy;

    @Column(name = "create_date")
    @GenericField(sortable = Sortable.YES)
    protected LocalDateTime createdDate;

    @Column(name = "modify_by", length = 100)
    protected String modifyBy;

    @Column(name = "modify_date")
    @GenericField(sortable = Sortable.YES)
    protected LocalDateTime modifyDate;

    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof Jwt)) {
            this.createdBy = authentication.getName();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.modifyDate = LocalDateTime.now();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof Jwt)) {
            this.modifyBy = authentication.getName();
        }
    }
}
