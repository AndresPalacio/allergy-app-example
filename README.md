# Serverless To Do REST API

This project is a serverless REST API for a To Do App. It is built using AWS Cloud Development Kit (CDK) with the following AWS services:

1. AWS REST API Gateway
2. AWS Lambda
4. AWS DynamoDB

AWS CDK is an open-source software development framework for defining cloud infrastructure as code with modern programming languages and deploying it through AWS CloudFormation.

## Project config 

Modify the application.properties file with your AWS account ID and region.

region=us-east-1
account=1234456789



## Useful commands
This project does not run locally, it requires CDK CLI to deploy it onto AWS.
Guide on installing CDK CLI is [available here.](https://docs.aws.amazon.com/cdk/v2/guide/cli.html)

 * `mvn package`     compile and run tests
 * `cdk ls`          list all stacks in the app
 * `cdk synth`       emits the synthesized CloudFormation template
 * `cdk deploy`      deploy this stack to default AWS account/region
 * `cdk diff`        compare deployed stack with current state
 * `cdk docs`        open CDK documentation





curl --location 'path/prod/allergy' \
--header 'Content-Type: application/json' \
--data '{
"id": "1",
"generalStatus": "aplicado",
"status": "true",
"inhaler": true,
"percentage": 10,
"pm25": 250
}'



curl --location 'path/prod/'

