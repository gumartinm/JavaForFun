package de.spring.example.persistence.domain;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
//import javax.persistence.NamedNativeQueries;
//import javax.persistence.NamedNativeQuery;
//import javax.persistence.NamedQueries;
//import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;

import de.spring.example.persistence.converters.OffsetDateTimeAttributeConverter;

@Entity
@Table(name="ad", schema="mybatis_example")
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
public class Ad implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", updatable=false, nullable=false)
	private Long id;
	
//  1. Using just OneToMany:
//	@OneToMany(mappedBy="ad", fetch=FetchType.LAZY,
//			cascade = CascadeType.ALL, targetEntity=AdDescription.class)
//  2. The same using @OneToMany + @JoinTable:
	@OneToMany(fetch=FetchType.LAZY, cascade = CascadeType.ALL, targetEntity=AdDescription.class)
	@JoinTable(
            name="ad_description",
            joinColumns = @JoinColumn( name="id"),
            inverseJoinColumns = @JoinColumn( name="ad_id")
        )
	private Set<AdDescription> adDescriptions;
	
	@Max(60)
	@Column(name="company_id")
	private Long companyId;
	
	@Max(40)
	@Column(name="company_categ_id")
	private Long companyCategId;
	
	@Size(min=2, max=255)
	@Column(name="ad_mobile_image")
	private String adMobileImage;

	@NotNull
	@Convert(converter=OffsetDateTimeAttributeConverter.class)
	@Column(name="created_at", nullable=false)
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ssZ")
	private OffsetDateTime createdAt;
	
	@NotNull
	@Convert(converter=OffsetDateTimeAttributeConverter.class)
	@Column(name="updated_at", nullable = false)
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
