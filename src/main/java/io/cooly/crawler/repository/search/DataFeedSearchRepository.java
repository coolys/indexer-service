package io.cooly.crawler.repository.search;

import io.cooly.crawler.domain.DataFeed;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the DataFeed entity.
 */
public interface DataFeedSearchRepository extends ElasticsearchRepository<DataFeed, String> {
}
