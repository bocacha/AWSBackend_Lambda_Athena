package org.bootcamp.latam.constants;

public interface Constants {
    String ATHENA_OUTPUT_BUCKET = "s3://demo-aws-s3-athena-results/results/";
    String ATHENA_SAMPLE_QUERY = "SELECT * FROM employee limit 10;";
    int TIMEOUT = 100000;
    long SLEEP_AMOUNT_IN_MS = 1000;
    String ATHENA_DEFAULT_DATABASE = "employees";
}
