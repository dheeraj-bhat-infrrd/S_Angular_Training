package com.realtech.socialsurvey.stream.endpoints.repositories;

import org.springframework.boot.actuate.trace.InMemoryTraceRepository;
import org.springframework.stereotype.Component;


@Component
public class CustomTraceRepository extends InMemoryTraceRepository
{

    public CustomTraceRepository() {
        super.setCapacity(200);
    }

}
