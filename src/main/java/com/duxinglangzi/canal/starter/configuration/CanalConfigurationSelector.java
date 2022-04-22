package com.duxinglangzi.canal.starter.configuration;

import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author wuqiong 2022/4/12
 * @description
 */
public class CanalConfigurationSelector implements DeferredImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{CanalBootstrapConfiguration.class.getName()};
    }
}
