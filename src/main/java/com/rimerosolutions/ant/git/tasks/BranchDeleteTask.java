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
import org.eclipse.jgit.api.errors.GitAPIException;

import com.rimerosolutions.ant.git.GitBuildException;

/**
 * Delete branches
 *
 * <a href="http://www.kernel.org/pub/software/scm/git/docs/git-branch.html">Git branch documentation</a>
 *
 * See <a href="http://download.eclipse.org/jgit/docs/latest/apidocs/org/eclipse/jgit/api/DeleteBranchCommand.html">JGit DeleteBranchCommand javadoc</a>
 *
 * @author Yves Zoundi
 */
public class BranchDeleteTask extends AbstractGitRepoAwareTask {

        private static final String TASK_NAME = "git-branch-delete";
        private String[] branchNames;

        @Override
        public String getName() {
                return TASK_NAME;
        }

        /**
         * Sets the branches to delete (comma-separated list)
         *
         * @param branches Comma-separted list of branches to delete
         */
        public void setBranches(String branches) {
                this.branchNames = branches.split(",");
        }

        @Override
        protected void doExecute() throws BuildException {
                try {
                        git.branchDelete().setBranchNames(branchNames).call();
                } catch (GitAPIException e) {
                        throw new GitBuildException("Could not delete specified branches", e);
                }
        }

}
