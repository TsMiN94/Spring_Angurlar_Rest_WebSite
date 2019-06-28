package kr.ac.hansung.cse.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import kr.ac.hansung.cse.model.Cart;
import kr.ac.hansung.cse.model.User;
import kr.ac.hansung.cse.service.UserService;

@Controller
@RequestMapping("/cart")
public class CartController {
	
	@Autowired
	private UserService userService;

	@RequestMapping
	public String getCart(Model model) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		// 현재 인증된 사용자의 이름을 가져올 수 있다.
		String username = authentication.getName();

		User user = userService.getUserByUsername(username);
		int cartId = user.getCart().getId();
		
		model.addAttribute("cartId",cartId);
		return "cart";
	}
}
