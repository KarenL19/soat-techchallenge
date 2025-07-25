package com.store.soattechchallenge.shoppingCart.product.controller;

import com.store.soattechchallenge.shoppingCart.order.infrastructure.mappers.OrderMapper;
import com.store.soattechchallenge.shoppingCart.product.infrastructure.gateways.ProductRepositoryGatewayGateways;
import com.store.soattechchallenge.shoppingCart.product.infrastructure.jpa.JpaProduct;
import com.store.soattechchallenge.shoppingCart.product.infrastructure.mappers.ProductMapper;
import com.store.soattechchallenge.shoppingCart.product.presenters.ProductHttpPresenter;
import com.store.soattechchallenge.shoppingCart.product.usecases.CreateProductUseCase;
import com.store.soattechchallenge.shoppingCart.product.usecases.DeleteProductUseCase;
import com.store.soattechchallenge.shoppingCart.product.usecases.FindProductUseCase;
import com.store.soattechchallenge.shoppingCart.product.usecases.UpdateProductUseCase;
import com.store.soattechchallenge.shoppingCart.product.usecases.command.ProductRequestCommand;
import com.store.soattechchallenge.shoppingCart.product.infrastructure.api.dto.ProductGetResponseDTO;
import com.store.soattechchallenge.shoppingCart.product.infrastructure.api.dto.ProductPostUpResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ProductMainController {

    public final ProductRepositoryGatewayGateways adaptersRepository;
    private final ProductMapper productMapper;

    public ProductMainController(ProductRepositoryGatewayGateways adaptersRepository,ProductMapper productMapper) {
        this.adaptersRepository = adaptersRepository;
        this.productMapper = productMapper;

    }

    public ProductPostUpResponseDTO saveProduct(ProductRequestCommand command) {
        CreateProductUseCase createProductUseCase = new  CreateProductUseCase(this.adaptersRepository);
        ProductHttpPresenter productHttpPresenter = new ProductHttpPresenter(this.productMapper);
        createProductUseCase.saveProduct(command);
        return productHttpPresenter.execute();
    }

    public Page<ProductGetResponseDTO> getAllProducts(Pageable pageable) {
        FindProductUseCase findProductUseCase = new FindProductUseCase(this.adaptersRepository);
        ProductHttpPresenter productHttpPresenter = new ProductHttpPresenter(this.productMapper);
        Page<JpaProduct> jpaProducts = findProductUseCase.getAllProducts(pageable);
        return productHttpPresenter.executeGetAllProducts(jpaProducts);
    }

    public Optional<ProductGetResponseDTO> getProductById(Long id) {
        FindProductUseCase findProductUseCase = new FindProductUseCase(this.adaptersRepository);
        ProductHttpPresenter productHttpPresenter = new ProductHttpPresenter(this.productMapper);
        Optional<JpaProduct> jpaProduct = findProductUseCase.getProductById(id);
        return productHttpPresenter.executeGetProductById(jpaProduct);
    }

    public ProductPostUpResponseDTO updateProduct(Long id, ProductRequestCommand command) {
        UpdateProductUseCase updateProductUseCase = new UpdateProductUseCase(this.adaptersRepository);
        ProductHttpPresenter productHttpPresenter = new ProductHttpPresenter(this.productMapper);
        Boolean update = updateProductUseCase.updateProduct(id,command);
        return productHttpPresenter.executeUpdate(update);
    }

    public ProductPostUpResponseDTO deleteProduct(Long id) {
        DeleteProductUseCase deleteProductUseCase = new DeleteProductUseCase(this.adaptersRepository);
        ProductHttpPresenter productHttpPresenter = new ProductHttpPresenter(this.productMapper);
        Boolean delete = deleteProductUseCase.deleteProduct(id);
        return productHttpPresenter.executeDelete(delete);
    }


}
