package com.example.boot2;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 测试jsp
 */
@Controller
public class TestController {
	@RequestMapping("/test")
	public String test() {
		return "test";
	}
}
