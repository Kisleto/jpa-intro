package com.codecool.jpaintro.repository;

import com.codecool.jpaintro.entity.Address;
import com.codecool.jpaintro.entity.Location;
import com.codecool.jpaintro.entity.School;
import com.codecool.jpaintro.entity.Student;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.beans.Transient;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("test")
public class AllRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    SchoolRepository schoolRepository;

    @Test
    public void saveOneSimple(){
        Student john = Student.builder()
                .email("john@codecool.com")
                .name("John")
                .build();
        studentRepository.save(john);
        List<Student> studentList = studentRepository.findAll();

        assertThat(studentList).hasSize(1);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void saveUniqueFieldTwice(){
        Student student = Student.builder()
                .email("john@codecool.com")
                .name("john")
                .build();
        studentRepository.save(student);

        Student student1 = Student.builder()
                .email("john@codecool.com")
                .name("Atta")
                .build();

        studentRepository.saveAndFlush(student1);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void emailShouldBeNotNull(){
        Student student = Student.builder()
                .name("john")
                .build();
        studentRepository.save(student);
    }

    @Test
    public void transientIsNotSaved(){
        Student john = Student.builder()
                .birthDate(LocalDate.of(1993, 10,21))
                .email("john@codecool.com")
                .name("John")
                .build();
        john.calculateAge();
        assertThat(john.getAge()).isGreaterThanOrEqualTo(25);
        studentRepository.save(john);
        entityManager.clear();

        List<Student> students = studentRepository.findAll();
        assertThat(students).allMatch(student -> student.getAge() == 0L);
    }

    @Test
    public void addressIsPersistedWithStudent(){
        Address address = Address.builder()
                .country("Hungary")
                .city("Budapest")
                .address("Nagymező street 44")
                .zipCode(1065)
                .build();
        Student student = Student.builder()
                .email("temp@codecool.com")
                .address(address)
                .build();

        studentRepository.save(student);
        List<Address> addressList = addressRepository.findAll();
        assertThat(addressList)
                .hasSize(1)
                .allMatch(address1 -> address1.getId() > 0L);
    }

    @Test
    public void studentsArePersistedAndDeletedWithNewSchool(){
        Set<Student> students = IntStream.range(1, 10)
                .boxed()
                .map(integer -> Student.builder().email("student" + integer + "codecool.com").build())
                .collect(Collectors.toSet());

        School build = School.builder()
                .students(students)
                .location(Location.BUDAPEST)
                .build();

        schoolRepository.save(build);

        assertThat(studentRepository.findAll())
                .hasSize(9)
                .anyMatch(student -> student.getEmail().equals("student9@codecool.com"));

        schoolRepository.deleteAll();

        assertThat(studentRepository.findAll())
                .hasSize(0);

    }


}