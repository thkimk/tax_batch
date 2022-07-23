package com.hanwha.tax.apiserver.config;
//import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;

//@Configuration
public class QuerydslConfiguration {
    @Autowired
    EntityManager em;

//    @Bean
//    public JPAQueryFactory jpaQueryFactory() {
//        return new JPAQueryFactory(em);
//    }
}