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
package org.openwms.common.comm;

import org.ameba.annotation.EnableAspects;
import org.ameba.app.SolutionApp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * A DriverStarter is the starter class of the SpringBoot application that
 * <ul>
 *     <li>acts as Eureka client for service discovery</li>
 *     <li>can handle failing communication partners with Hystrix</li>
 * </ul>
 *
 * @author <a href="mailto:hscherrer@interface21.io">Heiko Scherrer</a>
 */
@SpringBootApplication(scanBasePackageClasses = {DriverStarter.class, SolutionApp.class})
//@ImportResource({"classpath:META-INF/spring/spring-integration-context.xml"})
@EnableAspects
public class DriverStarter {

    /**
     * Boot up!
     *
     * @param args Some args
     */
    public static void main(String[] args) {
        SpringApplication.run(DriverStarter.class, args);
    }
}
