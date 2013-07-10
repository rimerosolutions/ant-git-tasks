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
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import com.rimerosolutions.ant.git.GitBuildException;

/**
 * Delete tags
 *
 * @author Yves Zoundi
 *
 */
public class TagDeleteTask extends AbstractGitRepoAwareTask {

        private String tags;

        /**
         * Sets the tags to delete (comma-separated list)
         *
         * @param tags Comma-separted list of tags to delete
         */
        public void setTags(String tags) {
                this.tags = tags;
        }

        @Override
        protected void doExecute() {
                try {
                        Git.wrap(repo).tagDelete().setTags(tags.split(",")).call();
                } catch (GitAPIException e) {
                        throw new GitBuildException("Could not delete specified tags", e);
                }
        }

}
