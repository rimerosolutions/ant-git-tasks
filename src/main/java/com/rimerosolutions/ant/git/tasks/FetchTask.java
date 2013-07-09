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

public class FetchTask extends AbstractGitRepoAwareTask {
        private boolean dryRun = true;
        private boolean removeDeletedRefs = true;
        private boolean thinPack = false;
        
        /**
         * Sets the thin-pack preference for fetch operation.
         * 
         * @param thinPack
         */
        public void setThinPack(boolean thinPack) {
                this.thinPack = thinPack;
        }
        
        /**
         * (Default value is true)
         * @param removeDeletedRefs 
         */
        public void setRemoveDeletedRefs(boolean removeDeletedRefs) {
                this.removeDeletedRefs = removeDeletedRefs;
        }

        /**
         * (Default value is true)
         * @param dryRun
         */
        public void setDryRun(boolean dryRun) {
                this.dryRun = dryRun;
        }

        @Override
        public void doExecute() {

                try {
                        FetchCommand cmd = Git.wrap(repo).fetch().setDryRun(dryRun).setThin(thinPack).setRemote(getUri()).setRemoveDeletedRefs(removeDeletedRefs);
                        
                        if (getProgressMonitor() != null) {
                                cmd.setProgressMonitor(getProgressMonitor());
                        }
                        
                        cmd.call();
                } catch (Exception e) {
                        e.printStackTrace();
                        throw new BuildException("Unexpected exception: " + e.getMessage(), e);
                }

        }

}
