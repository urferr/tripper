package com.embabel.agent.web.security;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {
    @GetMapping("/user")
    public String getUserInfo(@AuthenticationPrincipal OAuth2User oAuth2User, Model model) {
	model.addAttribute("name", oAuth2User.<String>getAttribute("name"));
	model.addAttribute("email", oAuth2User.<String>getAttribute("email"));
	oAuth2User.getAttributes().entrySet().stream().filter(theEntry -> theEntry.getKey().equals("picture"))
		.map(theEntry -> theEntry.getValue()).forEach(thePictore -> model.addAttribute("picture", thePictore));
	model.addAttribute("attributes", oAuth2User.getAttributes());
	return "common/user-info";
    }

}
