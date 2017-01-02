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

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;

import com.rimerosolutions.ant.git.AbstractGitRepoAwareTask;
import com.rimerosolutions.ant.git.GitBuildException;

/**
 * Query the current branch.
 *
 * <pre>{@code
 * <git:git directory="${testLocalRepo}" verbose="true">
 *    <git:currentbranch outputproperty="${outputProperty}"/>
 * </git:git>
 * }</pre>
 *
 * <p><a href="http://www.kernel.org/pub/software/scm/git/docs/git-branch.html">Git branch documentation</a></p>
 * <p><a href="http://download.eclipse.org/jgit/docs/latest/apidocs/org/eclipse/jgit/lib/Repository.html">JGit Repository API</a></p>
 *
 * @author KenBarby
 */
public class CurrentBranchTask extends AbstractGitRepoAwareTask {

        private static final String TASK_NAME = "git-current-branch";

        private String outputProperty;

        @Override
        public String getName() {
                return TASK_NAME;
        }

        /**
         * Sets the output property name
         *
         * @antdoc.required
         * @param outputProperty - required: the ant property name to assign the current branch value to
         */
        public void setOutputProperty(String outputProperty) {
                this.outputProperty = outputProperty;
        }

        @Override
        protected void doExecute() {
                try {
                        Repository repository = git.getRepository();
                        ObjectId objectId = repository.getRef(Constants.HEAD).getObjectId();
                        String branch = repository.getBranch();
                        // Backward compatibility
						getProject().setProperty(outputProperty, branch);
						// Extended (nested) properties
						getProject().setProperty(outputProperty + ".name", branch);
						getProject().setProperty(outputProperty + ".id", objectId.name());
                        getProject().setProperty(outputProperty + ".shortId", objectId.abbreviate(8).name());
                } catch (IOException e) {
                        throw new GitBuildException("Could not query the current branch.", e);
                }
        }

}
