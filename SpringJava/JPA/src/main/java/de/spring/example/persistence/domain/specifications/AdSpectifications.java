package de.spring.example.persistence.domain.specifications;

import java.time.LocalDate;

//import javax.persistence.criteria.CriteriaBuilder;
//import javax.persistence.criteria.CriteriaQuery;
//import javax.persistence.criteria.Predicate;
//import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import de.spring.example.persistence.domain.Ad;

public class AdSpectifications {
	
//	public static Specification<Ad> createdToday() {
//		return new Specification<Ad>() {
//
//			@Override
//			public Predicate toPredicate(Root<Ad> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
//				final LocalDate date = LocalDate.now();
//				
//				return cb.equal(root.get("createdAt"), date);
//			}
//
//		};
//		
//	}
	
	/**
	 * The same using lambda expressions
	 */
	public static Specification<Ad> createdToday() {
		return (root, query, cb) -> {
			final LocalDate date = LocalDate.now();
			
			return cb.equal(root.get("createdAt"), date);
        };
	}
	
//	public static Specification<Ad> mobileImage(String image) {
//		return new Specification<Ad>() {
//
//			@Override
//			public Predicate toPredicate(Root<Ad> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
//				
//				return cb.equal(root.get("adMobileImage"), image);
//			}
//
//		};
//		
//	}
	
	/**
	 * The same using lambda expressions
	 */
	public static Specification<Ad> mobileImage(String image) {
		return (root, query, cb) -> {
			return cb.equal(root.get("adMobileImage"), image);
        };
	}
}
