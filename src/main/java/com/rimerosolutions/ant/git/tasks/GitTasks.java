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
package com.rimerosolutions.ant.git.tasks;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import smartrics.ant.git.AbstractGitTask;
import smartrics.ant.git.GitTask;
import smartrics.ant.git.PullTask;
import smartrics.ant.git.SimpleProgressMonitor;

public class GitTasks extends Task {

        private boolean verbose = false;
        private File localDirectory;
        private String username;
        private String password;

        private Queue<GitTask> tasks = new LinkedList<GitTask>();

        public void setVerbose(boolean v) {
            this.verbose = v;
        }
        
        public void setUsername(String username) {
                this.username = username;
        }
        
        public void setPassword(String password) {
                this.password = password;
        }
        
        public void setLocalDirectory(File dir) {
            this.localDirectory = dir;
        }

        public CloneTask createClone() {
            CloneTask c = new CloneTask();
            tasks.offer(c);
            return c;
        }
        
        public CommitTask createCommit() {
            CommitTask c = new CommitTask();
            tasks.offer(c);
            return c;
        }
        
         public UpToDateTask createUpToDate() {
            UpToDateTask c = new UpToDateTask();
            tasks.offer(c);
            return c;
        }
        
        public PushTask createPush() {
            PushTask c = new PushTask();
            tasks.offer(c);
            return c;
        }
        
          public TagTask createTag() {
            TagTask c = new TagTask();
            tasks.offer(c);
            return c;
        }
        
        public PullTask createPull() {
            PullTask p = new PullTask();
            tasks.offer(p);
            return p;
        }

        @Override
        public void execute() throws BuildException {
            if (localDirectory == null) {
                throw new BuildException("Please specify local repository directory");
            }
            
            while(!tasks.isEmpty()) {
                GitTask t = tasks.poll();
                
                if (verbose) {
                    t.setProgressMonitor(new SimpleProgressMonitor(t));
                }
                
                if (t instanceof AbstractGitTask) {
                        AbstractGitTask aTask = (AbstractGitTask) t;
                        if (aTask.getUsername() == null) {
                                aTask.setUsername(username);
                        }
                        
                        if (aTask.getPassword() == null) {
                                aTask.setPassword(password);
                        }
                        
                        String ifCondition = aTask.getIfCondition();
                        if (ifCondition != null && ifCondition.trim().length() != 0) {
                               if(getProject().getProperty(ifCondition) == null) {
                                       continue;
                               }
                        }
                        
                        String unlessCondition = aTask.getUnlessCondition();
                        if (unlessCondition != null && unlessCondition.trim().length() != 0) {
                                if(getProject().getProperty(ifCondition) != null) {
                                        continue;
                                }
                         }
                }
                
                t.setDirectory(localDirectory);
                
                try {
                    t.execute();
                } catch (Exception e) {
                    throw new BuildException("Unexpected exception occurred!", e);
                }
            }
        }
    }
