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
import org.eclipse.jgit.api.MergeResult.MergeStatus;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.transport.FetchResult;

import com.rimerosolutions.ant.git.AbstractGitRepoAwareTask;
import com.rimerosolutions.ant.git.GitBuildException;
import com.rimerosolutions.ant.git.GitTaskUtils;

/**
 * Pull changes from a repository.
 *
 * <p><a href="http://www.kernel.org/pub/software/scm/git/docs/git-pull.html">Git documentation about pull</a></p>
 *
 * <p><a href="http://download.eclipse.org/jgit/docs/latest/apidocs/org/eclipse/jgit/api/PullCommand.html">JGit PullCommand</a></p>
 *
 * @author Yves Zoundi
 */
public class PullTask extends AbstractGitRepoAwareTask {

        private static final String TASK_NAME = "git-pull";
private boolean rebase = false;

        @Override
        public String getName() {
                return TASK_NAME;
        }
        
        
public void setRebase(boolean rebase) {
        this.rebase = rebase;
}
        @Override
        public void doExecute() {
                try {
                        PullCommand pullCommand = git.pull().setRebase(rebase);

                        if (getProgressMonitor() != null) {
                                pullCommand.setProgressMonitor(getProgressMonitor());
                        }

                        setupCredentials(pullCommand);

                        PullResult pullResult = pullCommand.call();

                        if (!pullResult.isSuccessful()) {
                                FetchResult fetchResult = pullResult.getFetchResult();

                                GitTaskUtils.validateTrackingRefUpdates("Merge failed", fetchResult.getTrackingRefUpdates());

                                MergeStatus mergeStatus = pullResult.getMergeResult().getMergeStatus();

                                if (!mergeStatus.isSuccessful()) {
                                        throw new BuildException(String.format("Merge failed - Status '%s'.", mergeStatus.name()));
                                }
                        }
                }
                catch (Exception e) {
                        throw new GitBuildException(String.format("Could not clone URL '%s'.", getUri()), e);
                }
        }

}
