package com.store.soattechchallenge.shoppingCart.product.application.service;

import com.store.soattechchallenge.shoppingCart.product.application.usecases.ProductUseCases;
import com.store.soattechchallenge.shoppingCart.product.domain.model.Product;
import com.store.soattechchallenge.shoppingCart.product.infrastructure.adapters.in.dto.ProductGetResponseDTO;
import com.store.soattechchallenge.shoppingCart.product.infrastructure.adapters.in.dto.ProductRequestDTO;
import com.store.soattechchallenge.shoppingCart.product.infrastructure.adapters.in.dto.ProductPostUpResponseDTO;
import com.store.soattechchallenge.shoppingCart.product.infrastructure.adapters.out.repository.impl.ProductRepositoryImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class ProductServiceImpl implements ProductUseCases {

    public final ProductRepositoryImpl adaptersRepository;

    public ProductServiceImpl(ProductRepositoryImpl adaptersRepository) {
        this.adaptersRepository = adaptersRepository;
    }

    @Override
    public ProductPostUpResponseDTO saveProduct(ProductRequestDTO product) {
        Product ProductRequestModelModel = new Product(product.getProductName().toUpperCase(),product.getIdCategory(),product.getUnitPrice(), product.getPreparationTime());
        adaptersRepository.save(ProductRequestModelModel);
        return new ProductPostUpResponseDTO("Produto salvo com sucesso");
    }

    @Override
    public Page<ProductGetResponseDTO> getAllProducts(Pageable pageable) {
        return adaptersRepository.findAll(pageable);
    }

    @Override
    public Optional<ProductGetResponseDTO> getProductById(Long id) {
            return adaptersRepository.findById(id);
    }

    @Override
    public ProductPostUpResponseDTO updateProduct(Long id, ProductRequestDTO product) {
        Product Product = new Product(product.getProductName(),product.getIdCategory(),product.getUnitPrice(), product.getPreparationTime());
        return  adaptersRepository.update(Product,id);
    }

    @Override
    public ProductPostUpResponseDTO deleteProduct(Long id) {
        return adaptersRepository.deleteById(id);
    }
}