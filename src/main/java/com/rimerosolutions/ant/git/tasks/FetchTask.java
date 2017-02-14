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
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;

import com.rimerosolutions.ant.git.AbstractGitRepoAwareTask;
import com.rimerosolutions.ant.git.GitBuildException;
import com.rimerosolutions.ant.git.GitTaskUtils;

/**
 * Fetch remote repository data.
 *
 * <p><a href="http://www.kernel.org/pub/software/scm/git/docs/git-fetch.html">Git documentation about fetch</a></p>
 * <p><a href="http://download.eclipse.org/jgit/docs/latest/apidocs/org/eclipse/jgit/api/FetchCommand.html">JGit FetchCommand</a></p>
 *
 * @author Yves Zoundi
 */
public class FetchTask extends AbstractGitRepoAwareTask {

        private boolean dryRun = false;
        private String remoteRefSpec = "+" + Constants.R_HEADS + "*:" + Constants.R_REMOTES + Constants.DEFAULT_REMOTE_NAME + "/*";
        private boolean removeDeletedRefs = true;
        private boolean thinPack = true;
        private boolean defaultRefSpecs = true;
        private static final String TASK_NAME = "git-fetch";
        private static final String FETCH_FAILED_MESSAGE = "Fetch failed";

        @Override
        public String getName() {
                return TASK_NAME;
        }

        /**
         * Sets the thin-pack preference for fetch operation.
         *
         * @antdoc.notrequired
         * @param thinPack (Default value is true)
         */
        public void setThinPack(boolean thinPack) {
                this.thinPack = thinPack;
        }

        /**
         * Sets the remote refSpec preference for fetch operation.
         *
         * @antdoc.notrequired
         * @param remoteRefSpec (Default value is '+/refs/heads/*:refs/remote/origin/*' )
         *
         * One is able to provide an null or empty string to not set any refSpec.
         */
        public void setRemoteRefSpec(String remoteRefSpec) {
                this.remoteRefSpec = remoteRefSpec;
        }

        /**
         * If set to true, refs are removed which no longer exist in the source
         *
         * @antdoc.notrequired
         * @param removeDeletedRefs (Default value is true)
         */
        public void setRemoveDeletedRefs(boolean removeDeletedRefs) {
                this.removeDeletedRefs = removeDeletedRefs;
        }

        /**
         * Sets whether the fetch operation should be a dry run
         *
         * @antdoc.notrequired
         * @param dryRun (Default value is true)
         */
        public void setDryRun(boolean dryRun) {
                this.dryRun = dryRun;
        }

        /**
         * If set to true, the following additional refs are also fetched:
         * +/refs/notes/*:refs/notes/* and +/refs/tags/*:refs/tags/*
         *
         * @antdoc.notrequired
         * @param defaultRefSpecs (Default value is true)
         */
        public void setDefaultRefSpecs(boolean defaultRefSpecs) {
                this.defaultRefSpecs  = defaultRefSpecs;
        }

        @Override
        public void doExecute() {
                try {
                        StoredConfig config = git.getRepository().getConfig();
                        List<RemoteConfig> remoteConfigs = RemoteConfig.getAllRemoteConfigs(config);

                        if (remoteConfigs.isEmpty()) {
                                URIish uri = new URIish(getUri());

                                RemoteConfig remoteConfig = new RemoteConfig(config, Constants.DEFAULT_REMOTE_NAME);
                                remoteConfig.addURI(uri);
                                remoteConfig.addFetchRefSpec(new RefSpec("+" + Constants.R_HEADS + "*:" + Constants.R_REMOTES + Constants.DEFAULT_REMOTE_NAME + "/*"));

                                config.setString(ConfigConstants.CONFIG_BRANCH_SECTION, Constants.MASTER, ConfigConstants.CONFIG_KEY_REMOTE, Constants.DEFAULT_REMOTE_NAME);
                                config.setString(ConfigConstants.CONFIG_BRANCH_SECTION, Constants.MASTER, ConfigConstants.CONFIG_KEY_MERGE, Constants.R_HEADS + Constants.MASTER);

                                remoteConfig.update(config);
                                config.save();
                        }

                        List<RefSpec> specs = new ArrayList<RefSpec>(3);

                        if (null != remoteRefSpec && remoteRefSpec != "") {
                            specs.add(new RefSpec(remoteRefSpec));
                        }
                        if (defaultRefSpecs) {
                            specs.add(new RefSpec("+" + Constants.R_NOTES + "*:" + Constants.R_NOTES + "*"));
                            specs.add(new RefSpec("+" + Constants.R_TAGS + "*:" + Constants.R_TAGS + "*"));
                        }

                        FetchCommand fetchCommand = git.fetch().
                                setDryRun(dryRun).
                                setThin(thinPack).
                                setRemote(getUri()).
                                setRefSpecs(specs).
                                setRemoveDeletedRefs(removeDeletedRefs);

                        setupCredentials(fetchCommand);

                        if (getProgressMonitor() != null) {
                                fetchCommand.setProgressMonitor(getProgressMonitor());
                        }

                        FetchResult fetchResult = fetchCommand.call();

                        GitTaskUtils.validateTrackingRefUpdates(FETCH_FAILED_MESSAGE, fetchResult.getTrackingRefUpdates());

                        log(fetchResult.getMessages());

                }
                catch (URISyntaxException e) {
                        throw new GitBuildException("Invalid URI syntax: " + e.getMessage(), e);
                }
                catch (IOException e) {
                        throw new GitBuildException("Could not save or get repository configuration: " + e.getMessage(), e);
                }
                catch (InvalidRemoteException e) {
                        throw new GitBuildException("Invalid remote URI: " + e.getMessage(), e);
                }
                catch (TransportException e) {
                        throw new GitBuildException("Communication error: " + e.getMessage(), e);
                }
                catch (GitAPIException e) {
                        throw new GitBuildException("Unexpected exception: " + e.getMessage(), e);
                }
        }

}
