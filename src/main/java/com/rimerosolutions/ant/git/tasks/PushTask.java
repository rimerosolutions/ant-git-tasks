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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;

import com.rimerosolutions.ant.git.AbstractGitRepoAwareTask;
import com.rimerosolutions.ant.git.GitBuildException;
import com.rimerosolutions.ant.git.GitTaskUtils;

/**
 * Pushes a Git tag to a remote repository
 *
 * <p><a href="http://www.kernel.org/pub/software/scm/git/docs/git-push.html">Git documentation about push</a></p>
 *
 * <p><a href="http://download.eclipse.org/jgit/docs/latest/apidocs/org/eclipse/jgit/api/PushCommand.html">JGit PushCommand</a></p>
 *
 * @author Yves Zoundi
 */
public class PushTask extends AbstractGitRepoAwareTask {

        private String pushFailedProperty;
        private boolean includeTags = true;
        private static final String TASK_NAME = "git-push";

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
         * Sets a boolean property if the git push fails
         * 
         * @antdoc.notrequired
         * @param pushFailedProperty Property to set
         */
        public void setPushFailedProperty(String pushFailedProperty) {
                this.pushFailedProperty = pushFailedProperty;
        }

        @Override
        protected void doExecute() {
                try {

                        RemoteConfig remoteConfig = new RemoteConfig(git.getRepository().getConfig(), Constants.DEFAULT_REMOTE_NAME);
                        remoteConfig.addURI(new URIish(getUri()));
                        remoteConfig.addFetchRefSpec(new RefSpec("+refs/heads/*:refs/remotes/origin/*"));
                        remoteConfig.update(git.getRepository().getConfig());

                        git.getRepository().getConfig().setString("receive", null, "denyCurrentBranch", "ignore");
                        git.getRepository().getConfig().save();

                        log("Pushing tags.");

                        List<RefSpec> specs = new ArrayList<RefSpec>(3);

                        specs.add(new RefSpec("+refs/heads/*:refs/remotes/origin/*"));
                        specs.add(new RefSpec("+refs/notes/*:refs/notes/*"));
                        specs.add(new RefSpec("+refs/tags/*:refs/tags/*"));

                        PushCommand pushCommand = git.push().
                                                      setPushAll().
                                                      setRefSpecs(specs).
                                                      setDryRun(false).
                                                      setRemote(getUri());

                        setupCredentials(pushCommand);

                        if (includeTags) {
                                pushCommand.setPushTags();
                        }

                        if (getProgressMonitor() != null) {
                                pushCommand.setProgressMonitor(getProgressMonitor());
                        }

                        Iterable<PushResult> pushResults = pushCommand.setForce(true).call();

                        for (PushResult pushResult : pushResults) {
                                GitTaskUtils.validateTrackingRefUpdates("Push failed", pushResult.getTrackingRefUpdates());
                        }
                } catch (Exception e) {
                        if (pushFailedProperty != null) {
                                getProject().setProperty(pushFailedProperty, e.getMessage());
                        }

                        throw new GitBuildException("Git push failed.", e);
                }
        }
}
