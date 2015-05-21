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

import com.rimerosolutions.ant.git.AbstractGitRepoAwareTask;
import com.rimerosolutions.ant.git.GitBuildException;
import org.apache.tools.ant.BuildException;
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.api.MergeResult;

/**
 * Merge changes from a repository.
 *
 * <pre>{@code
 * <git:git directory="${testLocalRepoClient}" verbose="true" settingsRef="git.testing">
 *  <git:merge branchname="branchToMergeFrom"/>
 * </git:git>}</pre>
 *
 * <p><a href="http://www.kernel.org/pub/software/scm/git/docs/git-merge.html">Git documentation about merge</a></p>
 * <p><a href="http://download.eclipse.org/jgit/docs/latest/apidocs/org/eclipse/jgit/api/MergeCommand.html">JGit MergeCommand</a></p>
 *
 * @author Jonas Olsson
 */
public class MergeTask extends AbstractGitRepoAwareTask {

        private static final String TASK_NAME = "git-merge";
        private static final String MESSAGE_MERGE_FAILED = "Merge failed.";
        private static final String MESSAGE_MERGE_FAILED_WITH_STATUS = "Merge failed, status '%s'.";
        private static final String MESSAGE_MERGE_FAILED_WITH_URI = "Could not merge from uri '%s'.";
        private boolean squash = false;
        private String branchname = null;

        @Override
        public String getName() {
                return TASK_NAME;
        }

        /**
        * Set if merge should be squashed
        *
        * @param squash Whether or not to squash
        * @antdoc.notrequired
        */
        public void setSquash(boolean squash) {
                this.squash = squash;
        }

        /**
         * Set branch to merge
         *
         * @param branchname Branch to merge
         */
        public void setBranchname(String branchname) {
                this.branchname = branchname;
        }

        @Override
        public void doExecute() {
                try {
                        MergeCommand mergeCommand = git.merge().setSquash(squash);
                        mergeCommand.include(mergeCommand.getRepository().getRef(branchname));

                        setupCredentials(mergeCommand);
                        MergeResult mergeResult = mergeCommand.call();

                        if (!mergeResult.getMergeStatus().isSuccessful()) {

                                if (mergeResult.getFailingPaths() != null && mergeResult.getFailingPaths().size() > 0) {
                                        throw new BuildException(String.format("%s - Failing paths: %s", MESSAGE_MERGE_FAILED, mergeResult.getFailingPaths()));
                                }

                                throw new BuildException(String.format(MESSAGE_MERGE_FAILED_WITH_STATUS, mergeResult.getMergeStatus().name()));
                        }
                } catch (Exception e) {
                        throw new GitBuildException(String.format(MESSAGE_MERGE_FAILED_WITH_URI, getUri()), e);
                }
        }

}
