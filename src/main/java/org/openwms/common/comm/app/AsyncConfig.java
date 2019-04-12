/*
 * Copyright 2018 Heiko Scherrer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openwms.common.comm.app;

import org.openwms.common.comm.osip.OSIP;
import org.openwms.core.SpringProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SerializerMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.interceptor.StatefulRetryOperationsInterceptor;
import org.springframework.retry.support.RetryTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.ameba.LoggingCategories.BOOT;

/**
 * A AsyncConfig.
 *
 * @author <a href="mailto:hscherrer@interface21.io">Heiko Scherrer</a>
 */
@Profile(SpringProfiles.ASYNCHRONOUS_PROFILE)
@Configuration
@EnableRabbit
class AsyncConfig {

    private static final Logger BOOT_LOGGER = LoggerFactory.getLogger(BOOT);

    @Bean
    @OSIP
    @RefreshScope
    Map<String, Integer> asyncBootLogger(
            @Value("${owms.driver.osip.res.queue-name}") String queueName,
            @Value("${owms.driver.osip.res.exchange-name}") String exchangeMapping,
            @Value("${owms.driver.osip.res.routing-key-in}") String routingKey
    ) {
        Map<String, Integer> res = new HashMap<>();
        BOOT_LOGGER.info("In ASYNCHRONOUS mode. OSIP RES bound to Queue [{}], Exchange [{}] and using Routing Key [{}]", queueName, exchangeMapping, routingKey);
        return res;
    }

    @ConditionalOnExpression("'${owms.driver.serialization}'=='json'")
    @Bean
    MessageConverter messageConverter() {
        Jackson2JsonMessageConverter messageConverter = new Jackson2JsonMessageConverter();
        BOOT_LOGGER.info("Using JSON serialization over AMQP");
        return messageConverter;
    }

    @ConditionalOnExpression("'${owms.driver.serialization}'=='barray'")
    @Bean
    MessageConverter serializerMessageConverter() {
        SerializerMessageConverter messageConverter = new SerializerMessageConverter();
        BOOT_LOGGER.info("Using byte array serialization over AMQP");
        return messageConverter;
    }

    @Bean
    StatefulRetryOperationsInterceptor interceptor() {
        return RetryInterceptorBuilder.stateful()
                .maxAttempts(3)
                .backOffOptions(1000, 2.0, 10000)
                .build();
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);

        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setMultiplier(2);
        backOffPolicy.setMaxInterval(15000);
        backOffPolicy.setInitialInterval(500);

        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setBackOffPolicy(backOffPolicy);
        rabbitTemplate.setRetryTemplate(retryTemplate);

        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }
}
