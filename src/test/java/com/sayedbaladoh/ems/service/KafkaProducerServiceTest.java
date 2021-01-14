/**
 * 
 */
package com.sayedbaladoh.ems.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.kafka.test.assertj.KafkaConditions.key;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.sayedbaladoh.ems.model.Employee;
import com.sayedbaladoh.ems.model.EmployeeState;

import lombok.extern.java.Log;

/**
 * Kafka Producer Service Test
 * 
 * @author Sayed Baladoh
 *
 */
@Disabled
@Log
@SpringBootTest
@DirtiesContext
@EmbeddedKafka(
		partitions = 1,
		brokerProperties = { "listeners=PLAINTEXT://${kafka.bootstrap.servers}", "port=${kafka.bootstrap.port}" })
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KafkaProducerServiceTest {

	@Value("${kafka.topic.name}")
	private String topicName;

	@Autowired
	private EmbeddedKafkaBroker embeddedKafkaBroker;

	@Autowired
	private KafkaProducerService kafkaProducerService;

	private KafkaMessageListenerContainer<String, Employee> container;

	private BlockingQueue<ConsumerRecord<String, Employee>> consumerRecords;

	@BeforeAll
	void setUp() {
		Map<String, Object> configs = new HashMap<>(
				KafkaTestUtils.consumerProps("consumer", "false", embeddedKafkaBroker));
		DefaultKafkaConsumerFactory<String, Employee> consumerFactory = new DefaultKafkaConsumerFactory<>(configs,
				new StringDeserializer(), new JsonDeserializer<>(Employee.class));
		ContainerProperties containerProperties = new ContainerProperties(topicName);
		container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
		consumerRecords = new LinkedBlockingQueue<>();
		container.setupMessageListener((MessageListener<String, Employee>) consumerRecords::add);
		container.start();
		ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
	}

	@AfterAll
	void tearDown() {
		container.stop();
	}

	/**
	 * Test method for
	 * {@link com.sayedbaladoh.ems.service.KafkaProducerService#sendMessageWithCallback(com.sayedbaladoh.ems.model.Employee, java.lang.String)}.
	 */
	@Test
	public void givenEmbeddedKafkaBroker_whenSendEmployee_thenEnsureSendEmployeeIsReceived() throws InterruptedException, IOException {

		// Arrange
		Employee employee = getEmployee();

		// Act
		kafkaProducerService.sendMessageWithCallback(employee, topicName);

		// Assert
		ConsumerRecord<String, Employee> received = consumerRecords.poll(100,
				TimeUnit.MILLISECONDS);
		log.info("received >>>>>> " + received);
		assertThat(received).isNotNull();
		assertThat(received).has(key(null));
		assertThat(received.value().getFirstName()).isEqualTo(employee.getFirstName());
	}

	private Employee getEmployee() {

		Employee employee = new Employee();
		employee.setFirstName("Ahmed");
		employee.setLastName("Muhammad Mahmoud");
		employee.setEmail("ahmed@test.com");
		employee.setPhoneNumber("00201234567901");
		employee.setGender("male");
		employee.setCountryCode("EG");
		employee.setPosition("Technical Lead");
		employee.setBirthDate(Date.from(Instant.parse("2001-01-01T00:00:00.000Z")));
		employee.setPassword("12345");
		employee.setState(EmployeeState.ADDED);

		return employee;
	}

}
