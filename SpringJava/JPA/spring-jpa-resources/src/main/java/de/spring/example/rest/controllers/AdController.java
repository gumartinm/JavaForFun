package de.spring.example.rest.controllers;

import javax.inject.Inject;

import org.resthub.web.controller.RepositoryBasedRestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.history.Revision;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import de.spring.example.persistence.domain.Ad;
import de.spring.example.persistence.repository.AdRepository;

@RestController
@RequestMapping("/ads/")
public class AdController extends RepositoryBasedRestController<Ad, Long, AdRepository> {

    @Override
    @Inject
    public void setRepository(AdRepository repository) {
        this.repository = repository;
    }
    
	// I do not have to do anything here because all I need is implemented by RepositoryBasedRestController :)

    // @Transactional is implemented by org.springframework.data.jpa.repository.support.SimpleJpaRepository
    // By default, SimpleJpaRepository will be automatically implemented by my
    // Spring JPA repositories: AdRepository and AdDescriptionRepository.
    
    
    
    
    
    
    
    /** WE ARE EXTENDING RepositoryBasedRestController WITH METHODS FOR RETRIEVING REVISION NUMBERS!!! **/


    /**
     * Returns a {@link Page} of revisions for the entity with the given id
     *
     * @param page       Page number starting from 0. default to 0
     * @param size       Number of resources by pages. default to 10
     * @param direction  Optional sort direction, could be "asc" or "desc"
     * @param properties Ordered list of comma separeted properies used for sorting resulats. At least one property should be provided if direction is specified
     * @return OK http status code if the request has been correctly processed, with the a paginated collection of all resource enclosed in the body.
     */
    @RequestMapping(value="{id}/revisions/", method = RequestMethod.GET)
    @ResponseBody
    public Page<Revision<Integer, Ad>> findRevisionsPaginated(@PathVariable Long id,
    							 @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                 @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
                                 @RequestParam(value = "direction", required = false, defaultValue = "") String direction,
                                 @RequestParam(value = "properties", required = false) String properties) {
        Assert.isTrue(page > 0, "Page index must be greater than 0");
        Assert.isTrue(direction.isEmpty() || direction.equalsIgnoreCase(Sort.Direction.ASC.toString()) || direction.equalsIgnoreCase(Sort.Direction.DESC.toString()), "Direction should be ASC or DESC");
        if(direction.isEmpty()) {
        	return this.repository.findRevisions(id, new PageRequest(page - 1, size));
        } else {
            Assert.notNull(properties);
            return this.repository.findRevisions(id, new PageRequest(page - 1, size, new Sort(Sort.Direction.fromString(direction.toUpperCase()), properties.split(","))));
        }
    }
}
