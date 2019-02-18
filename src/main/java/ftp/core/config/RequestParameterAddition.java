package ftp.core.config;

import com.google.common.collect.Lists;
import java.util.Set;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;

public class RequestParameterAddition implements OperationBuilderPlugin {
  private final Parameter parameter;
  private final Set<HttpMethod> methods;

  public RequestParameterAddition(Parameter parameter, Set<HttpMethod> methods) {
    this.parameter = parameter;
    this.methods = methods;
  }

  public void apply(OperationContext context) {
    if (context != null && this.methods != null && !this.methods.isEmpty() && this.methods.contains(context.httpMethod())) {
      context.operationBuilder().parameters(Lists.newArrayList(this.parameter));
    }

  }

  public boolean supports(DocumentationType documentationType) {
    return DocumentationType.SWAGGER_2.equals(documentationType);
  }
}
