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

import java.io.IOException;

import com.rimerosolutions.ant.git.AbstractGitRepoAwareTask;
import com.rimerosolutions.ant.git.GitBuildException;
import com.rimerosolutions.ant.git.GitSettings;
import com.rimerosolutions.ant.git.GitTaskUtils;
import com.rimerosolutions.ant.git.MissingRequiredGitSettingsException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.resources.Union;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;

/**
 * Commits all local changes.
 *
 * <pre>{@code
 *  <git:git directory="${testLocalRepo}">
 *   <git:commit message="${dummy.commit.message}" revCommitIdProperty="testAdd.revcommit"/>
 *  </git:git>}</pre>
 *
 * <p><a href="http://www.kernel.org/pub/software/scm/git/docs/git-commit.html">Git documentation about commit</a></p>
 * <p><a href="http://download.eclipse.org/jgit/docs/latest/apidocs/org/eclipse/jgit/api/CommitCommand.html">JGit CommitCommand</a></p>
 *
 * @author Yves Zoundi
 */
public class CommitTask extends AbstractGitRepoAwareTask {

        private static final String TASK_NAME = "git-commit";
        private static final String MESSAGE_COMMIT_FAILED = "Cannot commit to Git repository";
        private String message = "Commit message";
        private boolean amend = false;
        private String reflogComment;
        private String revCommitIdProperty;
        private String only;
        private boolean brandedMessage = true;
        private Union path;

        @Override
        public String getName() {
                return TASK_NAME;
        }

        /**
         * Commit dedicated path only This method can be called several times to add multiple paths.
         *
         * @antdoc.notrequired
         * @param only A dedicated path to commit
         */
        public void setOnly(String only) {
                this.only = only;
        }

        /**
         * Prefix commit message with [ant-git-tasks} brand.
         * @param brandedMessage Flag to use branded message
         */
        public void setBrandedMessage(boolean brandedMessage) {
                this.brandedMessage = brandedMessage;
        }

        /**
         * Configure the fileset(s) of files to add to revision control
         *
         * @param fileset The fileset to add
         */
        public void addFileset(FileSet fileset) {
                getPath().add(fileset);
        }

        private synchronized Union getPath() {
                if (path == null) {
                        path = new Union();
                        path.setProject(getProject());
                }
                return path;
        }

        /**
         * Assign the commit revision id to a property
         *
         * @antdoc.notrequired
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

                        if (!GitTaskUtils.isNullOrBlankString(message)) {
                                cmd.setMessage(brandedMessage ? GitTaskUtils.BRANDING_MESSAGE + " " : "" + message);
                        }
                        else {
                                cmd.setMessage(GitTaskUtils.BRANDING_MESSAGE);
                        }

                        String prefix = getDirectory().getCanonicalPath();
                        String[] allFiles = getPath().list();

                        if (!GitTaskUtils.isNullOrBlankString(only)) {
                                cmd.setOnly(only);
                        }
                        else if (allFiles.length > 0) {
                                for (String file : allFiles) {
                                        String modifiedFile = translateFilePathUsingPrefix(file, prefix);
                                        log("Will commit " + modifiedFile);
                                        cmd.setOnly(modifiedFile);
                                }
                        }
                        else {
                                cmd.setAll(true);
                        }

                        GitSettings gitSettings = lookupSettings();

                        if (gitSettings == null) {
                                throw new MissingRequiredGitSettingsException();
                        }

                        cmd.setAmend(amend).setAuthor(gitSettings.getIdentity()).setCommitter(gitSettings.getIdentity());

                        if (reflogComment != null) {
                                cmd.setReflogComment(reflogComment);
                        }

                        RevCommit revCommit = cmd.call();

                        if (revCommitIdProperty != null) {
                                String revisionId = ObjectId.toString(revCommit.getId());
                                getProject().setProperty(revCommitIdProperty, revisionId);
                        }

                        log(revCommit.getFullMessage());
                } catch (IOException ioe) {
                        throw new GitBuildException(MESSAGE_COMMIT_FAILED, ioe);
                } catch (GitAPIException ex) {
                        throw new GitBuildException(MESSAGE_COMMIT_FAILED, ex);
                }
        }

        /**
         * Used to amend the tip of the current branch.
         *
         * @antdoc.notrequired
         * @param amend Whether or not to amend the tip for the current branch
         */
        public void setAmend(boolean amend) {
                this.amend = amend;
        }

        /**
         * Override the default reflogComment
         *
         * @antdoc.notrequired
         * @param reflogComment the reflogComment to set
         */
        public void setReflogComment(String reflogComment) {
                this.reflogComment = reflogComment;
        }

        /**
         * Sets the commit message
         *
         * @antdoc.notrequired
         * @param message The commit message
         */
        public void setMessage(String message) {
                this.message = message;
        }

}
