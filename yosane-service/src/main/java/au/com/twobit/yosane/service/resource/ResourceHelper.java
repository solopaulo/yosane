package au.com.twobit.yosane.service.resource;

import java.lang.reflect.Method;
import java.util.Collection;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.twobit.yosane.api.ErrorCode;
import au.com.twobit.yosane.service.resource.annotations.Relation;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.theoryinpractise.halbuilder.api.Link;
import com.theoryinpractise.halbuilder.api.Representation;
import static com.theoryinpractise.halbuilder.DefaultRepresentationFactory.HAL_JSON;

/** Utilities to assist in building a rest uri from a resource class
 * 
 * @author paul
 *
 */
public final class ResourceHelper {
    
    private final static Logger log = LoggerFactory.getLogger(ResourceHelper.class); 
    
    /** Builds a URI for a given class and named method
     * 
     * Methods are named by @Named annotations to avoid breakages resulting from refactoring
     * 
     * @param resourceClass The annotated resource class to base the path from
     * @param methodNamed The @Named method from which we wish to build a path
     * @param params Parameters to fill out the path
     * @return
     */
    public static Link createLink(Class<?> resourceClass,String methodNamed, Object ...params) {
        Link link = null;
        try {
            // find a matching method
            Collection<Method> methodList = 
                    Collections2.filter( Lists.newArrayList(resourceClass.getMethods()),
                                         NamedFilter.from(methodNamed));
            if ( methodList.size() != 1 ) {
                throw new Exception();
            }
            Method method = methodList.iterator().next();
            Relation relation = method.getAnnotation(Relation.class);
            link = new Link(null,relation.relation(), UriBuilder.fromResource(resourceClass).path(method).build(params).toString());
        } catch (Exception x) { 
            String error = String.format("Failed to create link: %s",x.getMessage());
            log.error(error);
        }
        return link;
    }
   
    
    /** Creates a base path from a jersey resource class
     * 
     * @param resourceClass A class with a Jersey @Path annotation
     * @return Returns an Optional that may have the Path URI, if an annotation was found
     */
    public static Link createLink(Class<?> resourceClass) {
        Link link = null;
        try {
            String rel = resourceClass.getAnnotation(Relation.class).relation();
            link = new Link(null,rel,UriBuilder.fromResource(resourceClass).build().toString());
        } catch (Exception x) {
            String error = String.format("Failed to create link: %s",x.getMessage());
            log.error(error);
        }
        return link;
    }
    
    
    /** Retrieve the relation name of a given resource
     * 
     */
    public static String createRelation(Class<?> resourceClass) {
        if ( resourceClass == null || resourceClass.getAnnotation(Relation.class) == null ) {
            return "unknown";
        }
        return resourceClass.getAnnotation(Relation.class).relation();
    }
    
    
    /** A filter to restrict to resource methods matching a named filter
     * 
     * @author paul
     *
     */
    final static class NamedFilter {
        public static Predicate<Method> from(final String named) {
            return new Predicate<Method>() {
                @Override
                public boolean apply(Method m) {
                    try {
                        return m.getAnnotation(Relation.class).method().equals(named);
                    } catch(Exception x) { }
                    return false;
                }
            };
        }
    }
    
    static Link createErrorLink(String errorCode) {
        return createLink(ErrorsResource.class,ErrorsResource.METHOD_ERROR_HELP, errorCode);
    }
    
    public static Response generateErrorResponse(Representation response, String errorCode, String errorDescription) {
        if ( response == null ) {
            return null;
        }
        Link errorLink = createErrorLink(errorCode);
        response.withBean(new ErrorCode(errorCode, errorDescription));
        response.withLink( errorLink.getRel(), errorLink.getHref());
        
        return Response
                .status(Status.INTERNAL_SERVER_ERROR)
                .entity( response.toString( HAL_JSON ) )
                .build();
    }
}
