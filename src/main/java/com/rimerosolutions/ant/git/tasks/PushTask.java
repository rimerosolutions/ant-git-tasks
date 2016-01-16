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

import java.util.Arrays;
import java.util.List;

import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;

import com.rimerosolutions.ant.git.AbstractGitRepoAwareTask;
import com.rimerosolutions.ant.git.GitBuildException;
import com.rimerosolutions.ant.git.GitTaskUtils;

/**
 * Push changes to a remote repository.
 *
 * <pre>{@code
 * <git:git directory="${testLocalRepoClone}" verbose="true" settingsRef="git.testing">
 *  <git:add>
 *   <fileset dir="${testLocalRepoClone}" includes="*.txt"/>
 *  </git:add>
 *  <git:commit message="${dummy.commit.message}"/>
 *  <git:uptodate failOnError="true"/>
 *  <git:push uri="file://${testLocalRepo}"/>
 * </git:git>}</pre>
 *
 * <p><a href="http://www.kernel.org/pub/software/scm/git/docs/git-push.html">Git documentation about push</a></p>
 * <p><a href="http://download.eclipse.org/jgit/docs/latest/apidocs/org/eclipse/jgit/api/PushCommand.html">JGit PushCommand</a></p>
 *
 * @author Yves Zoundi
 */
public class PushTask extends AbstractGitRepoAwareTask {

        private String pushFailedProperty;
        private boolean includeTags = true;
        private boolean forcePush = false;
        private String deleteRemoteBranch;
        private static final String TASK_NAME = "git-push";
        private static final String PUSH_FAILED_MESSAGE = "Push failed.";
        private static final String DEFAULT_REFSPEC_STRING = "+" + Constants.R_HEADS + "*:" + Constants.R_REMOTES + Constants.DEFAULT_REMOTE_NAME + "/*";

        @Override
        public String getName() {
                return TASK_NAME;
        }

        /**
         * Whether or not to include all tags while pushing
         *
         * @antdoc.notrequired
         * @param includeTags Default is true
         */
        public void setIncludeTags(boolean includeTags) {
                this.includeTags = includeTags;
        }

        /**
         * Whether or not to force push
         *
         * @antdoc.notrequired
         * @param forcePush Default is false
         */
        public void setForcePush(boolean forcePush) { this.forcePush = forcePush; }

        /**
         * Sets a boolean property if the git push fails
         *
         * @antdoc.notrequired
         * @param pushFailedProperty Property to set
         */
        public void setPushFailedProperty(String pushFailedProperty) {
                this.pushFailedProperty = pushFailedProperty;
        }

        /**
         * Sets a remote branch to delete.
         * @antdoc.notrequired
         * @param deleteRemoteBranch Locally deleted branch to delete remotely
         */
        public void setDeleteRemoteBranch(String deleteRemoteBranch) {
                this.deleteRemoteBranch = deleteRemoteBranch;
        }

        @Override
        protected void doExecute() {
                try {
                        StoredConfig config = git.getRepository().getConfig();
                        List<RemoteConfig> remoteConfigs = RemoteConfig.getAllRemoteConfigs(config);

                        if (remoteConfigs.isEmpty()) {
                                URIish uri = new URIish(getUri());

                                RemoteConfig remoteConfig = new RemoteConfig(config, Constants.DEFAULT_REMOTE_NAME);

                                remoteConfig.addURI(uri);
                                remoteConfig.addFetchRefSpec(new RefSpec(DEFAULT_REFSPEC_STRING));
                                remoteConfig.addPushRefSpec(new RefSpec(DEFAULT_REFSPEC_STRING));
                                remoteConfig.update(config);

                                config.save();
                        }

                        String currentBranch = git.getRepository().getBranch();
                        List<RefSpec> specs = Arrays.asList(new RefSpec(currentBranch + ":" + currentBranch));

                        if (deleteRemoteBranch != null) {
                                specs = Arrays.asList(new RefSpec(":" + Constants.R_HEADS + deleteRemoteBranch));
                        }

                        PushCommand pushCommand = git.push().
                                        setPushAll().
                                        setRefSpecs(specs).
                                        setDryRun(false).
                                        setForce(forcePush);

                        if (getUri() != null) {
                                pushCommand.setRemote(getUri());
                        }

                        setupCredentials(pushCommand);

                        if (includeTags) {
                                pushCommand.setPushTags();
                        }

                        if (getProgressMonitor() != null) {
                                pushCommand.setProgressMonitor(getProgressMonitor());
                        }

                        Iterable<PushResult> pushResults = pushCommand.call();

                        for (PushResult pushResult : pushResults) {
                                log(pushResult.getMessages());
                                GitTaskUtils.validateRemoteRefUpdates(PUSH_FAILED_MESSAGE, pushResult.getRemoteUpdates());
                                GitTaskUtils.validateTrackingRefUpdates(PUSH_FAILED_MESSAGE, pushResult.getTrackingRefUpdates());
                        }
                } catch (Exception e) {
                        if (pushFailedProperty != null) {
                                getProject().setProperty(pushFailedProperty, e.getMessage());
                        }

                        throw new GitBuildException(PUSH_FAILED_MESSAGE, e);
                }
        }
}
