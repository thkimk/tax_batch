package com.hanwha.tax.batch.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;


@Slf4j
public class MydataJob extends BaseJob {

    @Override
	protected void doExecute(JobExecutionContext context) throws JobExecutionException {
		System.out.println("doExecute......");
		log.error("aaaaaaa");
	}
}
