package org.bootcamp.latam.handlers;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.bootcamp.latam.configurations.AthenaClientFactory;
import org.bootcamp.latam.constants.Constants;
import org.bootcamp.latam.models.Employees;
import org.bootcamp.latam.services.AthenaOrchestrator;
import software.amazon.awssdk.services.athena.AthenaClient;

import java.util.List;

public class LambdaExecutor implements RequestHandler<LambdaRequest, List<List<Employees>>> {


    @Override
    public List<List<Employees>> handleRequest(LambdaRequest input, Context context) {

        AthenaClientFactory factory = new AthenaClientFactory();
        AthenaClient athenaClient = factory.createClientDev();

        AthenaOrchestrator orchestrator = new AthenaOrchestrator<>(
                athenaClient,
                Constants.ATHENA_SAMPLE_QUERY,
                Employees.class);

        return orchestrator.execute();
    }
}