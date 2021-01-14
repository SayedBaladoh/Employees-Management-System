package com.sayedbaladoh.ems.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sayedbaladoh.ems.model.Employee;

/**
 * Employee Repository extends <code>JpaRepository</code> provides JPA related
 * methods for standard data access layer in a standard DAO.
 * 
 * @author Sayed Baladoh
 *
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

	Boolean existsByPhoneNumber(String phoneNumber);

	Boolean existsByEmail(String email);

	public Optional<Employee> findByEmail(String email);
}
