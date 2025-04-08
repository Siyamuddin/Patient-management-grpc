package com.pm.patientservice.service;

import com.pm.patientservice.DTO.PatientRequestDTO;
import com.pm.patientservice.DTO.PatientResponseDTO;
import com.pm.patientservice.exceptions.EmailAlreadyExistException;
import com.pm.patientservice.exceptions.PatientNotFoundException;
import com.pm.patientservice.grpc.BillingServiceGrpcClient;
import com.pm.patientservice.kafka.KafkaProducer;
import com.pm.patientservice.mapper.PatientDTOMapper;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;
import org.checkerframework.checker.units.qual.K;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PatientService {

    private final KafkaProducer kafkaProducer;
    private final BillingServiceGrpcClient billingServiceGrpcClient;
    @Autowired
    private PatientRepository patientRepository;

    public PatientService(BillingServiceGrpcClient billingServiceGrpcClient,KafkaProducer kafkaProducer) {
        this.billingServiceGrpcClient = billingServiceGrpcClient;
        this.kafkaProducer=kafkaProducer;
    }

    public List<PatientResponseDTO> getPatients(){
    List<Patient> patients=patientRepository.findAll();
    List<PatientResponseDTO> patientDTOS=patients.stream().map(patient -> PatientMapper.toDTO(patient)).toList();
    return patientDTOS;
}

public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO){

    if(patientRepository.existsByEmail(patientRequestDTO.getEmail())){
        throw new EmailAlreadyExistException("Email already exists"+patientRequestDTO.getEmail());
    }

    Patient savedPatient=patientRepository.save(PatientDTOMapper.toPatient(patientRequestDTO));
    kafkaProducer.publishEvent(savedPatient);
    billingServiceGrpcClient.createBillingAccount(savedPatient.getId().toString(),savedPatient.getEmail(),savedPatient.getName());
    return PatientMapper.toDTO(savedPatient);
}

public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO){
    Patient patient=patientRepository.findById(id).orElseThrow(()-> new PatientNotFoundException("Patient","ID",id));
    if(patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(),id)){
        throw new EmailAlreadyExistException("Email already exists"+patientRequestDTO.getEmail());
    }
    patient.setName(patientRequestDTO.getName());
    patient.setEmail(patientRequestDTO.getEmail());
    patient.setAddress(patientRequestDTO.getAddress());
    patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));
    Patient updatedPatient=patientRepository.save(patient);

    return PatientMapper.toDTO(updatedPatient);
}
public void deletePatient(UUID uuid){
    Patient patient=patientRepository.findById(uuid).orElseThrow(()-> new PatientNotFoundException("Patient","ID",uuid));
    patientRepository.deleteById(uuid);
}

}
