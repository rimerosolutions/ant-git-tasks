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

/**
 * Git Ant Task Build Exception.
 *
 * @author Yves Zoundi
 */
public class GitBuildException extends BuildException {

        private static final long serialVersionUID = 7520461437069898546L;

        public GitBuildException(String msg, Exception e) {
                super(msg, e);
        }

        public GitBuildException(Exception e) {
                super(e);
        }

        public GitBuildException(String msg) {
                super(msg);
        }
        
}
