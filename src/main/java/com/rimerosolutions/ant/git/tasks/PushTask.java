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

import java.util.List;

import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RemoteConfig;

import com.rimerosolutions.ant.git.GitBuildException;
import com.rimerosolutions.ant.git.GitUtils;


/**
 * Pushes a Git tag to a remote repository
 *
 * <a href="http://www.kernel.org/pub/software/scm/git/docs/git-push.html">Git documentation about push</a>
 *
 * <a href="http://download.eclipse.org/jgit/docs/latest/apidocs/org/eclipse/jgit/api/PushCommand.html">JGit PushCommand javadoc</a>
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

        public void setIncludeTags(boolean includeTags) {
                this.includeTags = includeTags;
        }

        public void setPushFailedProperty(String pushFailedProperty) {
                this.pushFailedProperty = pushFailedProperty;
        }

        @Override
        protected void doExecute() {
                try {
                        List<RemoteConfig> remoteConfigs = RemoteConfig.getAllRemoteConfigs(git.getRepository().getConfig());

                        if (remoteConfigs != null && !remoteConfigs.isEmpty()) {
                                log("Pushing tags");

                                PushCommand pushCommand = git.push();

                                setupCredentials(pushCommand);


                                if (includeTags) {
                                        pushCommand.setPushTags();
                                }

                                Iterable<PushResult> pushResults = pushCommand.setForce(true).call();

                                for (PushResult pushResult : pushResults) {
                                        GitUtils.validateTrackingRefUpdates("Push failed",  pushResult.getTrackingRefUpdates());
                                }
                        }

                } catch (Exception e) {
                        if (pushFailedProperty != null) {
                                getProject().setProperty(pushFailedProperty, e.getMessage());
                        }

                        throw new GitBuildException("Git push failed", e);
                }
        }
}
