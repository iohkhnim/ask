package com.khoi.ask.service.askserviceimpl;

import com.khoi.ask.service.IAskService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AskServiceImpl implements IAskService {

  @Value("${productRestEndpoint}")
  private String productRestEndpoint;
  /**
   * {@inheritDoc}
   */
  public String getProductInfo(int productId) {
    if (productId == -1) {
      return "Khong ton tai san pham nay";
    } else {
      String url = productRestEndpoint + productId;
      RestTemplate restTemplate = new RestTemplate();
      try {
        return restTemplate.getForObject(url, String.class);
      } catch (Exception ex) {
        return "Loi khi lay thong tin san pham tu server";
      }
    }
  }
}
