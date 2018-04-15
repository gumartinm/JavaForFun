package de.spring.webservices.rest.controller.apidocs;

import java.util.List;

import de.spring.webservices.domain.Location;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

public interface LocationControllerDocumentation {
	
    @ApiOperation(value = "Get all available locations", nickname = "findAllLocationsPaginated", responseContainer = "List", response = Location.class)
    @ApiResponses({
            @ApiResponse(code = 400, message = "Specific invalid input")
    })
    List<Location> findAllLocationsPaginated();
}
