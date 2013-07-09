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

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import com.rimerosolutions.ant.git.GitBuildException;


/**
 * Pushes a Git tag to a remote repository
 * 
 * @author Yves Zoundi
 */
public class PushTask extends AbstractGitRepoAwareTask {

        private String pushFailedProperty;
        private boolean includeTags = true;
        
        public void setIncludeTags(boolean includeTags) {
                this.includeTags = includeTags;
        }

        public void setPushFailedProperty(String pushFailedProperty) {
                this.pushFailedProperty = pushFailedProperty;
        }

        @Override
        protected void doExecute() {
                try {
                        List<RemoteConfig> remoteConfigs = RemoteConfig.getAllRemoteConfigs(repo.getConfig());

                        if (!remoteConfigs.isEmpty()) { 
                               
                                log("Pushing tags");
                                
                                PushCommand cmd = Git.wrap(repo).push();
                                
                                if (getUsername() != null && getPassword() != null && getUsername().trim().length() != 0 && getPassword().trim().length() !=0) {
                                        CredentialsProvider cp = new UsernamePasswordCredentialsProvider(getUsername(), getPassword());
                                        cmd.setCredentialsProvider(cp); 
                                }
                                
                                if (includeTags) {
                                        cmd.setPushTags();
                                } 
                                
                                cmd.setForce(true).call();
                        }

                } catch (Exception e) {
                        if (pushFailedProperty != null) {
                                getProject().setProperty(pushFailedProperty, e.getMessage());
                        }
                        
                        log(e.getMessage());

                        throw new GitBuildException("Git push failed", e);
                }
        }
}
