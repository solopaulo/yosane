package au.com.twobit.yosane.service.resource;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.Collection;

import javax.inject.Named;
import javax.ws.rs.core.UriBuilder;

import au.com.twobit.yosane.service.resource.annotations.Relation;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

/** Utilities to assist in building a rest uri from a resource class
 * 
 * @author paul
 *
 */
public final class ResourcePathBuilder {
    /** Builds a URI for a given class and named method
     * 
     * Methods are named by @Named annotations to avoid breakages resulting from refactoring
     * 
     * @param resourceClass The annotated resource class to base the path from
     * @param methodNamed The @Named method from which we wish to build a path
     * @param params Parameters to fill out the path
     * @return
     */
    public static Optional<URI> createPath(Class<?> resourceClass,String methodNamed, Object ...params) {
        URI uri = null;
        try {
            // find a matching method
            Collection<Method> methodList = 
                    Collections2.filter( Lists.newArrayList(resourceClass.getMethods()),
                                         NamedFilter.from(methodNamed));
            if ( methodList.size() != 1 ) {
                throw new Exception();
            }
            Method method = methodList.iterator().next();
            uri = UriBuilder.fromResource(resourceClass).path(method).build(params);
        } catch (Exception x) { }
        return Optional.fromNullable(uri);
    }
   
    public static Optional<URI> createPath(Class<?> resourceClass) {
        URI uri = null;
        try {
            uri = UriBuilder.fromResource(resourceClass).build();
        } catch (Exception x) { }
        return Optional.fromNullable(uri);
    }
    
    
    /** Retrieve the relation name of a given resource
     * 
     */
    public static String createRelation(Class<?> resourceClass) {
        if ( resourceClass == null || resourceClass.getAnnotation(Relation.class) == null ) {
            return "unknown";
        }
        return resourceClass.getAnnotation(Relation.class).value();
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
                        return m.getAnnotation(Named.class).value().equals(named);
                    } catch(Exception x) { }
                    return false;
                }
            };
        }
    }
}
