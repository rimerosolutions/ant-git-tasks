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

import java.io.File;
import java.util.Arrays;

import org.apache.tools.ant.BuildException;
import org.eclipse.jgit.api.CloneCommand;

import smartrics.ant.git.AbstractGitTask;

public class CloneTask extends AbstractGitTask {

        private String branchToTrack;
        private boolean bare = false;
        private boolean cloneSubModules = true;
        private boolean cloneAllBranches = true;
        private boolean noCheckout = false;
        private String  branchesToCloneCommaSeparated;


        public void setBranchesToCloneCommaSeparated(String branchesToCloneCommaSeparated) {
                this.branchesToCloneCommaSeparated = branchesToCloneCommaSeparated;
        }
        
        public void setBranchToTrack(String branchToTrack) {
                this.branchToTrack = branchToTrack;
        }

        public void setBare(boolean bare) {
                this.bare = bare;
        }

        public void setCloneSubModules(boolean cloneSubModules) {
                this.cloneSubModules = cloneSubModules;
        }

        public void setCloneAllBranches(boolean cloneAllBranches) {
                this.cloneAllBranches = cloneAllBranches;
        }

        public void setNoCheckout(boolean noCheckout) {
                this.noCheckout = noCheckout;
        }
        
        @Override
        public void execute() {

                try {
                        CloneCommand clone = new CloneCommand();
                        if (branchToTrack != null) {
                                clone.setBranch(branchToTrack);
                        }
                        
                        if (branchesToCloneCommaSeparated != null) {
                                String[] branchNames = branchesToCloneCommaSeparated.split(",");
                                
                                if (branchNames.length > 0) {
                                        clone.setBranchesToClone(Arrays.asList(branchNames));
                                }
                        }
                        
                        clone.setURI(getUri()).setBare(bare).setCloneAllBranches(cloneAllBranches).setCloneSubmodules(cloneSubModules)
                                        .setNoCheckout(noCheckout).setDirectory(new File(getDirectory().getAbsolutePath()))
                                        .setProgressMonitor(getProgressMonitor());
                        clone.call();
                } catch (Exception e) {
                        e.printStackTrace();
                        throw new BuildException("Unexpected exception: " + e.getMessage(), e);
                }

        }

}
