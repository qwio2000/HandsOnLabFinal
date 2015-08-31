package org.ksug.webservice;

import java.util.Collections;
import java.util.List;

import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration.WebMvcAutoConfigurationAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.HttpPutFormContentFilter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;

@Configuration
public class WebConfig extends WebMvcAutoConfigurationAdapter {

	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		// XML 요청은 XML 응답, 그외 모든 요청은 JSON으로 응답하는 설정
		configurer.ignoreAcceptHeader(true)
				  .defaultContentTypeStrategy(webRequest -> {
					  String acceptHeader = webRequest.getHeader("Accept");
						try {
							if(StringUtils.hasText(acceptHeader)) {
								List<MediaType> mediaTypes = MediaType.parseMediaTypes(acceptHeader);								
								if(isSupportedXmlMediaTypes(mediaTypes)) {
									MediaType.sortBySpecificityAndQuality(mediaTypes);
									return mediaTypes;
								}
							}
						} catch (InvalidMediaTypeException ignore) { }
						
						return Collections.singletonList(MediaType.APPLICATION_JSON);
				  });
	}
	
	private boolean isSupportedXmlMediaTypes(List<MediaType> mediaTypes) {
		return mediaTypes.stream()
						 .filter(mediaType -> MediaType.APPLICATION_XML.equals(mediaType) || 
								 			  MediaType.TEXT_XML.equals(mediaType) || 
								 			  new MediaType("application", "*+xml").includes(mediaType))
						 .findAny()
						 .isPresent();
	}
	
	@Bean
	public HttpPutFormContentFilter httpPutFormContentFilter() {
		return new HttpPutFormContentFilter();
	}
	
}
