package com.example.redis.controller;

import com.example.redis.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lcc
 * @date 2023/09/08
 */
@RestController
@RequestMapping("/city")
public class CityController {

	private final CityService cityService;

	@Autowired
	public CityController(CityService cityService) {
		this.cityService = cityService;
	}

	@GetMapping("/{id}")
	public Object getCity(@PathVariable("id") String cityCode) {
		return cityService.getByCode(cityCode);
	}
}
