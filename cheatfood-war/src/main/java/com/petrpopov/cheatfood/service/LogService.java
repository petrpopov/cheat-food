package com.petrpopov.cheatfood.service;

import com.petrpopov.cheatfood.model.entity.LogEntity;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;

/**
 * User: petrpopov
 * Date: 27.08.13
 * Time: 21:40
 */

@Component
public class LogService extends GenericService<LogEntity> {

    @Value("#{properties.clean_logs_period}")
    private Integer cleanPeriod;

    public LogService() {
        super(LogEntity.class);
        logger = Logger.getLogger(LogService.class);
    }

    @PostConstruct
    public void cleanLogs() {

        logger.info("Cleaning logs older than " + cleanPeriod + " days before today...");

        DateTime today = new DateTime(new Date());
        today = today.minusDays(cleanPeriod);

        Criteria criteria = Criteria.where("dateTime").lte(today.toDate());
        Query query = new Query(criteria);

        op.remove(query, LogEntity.class);
    }
}
