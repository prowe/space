package com.sourceallies.space;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import cloud.orbit.actors.Stage;
import cloud.orbit.actors.extensions.json.InMemoryJSONStorageExtension;

@Component
public class StageFactoryBean implements FactoryBean<Stage>, InitializingBean, DisposableBean {

	private static Logger logger = LoggerFactory.getLogger(StageFactoryBean.class);

	private Stage stage;

	public void afterPropertiesSet() throws Exception {
		logger.info("Starting stage");
		stage = new Stage.Builder()
			.clusterName("space")
			.extensions(new InMemoryJSONStorageExtension())
			.build();
		stage.start().join();
		stage.bind();
		logger.info("Stage started");
	}

	public void destroy() throws Exception {
		if (stage != null) {
			stage.stop().join();
			logger.info("Stage stopped");
		}
	}

	@Override
	public Stage getObject() throws Exception {
		return stage;
	}

	@Override
	public Class<?> getObjectType() {
		return Stage.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
