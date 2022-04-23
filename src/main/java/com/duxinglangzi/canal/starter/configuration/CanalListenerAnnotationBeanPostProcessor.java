package com.duxinglangzi.canal.starter.configuration;


import com.duxinglangzi.canal.starter.annotation.CanalListener;
import com.duxinglangzi.canal.starter.factory.TransponderContainerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author wuqiong 2022/4/11
 */
public class CanalListenerAnnotationBeanPostProcessor implements
        BeanPostProcessor, SmartInitializingSingleton, BeanFactoryPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(CanalListenerAnnotationBeanPostProcessor.class);

    private final Set<Class<?>> notAnnotatedClasses = Collections.newSetFromMap(new ConcurrentHashMap<>(256));
    private Set<CanalListenerEndpointRegistrar> registrars = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private ConfigurableListableBeanFactory configurableListableBeanFactory;
    private CanalAutoConfigurationProperties canalAutoConfigurationProperties;

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        if (notAnnotatedClasses.contains(bean.getClass())) return bean;
        Class<?> targetClass = AopUtils.getTargetClass(bean);
        // 只扫描类的方法，目前 CanalListener 只支持在方法上
        Map<Method, CanalListener> annotatedMethods = MethodIntrospector.selectMethods(targetClass,
                (MethodIntrospector.MetadataLookup<CanalListener>) method -> findListenerAnnotations(method));
        if (annotatedMethods.isEmpty()) {
            this.notAnnotatedClasses.add(bean.getClass());
        } else {
            // 先加入到待注册里面
            annotatedMethods.entrySet().stream()
                    .filter(e -> e != null)
                    .forEach(ele -> registrars.add(new CanalListenerEndpointRegistrar(bean, ele)));
            logger.info("Registered @CanalListener methods processed on bean:{} , Methods :{} ", beanName,
                    annotatedMethods.keySet().stream().map(e -> e.getName()).collect(Collectors.toSet()));
        }
        return bean;
    }


    private CanalListener findListenerAnnotations(Method method) {
        return AnnotatedElementUtils.findMergedAnnotation(method, CanalListener.class);
    }


    @Override
    public void afterSingletonsInstantiated() {
        canalAutoConfigurationProperties = configurableListableBeanFactory.getBean(CanalAutoConfigurationProperties.class);
        TransponderContainerFactory.registerListenerContainer(configurableListableBeanFactory, canalAutoConfigurationProperties, registrars);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        this.configurableListableBeanFactory = configurableListableBeanFactory;
    }


}
