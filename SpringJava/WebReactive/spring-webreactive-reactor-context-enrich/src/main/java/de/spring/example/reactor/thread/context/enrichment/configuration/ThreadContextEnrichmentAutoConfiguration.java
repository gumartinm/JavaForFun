package de.spring.example.reactor.thread.context.enrichment.configuration;

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
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import de.spring.example.reactor.thread.context.enrichment.scheduler.TraceableScheduledExecutorService;
import de.spring.example.reactor.thread.context.enrichment.subscriber.ContextCoreSubscriber;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Operators;
import reactor.core.scheduler.Schedulers;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
// When using Spring Boot @EnableConfigurationProperties
public class ThreadContextEnrichmentAutoConfiguration {

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
		Hooks.onEachOperator(ThreadContextEnrichmentAutoConfiguration.TraceReactorConfiguration.TRACE_REACTOR_KEY,
		        Operators.lift((sc, sub) -> new ContextCoreSubscriber<Object>(sub, sub.currentContext())));
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
