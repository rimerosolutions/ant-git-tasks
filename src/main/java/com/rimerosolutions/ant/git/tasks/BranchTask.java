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
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;

import com.rimerosolutions.ant.git.AbstractGitRepoAwareTask;
import com.rimerosolutions.ant.git.GitBuildException;

/**
 * Create a branch or switch an existing one.
 *
 * <p><a href="http://www.kernel.org/pub/software/scm/git/docs/git-branch.html">Git documentation about branch</a></p>
 * <p>See <a href="http://download.eclipse.org/jgit/docs/latest/apidocs/index.html?org/eclipse/jgit/api/CreateBranchCommand.html">JGit CreateBranchCommand</a></p>
 *
 * @author Yves Zoundi
 */
public class BranchTask extends AbstractGitRepoAwareTask {

        private static final String TASK_NAME = "git-branch-add";
        private String branchName;
        private boolean force = true;
        private CreateBranchCommand.SetupUpstreamMode upstreamMode = CreateBranchCommand.SetupUpstreamMode.TRACK;

        @Override
        public String getName() {
                return TASK_NAME;
        }

        /**
         * Creates a branch name
         *
         * @param branchName the branchName to set
         */
        public void setBranchName(String branchName) {
                this.branchName = branchName;
        }

        /**
         *  if true and the branch with the given name already exists, the start-point of an existing branch will be set to a new start-point;
         *  if false, the existing branch will not be changed
         *
         * @antdoc.notrequired
         * @param force Whether or not to force branch creation (Default false)
         */
        public void setForce(boolean force) {
                this.force = force;
        }

        /**
         * Corresponds to the --track/--no-track/--set-upstream options; may be null
         *
         * @see <a href="http://download.eclipse.org/jgit/docs/latest/apidocs/org/eclipse/jgit/api/CreateBranchCommand.SetupUpstreamMode.html">CreateBranchCommand.SetupUpstreamMode string values</a>
         *
         * @antdoc.notrequired
         * @param upstreamMode the upstreamMode to set (Default is --track).
         */
        public void setUpstreamMode(String upstreamMode) {
                this.upstreamMode = CreateBranchCommand.SetupUpstreamMode.valueOf(upstreamMode);
        }

        @Override
        protected void doExecute() throws BuildException {
                try {
                        git.branchCreate().
                                setForce(force).
                                setUpstreamMode(upstreamMode).
                                setName(branchName).
                                call();
                } catch (GitAPIException e) {
                        throw new GitBuildException(String.format("Could not create branch '%s'.", branchName), e);
                }
        }

}
