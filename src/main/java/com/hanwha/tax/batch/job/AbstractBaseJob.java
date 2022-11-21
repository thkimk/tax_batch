package com.hanwha.tax.batch.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public abstract class AbstractBaseJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        doExecute(context);
    }

    protected abstract void doExecute(JobExecutionContext context) throws JobExecutionException;

}
