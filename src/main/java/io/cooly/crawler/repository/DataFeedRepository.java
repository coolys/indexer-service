package io.cooly.crawler.repository;

import io.cooly.crawler.domain.DataFeed;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data MongoDB repository for the DataFeed entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DataFeedRepository extends MongoRepository<DataFeed, String> {

}
