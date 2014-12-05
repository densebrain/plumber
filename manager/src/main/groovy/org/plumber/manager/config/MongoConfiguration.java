package org.plumber.manager.config;

import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import groovy.lang.MetaClass;
import org.plumber.client.domain.Timestampable;
import org.plumber.manager.domain.Job;
import org.plumber.manager.domain.Persistent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.data.convert.DefaultTypeMapper;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableMongoRepositories(basePackages = "org.plumber.manager.repo")
public class MongoConfiguration {


	@Bean
	public MongoDbFactory mongoDbFactory(@Value("${mongo.hostname:localhost}") String hostname) throws Exception {
		MongoClient client = new MongoClient(hostname);

		return new SimpleMongoDbFactory(client, "plumber");
	}


	public static class ExceptionConverter implements Converter<Exception, DBObject> {
		@Override
		public DBObject convert(Exception source) {
			return null;
		}
	}

	@Bean
	public MappingMongoConverter mongoConverter(MongoDbFactory mongoDbFactory) {
		MappingMongoConverter converter = new MappingMongoConverter(mongoDbFactory, new MongoMappingContext());


		ArrayList<Object> converters = new ArrayList<>();
		converters.add(new ExceptionConverter());
		CustomConversions conversions = new CustomConversions(converters);
		DefaultMongoTypeMapper mapper = new DefaultMongoTypeMapper();

		converter.setTypeMapper(mapper);
		converter.setCustomConversions(conversions);


		return converter;
	}

	@Bean
	public MongoTemplate mongoTemplate(MongoDbFactory mongoDbFactory, MappingMongoConverter converter) throws Exception {


		MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory, converter);

//		MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory);

		return mongoTemplate;

	}

	@Bean
	public ValidatingMongoEventListener validatingMongoEventListener() {
		return new ValidatingMongoEventListener(validator());
	}

	@Bean
	public LocalValidatorFactoryBean validator() {
		return new LocalValidatorFactoryBean();
	}

	@Bean
	public TimestampSaveEventListener timestampSaveEventListener() {
		return new TimestampSaveEventListener();
	}

	public class TimestampSaveEventListener extends AbstractMongoEventListener<Timestampable> {
		@Override
		public void onBeforeConvert(Timestampable object){
			object.ensureDates();
			object.modified();
		}
	}

	@Bean
	public PersistentEventListener persistentEventListener() {
		return new PersistentEventListener();
	}

	public class PersistentEventListener extends AbstractMongoEventListener<Persistent> {

		@Override
		public void onBeforeConvert(Persistent object){
			object.preUpdate();
		}

	}


}
