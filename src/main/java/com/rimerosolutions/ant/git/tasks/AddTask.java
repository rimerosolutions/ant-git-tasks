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
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.util.FileUtils;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.AddCommand;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.tools.ant.types.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import com.rimerosolutions.ant.git.AbstractGitRepoAwareTask;
import com.rimerosolutions.ant.git.GitUtils;
import com.rimerosolutions.ant.git.GitBuildException;

/**
 * Delete branches
 *
 * <p><a href="http://www.kernel.org/pub/software/scm/git/docs/git-add.html">Git documentation about add</a></p>
 *
 * <p><a href="http://download.eclipse.org/jgit/docs/jgit-2.0.0.201206130900-r/apidocs/org/eclipse/jgit/api/AddCommand.html">JGit AddCommand</a></p>
 *
 * @author Yves Zoundi
 */
public class AddTask extends AbstractGitRepoAwareTask {

        private static final String TASK_NAME = "git-patch";
        private boolean update;
        private Collection<FileSet> filesets = new ArrayList<FileSet>();

        /**
         * If set to true, the command only matches filepattern against already tracked files in the index rather than the working tree.
         *
         * @param update Default is false;
         */
        public void setUpdate(boolean update) {
                this.update = update;
        }

        @Override
        public String getName() {
                return TASK_NAME;
        }

        public void addFileset(FileSet fileset) {
                filesets.add(fileset);
        }

        @Override
        protected void doExecute() {

                try {
                        final AddCommand addCommand = git.add().setUpdate(update);

                        final FilePatternCallback cb = new FilePatternCallback() {
                                        public void onFilePattern(String pattern) {
                                                addCommand.addFilepattern(pattern);
                                        }
                                };

                        for (FileSet fileset : filesets) {
                                processResourceIterator(fileset.iterator(), cb);
                        }

                        addCommand.call();
                }
                catch (GitAPIException e) {
                        throw new GitBuildException(e);
                }
                catch (Exception e) {
                        throw new GitBuildException("Unexpected error", e);
                }
        }

        protected void processResource(Resource resource, FilePatternCallback cb) throws Exception {
                if (resource.isExists()) {
                        if (resource.isDirectory()) {
                                processResourceIterator(resource.iterator(), cb);
                        }
                        else {
                                FileResource fileResource = (FileResource) resource;
                                processFile(fileResource.getFile(), cb);
                        }
                }
        }

        protected void processFile(File file, FilePatternCallback cb) throws Exception {
                String relativePath = FileUtils.getRelativePath(getDirectory(), file);
                cb.onFilePattern(relativePath);
        }

        protected void processResourceIterator(Iterator<Resource> resourceIterator, FilePatternCallback cb) throws Exception {
                while(resourceIterator.hasNext()) {
                        Resource resource = resourceIterator.next();
                        processResource(resource, cb);
                }
        }

        private static interface FilePatternCallback {
                void onFilePattern(String pattern);
        }
}
