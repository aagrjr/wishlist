package br.com.luizalabs.wishlist.service;

import br.com.luizalabs.wishlist.domain.Wishlist;
import br.com.luizalabs.wishlist.domain.payload.AddProductPayload;
import br.com.luizalabs.wishlist.domain.response.WishlistResponse;
import br.com.luizalabs.wishlist.exception.ProductAlreadyOnWishListException;
import br.com.luizalabs.wishlist.exception.WishlistNotFoundException;
import br.com.luizalabs.wishlist.exception.WishlistTooBigException;
import br.com.luizalabs.wishlist.repository.WishlistRepository;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
@Slf4j
public class WishlistService {

  private final WishlistRepository repository;

  public WishlistResponse addProduct(AddProductPayload payload) {
    log.info("Add a product to the customer's wishlist - Payload: {}", payload);
    Wishlist customersWishList = Wishlist.from(payload);

    if (repository.existsByCustomerId(payload.getCustomerId())) {
      customersWishList = getWishlistByCustomerId(payload.getCustomerId());
      validateWishList(customersWishList, payload);
      customersWishList.getProducts().add(payload.getProductId());
    }
    return WishlistResponse.from(repository.save(customersWishList));
  }

  private Optional<String> findProductOnWishList(Wishlist customersWishList, String productId) {
    return customersWishList.getProducts().stream().filter(product -> product.equals(productId))
        .findAny();
  }

  private void validateWishList(Wishlist customersWishList, AddProductPayload payload) {
    if (customersWishList.getProducts().size() == 20) {
      throw new WishlistTooBigException();
    }
    if (findProductOnWishList(customersWishList, payload.getProductId()).isPresent()) {
      throw new ProductAlreadyOnWishListException();
    }
  }

  public WishlistResponse findById(String customerId) {
    return WishlistResponse.from(getWishlistByCustomerId(customerId));
  }

  public WishlistResponse removeProductFromCustomerWishlist(String customerId, String productId) {
    log.info("Delete product from wishlist -  customerId: {}, productId: {}", customerId,
        productId);
    final var customersWishList = getWishlistByCustomerId(customerId);

    if (findProductOnWishList(customersWishList, productId).isPresent()) {
      customersWishList.getProducts().remove(productId);
    }
    return WishlistResponse.from(repository.save(customersWishList));
  }

  private Wishlist getWishlistByCustomerId(String customerId) {
    return repository.findByCustomerId(customerId).orElseThrow(WishlistNotFoundException::new);
  }

  public boolean hasProduct(String customerId, String productId) {
    var customersWishList = getWishlistByCustomerId(customerId);
    return findProductOnWishList(customersWishList, productId).isPresent();
  }
}
