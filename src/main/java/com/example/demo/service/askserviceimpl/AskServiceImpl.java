package com.example.demo.service.askserviceimpl;

import com.example.demo.service.IAskService;
import com.khoi.productproto.GetProductRequest;
import com.khoi.productproto.ProductEntry;
import com.khoi.productproto.ProductServiceGrpc;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AskServiceImpl implements IAskService {

  @Qualifier("productService")
  private final ProductServiceGrpc.ProductServiceBlockingStub productService;

  public AskServiceImpl(ProductServiceGrpc.ProductServiceBlockingStub productService) {
    this.productService = productService;
  }

  public ProductEntry getProductInfo(int productId) {
    return productService.getProduct(GetProductRequest.newBuilder().setProductId(productId).build());
  }
}
