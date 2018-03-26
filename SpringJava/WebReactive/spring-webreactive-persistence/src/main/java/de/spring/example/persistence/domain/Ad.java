package de.spring.example.persistence.domain;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Set;

import javax.persistence.Id;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class, property="jsonId")
// 1. Named query is JPL. It is portable.
// 2. Instead of annotating the domain class we should be using @Query annotation at the query method
//    because it should be cleaner :)
//    So you'd better use @Query.
//http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.at-query
//See: de.spring.persistence.example.repository.AdRepository 
//@NamedQueries(
//		{
//			@NamedQuery(
//			name="Ad.findByIdQuery",
//			query="select a from Ad a where a.id = :id)
//		}
//	
//)
// 1. Native query IS NOT JPL. It is not portable and it is written directly in the native language
//    of the store. We can use special features at the cost of portability.
// 2. Instead of annotating the domain class we should be using @Query annotation at the query method
// 	  because it should be cleaner :)
//    So you'd better use @Query.
//    http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.at-query
//    See: de.spring.persistence.example.repository.AdRepository 
//@NamedNativeQueries(
//	{
//		@NamedNativeQuery(
//			name="Ad.findByIdNativeQuery",
//			query="SELECT * FROM ad WHERE ad.id = :id",
//			resultClass=Ad.class)
//	}		
//)
@Document
public class Ad implements Serializable {
	@Id
	private Long id;
	
	@JsonManagedReference
	private Set<AdDescription> adDescriptions;
	
	@Max(60)
	private Long companyId;
	
	@Max(40)
	private Long companyCategId;
	
	@Size(min=2, max=255)
	private String adMobileImage;

	@NotNull
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ssZ")
	private OffsetDateTime createdAt;
	
	@NotNull
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ssZ")
	private OffsetDateTime updatedAt;

	// It will be used by JPA when filling the property fields with data coming from data base.
	protected Ad() {

	}

	// It will be used by my code (for example by Unit Tests)
	public Ad(Long id, Set<AdDescription> adDescriptions, Long companyId, Long companyCategId, String adMobileImage,
			OffsetDateTime createdAt, OffsetDateTime updatedAt) {
		this.id = id;
		this.adDescriptions = adDescriptions;
		this.companyId = companyId;
		this.companyCategId = companyCategId;
		this.adMobileImage = adMobileImage;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	/**
	 * WARNING: JPA REQUIRES GETTERS!!!
	 */
	
	public Long getId() {
		return id;
	}

	public Set<AdDescription> getAdDescriptions() {
		return adDescriptions;
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

	public OffsetDateTime getCreatedAt() {
		return createdAt;
	}

	public OffsetDateTime getUpdatedAt() {
		return updatedAt;
	}
}
