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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jgit.api.CloneCommand;

import com.rimerosolutions.ant.git.AbstractGitTask;
import com.rimerosolutions.ant.git.GitBuildException;

/**
 * Clone a repository
 * 
 * <pre>{@code 
 *  <git:git localDirectory="${testLocalRepoClone}">
 *    <git:clone uri="file://${testLocalRepo}/.git" directory="${testLocalRepoClone}"/>
 *  </git:git>}</pre> 
 * 
 * <p><a href="http://www.kernel.org/pub/software/scm/git/docs/git-clone.html">Git documentation about clone</a></p>
 * <p><a href="http://download.eclipse.org/jgit/docs/latest/apidocs/org/eclipse/jgit/api/CloneCommand.html">JGit CloneCommand</a></p>
 *
 * @author Yves Zoundi
 */
public class CloneTask extends AbstractGitTask {

        private String branchToTrack;
        private boolean bare = false;
        private boolean cloneSubModules = true;
        private boolean cloneAllBranches = false;
        private boolean noCheckout = false;
        private List<String> branchNames = new ArrayList<String>();
        private static final String TASK_NAME = "git-clone";

        @Override
        public String getName() {
                return TASK_NAME;
        }
        /**
         * Sets the branch names to clone
         *
         * @param branchNames Comma separated list of branches to clone
         */
        public void setBranchNames(String branchNames) {
                this.branchNames.addAll(Arrays.asList(branchNames.split(",")));

        }

        /**
         * Sets the branch to track
         *
         * @antdoc.notrequired
         * @param branchToTrack The branch to track
         */
        public void setBranchToTrack(String branchToTrack) {
                this.branchToTrack = branchToTrack;
        }

        /**
         * Sets whether or not the repository is bare
         *
         * @antdoc.notrequired
         * @param bare Whether or not the repository is bare (Default false)
         */
        public void setBare(boolean bare) {
                this.bare = bare;
        }

        /**
         * Sets whether or not sub-modules should be cloned
         *
         * @antdoc.notrequired
         * @param cloneSubModules Whether or not to clone sub-modules (Default true)
         */
        public void setCloneSubModules(boolean cloneSubModules) {
                this.cloneSubModules = cloneSubModules;
        }

        /**
         * Whether or not to clone all branches
         * 
         * @antdoc.notrequired
         * @param cloneAllBranches Default is true
         */
        public void setCloneAllBranches(boolean cloneAllBranches) {
                this.cloneAllBranches = cloneAllBranches;
        }

        /**
         * Sets whether or not to checkout any branches
         *
         * @antdoc.notrequired
         * @param noCheckout Whether or not to checkout any branch (Default false)
         */
        public void setNoCheckout(boolean noCheckout) {
                this.noCheckout = noCheckout;
        }

        @Override
        public void execute() {
                try {
                        CloneCommand cloneCommand = new CloneCommand();

                        if (branchToTrack != null) {
                                cloneCommand.setBranch(branchToTrack);
                        }

                        if (!branchNames.isEmpty()) {
                                cloneCommand.setBranchesToClone(branchNames);
                        }

                        cloneCommand.setURI(getUri()).
                                setBare(bare).
                                setCloneAllBranches(cloneAllBranches).
                                setCloneSubmodules(cloneSubModules).
                                setNoCheckout(noCheckout).
                                setDirectory(new File(getDirectory().getAbsolutePath()));

                        setupCredentials(cloneCommand);

                        if (getProgressMonitor() != null) {
                                cloneCommand.setProgressMonitor(getProgressMonitor());
                        }

                        cloneCommand.call();
                }
                catch (Exception e) {
                        throw new GitBuildException(String.format("Could not clone URL '%s'.", getUri()), e);
                }
        }

}
