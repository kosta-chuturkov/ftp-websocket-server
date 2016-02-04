package ftp.core.persistance.hibernate.session.factory;

import java.net.URI;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import com.google.common.collect.Lists;

public class CustomLocalSessionFactoryBean extends LocalSessionFactoryBean {

	private static final Logger logger = Logger.getLogger(CustomLocalSessionFactoryBean.class);
	private final String classpathPrefix = "classpath*:";
	private String hibernateQueriesTemplate;

	@Override
	public void setMappingResources(String... mappingResources) {
		String resourcesPath = "";
		Resource[] resources = null;
		final List<String> result = Lists.newArrayList();
		PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver();
		try {
			for (String mappingResource : mappingResources) {
				if (mappingResource.endsWith("/")) {
					resourcesPath = classpathPrefix + mappingResource + hibernateQueriesTemplate;
					resources = pathMatchingResourcePatternResolver.getResources(resourcesPath);
					if (resources == null || resources.length == 0) {
						String errorMessage = "No resources found on path:" + resourcesPath;
						logger.error(errorMessage);
						throw new Exception(errorMessage);
					}
					URI resourceUri = null;
					String fileClasspathPath = null;
					for (Resource resource : resources) {
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
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		super.setMappingResources(result.toArray(new String[0]));
	}

	public void setHibernateQueriesTemplate(String hibernateQueriesTemplate) {
		this.hibernateQueriesTemplate = hibernateQueriesTemplate;
	}
}
