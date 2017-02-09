package com.solveig.controller;

/**
 * Created by solveig on 09.02.2017.
 */
import java.io.IOException;

import java.io.UnsupportedEncodingException;

import java.net.URLDecoder;

import java.util.Arrays;

import java.util.LinkedHashMap;

import java.util.Map;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;

import org.springframework.http.HttpEntity;

import org.springframework.http.HttpHeaders;

import org.springframework.http.MediaType;

import org.springframework.http.ResponseEntity;

import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;

import org.springframework.util.MultiValueMap;

import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.client.RestTemplate;

@RestController

@RequestMapping("/user")

public class UserController {

    private static final String POST_URL_LOGIN = "https://sumo.tv2.no/api/authentication/user/login";

    private static final String USER_AGENT = "Mozilla/5.0";

    private final Logger log = LoggerFactory.getLogger(UserController.class);

    @RequestMapping(value = "/login", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)

    public ResponseEntity<String> loginUser(@RequestBody String postPayload){

        ResponseEntity<String> response = null; // "HTTP/1.1 400 Bad Request";

        try {

            response = sendPost(extractCredentials(postPayload));

        } catch (IOException e) {

            e.printStackTrace();

        }

        return response;

    }

    private String[] extractCredentials(String postPayload) throws UnsupportedEncodingException {

        /**
         * The postPayload has the following structure:
         *
         *
           body={:username=>"postman.pat@tv2.no",+:password=>"missyTheCat",+:rememberMe=>"true"}
         *
          &headers={"Content-Type"=>"application/x-www-form-urlencoded;+charset=UTF-8"}
         *
          &session=#<ActionDispatch::Request::Session:0x00000002301f90>
         */

        String decPayload = URLDecoder.decode(postPayload, "UTF-8");



        // Printing payload to the log. Don't do this in production!

        log.info("### Incoming payload: " + decPayload);

        String body = decPayload.split("&")[0];

        String[] bodyElem = body.split("\"");

        String[] credentials = { bodyElem[1], bodyElem[3], bodyElem[5] };

        return credentials;

    }

    private ResponseEntity<String> sendPost(String[] credentials) throws IOException {

        ResponseEntity<String> resEnt = null;

        //String rest.setMessageConverters(Arrays.asList(new StringHttpMessageConverter(), new FormHttpMessageConverter()));

        Map<String, String> params = new LinkedHashMap<String, String>();

        params.put("username", credentials[0]);

        params.put("password", credentials[1]);

        params.put("rememberMe", credentials[2]);

        HttpHeaders headers = new HttpHeaders();

        headers.setAccept(Arrays.asList(MediaType.APPLICATION_FORM_URLENCODED));

        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();

        map.add("username", credentials[0]);
        map.add("password", credentials[1]);
        map.add("rememberMe", credentials[2]);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        RestTemplate restTemplate = new RestTemplate();

        resEnt = restTemplate.postForEntity( POST_URL_LOGIN, request , String.class );

        // Printing headers to the log. Don't do this in production!

        HttpHeaders resHeaders = resEnt.getHeaders();

        for (String key : resHeaders.keySet()) {

            log.info("### header: " + key + " ### value: " +

                    resHeaders.get(key));

        }

        return resEnt;

    }

}
