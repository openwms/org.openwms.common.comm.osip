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
package org.openwms.common.comm.req;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.openwms.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;

import static java.lang.String.format;

/**
 * A HttpRequestMessageHandler forwards the request to the routing service.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@Component
@RefreshScope
class HttpRequestMessageHandler implements Function<RequestMessage, Void> {

    @Autowired
    private RestTemplate aLoadBalanced;
    @Autowired
    private DiscoveryClient dc;
    @Value("${owms.driver.server.routing-service-name:routing-service}")
    private String routingServiceName;

    @Override
    public Void apply(RequestMessage msg) {
        List<ServiceInstance> list = dc.getInstances(routingServiceName);
        if (list == null || list.size() == 0) {
            throw new RuntimeException(format("No deployed service with name [%s] found", routingServiceName));
        }
        ServiceInstance si = list.get(0);
        String endpoint = si.getMetadata().get("protocol") + "://" + si.getServiceId() + "/req";
        aLoadBalanced.exchange(
                endpoint,
                HttpMethod.POST,
                new HttpEntity<>(new RequestVO(msg.getActualLocation(), msg.getBarcode()), SecurityUtils.createHeaders(si.getMetadata().get("username"), si.getMetadata().get("password"))),
                Void.class
        );
        return null;
    }

    private static class RequestVO implements Serializable {

        @JsonProperty
        String actualLocation, barcode;

        RequestVO(String actualLocation, String barcode) {
            this.actualLocation = actualLocation;
            this.barcode = barcode;
        }
    }
}
