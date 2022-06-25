package org.bootcamp.latam.services;



import org.bootcamp.latam.models.Employees;

import java.util.List;

public interface IAthenaService {
    List<List<Employees>> getDataFromAthena(String myQuery);
}