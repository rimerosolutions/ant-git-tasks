/*
 * Copyright 2013 Fabrizio Cannizzo (https://github.com/smartrics/jgit-ant), Rimero Solutions
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
package smartrics.ant.git;

import java.io.File;
import java.net.URISyntaxException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.transport.URIish;

public abstract class AbstractGitTask extends Task implements GitTask {

        private String uri;
        private ProgressMonitor progressMonitor;
        private File directory;
        private String username;
        private String password;
        private String unlessCondition;
        private String ifCondition;

        public void setUnlessCondition(String unlessCondition) {
                this.unlessCondition = unlessCondition;
        }
        
        public void setIfCondition(String ifCondition) {
                this.ifCondition = ifCondition;
        }
        
        public String getUnlessCondition() {
                return unlessCondition;
        }
        
        public String getIfCondition() {
                return ifCondition;
        }

        public void setPassword(String password) {
                this.password = password;
        }

        public void setUsername(String username) {
                this.username = username;
        }

        public String getUsername() {
                return username;
        }

        public String getPassword() {
                return password;
        }

        public void setUri(String uri) {
                if (uri == null) {
                        throw new BuildException("Can;t set null URI attribute");
                }
                try {
                        new URIish(uri);
                        this.uri = uri;
                } catch (URISyntaxException e) {
                        throw new BuildException("Invalid URI: " + uri, e);
                }
        }

        @Override
        public void setDirectory(File dir) {
                if (dir == null) {
                        throw new BuildException("Can;t set null directory attribute");
                }
                this.directory = new File(dir.getAbsolutePath());
        }

        @Override
        public void setProgressMonitor(ProgressMonitor pm) {
                this.progressMonitor = pm;
        }

        protected String getUri() {
                return this.uri;
        }

        protected File getDirectory() {
                return this.directory;
        }

        protected ProgressMonitor getProgressMonitor() {
                return this.progressMonitor;
        }

        abstract public void execute();
}
