package io.cooly.crawler.service;

import io.cooly.crawler.domain.DataFeed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing DataFeed.
 */
public interface DataFeedService {

    /**
     * Save a dataFeed.
     *
     * @param dataFeed the entity to save
     * @return the persisted entity
     */
    DataFeed save(DataFeed dataFeed);

    /**
     * Get all the dataFeeds.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<DataFeed> findAll(Pageable pageable);


    /**
     * Get the "id" dataFeed.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<DataFeed> findOne(String id);

    /**
     * Delete the "id" dataFeed.
     *
     * @param id the id of the entity
     */
    void delete(String id);

    /**
     * Search for the dataFeed corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<DataFeed> search(String query, Pageable pageable);
}
