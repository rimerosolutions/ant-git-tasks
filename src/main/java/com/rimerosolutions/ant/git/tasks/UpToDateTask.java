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

import org.apache.tools.ant.BuildException;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.IndexDiff;
import org.eclipse.jgit.treewalk.FileTreeIterator;

import com.rimerosolutions.ant.git.GitBuildException;


/**
 * Checks whether or not the Git Tree is up to date.
 * 
 * @author Yves Zoundi
 */
public class UpToDateTask extends AbstractGitRepoAwareTask {

        private String modificationExistProperty;

        /**
         * Sets a given project property if the tree is modified
         * 
         * @param p Property name to set
         */
        public void setModificationExistProperty(String p) {
                this.modificationExistProperty = p;
        }

        @Override
        protected void doExecute() throws BuildException {
                FileTreeIterator workingTreeIterator = new FileTreeIterator(repo);

                try {
                        IndexDiff diff = new IndexDiff(repo, Constants.HEAD, workingTreeIterator);
                        diff.diff();

                        Status status = new Status(diff);

                        if (!status.isClean()) {
                                if (modificationExistProperty != null) {
                                        getProject().setProperty(modificationExistProperty, "true");
                                }

                                if (isFailOnError()) {
                                        StringBuilder msg = new StringBuilder();
                                        msg.append("The Git tree was modified.");
                                        msg.append("\n").append("Changed:").append(status.getChanged());
                                        msg.append("\n").append("Added:").append(status.getAdded());
                                        msg.append("\n").append("Modified:").append(status.getModified());
                                        msg.append("\n").append("Missing:").append(status.getMissing());

                                        throw new GitBuildException("Status is not clean:" + msg.toString());
                                }
                        } else {
                                log("The Git tree is up to date!");
                        }
                } catch (IOException ioe) {
                        throw new GitBuildException("IO Error when checking repository status", ioe);
                }

        }
}
