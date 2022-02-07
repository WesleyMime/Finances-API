package br.com.finances.api.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController implements ErrorController {
	
    @RequestMapping("/error")
    public String error() {
        return "<h1>Finances API</h1>"
        		+ "<h2><a "
        		+ "href=\"https://documenter.getpostman.com/view/19203694/UVeGs6cv\" "
        		+ "target=\"_blank\">Documentation"
        		+ "</a></h2>";
    }
}