package com.example.demo.service.askserviceimpl;

import com.example.demo.service.IAskService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AskServiceImpl implements IAskService {

  public String getProductInfo(int productId) {
    String url = "http://localhost:8080/product/" + productId;
    RestTemplate restTemplate = new RestTemplate();
    String rs = restTemplate.getForObject(url, String.class);
    return rs;
  }
}
