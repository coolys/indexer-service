package io.cooly.crawler.web.rest;

import io.cooly.crawler.domain.DataFeed;
import io.cooly.crawler.service.DataFeedService;
import io.cooly.crawler.web.rest.errors.BadRequestAlertException;
import io.cooly.crawler.web.rest.util.HeaderUtil;
import io.cooly.crawler.web.rest.util.PaginationUtil;
import io.github.coolys.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing DataFeed.
 */
@RestController
@RequestMapping("/api")
public class DataFeedResource {

    private final Logger log = LoggerFactory.getLogger(DataFeedResource.class);

    private static final String ENTITY_NAME = "indexerDataFeed";

    private final DataFeedService dataFeedService;

    public DataFeedResource(DataFeedService dataFeedService) {
        this.dataFeedService = dataFeedService;
    }

    /**
     * POST  /data-feeds : Create a new dataFeed.
     *
     * @param dataFeed the dataFeed to create
     * @return the ResponseEntity with status 201 (Created) and with body the new dataFeed, or with status 400 (Bad Request) if the dataFeed has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/data-feeds")
    public ResponseEntity<DataFeed> createDataFeed(@RequestBody DataFeed dataFeed) throws URISyntaxException {
        log.debug("REST request to save DataFeed : {}", dataFeed);
        if (dataFeed.getId() != null) {
            throw new BadRequestAlertException("A new dataFeed cannot already have an ID", ENTITY_NAME, "idexists");
        }
        DataFeed result = dataFeedService.save(dataFeed);
        return ResponseEntity.created(new URI("/api/data-feeds/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /data-feeds : Updates an existing dataFeed.
     *
     * @param dataFeed the dataFeed to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated dataFeed,
     * or with status 400 (Bad Request) if the dataFeed is not valid,
     * or with status 500 (Internal Server Error) if the dataFeed couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/data-feeds")
    public ResponseEntity<DataFeed> updateDataFeed(@RequestBody DataFeed dataFeed) throws URISyntaxException {
        log.debug("REST request to update DataFeed : {}", dataFeed);
        if (dataFeed.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        DataFeed result = dataFeedService.save(dataFeed);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, dataFeed.getId().toString()))
            .body(result);
    }

    /**
     * GET  /data-feeds : get all the dataFeeds.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of dataFeeds in body
     */
    @GetMapping("/data-feeds")
    public ResponseEntity<List<DataFeed>> getAllDataFeeds(Pageable pageable) {
        log.debug("REST request to get a page of DataFeeds");
        Page<DataFeed> page = dataFeedService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/data-feeds");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /data-feeds/:id : get the "id" dataFeed.
     *
     * @param id the id of the dataFeed to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the dataFeed, or with status 404 (Not Found)
     */
    @GetMapping("/data-feeds/{id}")
    public ResponseEntity<DataFeed> getDataFeed(@PathVariable String id) {
        log.debug("REST request to get DataFeed : {}", id);
        Optional<DataFeed> dataFeed = dataFeedService.findOne(id);
        return ResponseUtil.wrapOrNotFound(dataFeed);
    }

    /**
     * DELETE  /data-feeds/:id : delete the "id" dataFeed.
     *
     * @param id the id of the dataFeed to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/data-feeds/{id}")
    public ResponseEntity<Void> deleteDataFeed(@PathVariable String id) {
        log.debug("REST request to delete DataFeed : {}", id);
        dataFeedService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id)).build();
    }

    /**
     * SEARCH  /_search/data-feeds?query=:query : search for the dataFeed corresponding
     * to the query.
     *
     * @param query the query of the dataFeed search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/data-feeds")
    public ResponseEntity<List<DataFeed>> searchDataFeeds(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of DataFeeds for query {}", query);
        Page<DataFeed> page = dataFeedService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/data-feeds");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

}
