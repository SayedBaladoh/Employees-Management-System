package com.sayedbaladoh.ems.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.sayedbaladoh.ems.model.Employee;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaProducerService {

	private final KafkaTemplate<String, Employee> employeeKafkaTemplate;

	void sendMessage(Employee employee, String topicName) {
		employeeKafkaTemplate.send(topicName, employee);
	}

	void sendMessageWithCallback(Employee employee, String topicName) {

		ListenableFuture<SendResult<String, Employee>> future = employeeKafkaTemplate.send(topicName, employee);

		future.addCallback(new ListenableFutureCallback<SendResult<String, Employee>>() {
			@Override
			public void onSuccess(SendResult<String, Employee> result) {
				log.info(
						"Employee: [{}], delivered succuessfully to topic: {}, partition: {}, with offset: {}.",
						employee,
						result.getRecordMetadata().topic(),
						result.getRecordMetadata().partition(),
						result.getRecordMetadata().offset());
			}

			@Override
			public void onFailure(Throwable ex) {
				log.error("Unable to deliver employee: [{}]. Error: {}.",
						employee,
						ex.getMessage());
			}
		});
	}
}
