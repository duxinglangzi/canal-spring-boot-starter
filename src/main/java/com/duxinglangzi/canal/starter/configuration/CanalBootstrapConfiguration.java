package com.duxinglangzi.canal.starter.configuration;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author wuqiong 2022/4/12
 */
public class CanalBootstrapConfiguration implements ImportBeanDefinitionRegistrar {

    public static final String CANAL_LISTENER_ANNOTATION_PROCESSOR_BEAN_NAME =
            "com.duxinglangzi.canal.starter.configuration.CanalListenerAnnotationBeanPostProcessor";

    /**
     * 注册 CanalListenerAnnotationBeanPostProcessor 到spring bean 容器内
     *
     * @param importingClassMetadata
     * @param registry
     * @return void
     * @author wuqiong 2022-04-23 20:21
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        if (!registry.containsBeanDefinition(CANAL_LISTENER_ANNOTATION_PROCESSOR_BEAN_NAME)) {

            registry.registerBeanDefinition(CANAL_LISTENER_ANNOTATION_PROCESSOR_BEAN_NAME,
                    new RootBeanDefinition(CanalListenerAnnotationBeanPostProcessor.class));
        }


    }

}
