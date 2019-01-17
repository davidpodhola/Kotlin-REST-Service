/*******************************************************************************
 * (c) Copyright IBM Corporation 2017.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package gcc.kt.rest

import javax.ws.rs.core.Application
import javax.ws.rs.core.Response
import javax.ws.rs.Path
import javax.ws.rs.ApplicationPath
import javax.ws.rs.PathParam
import javax.ws.rs.GET
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

import gcc.kt.rest.Greeting
import graphql.ExecutionResult
import graphql.GraphQL
import graphql.schema.GraphQLSchema
import graphql.schema.idl.SchemaGenerator
import graphql.schema.StaticDataFetcher
import graphql.schema.idl.RuntimeWiring.newRuntimeWiring
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.TypeDefinitionRegistry
import graphql.schema.idl.SchemaParser



@Path("/hello")
@ApplicationPath("/")
class HelloService : Application() {
    
    @GET
    @Path("/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    fun sayHello(@PathParam("name") name: String): Greeting {

        println("HelloService sayHello called: " + name)
        return Greeting("Hello", test())
    }

    fun test(): String {
        val schema = "type Query{hello: String}"

        val schemaParser = SchemaParser()
        val typeDefinitionRegistry = schemaParser.parse(schema)

        val runtimeWiring = newRuntimeWiring()
                .type("Query") { builder -> builder.dataFetcher("hello", StaticDataFetcher("world")) }
                .build()

        val schemaGenerator = SchemaGenerator()
        val graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring)

        val build = GraphQL.newGraphQL(graphQLSchema).build()
        val executionResult = build.execute("{hello}")

        return executionResult.getData<Any>().toString()
    }
}