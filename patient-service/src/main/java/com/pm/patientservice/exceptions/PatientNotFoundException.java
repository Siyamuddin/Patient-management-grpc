package com.pm.patientservice.exceptions;

import java.util.UUID;

public class PatientNotFoundException extends RuntimeException {
    public PatientNotFoundException(String field, String domain, UUID uuid)
    {
        super(String.format(field+" with "+domain+" : "+uuid+" is not found!"));

    }
}
