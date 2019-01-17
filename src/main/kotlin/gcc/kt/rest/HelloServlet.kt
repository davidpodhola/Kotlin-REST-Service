package gcc.kt.rest

import graphql.schema.idl.SchemaGenerator
import graphql.schema.StaticDataFetcher
import graphql.schema.idl.RuntimeWiring.newRuntimeWiring
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.TypeDefinitionRegistry
import graphql.schema.idl.SchemaParser
import graphql.schema.GraphQLSchema
import graphql.servlet.GraphQLConfiguration
import graphql.servlet.GraphQLHttpServlet
import sun.security.krb5.internal.KDCOptions.with
import javax.servlet.annotation.WebServlet


@WebServlet(name = "HelloServlet", urlPatterns = arrayOf("graphql"), loadOnStartup = 1)
class HelloServlet : GraphQLHttpServlet() {

    override fun getConfiguration(): GraphQLConfiguration {
        return GraphQLConfiguration.with(createSchema()).build()
    }

    private fun createSchema(): GraphQLSchema {
        val schema = "type Query{hello: String}"

        val schemaParser = SchemaParser()
        val typeDefinitionRegistry = schemaParser.parse(schema)

        val runtimeWiring = newRuntimeWiring()
                .type("Query") { builder -> builder.dataFetcher("hello", StaticDataFetcher("world")) }
                .build()

        val schemaGenerator = SchemaGenerator()
        return schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring)
    }

}