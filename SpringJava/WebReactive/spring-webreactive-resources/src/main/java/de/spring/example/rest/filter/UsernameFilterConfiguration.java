/*
 * Copyright 2013-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.spring.example.rest.filter;

import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;

import javax.annotation.PreDestroy;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import reactor.core.scheduler.Schedulers;

@Configuration
public class UsernameFilterConfiguration {

	@Configuration
	static class TraceReactorConfiguration {

		@PreDestroy
		public void cleanupHooks() {
			Schedulers.resetFactory();
		}

		@Bean
		// for tests
		static HookRegisteringBeanDefinitionRegistryPostProcessor traceHookRegisteringBeanDefinitionRegistryPostProcessor() {
			return new HookRegisteringBeanDefinitionRegistryPostProcessor();
		}
	}
}

class HookRegisteringBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
	}

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		Schedulers.setFactory(factoryInstance(beanFactory));
	}

	private Schedulers.Factory factoryInstance(final BeanFactory beanFactory) {
		return new Schedulers.Factory() {
            @Override
            public ScheduledExecutorService decorateExecutorService(String schedulerType,
					Supplier<? extends ScheduledExecutorService> actual) {
                return new TraceableScheduledExecutorService(beanFactory, actual.get());
			}
		};
	}
}
