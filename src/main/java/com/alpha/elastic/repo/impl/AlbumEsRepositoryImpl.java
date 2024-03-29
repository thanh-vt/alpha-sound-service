package com.alpha.elastic.repo.impl;

import com.alpha.elastic.model.AlbumEs;
import com.alpha.elastic.repo.AlbumEsRepositoryCustom;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Repository;

/**
 * @author thanhvt
 * @created 10/7/2021 - 2:30 AM
 * @project vengeance
 * @since 1.0
 **/
@Repository
public class AlbumEsRepositoryImpl implements AlbumEsRepositoryCustom {

    private final ElasticsearchOperations elasticsearchRestTemplate;

    @Autowired
    public AlbumEsRepositoryImpl(
        ElasticsearchOperations elasticsearchRestTemplate) {
        this.elasticsearchRestTemplate = elasticsearchRestTemplate;
    }

    @Override
    public Page<AlbumEs> findPageByName(String name, Pageable pageable) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        BoolQueryBuilder boolQueryBuilder2 = QueryBuilders.boolQuery();
        String[] tokens = name.split("\\s+");
        for (String token : tokens) {
            boolQueryBuilder
                .should(QueryBuilders
                    .wildcardQuery("unaccentTitle", "*" + token + "*"));
            boolQueryBuilder
                .should(QueryBuilders
                    .termQuery("tags", token));
            boolQueryBuilder2
                .should(QueryBuilders
                    .wildcardQuery("artists.unaccentName", "*" + token + "*"));
        }
        boolQueryBuilder.should(QueryBuilders.nestedQuery("artists", boolQueryBuilder2, ScoreMode.None));
        SearchHits<AlbumEs> searchHits = this.elasticsearchRestTemplate
            .search(queryBuilder.withQuery(boolQueryBuilder).build(), AlbumEs.class);
        return SearchHitSupport.searchPageFor(searchHits, pageable).map(SearchHit::getContent);
    }
}
