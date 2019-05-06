package io.cooly.crawler.service.impl;

import io.cooly.crawler.domain.DataFeed;
import io.cooly.crawler.repository.DataFeedRepository;
import io.cooly.crawler.repository.search.DataFeedSearchRepository;
import io.cooly.crawler.service.DataFeedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

/**
 * Service Implementation for managing DataFeed.
 */
@Service
public class DataFeedServiceImpl implements DataFeedService {

    private final Logger log = LoggerFactory.getLogger(DataFeedServiceImpl.class);

    private final DataFeedRepository dataFeedRepository;

    private final DataFeedSearchRepository dataFeedSearchRepository;

    public DataFeedServiceImpl(DataFeedRepository dataFeedRepository, DataFeedSearchRepository dataFeedSearchRepository) {
        this.dataFeedRepository = dataFeedRepository;
        this.dataFeedSearchRepository = dataFeedSearchRepository;
    }

    /**
     * Save a dataFeed.
     *
     * @param dataFeed the entity to save
     * @return the persisted entity
     */
    @Override
    public DataFeed save(DataFeed dataFeed) {
        log.debug("Request to save DataFeed : {}", dataFeed);
        DataFeed result = dataFeedRepository.save(dataFeed);
        dataFeedSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the dataFeeds.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    public Page<DataFeed> findAll(Pageable pageable) {
        log.debug("Request to get all DataFeeds");
        return dataFeedRepository.findAll(pageable);
    }


    /**
     * Get one dataFeed by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    public Optional<DataFeed> findOne(String id) {
        log.debug("Request to get DataFeed : {}", id);
        return dataFeedRepository.findById(id);
    }

    /**
     * Delete the dataFeed by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(String id) {
        log.debug("Request to delete DataFeed : {}", id);
        dataFeedRepository.deleteById(id);
        dataFeedSearchRepository.deleteById(id);
    }

    /**
     * Search for the dataFeed corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    public Page<DataFeed> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of DataFeeds for query {}", query);
        return dataFeedSearchRepository.search(queryStringQuery(query), pageable);    }
}
