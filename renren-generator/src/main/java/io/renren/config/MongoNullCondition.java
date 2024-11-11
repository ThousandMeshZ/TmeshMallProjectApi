package io.renren.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @author: TMesh
 * @createTime: 2024-01-05 16:16
 **/
public class MongoNullCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String database = context.getEnvironment().getProperty("renren.database");
        return !"mongodb".equalsIgnoreCase(database);
    }
}
