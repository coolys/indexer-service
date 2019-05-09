package io.cooly.crawler.web.rest;

import io.cooly.crawler.IndexerApp;

import io.cooly.crawler.config.SecurityBeanOverrideConfiguration;

import io.cooly.crawler.domain.DataFeed;
import io.cooly.crawler.repository.DataFeedRepository;
import io.cooly.crawler.repository.search.DataFeedSearchRepository;
import io.cooly.crawler.service.DataFeedService;
import io.cooly.crawler.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;


import static io.cooly.crawler.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the DataFeedResource REST controller.
 *
 * @see DataFeedResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SecurityBeanOverrideConfiguration.class, IndexerApp.class})
public class DataFeedResourceIntTest {

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final String DEFAULT_DOMAIN = "AAAAAAAAAA";
    private static final String UPDATED_DOMAIN = "BBBBBBBBBB";

    private static final Boolean DEFAULT_FETCHED = false;
    private static final Boolean UPDATED_FETCHED = true;

    private static final Instant DEFAULT_CREATED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_ENGINE = "AAAAAAAAAA";
    private static final String UPDATED_ENGINE = "BBBBBBBBBB";

    private static final String DEFAULT_DATA = "AAAAAAAAAA";
    private static final String UPDATED_DATA = "BBBBBBBBBB";

    private static final String DEFAULT_HTML = "AAAAAAAAAA";
    private static final String UPDATED_HTML = "BBBBBBBBBB";

    private static final String DEFAULT_LINKS = "AAAAAAAAAA";
    private static final String UPDATED_LINKS = "BBBBBBBBBB";

    private static final Integer DEFAULT_LEVEL = 1;
    private static final Integer UPDATED_LEVEL = 2;

    private static final String DEFAULT_FILE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FILE_NAME = "BBBBBBBBBB";

    @Autowired
    private DataFeedRepository dataFeedRepository;

    @Autowired
    private DataFeedService dataFeedService;

    /**
     * This repository is mocked in the io.cooly.crawler.repository.search test package.
     *
     * @see io.cooly.crawler.repository.search.DataFeedSearchRepositoryMockConfiguration
     */
    @Autowired
    private DataFeedSearchRepository mockDataFeedSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private Validator validator;

    private MockMvc restDataFeedMockMvc;

    private DataFeed dataFeed;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final DataFeedResource dataFeedResource = new DataFeedResource(dataFeedService);
        this.restDataFeedMockMvc = MockMvcBuilders.standaloneSetup(dataFeedResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DataFeed createEntity() {
        DataFeed dataFeed = new DataFeed()
            .url(DEFAULT_URL)
            .domain(DEFAULT_DOMAIN)
            .fetched(DEFAULT_FETCHED)
            .created(DEFAULT_CREATED)
            .engine(DEFAULT_ENGINE)
            .data(DEFAULT_DATA)
            .html(DEFAULT_HTML)
            .links(DEFAULT_LINKS)
            .level(DEFAULT_LEVEL)
            .fileName(DEFAULT_FILE_NAME);
        return dataFeed;
    }

    @Before
    public void initTest() {
        dataFeedRepository.deleteAll();
        dataFeed = createEntity();
    }

    @Test
    public void createDataFeed() throws Exception {
        int databaseSizeBeforeCreate = dataFeedRepository.findAll().size();

        // Create the DataFeed
        restDataFeedMockMvc.perform(post("/api/data-feeds")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dataFeed)))
            .andExpect(status().isCreated());

        // Validate the DataFeed in the database
        List<DataFeed> dataFeedList = dataFeedRepository.findAll();
        assertThat(dataFeedList).hasSize(databaseSizeBeforeCreate + 1);
        DataFeed testDataFeed = dataFeedList.get(dataFeedList.size() - 1);
        assertThat(testDataFeed.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testDataFeed.getDomain()).isEqualTo(DEFAULT_DOMAIN);
        assertThat(testDataFeed.isFetched()).isEqualTo(DEFAULT_FETCHED);
        assertThat(testDataFeed.getCreated()).isEqualTo(DEFAULT_CREATED);
        assertThat(testDataFeed.getEngine()).isEqualTo(DEFAULT_ENGINE);
        assertThat(testDataFeed.getData()).isEqualTo(DEFAULT_DATA);
        assertThat(testDataFeed.getHtml()).isEqualTo(DEFAULT_HTML);
        assertThat(testDataFeed.getLinks()).isEqualTo(DEFAULT_LINKS);
        assertThat(testDataFeed.getLevel()).isEqualTo(DEFAULT_LEVEL);
        assertThat(testDataFeed.getFileName()).isEqualTo(DEFAULT_FILE_NAME);

        // Validate the DataFeed in Elasticsearch
        verify(mockDataFeedSearchRepository, times(1)).save(testDataFeed);
    }

    @Test
    public void createDataFeedWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = dataFeedRepository.findAll().size();

        // Create the DataFeed with an existing ID
        dataFeed.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        restDataFeedMockMvc.perform(post("/api/data-feeds")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dataFeed)))
            .andExpect(status().isBadRequest());

        // Validate the DataFeed in the database
        List<DataFeed> dataFeedList = dataFeedRepository.findAll();
        assertThat(dataFeedList).hasSize(databaseSizeBeforeCreate);

        // Validate the DataFeed in Elasticsearch
        verify(mockDataFeedSearchRepository, times(0)).save(dataFeed);
    }

    @Test
    public void getAllDataFeeds() throws Exception {
        // Initialize the database
        dataFeedRepository.save(dataFeed);

        // Get all the dataFeedList
        restDataFeedMockMvc.perform(get("/api/data-feeds?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dataFeed.getId())))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL.toString())))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())))
            .andExpect(jsonPath("$.[*].fetched").value(hasItem(DEFAULT_FETCHED.booleanValue())))
            .andExpect(jsonPath("$.[*].created").value(hasItem(DEFAULT_CREATED.toString())))
            .andExpect(jsonPath("$.[*].engine").value(hasItem(DEFAULT_ENGINE.toString())))
            .andExpect(jsonPath("$.[*].data").value(hasItem(DEFAULT_DATA.toString())))
            .andExpect(jsonPath("$.[*].html").value(hasItem(DEFAULT_HTML.toString())))
            .andExpect(jsonPath("$.[*].links").value(hasItem(DEFAULT_LINKS.toString())))
            .andExpect(jsonPath("$.[*].level").value(hasItem(DEFAULT_LEVEL)))
            .andExpect(jsonPath("$.[*].fileName").value(hasItem(DEFAULT_FILE_NAME.toString())));
    }
    
    @Test
    public void getDataFeed() throws Exception {
        // Initialize the database
        dataFeedRepository.save(dataFeed);

        // Get the dataFeed
        restDataFeedMockMvc.perform(get("/api/data-feeds/{id}", dataFeed.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(dataFeed.getId()))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL.toString()))
            .andExpect(jsonPath("$.domain").value(DEFAULT_DOMAIN.toString()))
            .andExpect(jsonPath("$.fetched").value(DEFAULT_FETCHED.booleanValue()))
            .andExpect(jsonPath("$.created").value(DEFAULT_CREATED.toString()))
            .andExpect(jsonPath("$.engine").value(DEFAULT_ENGINE.toString()))
            .andExpect(jsonPath("$.data").value(DEFAULT_DATA.toString()))
            .andExpect(jsonPath("$.html").value(DEFAULT_HTML.toString()))
            .andExpect(jsonPath("$.links").value(DEFAULT_LINKS.toString()))
            .andExpect(jsonPath("$.level").value(DEFAULT_LEVEL))
            .andExpect(jsonPath("$.fileName").value(DEFAULT_FILE_NAME.toString()));
    }

    @Test
    public void getNonExistingDataFeed() throws Exception {
        // Get the dataFeed
        restDataFeedMockMvc.perform(get("/api/data-feeds/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateDataFeed() throws Exception {
        // Initialize the database
        dataFeedService.save(dataFeed);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockDataFeedSearchRepository);

        int databaseSizeBeforeUpdate = dataFeedRepository.findAll().size();

        // Update the dataFeed
        DataFeed updatedDataFeed = dataFeedRepository.findById(dataFeed.getId()).get();
        updatedDataFeed
            .url(UPDATED_URL)
            .domain(UPDATED_DOMAIN)
            .fetched(UPDATED_FETCHED)
            .created(UPDATED_CREATED)
            .engine(UPDATED_ENGINE)
            .data(UPDATED_DATA)
            .html(UPDATED_HTML)
            .links(UPDATED_LINKS)
            .level(UPDATED_LEVEL)
            .fileName(UPDATED_FILE_NAME);

        restDataFeedMockMvc.perform(put("/api/data-feeds")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedDataFeed)))
            .andExpect(status().isOk());

        // Validate the DataFeed in the database
        List<DataFeed> dataFeedList = dataFeedRepository.findAll();
        assertThat(dataFeedList).hasSize(databaseSizeBeforeUpdate);
        DataFeed testDataFeed = dataFeedList.get(dataFeedList.size() - 1);
        assertThat(testDataFeed.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testDataFeed.getDomain()).isEqualTo(UPDATED_DOMAIN);
        assertThat(testDataFeed.isFetched()).isEqualTo(UPDATED_FETCHED);
        assertThat(testDataFeed.getCreated()).isEqualTo(UPDATED_CREATED);
        assertThat(testDataFeed.getEngine()).isEqualTo(UPDATED_ENGINE);
        assertThat(testDataFeed.getData()).isEqualTo(UPDATED_DATA);
        assertThat(testDataFeed.getHtml()).isEqualTo(UPDATED_HTML);
        assertThat(testDataFeed.getLinks()).isEqualTo(UPDATED_LINKS);
        assertThat(testDataFeed.getLevel()).isEqualTo(UPDATED_LEVEL);
        assertThat(testDataFeed.getFileName()).isEqualTo(UPDATED_FILE_NAME);

        // Validate the DataFeed in Elasticsearch
        verify(mockDataFeedSearchRepository, times(1)).save(testDataFeed);
    }

    @Test
    public void updateNonExistingDataFeed() throws Exception {
        int databaseSizeBeforeUpdate = dataFeedRepository.findAll().size();

        // Create the DataFeed

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDataFeedMockMvc.perform(put("/api/data-feeds")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dataFeed)))
            .andExpect(status().isBadRequest());

        // Validate the DataFeed in the database
        List<DataFeed> dataFeedList = dataFeedRepository.findAll();
        assertThat(dataFeedList).hasSize(databaseSizeBeforeUpdate);

        // Validate the DataFeed in Elasticsearch
        verify(mockDataFeedSearchRepository, times(0)).save(dataFeed);
    }

    @Test
    public void deleteDataFeed() throws Exception {
        // Initialize the database
        dataFeedService.save(dataFeed);

        int databaseSizeBeforeDelete = dataFeedRepository.findAll().size();

        // Delete the dataFeed
        restDataFeedMockMvc.perform(delete("/api/data-feeds/{id}", dataFeed.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<DataFeed> dataFeedList = dataFeedRepository.findAll();
        assertThat(dataFeedList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the DataFeed in Elasticsearch
        verify(mockDataFeedSearchRepository, times(1)).deleteById(dataFeed.getId());
    }

    @Test
    public void searchDataFeed() throws Exception {
        // Initialize the database
        dataFeedService.save(dataFeed);
        when(mockDataFeedSearchRepository.search(queryStringQuery("id:" + dataFeed.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(dataFeed), PageRequest.of(0, 1), 1));
        // Search the dataFeed
        restDataFeedMockMvc.perform(get("/api/_search/data-feeds?query=id:" + dataFeed.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dataFeed.getId())))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN)))
            .andExpect(jsonPath("$.[*].fetched").value(hasItem(DEFAULT_FETCHED.booleanValue())))
            .andExpect(jsonPath("$.[*].created").value(hasItem(DEFAULT_CREATED.toString())))
            .andExpect(jsonPath("$.[*].engine").value(hasItem(DEFAULT_ENGINE)))
            .andExpect(jsonPath("$.[*].data").value(hasItem(DEFAULT_DATA)))
            .andExpect(jsonPath("$.[*].html").value(hasItem(DEFAULT_HTML)))
            .andExpect(jsonPath("$.[*].links").value(hasItem(DEFAULT_LINKS)))
            .andExpect(jsonPath("$.[*].level").value(hasItem(DEFAULT_LEVEL)))
            .andExpect(jsonPath("$.[*].fileName").value(hasItem(DEFAULT_FILE_NAME)));
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DataFeed.class);
        DataFeed dataFeed1 = new DataFeed();
        dataFeed1.setId("id1");
        DataFeed dataFeed2 = new DataFeed();
        dataFeed2.setId(dataFeed1.getId());
        assertThat(dataFeed1).isEqualTo(dataFeed2);
        dataFeed2.setId("id2");
        assertThat(dataFeed1).isNotEqualTo(dataFeed2);
        dataFeed1.setId(null);
        assertThat(dataFeed1).isNotEqualTo(dataFeed2);
    }
}
