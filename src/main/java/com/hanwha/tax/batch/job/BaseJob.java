package com.hanwha.tax.batch.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public abstract class BaseJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
    }

    protected abstract void doExecute(JobExecutionContext context) throws JobExecutionException;

}
