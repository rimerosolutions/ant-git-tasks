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

import org.apache.tools.ant.BuildException;
import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import com.rimerosolutions.ant.git.GitBuildException;

/**
 * Git fetch Ant task
 *
 * @author Yves Zoundi
 */
public class FetchTask extends AbstractGitRepoAwareTask {
        private boolean dryRun = true;
        private boolean removeDeletedRefs = true;
        private boolean thinPack = true;

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
                        FetchCommand cmd = Git.wrap(repo).
                                fetch().
                                setDryRun(dryRun).
                                setThin(thinPack).
                                setRemote(getUri()).
                                setRemoveDeletedRefs(removeDeletedRefs);

                        if (getProgressMonitor() != null) {
                                cmd.setProgressMonitor(getProgressMonitor());
                        }

                        cmd.call();
                } catch (Exception e) {
                        throw new GitBuildException("Unexpected exception: " + e.getMessage(), e);
                }
        }

}
