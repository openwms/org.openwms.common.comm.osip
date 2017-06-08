/*
 * openwms.org, the Open Warehouse Management System.
 * Copyright (C) 2014 Heiko Scherrer
 *
 * This file is part of openwms.org.
 *
 * openwms.org is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as 
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * openwms.org is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software. If not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.openwms.common.comm.sysu;

import org.openwms.SecurityUtils;
import org.openwms.common.comm.CommConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.function.Function;

/**
 * A HttpSystemUpdateMessageHandler forwards system updates to the LocationGroup services directly without using the routing service.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@Profile("!"+ CommConstants.DEFAULT_HTTP_SERVICE_ACCESS)
@Component
class HttpSystemUpdateMessageHandler implements Function<SystemUpdateMessage, Void> {

    private final RestTemplate restTemplate;
    private final String routingServiceName;
    private final String routingServiceProtocol;
    private final String routingServiceUsername;
    private final String routingServicePassword;

    HttpSystemUpdateMessageHandler(RestTemplate restTemplate,
                              @Value("${owms.driver.server.routing-service.name:routing-service}") String routingServiceName,
                              @Value("${owms.driver.server.routing-service.protocol:http}") String routingServiceProtocol,
                              @Value("${owms.driver.server.routing-service.username:user}") String routingServiceUsername,
                              @Value("${owms.driver.server.routing-service.password:sa}") String routingServicePassword) {
        this.restTemplate = restTemplate;
        this.routingServiceName = routingServiceName;
        this.routingServiceProtocol = routingServiceProtocol;
        this.routingServiceUsername = routingServiceUsername;
        this.routingServicePassword = routingServicePassword;
    }

    @Override
    public Void apply(SystemUpdateMessage msg) {
        restTemplate.exchange(
                routingServiceProtocol+"://"+routingServiceName+"/sysu",
                HttpMethod.POST,
                new HttpEntity<>(new RequestVO(msg.getLocationGroupName(), msg.getErrorCode()), SecurityUtils.createHeaders(routingServiceUsername, routingServicePassword)),
                Void.class
        );
        return null;
    }

    private static class RequestVO {

        String locationGroupName, errorCode;

        RequestVO(String locationGroupName, String errorCode) {
            this.locationGroupName = locationGroupName;
            this.errorCode = errorCode;
        }
    }
}
