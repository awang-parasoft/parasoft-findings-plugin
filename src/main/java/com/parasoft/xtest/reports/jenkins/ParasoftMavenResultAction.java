/*
 * $Id$
 *
 * (C) Copyright Parasoft Corporation 2013. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF Parasoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */
package com.parasoft.xtest.reports.jenkins;

import hudson.maven.MavenAggregatedReport;
import hudson.maven.MavenBuild;
import hudson.maven.MavenModule;
import hudson.maven.MavenModuleSet;
import hudson.maven.MavenModuleSetBuild;
import hudson.model.Action;
import hudson.model.AbstractBuild;
import hudson.plugins.analysis.core.HealthDescriptor;
import hudson.plugins.analysis.core.ParserResult;
import hudson.plugins.analysis.core.MavenResultAction;

import java.util.List;
import java.util.Map;

public class ParasoftMavenResultAction 
    extends MavenResultAction<ParasoftResult> 
{
    /**
     * Creates a new instance of {@link ParasoftMavenResultAction}.
     *
     * @param owner the associated build of this action
     * @param healthDescriptor health descriptor to use
     * @param defaultEncoding the default encoding to be used when reading and parsing files
     * @param result the result in this build
     */
    public ParasoftMavenResultAction(AbstractBuild<?, ?> owner, HealthDescriptor healthDescriptor, String defaultEncoding, ParasoftResult result) 
    {
        super(new ParasoftResultAction(owner, healthDescriptor, result), defaultEncoding, ParasoftDescriptor.PLUGIN_ID);
    }

    /**
     * @see hudson.maven.AggregatableAction#createAggregatedAction(hudson.maven.MavenModuleSetBuild, java.util.Map)
     */
    public MavenAggregatedReport createAggregatedAction(MavenModuleSetBuild build, Map<MavenModule, List<MavenBuild>> moduleBuilds) 
    {
        String sDefaultEncoding = getDefaultEncoding();
        ParasoftResult result = new ParasoftResult(build, sDefaultEncoding, new ParserResult(), false);
        return new ParasoftMavenResultAction(build, getHealthDescriptor(), sDefaultEncoding, result);
    }

    /**
     * @see hudson.maven.MavenAggregatedReport#getProjectAction(hudson.maven.MavenModuleSet)
     */
    public Action getProjectAction(MavenModuleSet moduleSet) 
    {
        return new ParasoftProjectAction(moduleSet, ParasoftMavenResultAction.class);
    }

    @Override
    public Class<? extends MavenResultAction<ParasoftResult>> getIndividualActionType() 
    {
        return ParasoftMavenResultAction.class;
    }

    @Override
    protected ParasoftResult createResult(ParasoftResult existingResult, ParasoftResult additionalResult) 
    {
        ParserResult aggregate = aggregate(existingResult, additionalResult);
        String sDefaultEncoding = additionalResult.getDefaultEncoding();
        return new ParasoftReporterResult(getOwner(), sDefaultEncoding, aggregate, existingResult.useOnlyStableBuildsAsReference());
    }
}

