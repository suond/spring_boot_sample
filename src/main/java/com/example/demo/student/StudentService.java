package com.example.demo.student;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service

public class StudentService {

    private final StudentRepository studentRepository;
    @Autowired
    public StudentService(StudentRepository studentRepository){
        this.studentRepository = studentRepository;
    }
    public List<Student> getStudents(){
//        return LocalDate dob = LocalDate.parse(("2000-01-05"));
//        int age = getAgeFromDob(dob);
        return studentRepository.findAll();
    }
    public void addNewStudent(Student student){
//        System.out.println(student);
        Optional<Student> o = studentRepository.findByEmail(student.getEmail());
        if (o.isPresent()) {
            throw new IllegalStateException("email already taken");
        }
        if(student.getEmail().isEmpty() || student.getEmail() == null ){
            throw new IllegalStateException("email cannot be empty");
        }
        if (!isEmail(student.getEmail())){
            throw new IllegalStateException("not a valid email address");
        }

        studentRepository.save(student);
    }
    private int getAgeFromDob(LocalDate dob){
        long elapsedDay = ChronoUnit.YEARS.between(dob, LocalDate.now());
        return (int) elapsedDay;
    }
    private boolean isEmail(String s) {
        return s.matches("^[-0-9a-zA-Z.+_]+@[-0-9a-zA-Z.+_]+\\.[a-zA-Z]{2,4}");
    }

    public void deleteStudent(Long id) {
       boolean exist =  studentRepository.existsById(id);
       if (!exist){
           throw new IllegalStateException("student with id " + id + " does not exist");
       }
       studentRepository.deleteById(id);
    }
    @Transactional
    public void updateStudent(Long id, String name, String email) {
        Student student = studentRepository.findById(id).orElseThrow( () ->(
                new IllegalStateException("student with id " + id + " does not exist")
                ));
        if (name != null && name.length() > 0 &&
            !Objects.equals(student.getName(), name)) {
            student.setName(name);
        }
        if (email != null && email.length() > 0 &&
                !Objects.equals(student.getEmail(), email)) {
            Optional<Student> optionalStudent = studentRepository.findByEmail(email);
            if (optionalStudent.isPresent()){
                throw new IllegalStateException("email already taken");
            }
            student.setEmail(email);
        }

    }
}
