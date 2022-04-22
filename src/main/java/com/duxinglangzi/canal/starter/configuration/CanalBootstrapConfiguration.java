package com.duxinglangzi.canal.starter.configuration;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author wuqiong 2022/4/12
 * @description
 */
public class CanalBootstrapConfiguration implements ImportBeanDefinitionRegistrar {

    public static final String CANAL_LISTENER_ANNOTATION_PROCESSOR_BEAN_NAME =
            "com.duxinglangzi.canal.starter.configuration.CanalListenerAnnotationBeanPostProcessor";

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        if (!registry.containsBeanDefinition(CANAL_LISTENER_ANNOTATION_PROCESSOR_BEAN_NAME)) {

            registry.registerBeanDefinition(CANAL_LISTENER_ANNOTATION_PROCESSOR_BEAN_NAME,
                    new RootBeanDefinition(CanalListenerAnnotationBeanPostProcessor.class));
        }


    }

}
