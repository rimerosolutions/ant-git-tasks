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
import org.eclipse.jgit.api.errors.GitAPIException;

import com.rimerosolutions.ant.git.AbstractGitRepoAwareTask;
import com.rimerosolutions.ant.git.GitBuildException;
import com.rimerosolutions.ant.git.GitUtils;

/**
 * Delete tags
 * 
 * <pre>
 * {@code 
 *  <git:git localDirectory="${testLocalRepo}">
 *    <git:tagdelete tags="${dummy.tag.name}"/>
 *  </git:git>
 * }
 * </pre> 
 * 
 * <p><a href="http://www.kernel.org/pub/software/scm/git/docs/git-tag.html">Git documentation about tag</a></p>
 *
 * <p><a href="http://download.eclipse.org/jgit/docs/latest/apidocs/org/eclipse/jgit/api/DeleteTagCommand.html">JGit DeleteTagCommand</a></p>
 *
 * @author Yves Zoundi
 */
public class TagDeleteTask extends AbstractGitRepoAwareTask {

        private String[] tags = {};
        private static final String TASK_NAME = "git-tag-delete";

        @Override
        public String getName() {
                return TASK_NAME;
        }

        /**
         * Sets the tags to delete (comma-separated list)
         *
         * @param tags Comma-separted list of tags to delete
         */
        public void setTags(String tags) {
                if (GitUtils.isNullOrBlankString(tags)) {
                        throw new BuildException("Invalid tag names provided.");
                }
                
                this.tags = tags.split(",");
        }

        @Override
        protected void doExecute() {
                try {
                        git.tagDelete().setTags(tags).call();
                } catch (GitAPIException e) {
                        throw new GitBuildException("Could not delete specified tags", e);
                }
        }

}
