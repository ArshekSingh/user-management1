package com.sts.finncub.usermanagement.controller;

import com.sts.finncub.core.response.Response;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@AllArgsConstructor
public class TestController {

    @Autowired
    private final RestTemplate restTemplate;

    @GetMapping("/hello")
    public Response getIP(HttpServletRequest request) {

        String[] IP_HEADER_CANDIDATES = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_CLIENT_IP",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED",
                "HTTP_VIA", "REMOTE_ADDR"
        };
        StringBuffer stringBuffer = new StringBuffer();
        for (String header : IP_HEADER_CANDIDATES) {
            log.info(header + " : " + request.getHeader(header));
            stringBuffer.append(request.getHeader(header));
        }
        log.info("REMOTE_ADDR: " + request.getAttribute("REMOTE_ADDR"));
        stringBuffer.append(request.getAttribute("REMOTE_ADDR"));
        log.info(request.getRemoteAddr());
        stringBuffer.append(request.getRemoteAddr());
        return new Response("OK",stringBuffer,HttpStatus.OK);
    }

    @GetMapping("/getIP")
    public Response getIPDetails(HttpServletRequest request) {
        String fooResourceUrl = "https://geolocation-db.com/json/";
        ResponseEntity<String> response = restTemplate.getForEntity(fooResourceUrl, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            log.info(response.getBody());
            return new Response("OK", response.getBody(), HttpStatus.OK);
        }
        return new Response("OK", HttpStatus.OK);
    }
}
