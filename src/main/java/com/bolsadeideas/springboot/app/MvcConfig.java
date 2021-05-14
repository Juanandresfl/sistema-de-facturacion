package com.bolsadeideas.springboot.app;

import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
	
//	@Override
//	public void addResourceHandlers(ResourceHandlerRegistry registry) {
//		// TODO Auto-generated method stub
//		WebMvcConfigurer.super.addResourceHandlers(registry);
////		se agrega para que se puedan guardar las fotos en una carpeta externa
//		
//		String resource = Paths.get("uploads").toAbsolutePath().toUri().toString();
//		
//		registry.addResourceHandler("/uploads/**")
//		.addResourceLocations(resource);
//	}

}
