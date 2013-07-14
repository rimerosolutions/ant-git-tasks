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
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.errors.GitAPIException;

import com.rimerosolutions.ant.git.GitBuildException;

/**
 * Commits all local changes
 *
 * <p><a href="http://www.kernel.org/pub/software/scm/git/docs/git-commit.html">Git documentation about commit</a></p>
 *
 * <p><a href="http://download.eclipse.org/jgit/docs/latest/apidocs/org/eclipse/jgit/api/CommitCommand.html">JGit CommitCommand</a></p>
 *
 * @author Yves Zoundi
 */
public class CommitTask extends AbstractGitRepoAwareTask {

        private String message = "Commit message";
        private boolean all = true;
        private boolean amend = false;
        private String reflogComment;
        private static final String TASK_NAME = "git-commit";

        @Override
        public String getName() {
                return TASK_NAME;
        }

        @Override
        protected void doExecute() throws BuildException {
                try {
                        setFailOnError(true);
                        CommitCommand cmd = git.commit();

                        if (message != null) {
                                cmd.setMessage(message);
                        }

                        cmd.setAll(all).setAmend(amend);

                        cmd.setAuthor(lookupSettings().getIdentity());

                        if (reflogComment != null) {
                                cmd.setReflogComment(reflogComment);
                        }

                        cmd.call();
                } catch (GitAPIException ex) {
                        throw new GitBuildException("Cannot commit to Git repository", ex);
                }
        }

        /**
         * @param all
         *                the all to set
         */
        public void setAll(boolean all) {
                this.all = all;
        }

        /**
         * @param amend
         *                the amend to set
         */
        public void setAmend(boolean amend) {
                this.amend = amend;
        }

        /**
         * @param reflogComment
         *                the reflogComment to set
         */
        public void setReflogComment(String reflogComment) {
                this.reflogComment = reflogComment;
        }

        public void setMessage(String message) {
                this.message = message;
        }

}
