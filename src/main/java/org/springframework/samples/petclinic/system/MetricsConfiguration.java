package org.springframework.samples.petclinic.system;

import java.util.List;

import com.github.raipc.metrics.hiccups.JvmHiccups;
import com.github.raipc.metrics.jfr.JfrAllocationStall;
import com.github.raipc.metrics.jfr.JfrEventHandler;
import com.github.raipc.metrics.jfr.JfrGarbageCollection;
import com.github.raipc.metrics.jfr.JfrMetrics;
import com.github.raipc.metrics.jmx.JvmGc;
import com.github.raipc.metrics.jmx.JvmGcPromotions;
import com.github.raipc.metrics.jmx.JvmThreadsCpu;
import com.github.raipc.metrics.util.NumRemovingThreadPoolNameExtractor;
import com.github.raipc.metrics.util.ThreadPoolNameExtractor;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.metrics.Meter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
class MetricsConfiguration {

	@Bean
	ThreadPoolNameExtractor threadPoolNameExtractor() {
		return new NumRemovingThreadPoolNameExtractor();
	}

	@Bean(initMethod = "start")
	JfrMetrics jfrMetrics(OpenTelemetry openTelemetry, ThreadPoolNameExtractor extractor) {
		Meter meter = openTelemetry.getMeter("jvm-metrics-ext");
		List<JfrEventHandler> handlers = JfrAllocationStall.isApplicable()
				? List.of(new JfrGarbageCollection(meter), new JfrAllocationStall(meter, extractor))
				: List.of(new JfrGarbageCollection(meter));
		return new JfrMetrics(handlers);
	}

	@Bean
	JvmThreadsCpu jvmThreadsCpu(OpenTelemetry openTelemetry, ThreadPoolNameExtractor extractor) {
		Meter meter = openTelemetry.getMeter("jvm-metrics-ext");
		return new JvmThreadsCpu(meter, extractor);
	}

	@Bean
	JvmHiccups jvmHiccups(OpenTelemetry openTelemetry) {
		Meter meter = openTelemetry.getMeter("jvm-metrics-ext");
		return new JvmHiccups(meter);
	}

	@Bean
	JvmGcPromotions jvmGcPromotions(OpenTelemetry openTelemetry) {
		Meter meter = openTelemetry.getMeter("jvm-metrics-ext");
		return new JvmGcPromotions(meter);
	}

	@Bean
	JvmGc jvmGc(OpenTelemetry openTelemetry) {
		Meter meter = openTelemetry.getMeter("jvm-metrics-ext");
		return new JvmGc(meter);
	}
}
