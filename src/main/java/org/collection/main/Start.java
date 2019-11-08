
package org.collection.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

/**
 * 
 *     
 *
 */

/**
 * 
 * class for starting the spring boot application
 *
 */
@SpringBootApplication
@ComponentScan(basePackages = { "org.collection" })
public class Start extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Start.class);

	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Start.class, args);
	}

}
