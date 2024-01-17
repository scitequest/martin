package com.scitequest.martin;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

public final class JsonSchemaIT {

    public static void assertValidSchema(Path schemaPath, Path jsonPath) throws IOException {
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
        String schemaString = Files.readString(schemaPath);
        JsonSchema schema = factory.getSchema(schemaString);
        String jsonString = Files.readString(jsonPath);
        JsonNode node = Const.mapper.readTree(jsonString);

        schema.initializeValidators();
        Set<ValidationMessage> errors = schema.validate(node);
        for (var error : errors) {
            System.out.println(error);
        }
        assertTrue(errors.isEmpty());
    }

    @Test
    public void testMetadataSchema() throws IOException {
        assertValidSchema(Path.of("src/test/resources/schema/metadata.json"),
                Path.of("src/test/resources/export/metadata.json"));
    }

    @Ignore
    @Test
    public void testParametersSchema() throws IOException {
        assertValidSchema(Path.of("src/test/resources/schema/parameters.json"),
                Path.of("src/test/resources/export/parameters.json"));
    }

    @Ignore
    @Test
    public void testDataSchema() throws IOException {
        assertValidSchema(Path.of("src/test/resources/schema/data.json"),
                Path.of("src/test/resources/export/data.json"));
    }

    @Ignore
    @Test
    public void testDataStatisticsSchema() throws IOException {
        assertValidSchema(Path.of("src/test/resources/schema/data_statistics.json"),
                Path.of("src/test/resources/export/data_statistics.json"));
    }
}
