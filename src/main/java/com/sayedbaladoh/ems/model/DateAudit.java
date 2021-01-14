package com.sayedbaladoh.ems.model;

import java.io.Serializable;
import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModelProperty.AccessMode;

/**
 * The DateAudit entity.
 * 
 * Provides auditing fields (createdAt and an updatedAt) to other domain models
 * that extend this class.
 * 
 * It uses JPA’s <code>AuditingEntityListener</code> to automatically populate
 * createdAt and updatedAt values when we persist an entity.
 * 
 * @author Sayed Baladoh
 *
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(
		value = { "createdAt", "updatedAt" },
		allowGetters = true)
public abstract class DateAudit implements Serializable {

	@ApiModelProperty(
			hidden = true,
			accessMode = AccessMode.READ_ONLY)
	@CreatedDate
	@Column(
			nullable = false,
			updatable = false)
	private Instant createdAt;

	@ApiModelProperty(
			hidden = true,
					accessMode=AccessMode.READ_ONLY)
	@LastModifiedDate
	@Column(
			nullable = false)
	private Instant updatedAt;

	/**
	 * Get when the entity is persisted to the database for the first time.
	 * 
	 * @return when the entity is persisted to the database for the first time.
	 */
	public Instant getCreatedAt() {
		return createdAt;
	}

	/**
	 * Set when the entity is persisted to the database for the first time.
	 * 
	 * @param createdAt
	 */
	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	/**
	 * Get when the entity is updated to the database.
	 * 
	 * @return when the entity is updated to the database.
	 */
	public Instant getUpdatedAt() {
		return updatedAt;
	}

	/**
	 * Set when the entity is updated to the database.
	 * 
	 * @param updatedAt
	 */
	public void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
	}
}