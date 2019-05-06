package io.cooly.crawler.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of DataFeedSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class DataFeedSearchRepositoryMockConfiguration {

    @MockBean
    private DataFeedSearchRepository mockDataFeedSearchRepository;

}
