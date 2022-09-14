//package com.toeic.online.commons;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Component;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//
//@Component
//public class CommonCallApi {
//    @Value("${request.http.auth-token-header-name}")
//    private String principalRequestHeader;
//
//    @Value("${request.http.auth-token}")
//    private String principalRequestValue;
//
//    public MultiValueMap<String, String> getHeaders(String type) {
//        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
//        headers.add(principalRequestHeader, principalRequestValue);
//        headers.add("Content-Type", type.equals("0") ? MediaType.APPLICATION_JSON_VALUE : MediaType.MULTIPART_FORM_DATA_VALUE);
//        return headers;
//    }
//
//}
