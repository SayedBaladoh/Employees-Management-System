package com.sayedbaladoh.ems.config;

import java.util.EnumSet;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import com.sayedbaladoh.ems.model.EmployeeEvent;
import com.sayedbaladoh.ems.model.EmployeeState;

import lombok.extern.java.Log;

@Log
@Configuration
@EnableStateMachineFactory
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<EmployeeState, EmployeeEvent> {

	@Override
	public void configure(StateMachineStateConfigurer<EmployeeState, EmployeeEvent> states) throws Exception {
		states.withStates()
				.initial(EmployeeState.ADDED)
				.states(EnumSet.allOf(EmployeeState.class))
				.end(EmployeeState.ACTIVE)
				.end(EmployeeState.REJECTED)
				.end(EmployeeState.INACTIVE);
	}

	@Override
	public void configure(StateMachineTransitionConfigurer<EmployeeState, EmployeeEvent> transitions) throws Exception {
		transitions.withExternal().source(EmployeeState.ADDED).target(EmployeeState.ADDED).event(EmployeeEvent.ADD)
				.and()
				.withExternal().source(EmployeeState.ADDED).target(EmployeeState.IN_CHECK).event(EmployeeEvent.CHECK).action(checkAction())
				.and()
				.withExternal().source(EmployeeState.UPDATED).target(EmployeeState.IN_CHECK).event(EmployeeEvent.CHECK).action(checkAction())
				.and()
				.withExternal().source(EmployeeState.IN_CHECK).target(EmployeeState.APPROVED).event(EmployeeEvent.APPROVE)
				.and()
				.withExternal().source(EmployeeState.IN_CHECK).target(EmployeeState.REJECTED).event(EmployeeEvent.REJECT)
				.and()
				.withExternal().source(EmployeeState.APPROVED).target(EmployeeState.ACTIVE).event(EmployeeEvent.ACTIVATE)
				.and()
				.withExternal().source(EmployeeState.APPROVED).target(EmployeeState.INACTIVE).event(EmployeeEvent.DEACTIVATE);
	}

	@Override
	public void configure(StateMachineConfigurationConfigurer<EmployeeState, EmployeeEvent> config) throws Exception {

		StateMachineListenerAdapter<EmployeeState, EmployeeEvent> adapter = new StateMachineListenerAdapter<>() {
			@Override
			public void stateChanged(State<EmployeeState, EmployeeEvent> from, State<EmployeeState, EmployeeEvent> to) {
				log.info(String.format("State changed from: %s, to: -> %s.", from == null ? "none" : from.getId(),
						to.getId()));
			}
		};

		config.withConfiguration()
				.listener(adapter);
	}

	@Bean
	public Action<EmployeeState, EmployeeEvent> checkAction() {
		return context -> {
			// TODO Check logic
			log.info("Check action was called!!!");
			log.info("Exiting state: " + context.getSource().getId() + ", Entering state: -> "
					+ context.getTarget().getId());
		};
	}

}
