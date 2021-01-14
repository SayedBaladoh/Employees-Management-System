package com.sayedbaladoh.ems.config;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;

import com.sayedbaladoh.ems.model.EmployeeEvent;
import com.sayedbaladoh.ems.model.EmployeeState;

import lombok.extern.java.Log;

@Log
@SpringBootTest
class StateMachineConfigTest {

	@Autowired
	StateMachineFactory<EmployeeState, EmployeeEvent> factory;

	@Test
	void testNewStateMachine() {

		StateMachine<EmployeeState, EmployeeEvent> sm = factory.getStateMachine(UUID.randomUUID());

		sm.start();
		log.info("Init State: " + sm.getState().toString());

		sm.sendEvent(EmployeeEvent.ADD);
		log.info("State after Add Event: " + sm.getState().toString());

		sm.sendEvent(EmployeeEvent.CHECK);
		log.info("State after Check Event: " + sm.getState().toString());

		sm.sendEvent(EmployeeEvent.APPROVE);
		log.info("State after Approve Event: " + sm.getState().toString());

		sm.sendEvent(EmployeeEvent.REJECT);
		log.info("State after REJECT Event: " + sm.getState().toString());

		sm.sendEvent(EmployeeEvent.ACTIVATE);
		log.info("State after active Event: " + sm.getState().toString());
	}

}
