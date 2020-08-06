package com.postings.demo.user.extension;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MongoExtension implements BeforeEachCallback, AfterEachCallback {

	private static Path JSON_PATH = Paths.get("src", "test", "resources", "data")  ;
	
	private ObjectMapper mapper = new ObjectMapper() ;
	
	@Override
	public void afterEach(ExtensionContext context) throws Exception {
		getAnnotation(context).ifPresent(mongo -> {
			getMongoTemplate(context).ifPresent(template -> {
				template.dropCollection(mongo.collectionName());
			});
		});	
	}

	@Override
	public void beforeEach(ExtensionContext context) throws Exception {
		getAnnotation(context).ifPresent(mongo -> {
			getMongoTemplate(context).ifPresent(template -> {
				try {
					List<?> objects = mapper.readValue(JSON_PATH.resolve(mongo.value()).toFile(), 
							mapper.getTypeFactory().constructCollectionType(List.class, mongo.classType())) ;

					objects.forEach(template::save);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		});
	}
	
	public Optional<MongoDataFile> getAnnotation(ExtensionContext context) {
		Optional<Method> testMethod = context.getTestMethod() ;
		if (testMethod.isPresent() && testMethod.get().isAnnotationPresent(MongoDataFile.class)) {
			return Optional.of(testMethod.get().getAnnotation(MongoDataFile.class)) ;
		} else {
			Optional<Class<?>> clazz = context.getTestClass() ;
			if (clazz.isPresent() && clazz.get().isAnnotationPresent(MongoDataFile.class)) {
				return Optional.of(clazz.get().getAnnotation(MongoDataFile.class)) ;
			}
		}
		return Optional.empty() ;
	}
	
	public Optional<MongoTemplate> getMongoTemplate(ExtensionContext context) {
		Optional<Class<?>> clazz = context.getTestClass() ;
		if (clazz.isPresent()) {
			Class<?> c = clazz.get() ;
			try {
				Method method = c.getMethod("getMongoTemplate") ;
				
				Optional<Object> instance = context.getTestInstance() ;
				if (instance.isPresent()) {
					return Optional.of((MongoTemplate) method.invoke(instance.get())) ;
				}
			} catch (Exception e) {
				e.printStackTrace(); 
			}
		}
		return Optional.empty() ;
	}
}
