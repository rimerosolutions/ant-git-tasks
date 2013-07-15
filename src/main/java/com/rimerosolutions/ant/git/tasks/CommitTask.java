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
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.lib.ObjectId;
import com.rimerosolutions.ant.git.GitBuildException;
import com.rimerosolutions.ant.git.GitUtils;

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

        private static final String TASK_NAME = "git-commit";

        private String message = "Commit message";
        private boolean all = true;
        private boolean amend = false;
        private String reflogComment;
        private String revCommitIdProperty;

        @Override
        public String getName() {
                return TASK_NAME;
        }

        /**
         * Assign the commit revision id to a property
         *
         * @param revCommitIdProperty The property to set the commit id value to
         */
        public void setRevCommitIdProperty(String revCommitIdProperty) {
                this.revCommitIdProperty = revCommitIdProperty;
        }

        @Override
        protected void doExecute() throws BuildException {
                try {
                        setFailOnError(true);
                        CommitCommand cmd = git.commit();

                        if (message != null) {
                                cmd.setMessage(GitUtils.BRANDING_MESSAGE + " " + message);
                        }

                        cmd.setAll(all).setAmend(amend);

                        cmd.setAuthor(lookupSettings().getIdentity());

                        if (reflogComment != null) {
                                cmd.setReflogComment(reflogComment);
                        }

                        RevCommit revCommit = cmd.call();

                        if (revCommitIdProperty != null) {
                                getProject().setProperty(revCommitIdProperty, ObjectId.toString(revCommit.getId()));
                        }
                } catch (GitAPIException ex) {
                        throw new GitBuildException("Cannot commit to Git repository", ex);
                }
        }

        /**
         * Whether or not to commit everything
         *
         * @param all Commit all files
         */
        public void setAll(boolean all) {
                this.all = all;
        }

        /**
         * Used to amend the tip of the current branch.
         *
         * @param amend Whether or not to amend the tip for the current branch
         */
        public void setAmend(boolean amend) {
                this.amend = amend;
        }

        /**
         * Override the default reflogComment
         *
         * @param reflogComment the reflogComment to set
         */
        public void setReflogComment(String reflogComment) {
                this.reflogComment = reflogComment;
        }

        /**
         * Sets the commit message
         *
         * @param message The commit message
         */
        public void setMessage(String message) {
                this.message = message;
        }

}
