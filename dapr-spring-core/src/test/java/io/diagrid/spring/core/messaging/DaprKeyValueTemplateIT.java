package io.diagrid.spring.core.messaging;

import io.dapr.client.DaprClientBuilder;
import io.diagrid.BaseIntegrationTest;
import io.diagrid.spring.core.keyvalue.DaprKeyValueAdapter;
import io.diagrid.spring.core.keyvalue.DaprKeyValueTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.data.keyvalue.core.query.KeyValueQuery;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link DaprKeyValueTemplateIT}.
 */
public class DaprKeyValueTemplateIT extends BaseIntegrationTest {

    private final DaprKeyValueTemplate keyValueTemplate = new DaprKeyValueTemplate(new DaprKeyValueAdapter(
            new DaprClientBuilder().build(), new DaprClientBuilder().buildPreviewClient(),
            "kvstore", "MyQueryIndex")
    );

    @Test
    public void testInsertAndQueryDaprKeyValueTemplate() {
        var savedType = keyValueTemplate.insert(new TestType(3, "test"));
        assertThat(savedType).isNotNull();

        var findById = keyValueTemplate.findById(3, TestType.class).get();
        assertThat(findById).isNotNull();
        assertThat(findById).isEqualTo(savedType);

        KeyValueQuery<String> keyValueQuery = new KeyValueQuery<String>("'content' == 'test'");

        Iterable<TestType> myTypes = keyValueTemplate.find(keyValueQuery, TestType.class);
        assertThat(myTypes.iterator().hasNext()).isTrue();

        TestType item = myTypes.iterator().next();
        assertThat(item.id()).isEqualTo(Integer.valueOf(3));
        assertThat(item.content()).isEqualTo("test");

        keyValueQuery = new KeyValueQuery<>("'content' == 'asd'");

        myTypes = keyValueTemplate.find(keyValueQuery, TestType.class);
        assertThat(!myTypes.iterator().hasNext()).isTrue();
    }

    @Test
    public void testUpdateDaprKeyValueTemplate() {
        var insertedType = keyValueTemplate.insert(new TestType(2, "test"));
        assertThat(insertedType).isNotNull();

        var updatedType = keyValueTemplate.update(new TestType(2, "test2"));
        assertThat(updatedType).isNotNull();
    }

}
