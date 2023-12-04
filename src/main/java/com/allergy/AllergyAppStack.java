package com.allergy;

import com.amazonaws.HttpMethod;
import com.allergy.lambda.*;
import software.amazon.awscdk.*;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.apigateway.Resource;
import software.amazon.awscdk.services.apigateway.*;
import software.amazon.awscdk.services.dynamodb.*;

import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.FunctionProps;
import software.amazon.awscdk.services.lambda.Runtime;
import software.constructs.Construct;
import software.constructs.IConstruct;

import java.util.*;

public class AllergyAppStack extends Stack {

    private List<IConstruct> constructList = new ArrayList<>();

    public AllergyAppStack(final Construct scope, final String id) {
        this(scope, id, null,null);
    }

    public AllergyAppStack(final Construct scope, final String id, final StackProps props, final Properties properties) {
        super(scope, id, props);

        Table dynamodbTable = createDynamoDB();

        // Setting up of lambda functions
        Map<String, String> lambdaEnvMap = new HashMap<>();
        lambdaEnvMap.put("TABLE_NAME", dynamodbTable.getTableName());
        lambdaEnvMap.put("PRIMARY_KEY", "id");

        // Declaring of Lambda functions, handler name must be same as name of class
        Function createToDoFunction = new Function(this, CreateAllergy.class.getSimpleName(),
                getLambdaFunctionProps(lambdaEnvMap, CreateAllergy.class.getName(), "Create Allergy"));

        Function getAllToDoFunction = new Function(this, GetAllAllergy.class.getSimpleName(),
                getLambdaFunctionProps(lambdaEnvMap, GetAllAllergy.class.getName(), "Get All Allergy"));


        dynamodbTable.grantFullAccess(createToDoFunction);
        dynamodbTable.grantFullAccess(getAllToDoFunction);

        //LambdaRestApi api = createApiGateway(authorizer, getAllToDoFunction);

        LambdaRestApi api = buildCreateApiGateway(getAllToDoFunction);

        //Set resource path: https://api-gateway/allergy
        Resource todo = api.getRoot().addResource("allergy");

        // HTTP GET https://api-gateway/allergy
        //Endpoint is secured and requires token to call.

        // HTTP POST https://api-gateway/allergy
        todo.addMethod(HttpMethod.POST.name(), new LambdaIntegration(createToDoFunction));

        //Set {ID} path: https://api-gateway/allergy/{ID}
        Resource todoId = todo.addResource("{id}");


        constructList.add(dynamodbTable);
        constructList.add(api);
        constructList.add(createToDoFunction);
        constructList.add(getAllToDoFunction);
        addTags(constructList);
    }



    private FunctionProps getLambdaFunctionProps(Map<String, String> lambdaEnvMap, String handler, String description) {
        return FunctionProps.builder()
                //Note: Use of Maven Shade plugin to include dependency JARs into final jar file
                .code(Code.fromAsset("./target/allergy-api-0.1.jar"))
                .handler(handler)
                .runtime(Runtime.JAVA_11)
                .environment(lambdaEnvMap)
                .timeout(Duration.seconds(30))
                .memorySize(512)
                .description(description)
                .build();
    }

    /**
     * @return
     */
    private Table createDynamoDB() {
        //DynamoDB Partition(Primary) Key
        Attribute partitionKey = Attribute.builder()
                .name("id")
                .type(AttributeType.STRING)
                .build();

        TableProps tableProps = TableProps.builder()
                .tableName("Allergy")
                .partitionKey(partitionKey)
                // The default removal policy is RETAIN, which means that cdk destroy will not attempt to delete
                // the new table, and it will remain in your account until manually deleted. By setting the policy to
                // DESTROY, cdk destroy will delete the table (even if it has data in it)
                .removalPolicy(RemovalPolicy.DESTROY)
//                .removalPolicy(RemovalPolicy.RETAIN)
                .readCapacity(1)
                .writeCapacity(1)
                .billingMode(BillingMode.PROVISIONED)
                .build();

        return new Table(this, "AllergyTable", tableProps);
    }

    /**
     * @return
     */


    private LambdaRestApi buildCreateApiGateway(Function function){
        return LambdaRestApi.Builder.create(this, "serverless")
                .restApiName("serverless-ui")
                .description("CDK built serverless API")
                .handler(function)
                .build();
    }

    private void addTags(List<IConstruct> constructList) {
        constructList.forEach(construct -> {
                    Tags.of(construct).add("project", "AllergyApp");
                    Tags.of(construct).add("environment", "dev");
                }
        );
    }

}
