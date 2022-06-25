package org.bootcamp.latam.services;


import org.bootcamp.latam.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.athena.AthenaClient;

import java.util.List;

@Service
public class AthenaServiceImpl<T> implements IAthenaService
{
    @Autowired
    AthenaClient athenaClient;

    @Override
    public List<List<Employees>> getDataFromAthena(String myQuery){
        return new AthenaOrchestrator<>(athenaClient, myQuery, Employees.class).execute();
    }
}
