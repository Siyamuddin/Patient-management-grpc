package com.pm.patientservice.kafka;

import com.pm.patientservice.model.Patient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import patient.events.PatientEventOuterClass;

@Slf4j
@Service
public class KafkaProducer {
    private final KafkaTemplate<String,byte[]> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishEvent(Patient patient){
        PatientEventOuterClass.PatientEvent patientEvent= PatientEventOuterClass.PatientEvent.newBuilder()
                .setEmail(patient.getEmail())
                .setPatientId(patient.getId().toString())
                .setName(patient.getName())
                .setEventType("PATIENT CREATED")
                .build();
        try{
            kafkaTemplate.send("patient",patientEvent.toByteArray());
        } catch (RuntimeException e) {
            log.error("There is a problem creating Patient.");
        }
    }
}
