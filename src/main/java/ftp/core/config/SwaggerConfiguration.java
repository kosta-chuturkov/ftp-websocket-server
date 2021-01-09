package ftp.core.config;

import ftp.core.controller.FileManagementController;
import ftp.core.controller.UserManagementController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.RedirectView;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.ModelRendering;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static ftp.core.constants.ServerConstants.AUTHORIZATION_HEADER;
import static ftp.core.constants.ServerConstants.REQUEST_ID_HEADER;
import static springfox.documentation.builders.PathSelectors.regex;

/**
 * Configures swagger documentation.
 */
@Configuration
@ComponentScan(basePackageClasses = {
        FileManagementController.class
})
@EnableSwagger2WebMvc
public class SwaggerConfiguration extends WebMvcConfigurationSupport {

    private static final String INFO_TITLE = "FTP Server REST API";
    private static final String SWAGGER_INFO_FILE = "info.md";
    private static final String INFO_API_VERSION = "2.0.0";
    private static final String API_PATH_REGEX = "/api/v1*|/api/v1/.*";
    private static final String GROUP_NAME = "ftp-server";


    @Bean
    public RequestParameterAddition authorizationHeader() {

        final String PARAM_TYPE = "header";
        final String PARAM_DESCRIPTION = "Authorization hedear needed for user authentication";

        final Parameter parameter = new ParameterBuilder()
                .name(AUTHORIZATION_HEADER)
                .description(PARAM_DESCRIPTION)
                .defaultValue("dummy-authorization")
                .required(true)
                .modelRef(new ModelRef("string"))
                .parameterType(PARAM_TYPE)
                .build();

        return new RequestParameterAddition(parameter,
                EnumSet.of(HttpMethod.GET, HttpMethod.POST, HttpMethod.DELETE, HttpMethod.PUT));
    }

    @Bean
    public RequestParameterAddition requestIdHeader() {

        final String PARAM_TYPE = "header";
        final String PARAM_DESCRIPTION = "Request id";

        final Parameter parameter = new ParameterBuilder()
                .name(REQUEST_ID_HEADER)
                .description(PARAM_DESCRIPTION)
                .defaultValue("dummy-request-id")
                .required(true)
                .modelRef(new ModelRef("string"))
                .parameterType(PARAM_TYPE)
                .build();

        return new RequestParameterAddition(parameter,
                EnumSet.of(HttpMethod.GET, HttpMethod.POST, HttpMethod.DELETE, HttpMethod.PUT));
    }


    @Bean
    public Docket ftpApi() throws IOException {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName(GROUP_NAME)
                .apiInfo(apiInfo())
                .useDefaultResponseMessages(false)
                .directModelSubstitute(java.time.ZonedDateTime.class, Date.class)
                .directModelSubstitute(RedirectView.class, Void.class)
                .globalResponseMessage(RequestMethod.GET,
                        ResponseDefinition.getResponses(RequestMethod.GET))
                .globalResponseMessage(RequestMethod.POST,
                        ResponseDefinition.getResponses(RequestMethod.POST))
                .globalResponseMessage(RequestMethod.PUT,
                        ResponseDefinition.getResponses(RequestMethod.PUT))
                .globalResponseMessage(RequestMethod.DELETE,
                        ResponseDefinition.getResponses(RequestMethod.DELETE))
                .tags(
                        new Tag(FileManagementController.TAG, "Files API"),
                        new Tag(UserManagementController.TAG, "Users API")
                )
                .select()
                .paths(regex(API_PATH_REGEX))
                .build()
                .securityContexts(Collections.singletonList(actuatorSecurityContext()))
                .securitySchemes(Collections.singletonList(basicAuthScheme()));
    }

    private SecurityContext actuatorSecurityContext() {
        return SecurityContext.builder()
                .securityReferences(Collections.singletonList(basicAuthReference()))
                .forPaths(PathSelectors.ant("/actuator/**"))
                .build();
    }

    private SecurityScheme basicAuthScheme() {
        return new BasicAuth("basicAuth");
    }

    private SecurityReference basicAuthReference() {
        return new SecurityReference("basicAuth", new AuthorizationScope[0]);
    }

    @Bean
    public UiConfiguration uiConfig() {
        return UiConfigurationBuilder.builder()
                .defaultModelRendering(ModelRendering.MODEL)
                .build();
    }

    private ApiInfo apiInfo() throws IOException {
        return new ApiInfoBuilder()
                .title(INFO_TITLE)
                .version(INFO_API_VERSION)
                .build();
    }

    /**
     * Generates response message definitions via http method
     */
    private static final class ResponseDefinition {

        private static final Map<Integer, String> RESPONSE_DEF_MAP = Collections
                .unmodifiableMap(new HashMap<Integer, String>() {
                    {
                        put(HttpStatus.OK.value(), "OK");
                        put(HttpStatus.NOT_MODIFIED.value(), "Not Modified");
                        put(HttpStatus.TEMPORARY_REDIRECT.value(),
                                "Temporary Redirect (be aware that not all REST clients follow redirects)");
                        put(HttpStatus.BAD_REQUEST.value(),
                                "Bad request! Check if you provided all required and correctly formatted data..");
                        put(HttpStatus.UNAUTHORIZED.value(),
                                "Unauthorised (really means unauthenticated - missing or invalid credentials)");
                        put(HttpStatus.FORBIDDEN.value(),
                                "Forbidden (credentials found, but user not authorised to access this resource)");
                        put(HttpStatus.NOT_FOUND.value(), "Not Found");
                        put(HttpStatus.CONFLICT.value(),
                                "Conflict! Invalid current state or database constraints not met!");
                        put(HttpStatus.PRECONDITION_FAILED.value(),
                                "Precondition failed! Check if you data is valid!");
                    }
                });

        private static final Map<RequestMethod, List<Integer>> METHOD_RESPONSES_MAP = Collections
                .unmodifiableMap(new HashMap<RequestMethod, List<Integer>>() {
                    {
                        put(RequestMethod.GET, Arrays.asList(
                                HttpStatus.OK.value(),
                                HttpStatus.NOT_MODIFIED.value(),
                                HttpStatus.BAD_REQUEST.value(),
                                HttpStatus.NOT_FOUND.value(),
                                HttpStatus.PRECONDITION_FAILED.value()
                        ));
                        put(RequestMethod.POST, Arrays.asList(
                                HttpStatus.OK.value(),
                                HttpStatus.TEMPORARY_REDIRECT.value(),
                                HttpStatus.BAD_REQUEST.value(),
                                HttpStatus.NOT_FOUND.value(),
                                HttpStatus.CONFLICT.value(),
                                HttpStatus.PRECONDITION_FAILED.value()
                        ));
                        put(RequestMethod.DELETE, Arrays.asList(
                                HttpStatus.OK.value(),
                                HttpStatus.BAD_REQUEST.value(),
                                HttpStatus.NOT_FOUND.value(),
                                HttpStatus.CONFLICT.value(),
                                HttpStatus.PRECONDITION_FAILED.value()
                        ));
                        put(RequestMethod.PUT, Arrays.asList(
                                HttpStatus.OK.value(),
                                HttpStatus.TEMPORARY_REDIRECT.value(),
                                HttpStatus.BAD_REQUEST.value(),
                                HttpStatus.NOT_FOUND.value(),
                                HttpStatus.CONFLICT.value(),
                                HttpStatus.PRECONDITION_FAILED.value()
                        ));
                    }
                });

        private static ResponseMessage buildResponse(int code, String message) {
            return new ResponseMessageBuilder()
                    .code(code)
                    .message(message)
                    .build();
        }

        static List<ResponseMessage> getResponses(RequestMethod requestMethod) {
            return METHOD_RESPONSES_MAP.get(requestMethod).stream()
                    .map(code -> buildResponse(code, RESPONSE_DEF_MAP.get(code)))
                    .collect(Collectors.toList());
        }
    }
}
