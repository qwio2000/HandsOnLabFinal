package org.ksug.webservice;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ConfigurationProperties(prefix="service.info")
public class ServiceInfoController {
	
	private Environment environment;
	
	private String name;
	private String version;
	
	@RequestMapping("/")
	public ResponseEntity<Map<String, Object>> info() {
		Map<String, Object> body = new HashMap<>();
		body.put("name", name);
		body.put("version", version);
		body.put("activeProfiles", environment.getActiveProfiles());
		body.put("timestamp", new Date());		
		
		return ResponseEntity.ok().body(body);
	}

	
	@Autowired
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
}
