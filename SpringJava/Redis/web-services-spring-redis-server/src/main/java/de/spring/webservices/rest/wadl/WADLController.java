package de.spring.webservices.rest.wadl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.jvnet.ws.wadl.Application;
import org.jvnet.ws.wadl.Doc;
import org.jvnet.ws.wadl.Param;
import org.jvnet.ws.wadl.ParamStyle;
import org.jvnet.ws.wadl.Representation;
import org.jvnet.ws.wadl.Request;
import org.jvnet.ws.wadl.Resource;
import org.jvnet.ws.wadl.Resources;
import org.jvnet.ws.wadl.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.servlet.handler.AbstractHandlerMethodMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

/**
 * Taken from: http://javattitude.com/2014/05/26/wadl-generator-for-spring-rest/
 * 
 * With some modifications.
 *
 */
@Controller
@RequestMapping("/rest.wadl")
public class WADLController {
	private static final String NAMESPACE_URI = "http://www.w3.org/2001/XMLSchema" ;
	private static final String WADL_TITLE = "Spring REST Service WADL";
	
    private final AbstractHandlerMethodMapping<RequestMappingInfo> handlerMapping;
    private final ApplicationContext context;
    
    @Autowired
	public WADLController(AbstractHandlerMethodMapping<RequestMappingInfo> handlerMapping, ApplicationContext context) {
		this.handlerMapping = handlerMapping;
		this.context = context;
	}
	
    @RequestMapping(produces = { MediaType.APPLICATION_XML_VALUE }, method=RequestMethod.GET ) 
    public @ResponseBody Application generateWadl(HttpServletRequest request) {
        Application result = new Application();
        
        Doc doc = new Doc();
        doc.setTitle(WADL_TITLE);
        result.getDoc().add(doc);
        
        Resources wadlResources = new Resources();
        wadlResources.setBase(getBaseUrl(request));
                 
        handlerMapping.getHandlerMethods().forEach( (mappingInfo, handlerMethod) -> {
            Object object = handlerMethod.getBean();
            Object bean = context.getBean(object.toString());
            if(!bean.getClass().isAnnotationPresent(RestController.class)) {
            	return;
            }
            
            mappingInfo.getMethodsCondition().getMethods().forEach(httpMethod -> {
            	Resource wadlResource = null; 
                org.jvnet.ws.wadl.Method wadlMethod = new org.jvnet.ws.wadl.Method();
     
                Set<String> pattern =  mappingInfo.getPatternsCondition().getPatterns();
                for (String uri : pattern) {
                	wadlResource = createOrFind(uri, wadlResources); 
                    wadlResource.setPath(uri);      
                }
                 
                wadlMethod.setName(httpMethod.name());
                Method javaMethod = handlerMethod.getMethod();
                wadlMethod.setId(javaMethod.getName());
                Doc wadlDocMethod = new Doc();
                wadlDocMethod.setTitle(javaMethod.getDeclaringClass().getSimpleName() + "." + javaMethod.getName());
                wadlMethod.getDoc().add(wadlDocMethod);
                 
                // Request
                Request wadlRequest = new Request();
                Annotation[][] annotations = javaMethod.getParameterAnnotations();
                Class<?>[] paramTypes = javaMethod.getParameterTypes();
                int i = 0;
                for (Annotation[] annotation : annotations) {
                	Class<?> paramType =paramTypes[i];
                	i++;
                    for (Annotation annotation2 : annotation) {
                    
                    	Param wadlParam = doParam(annotation2, paramType);
                    	if (wadlParam != null) {
                    		wadlRequest.getParam().add(wadlParam);
                    	}
                    }
                }
                if (!wadlRequest.getParam().isEmpty() ) {
                    wadlMethod.setRequest(wadlRequest);
                }
                 
                // Response
                Set<MediaType> mediaTypes = mappingInfo.getProducesCondition().getProducibleMediaTypes();
                if (!mediaTypes.isEmpty()) {
                	ResponseStatus status = handlerMethod.getMethodAnnotation(ResponseStatus.class);
                    Response wadlResponse = doResponse(mediaTypes, status);
                    wadlMethod.getResponse().add(wadlResponse);
                }
                
                
                wadlResource.getMethodOrResource().add(wadlMethod);          
            });
             
        });
        result.getResources().add(wadlResources);
         
        return result;
    }
    
    private Param doParam(Annotation annotation2, Class<?> paramType) {
    	Param wadlParam = null;
    	
        if (annotation2 instanceof RequestParam ) {
            RequestParam param = (RequestParam)annotation2;
            
            wadlParam = new Param();
            QName nm = convertJavaToXMLType(paramType);
            if (StringUtils.isNotEmpty(nm.getLocalPart())) {
            	wadlParam.setType(nm);
            }
            wadlParam.setName(param.value());
            
            
            if (!ValueConstants.DEFAULT_NONE.equals(param.defaultValue())) {
                String defaultValue = cleanDefault(param.defaultValue());
                if (StringUtils.isNotEmpty(defaultValue) ) {
                    wadlParam.setDefault(defaultValue);
                }
            }

            wadlParam.setStyle(ParamStyle.QUERY);
            wadlParam.setRequired(param.required());       
        } else if (annotation2 instanceof PathVariable ) {
            PathVariable param = (PathVariable)annotation2;
            
            wadlParam = new Param();                            
            QName nm = convertJavaToXMLType(paramType);
            if (StringUtils.isNotEmpty(nm.getLocalPart())) {
            	wadlParam.setType(nm);
            }
            wadlParam.setName(param.value());
            
            
            wadlParam.setStyle(ParamStyle.TEMPLATE);
            wadlParam.setRequired(true);
        }
        
        return wadlParam;
    }
     
    private Response doResponse(Set<MediaType> mediaTypes, ResponseStatus status) {
    	Response wadlResponse = new Response();
        
        mediaTypes.forEach(mediaType -> {
            Representation wadlRepresentation = new Representation();
            wadlRepresentation.setMediaType(mediaType.toString());
            wadlResponse.getRepresentation().add(wadlRepresentation);
        });
        
        wadlResponse.getStatus().add(getResposeValue(status));

        return wadlResponse;
    }
    
    private long getResposeValue(ResponseStatus status) {
    	if(status == null) {
        	return HttpStatus.OK.value();
        } else {
            HttpStatus httpcode = status.value();
            return httpcode.value();
        }
    }
    
    private QName convertJavaToXMLType(Class<?> type) {
    	QName nm = new QName("");
    	String classname = type.toString();
    	classname = classname.toLowerCase();
    	
		if (classname.indexOf("string") >= 0) {
			nm = new QName(NAMESPACE_URI, "string", "xs");
		} else if (classname.indexOf("integer") >= 0) {
			nm = new QName(NAMESPACE_URI, "int", "xs");
		} else if (classname.indexOf("long") >= 0) {
			nm = new QName(NAMESPACE_URI, "long", "xs");
		}
    	
    	return nm;
    }
    
    private Resource createOrFind(String uri, Resources wadResources) {
    	  List<Resource> current = wadResources.getResource();
    	  for(Resource resource:current) {
    		  if(resource.getPath().equalsIgnoreCase(uri)){
    			  return resource;
    		  }
    	  }
    	  Resource wadlResource = new Resource();
    	  current.add(wadlResource);
    	  return wadlResource;
    }
    
    private String getBaseUrl(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        int index = requestUri.lastIndexOf('/');
        requestUri = requestUri.substring(0, index);
        
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + requestUri;
    }
     
    private String cleanDefault(String value) {
        value = value.replaceAll("\t", "");
        value = value.replaceAll("\n", "");
        return value;
    }
}
