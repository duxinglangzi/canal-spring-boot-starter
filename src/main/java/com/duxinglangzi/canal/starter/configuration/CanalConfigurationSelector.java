package com.duxinglangzi.canal.starter.configuration;

import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author wuqiong 2022/4/12
 */
public class CanalConfigurationSelector implements DeferredImportSelector {

    /**
     * 扫描器 导入指定类， 这里导入 CanalBootstrapConfiguration.class
     *
     * @param importingClassMetadata
     * @return java.lang.String[]
     * @author wuqiong 2022-04-23 20:20
     */
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{CanalBootstrapConfiguration.class.getName()};
    }
}
