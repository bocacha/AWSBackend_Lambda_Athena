package org.bootcamp.latam.services;

import org.bootcamp.latam.constants.*;
import lombok.SneakyThrows;
import org.bootcamp.latam.models.Employees;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import software.amazon.awssdk.services.athena.AthenaClient;
import software.amazon.awssdk.services.athena.model.*;
import software.amazon.awssdk.services.athena.paginators.GetQueryResultsIterable;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.bootcamp.latam.helpers.Helpers.createGenericInstance;
import static org.bootcamp.latam.helpers.Helpers.holdColumnInfo;

public class AthenaQueryExecutor<T> {
    private static Logger logger= LoggerFactory.getLogger(AthenaQueryExecutor.class);


    public AthenaQueryExecutor() {

    }

    public static String submitAthenaQuery(AthenaClient athenaClient, String query) {

        QueryExecutionContext queryExecutionContext = QueryExecutionContext.builder()
                .database(Constants.ATHENA_DEFAULT_DATABASE).build();

        ResultConfiguration resultConfiguration = ResultConfiguration.builder()
                .outputLocation(Constants.ATHENA_OUTPUT_BUCKET).build();

        StartQueryExecutionRequest startQueryExecutionRequest = StartQueryExecutionRequest.builder()
                .queryString(query)
                .queryExecutionContext(queryExecutionContext)
                .resultConfiguration(resultConfiguration).build();

        StartQueryExecutionResponse startQueryExecutionResponse = athenaClient.startQueryExecution(startQueryExecutionRequest);

        return startQueryExecutionResponse.queryExecutionId();
    }

    // Waits for an Amazon Athena query to complete, fail or to be cancelled.
    public static void waitForQueryToComplete(
            AthenaClient athenaClient, String queryExecutionId) throws InterruptedException {
        GetQueryExecutionRequest getQueryExecutionRequest = GetQueryExecutionRequest.builder()
                .queryExecutionId(queryExecutionId).build();

        GetQueryExecutionResponse getQueryExecutionResponse;
        boolean isQueryStillRunning = true;

        while (isQueryStillRunning) {
            getQueryExecutionResponse = athenaClient.getQueryExecution(getQueryExecutionRequest);
            String queryState =
                    getQueryExecutionResponse.queryExecution().status().state().toString();
            if (queryState.equals(QueryExecutionState.FAILED.toString())) {
                throw new RuntimeException("Error message: " + getQueryExecutionResponse
                        .queryExecution().status().stateChangeReason());
            } else if (queryState.equals(QueryExecutionState.CANCELLED.toString())) {
                throw new RuntimeException("The Amazon Athena query was cancelled.");
            } else if (queryState.equals(QueryExecutionState.SUCCEEDED.toString())) {
                isQueryStillRunning = false;
            } else {
                Thread.sleep(Constants.SLEEP_AMOUNT_IN_MS);
            }
            System.out.println("The current status is: " + queryState);
        }
    }

    // Process the result of each row
    public static  List<List<Employees>> processResultRows(AthenaClient athenaClient, String queryExecutionId) {
        List<List<Employees>> res = new ArrayList<>();
        try {
            GetQueryResultsRequest getQueryResultsRequest =
                    GetQueryResultsRequest.builder()
                            .queryExecutionId(queryExecutionId).build();

            GetQueryResultsIterable getQueryResultsResults =
                    athenaClient.getQueryResultsPaginator(getQueryResultsRequest);

            for (GetQueryResultsResponse result : getQueryResultsResults) {
                List<ColumnInfo> columnInfoList = result.resultSet().resultSetMetadata().columnInfo();
                List<Row> results = result.resultSet().rows();
                res.add(processRows(results, columnInfoList));
                logger.info("RES {} RES**************", res);
                logger.info("RESULT {} RESULT**************", result);
            }
        } catch (AthenaException e) {
            logger.error("Failed to process with reason: {}", e.getMessage());
        }
        return res;
    }


    private static List<Employees> processRows(List<Row> row, List<ColumnInfo> columnInfoList) {
        List<Employees> employeeList = new ArrayList<>();
        try {
            for(Row myRow : row){
                List<Datum> allData = myRow.data();

                Employees employee = new Employees();
                employee.setEmpid(allData.get(0).varCharValue());
                employee.setNameprefix(allData.get(1).varCharValue());
                employee.setFirstname(allData.get(2).varCharValue());
                employee.setMiddleinitial(allData.get(3).varCharValue());
                employee.setLastname(allData.get(4).varCharValue());
                employee.setGender(allData.get(5).varCharValue());
                employee.setEmail(allData.get(6).varCharValue());
                employee.setFathername(allData.get(7).varCharValue());
                employee.setMothername(allData.get(8).varCharValue());
                employee.setMothermaidenname(allData.get(9).varCharValue());
                employee.setDateofbirth(allData.get(10).varCharValue());
                employee.setTimeofbirth(allData.get(11).varCharValue());
                employee.setAgeinyears(allData.get(12).varCharValue());
                employee.setWeightinkgs(allData.get(13).varCharValue());
                employee.setDateofjoining(allData.get(14).varCharValue());
                employee.setQuarterofjoining(allData.get(15).varCharValue());
                employee.setHalfofjoining(allData.get(16).varCharValue());
                employee.setYearofjoining(allData.get(17).varCharValue());
                employee.setMonthofjoining(allData.get(18).varCharValue());
                employee.setMonthnameofjoining(allData.get(19).varCharValue());
                employee.setShortmonth(allData.get(20).varCharValue());
                employee.setDayofjoining(allData.get(21).varCharValue());
                employee.setDowofjoining(allData.get(22).varCharValue());
                employee.setShortdow(allData.get(23).varCharValue());
                employee.setAgeincompany(allData.get(24).varCharValue());
                employee.setSalary(allData.get(25).varCharValue());
                employee.setLasthike(allData.get(26).varCharValue());
                employee.setSsn(allData.get(27).varCharValue());
                employee.setPhoneno(allData.get(28).varCharValue());
                employee.setPlacename(allData.get(29).varCharValue());
                employee.setCounty(allData.get(30).varCharValue());
                employee.setCity(allData.get(31).varCharValue());
                employee.setState(allData.get(32).varCharValue());
                employee.setZip(allData.get(33).varCharValue());
                employee.setRegion(allData.get(34).varCharValue());
                employee.setUsername(allData.get(35).varCharValue());
                employee.setPassword(allData.get(36).varCharValue());
                employeeList.add(employee);
                logger.info("Employee: {}", employee);

            }
        }catch(Exception e){
            logger.error("Failed to process with reason: {}", e.getMessage());
        }
        logger.info("Employee: {}", employeeList);
        return employeeList;
    }

}
