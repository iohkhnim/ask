package com.khoi.ask.service;

public interface IAskService {

  /**
   * This methods get product information from Product REST API then return in String type
   * @param productId product ID needs to be retrieved information
   * @return Return Product information
   */
  String getProductInfo(int productId);
}
