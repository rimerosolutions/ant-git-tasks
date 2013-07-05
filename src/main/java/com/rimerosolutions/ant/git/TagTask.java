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
package com.rimerosolutions.ant.git;

import org.apache.tools.ant.BuildException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

/**
 * Create a git tag and commit unless conditions are not met.
 * 
 * @author Yves Zoundi
 */
public class TagTask extends AbstractGitRepoAwareTask {

        private String name;

        /**
         * Sets the Git Tag name
         * 
         * @param name The tag name
         */
        public void setName(String name) {
                this.name = name;
        }

        @Override
        protected void doExecute() {
                String message = String.format("Creating tag '%s'", name);
                try {
                        // TODO log result?
                        Git.wrap(repo).commit().setAll(true).setMessage("commit all pending changes before tagging " + name);
                        Git.wrap(repo).tag().setName(name).setMessage(message).call();
                        Git.wrap(repo).commit().setAll(true).setMessage(message);
                } catch (GitAPIException ex) {
                         throw new BuildException(String.format("Could not create tag %s", name), ex);
                } 
        }
}
