/*
 * Copyright 2013 Rimero Solutions
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rimerosolutions.ant.git.tasks;

import java.util.List;
import java.util.ArrayList;
import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.RefSpec;
import com.rimerosolutions.ant.git.AbstractGitRepoAwareTask;
import com.rimerosolutions.ant.git.GitBuildException;
import com.rimerosolutions.ant.git.GitTaskUtils;

/**
 * Git fetch Ant task
 *
 * <p><a href="http://www.kernel.org/pub/software/scm/git/docs/git-fetch.html">Git documentation about fetch</a></p>
 *
 * <p><a href="http://download.eclipse.org/jgit/docs/latest/apidocs/org/eclipse/jgit/api/FetchCommand.html">JGit FetchCommand</a></p>
 *
 * @author Yves Zoundi
 */
public class FetchTask extends AbstractGitRepoAwareTask {
        private boolean dryRun = false;
        private boolean removeDeletedRefs = true;
        private boolean thinPack = true;
        private static final String TASK_NAME = "git-fetch";
        private static final String FETCH_FAILED_MESSAGE = "Fetch failed";

        @Override
        public String getName() {
                return TASK_NAME;
        }

        /**
         * Sets the thin-pack preference for fetch operation.
         * 
         * @param thinPack (Default value is true)
         */
        public void setThinPack(boolean thinPack) {
                this.thinPack = thinPack;
        }

        /**
         * If set to true, refs are removed which no longer exist in the source
         * 
         * @param removeDeletedRefs (Default value is true)
         */
        public void setRemoveDeletedRefs(boolean removeDeletedRefs) {
                this.removeDeletedRefs = removeDeletedRefs;
        }

        /**
         * Sets whether the fetch operation should be a dry run
         * 
         * @param dryRun (Default value is true)
         */
        public void setDryRun(boolean dryRun) {
                this.dryRun = dryRun;
        }

        @Override
        public void doExecute() {
                try {

                        List<RefSpec> specs = new ArrayList<RefSpec>(3);
                        specs.add(new RefSpec("+refs/heads/*:refs/remotes/origin/*"));
                        specs.add(new RefSpec("+refs/notes/*:refs/notes/*"));
                        specs.add(new RefSpec("+refs/tags/*:refs/tags/*"));

                        FetchCommand fetchCommand = git.fetch().
                                        setDryRun(dryRun).
                                        setThin(thinPack).
                                        setRemote(getUri()).
                                        setRefSpecs(specs).
                                        setRemoveDeletedRefs(removeDeletedRefs);

                        setupCredentials(fetchCommand);

                        if (getProgressMonitor() != null) {
                                fetchCommand.setProgressMonitor(getProgressMonitor());
                        }

                        FetchResult fetchResult = fetchCommand.call();
                        
                        GitTaskUtils.validateTrackingRefUpdates(FETCH_FAILED_MESSAGE, fetchResult.getTrackingRefUpdates());
                        
                        log(fetchResult.getMessages());
                } catch (Exception e) {
                        throw new GitBuildException("Unexpected exception: " + e.getMessage(), e);
                }
        }

}
