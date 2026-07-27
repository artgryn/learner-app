package com.artgr.learner.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "language")
public class Language {

    // Postgres char(2)/bpchar, not varchar - JdbcTypeCode.CHAR keeps Hibernate's
    // schema validation (Types#CHAR) matching the actual column type.
    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "char(2)")
    private String code;

    @Column(nullable = false)
    private String name;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
