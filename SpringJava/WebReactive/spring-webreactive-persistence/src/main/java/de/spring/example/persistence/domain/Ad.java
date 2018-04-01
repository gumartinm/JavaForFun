package de.spring.example.persistence.domain;

import java.io.Serializable;
import java.util.Date;
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

@JsonIdentityInfo(generator=ObjectIdGenerators.StringIdGenerator.class, property="jsonId")
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
	// https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/#mongo-template.id-handling
	// So, no way of using Long :(
	@Id
	private String id;
	
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
	// IT DOES NOT WORK. I THINK THERE IS A BUG BECAUSE MappingMongoConverter.this.conversionService never includes my custom converters!!!!
	// ALL THIS STUFF SUCKS A LOT!!!
	//private OffsetDateTime createdAt;
	private Date createdAt;
	
	@NotNull
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ssZ")
	// IT DOES NOT WORK. I THINK THERE IS A BUG BECAUSE MappingMongoConverter.this.conversionService never includes my custom converters!!!!
	// ALL THIS STUFF SUCKS A LOT!!!
	//private OffsetDateTime updatedAt;
	private Date updatedAt;

	// It will be used by JPA when filling the property fields with data coming from data base.
	protected Ad() {

	}

	// It will be used by my code (for example by Unit Tests)
	public Ad(String id, Set<AdDescription> adDescriptions, Long companyId, Long companyCategId, String adMobileImage,
			Date createdAt, Date updatedAt) {
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
	
	public String getId() {
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

	public Date getCreatedAt() {
		return createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}
}
