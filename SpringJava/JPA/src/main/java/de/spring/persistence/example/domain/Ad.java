package de.spring.persistence.example.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(schema = "mybatis_example")
public class Ad implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;
	
	@Max(60)
	private Long companyId;
	
	@Max(40)
	@Column
	private Long companyCategId;
	
	@Size(min=2, max=240)
	@Column
	private String adMobileImage;

	@NotNull
	@Column(nullable = false)
	private LocalDateTime createdAt;
	
	@NotNull
	@Column(nullable = false)
	private LocalDateTime updatedAt;

	// It will be used by JPA when filling the property fields with data coming from data base.
	protected Ad() {

	}

	// It will be used by my code (for example by Unit Tests)
	public Ad(Long id, Long companyId, Long companyCategId, String adMobileImage, LocalDateTime createdAt,
			LocalDateTime updatedAt) {
		this.id = id;
		this.companyCategId = companyCategId;
		this.adMobileImage = adMobileImage;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public Long getId() {
		return id;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public Long getCompanyCategId() {
		return companyCategId;
	}

	public String getAdMobileImage() {
		return adMobileImage;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((adMobileImage == null) ? 0 : adMobileImage.hashCode());
		result = prime * result + ((companyCategId == null) ? 0 : companyCategId.hashCode());
		result = prime * result + ((companyId == null) ? 0 : companyId.hashCode());
		result = prime * result + ((createdAt == null) ? 0 : createdAt.hashCode());
		result = prime * result + ((updatedAt == null) ? 0 : updatedAt.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ad other = (Ad) obj;
		if (adMobileImage == null) {
			if (other.adMobileImage != null)
				return false;
		} else if (!adMobileImage.equals(other.adMobileImage))
			return false;
		if (companyCategId == null) {
			if (other.companyCategId != null)
				return false;
		} else if (!companyCategId.equals(other.companyCategId))
			return false;
		if (companyId == null) {
			if (other.companyId != null)
				return false;
		} else if (!companyId.equals(other.companyId))
			return false;
		if (createdAt == null) {
			if (other.createdAt != null)
				return false;
		} else if (!createdAt.equals(other.createdAt))
			return false;
		if (updatedAt == null) {
			if (other.updatedAt != null)
				return false;
		} else if (!updatedAt.equals(other.updatedAt))
			return false;
		return true;
	}
}
