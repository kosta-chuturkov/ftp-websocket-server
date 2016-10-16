package ftp.core.persistance.hibernate.session.factory;

import com.google.common.collect.Lists;
import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import java.net.URI;
import java.util.List;

public class CustomLocalSessionFactoryBean extends LocalSessionFactoryBean {

    private static final Logger logger = Logger.getLogger(CustomLocalSessionFactoryBean.class);
    private final String classpathPrefix = "classpath*:";
    private String hibernateQueriesTemplate;

    @Override
    public void setMappingResources(final String... mappingResources) {
        String resourcesPath;
        Resource[] resources;
        final List<String> result = Lists.newArrayList();
        final PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver();
        try {
            for (final String mappingResource : mappingResources) {
                if (mappingResource.endsWith("/")) {
                    resourcesPath = this.classpathPrefix + mappingResource + this.hibernateQueriesTemplate;
                    resources = pathMatchingResourcePatternResolver.getResources(resourcesPath);
                    if (resources == null || resources.length == 0) {
                        final String errorMessage = "No resources found on path:" + resourcesPath;
                        logger.error("error getting resources", new Exception(errorMessage));
                        throw new Exception(errorMessage);
                    }
                    URI resourceUri;
                    String fileClasspathPath;
                    for (final Resource resource : resources) {
                        resourceUri = resource.getURI();
                        fileClasspathPath = resourceUri.toString();
                        // Parse the file back to reletive classpath path.
                        fileClasspathPath = fileClasspathPath.substring(fileClasspathPath.indexOf(mappingResource),
                                fileClasspathPath.length());
                        result.add(fileClasspathPath);
                    }
                } else {
                    result.add(mappingResource);
                }
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        super.setMappingResources(result.toArray(new String[0]));
    }

    public void setHibernateQueriesTemplate(final String hibernateQueriesTemplate) {
        this.hibernateQueriesTemplate = hibernateQueriesTemplate;
    }
}
