package kr.ac.hansung.cse.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import kr.ac.hansung.cse.model.Cart;
import kr.ac.hansung.cse.model.CartItem;
import kr.ac.hansung.cse.model.Product;
import kr.ac.hansung.cse.model.User;
import kr.ac.hansung.cse.service.CartItemService;
import kr.ac.hansung.cse.service.CartService;
import kr.ac.hansung.cse.service.ProductService;
import kr.ac.hansung.cse.service.UserService;

@RestController
@RequestMapping("/api/cart")
public class CartRestController {

	@Autowired
	private CartService cartService;

	@Autowired
	private CartItemService cartItemService;

	@Autowired
	private UserService userService;

	@Autowired
	private ProductService productService;

	static Logger logger = LoggerFactory.getLogger(CartRestController.class);
	
	
	@RequestMapping("/{cartId}")
	public ResponseEntity<Cart> getCartById(@PathVariable(value = "cartId") int cartId) {
		Cart cart = cartService.getCartById(cartId);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setCacheControl("max-age=10");

		return new ResponseEntity<Cart>(cart, headers, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/{cartId}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> clearCart(@PathVariable(value = "cartId") int cartId) {
		Cart cart = cartService.getCartById(cartId);
		cartItemService.removeAllCartItem(cart);

		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

	@RequestMapping(value = "/add/{productId}", method = RequestMethod.PUT)
	public ResponseEntity<Void> addItem(@PathVariable(value = "productId") int productId) {

		Product product = productService.getProductById(productId);
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		// 현재 인증된 사용자의 이름을 가져올 수 있다.
		String username = authentication.getName();

		User user = userService.getUserByUsername(username);
		Cart cart = user.getCart();

		List<CartItem> cartItems = cart.getCartItems();

		for (int i = 0; i < cartItems.size(); i++) {
			if (product.getId() == cartItems.get(i).getProduct().getId()) {
				CartItem cartItem = cartItems.get(i);
				cartItem.setQuantity(cartItem.getQuantity() + 1);
				cartItem.setTotalPrice(product.getPrice() * cartItem.getQuantity());
				cartItemService.addCartItem(cartItem);
				logger.debug("TS Logging .... cartId = " +cart.getId() + " productId = " + cartItem.getProduct().getId() + " quantity = "+ cartItem.getQuantity()
				+" totalprice =" +cartItem.getTotalPrice() + " id =" + cartItem.getId()
			);
				return new ResponseEntity<Void>(HttpStatus.OK);
			}
		}

		CartItem cartItem = new CartItem();
		cartItem.setQuantity(1);
		cartItem.setTotalPrice(product.getPrice() * cartItem.getQuantity());
		cartItem.setProduct(product);
		cartItem.setCart(cart);

		cart.getCartItems().add(cartItem);

		cartItemService.addCartItem(cartItem);

		logger.debug("TS Logging .... cartId = " +cart.getId() + " productId = " + cartItem.getProduct().getId() + " quantity = "+ cartItem.getQuantity()
			+" totalprice =" +cartItem.getTotalPrice() + " id =" + cartItem.getId()
		);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/cartItem/{productId}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> removeItem(@PathVariable(value = "productId") int productId) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();

		User user = userService.getUserByUsername(username);
		Cart cart = user.getCart();
		
		CartItem cartItem = cartItemService.getCartItemByProductId(cart.getId(),productId);
		cartItemService.removeCartItem(cartItem);
		
		return new ResponseEntity<Void> (HttpStatus.NO_CONTENT);
		
	}
	
	@RequestMapping(value = "/cartItem/plus/{productId}", method = RequestMethod.PUT)
	public ResponseEntity<Void> plusItem(@PathVariable(value = "productId") int productId) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();

		User user = userService.getUserByUsername(username);
		Cart cart = user.getCart();
		
		CartItem cartItem = cartItemService.getCartItemByProductId(cart.getId(),productId);
	
		if(cartItem.getQuantity() +1 > cartItem.getProduct().getUnitInStock())
			return new ResponseEntity<Void> (HttpStatus.NO_CONTENT);
		
			
		cartItem.setQuantity(cartItem.getQuantity()+1);
		cartItem.setTotalPrice(cartItem.getQuantity() * cartItem.getProduct().getPrice());
		cartItemService.updateCartItem(cartItem);
		
		return new ResponseEntity<Void> (HttpStatus.OK);
		
		
	}
	
	@RequestMapping(value = "/cartItem/minus/{productId}", method = RequestMethod.PUT)
	public ResponseEntity<Void> minusItem(@PathVariable(value = "productId") int productId) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();

		User user = userService.getUserByUsername(username);
		Cart cart = user.getCart();
		
		CartItem cartItem = cartItemService.getCartItemByProductId(cart.getId(),productId);
		if(cartItem.getQuantity() -1 < 0)
			return new ResponseEntity<Void> (HttpStatus.NO_CONTENT);
		
			
		cartItem.setQuantity(cartItem.getQuantity()-1);
		cartItem.setTotalPrice(cartItem.getQuantity() * cartItem.getProduct().getPrice());
		cartItemService.updateCartItem(cartItem);
		return new ResponseEntity<Void> (HttpStatus.OK);
		
	}
	
}
