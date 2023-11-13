package com.thanglv.documentapi.entity;

import com.thanglv.documentapi.controller.SuperEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "file_info")
@Indexed
@ToString
public class FileInfo extends SuperEntity {

    @Id
    @GeneratedValue(generator = "hibernate-uuid")
    @GenericGenerator(name = "hibernate-uuid", strategy = "uuid2")
    @GenericField(sortable = Sortable.YES)
    private String id;

    private String kcResourceId;
    private String name;
    @Column(length = 200)
    private String originalName;
    private String extension;
    @Column(length = 1000)
    private String path;
    private Long size;
    private String isPublic;
    private String isDeleted;
}
