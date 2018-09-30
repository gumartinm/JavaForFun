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

import reactor.core.publisher.Hooks;
import reactor.core.scheduler.Schedulers;

@Configuration
public class UsernameFilterConfiguration {

	@Configuration
	static class TraceReactorConfiguration {
		static final String TRACE_REACTOR_KEY = TraceReactorConfiguration.class.getName();

		@PreDestroy
		public void cleanupHooks() {
			Hooks.resetOnEachOperator(TRACE_REACTOR_KEY);
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
		setupHooks(beanFactory);

	}

	void setupHooks(BeanFactory beanFactory) {
		Hooks.onEachOperator(UsernameFilterConfiguration.TraceReactorConfiguration.TRACE_REACTOR_KEY,
				ReactorSleuth.scopePassingSpanOperator(beanFactory));
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
